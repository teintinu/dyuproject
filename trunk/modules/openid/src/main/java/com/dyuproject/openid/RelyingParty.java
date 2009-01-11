//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.openid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.openid.Identifier.Resolver;
import com.dyuproject.openid.Identifier.ResolverCollection;
import com.dyuproject.openid.manager.HttpSessionUserManager;

/**
 * Relying party which discovers, associates and verifies the authentication of a user.
 * An implementation of RelyingParty.Listener will enable you to listen to events 
 * during a user's authentication lifecycle.
 * 
 * @author David Yu
 * @created Sep 21, 2008
 */

public class RelyingParty
{
    
    public static final String DEFAULT_RESOURCE_PATH = "openid.properties";
    public static final String DEFAULT_IDENTIFIER_PARAMETER = "openid_identifier";
    
    private static RelyingParty __instance = null;
    
    public static RelyingParty getInstance()
    {
        if(__instance==null)
        {
            synchronized(RelyingParty.class)
            {
                if(__instance==null)
                {
                    URL resource = getResource(DEFAULT_RESOURCE_PATH);
                    try
                    {                        
                        // configure defaults if openid.properties is not available
                        __instance = resource==null ? newInstance(new Properties()) : 
                            newInstance(resource);
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }    
                }
            }
        }
        return __instance;
    }
    
    public static RelyingParty newInstance(String resourceLoc)
    {        
        URL resource = getResource(resourceLoc);
        if(resource==null)
            throw new IllegalStateException(resourceLoc + " could not be resolved in classpath.");
        try
        {
            return newInstance(resource);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static RelyingParty newInstance(URL resource) throws IOException
    {
        return newInstance(resource.openStream());
    }
    
    public static RelyingParty newInstance(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);
        return newInstance(props);
    }
    
    public static RelyingParty newInstance(Properties properties)
    {  
        // discovery
        String discoveryParam = properties.getProperty("openid.discovery");
        Discovery discovery = discoveryParam==null ? new DefaultDiscovery() : 
            (Discovery)newObjectInstance(discoveryParam);            

        // association
        String associationParam = properties.getProperty("openid.association");
        Association association = associationParam==null ? new DiffieHellmanAssociation() : 
            (Association)newObjectInstance(associationParam);
        
        // http connector
        String httpConnectorParam = properties.getProperty("openid.httpconnector");
        HttpConnector httpConnector = httpConnectorParam==null ? new SimpleHttpConnector() : 
            (HttpConnector)newObjectInstance(httpConnectorParam);       
        
        // user manager
        String managerParam = properties.getProperty("openid.user.manager");            
        OpenIdUserManager manager = managerParam == null ? new HttpSessionUserManager() :
            (OpenIdUserManager)newObjectInstance(managerParam);        
        manager.init(properties);        
        
        OpenIdContext context = new OpenIdContext(discovery, association, httpConnector);        
        RelyingParty relyingParty = new RelyingParty(context, manager);
        
        // identifier parameter (default is openid_identifier)
        String identifierParameter = properties.getProperty("openid.identifier.parameter");
        if(identifierParameter!=null)
            relyingParty._identifierParameter = identifierParameter;
        
        // relying party listeners
        String listenersParam = properties.getProperty("openid.relyingparty.listeners");
        if(listenersParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(listenersParam, ",;");
            while(tokenizer.hasMoreTokens())
                relyingParty.addListener((Listener)newObjectInstance(tokenizer.nextToken().trim()));
        }
        
        // openid identifier resolvers
        String resolversParam = properties.getProperty("openid.identifier.resolvers");
        if(resolversParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(resolversParam, ",;");
            while(tokenizer.hasMoreTokens())
                relyingParty.addResolver((Resolver)newObjectInstance(tokenizer.nextToken().trim()));
        }
        
        return relyingParty;
    }    
    
    private static URL getResource(String path)
    {
        URL resource = RelyingParty.class.getClassLoader().getResource(path);
        return resource==null ? Thread.currentThread().getContextClassLoader().getResource(path) :
            resource;
    }
    
    private static Object newObjectInstance(String className)
    {
        try
        {
            return RelyingParty.class.getClassLoader().loadClass(className).newInstance();
        }
        catch(Exception e)
        {
            try
            {
                return Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
            }
            catch(Exception e2)
            {
                throw new RuntimeException(e2);
            }
        }
    }
    
    public static Map<String,String> getAuthParameters(HttpServletRequest request)
    {
        Map<String,String> params = new HashMap<String,String>();
        Enumeration<String> names = (Enumeration<String>)request.getParameterNames();
        while(names.hasMoreElements())
        {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }        
        return params;
    }
    
    public static boolean isAuthResponse(HttpServletRequest request)
    {
        return Constants.Mode.ID_RES.equals(request.getParameter(Constants.OPENID_MODE));
    }
    
    public static UrlEncodedParameterMap getAuthUrlMap(OpenIdUser user, String trustRoot, 
            String realm, String returnTo)
    {
        if(!user.isAssociated())
            throw new IllegalArgumentException("claimed_id of user has not been verified.");
        
        UrlEncodedParameterMap map = new UrlEncodedParameterMap(user.getOpenIdServer());
        
        String identity = user.getOpenIdDelegate();
        if(identity==null)
            identity = user.getClaimedId();
        
        map.put(Constants.OPENID_NS, Constants.DEFAULT_NS);
        map.put(Constants.OPENID_MODE, Constants.Mode.CHECKID_SETUP);
        
        map.put(Constants.OPENID_TRUST_ROOT, trustRoot);
        map.put(Constants.OPENID_REALM, realm);
        map.put(Constants.OPENID_RETURN_TO, returnTo);
        map.put(Constants.OPENID_ASSOC_HANDLE, user.getAssocHandle());
        
        map.put(Constants.OPENID_IDENTITY, identity);
        map.put(Constants.OPENID_CLAIMED_ID, identity);
        
        return map;
    }
    
    public static StringBuilder getAuthUrlBuffer(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        if(!user.isAssociated())
            throw new IllegalArgumentException("claimed_id of user has not been verified.");    
        
        String identity = user.getOpenIdDelegate();
        if(identity==null)
            identity = user.getClaimedId();
        
        StringBuilder buffer = new StringBuilder().append(user.getOpenIdServer());        
        
        buffer.append('?').append(Constants.OPENID_NS).append('=').append(Constants.DEFAULT_NS);
        
        buffer.append('&').append(Constants.OPENID_MODE).append('=').append(
                Constants.Mode.CHECKID_SETUP);
        
        buffer.append('&').append(Constants.OPENID_TRUST_ROOT).append('=').append(
                UrlEncodedParameterMap.encode(trustRoot));
        buffer.append('&').append(Constants.OPENID_REALM).append('=').append(
                UrlEncodedParameterMap.encode(realm));
        buffer.append('&').append(Constants.OPENID_RETURN_TO).append('=').append(
                UrlEncodedParameterMap.encode(returnTo));
        buffer.append('&').append(Constants.OPENID_ASSOC_HANDLE).append('=').append(
                UrlEncodedParameterMap.encode(user.getAssocHandle())); 
        
        buffer.append('&').append(Constants.OPENID_IDENTITY).append('=').append(
                UrlEncodedParameterMap.encode(identity));
        buffer.append('&').append(Constants.OPENID_CLAIMED_ID).append('=').append(
                UrlEncodedParameterMap.encode(identity));      
        
        return buffer;
    }
    
    public static String getAuthUrlString(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        return getAuthUrlBuffer(user, trustRoot, realm, returnTo).toString();
    }
    
    private OpenIdUserManager _manager;
    private OpenIdContext _context;
    private String _identifierParameter = DEFAULT_IDENTIFIER_PARAMETER;
    
    private ListenerCollection _listener = new ListenerCollection();
    private ResolverCollection _resolver = new ResolverCollection();
    
    private boolean _destroyed = false;
    
    public RelyingParty()
    {
        
    }    

    public RelyingParty(OpenIdContext context, OpenIdUserManager manager)
    {
        _context = context;
        _manager = manager;
    }
    
    public void setOpenIdUserManager(OpenIdUserManager manager)
    {
        if(_manager!=null)
            throw new IllegalArgumentException("manager already set.");
        
        _manager = manager;            
    }
    
    public OpenIdUserManager getOpenIdUserManager()
    {
        return _manager;
    }
    
    public void setOpenIdContext(OpenIdContext context)
    {
        if(_context!=null)
            throw new IllegalArgumentException("context already set.");
        
        _context = context;    
    }
    
    public OpenIdContext getOpenIdContext()
    {
        return _context;
    }
    
    public boolean isDestroyed()
    {
        return _destroyed;
    }
    
    public OpenIdUser discover(HttpServletRequest request) 
    throws Exception
    {
        OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
        if(user!=null)
            return user;

        user = _manager.getUser(request);
        if(user!=null)
        {
            if(user.isAuthenticated())
                _listener.onAccess(user, request);
            request.setAttribute(OpenIdUser.ATTR_NAME, user);
            return user;
        }
        String id = request.getParameter(_identifierParameter);
        if(id==null)
            return null;
        id = id.trim();
        if(id.length()==0)
            return null;
        
        Identifier identifier = Identifier.getIdentifier(id, _resolver, _context);
        if(!identifier.isResolved())
            return null;        
        
        String url = identifier.getUrl();
        String claimedId = identifier.getId();
        if(claimedId==null)
            claimedId = url;
        
        user = _context.getDiscovery().discover(claimedId, url, _context);
        if(user!=null)
        {
            _listener.onDiscovery(user, request);
            request.setAttribute(OpenIdUser.ATTR_NAME, user);
        }
        
        return user;
    }
    
    public boolean verifyAuth(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws Exception
    {
        if(_context.getAssociation().verifyAuth(user, getAuthParameters(request), _context))
        {
            _listener.onAuthenticate(user, request);
            if(!response.isCommitted())
                _manager.saveUser(user, request, response);
            return true;
        }        
        return false;
    }
    
    public boolean associateAndAuthenticate(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response, String trustRoot, String realm, 
            String returnTo) throws Exception
    {
        return _context.getAssociation().associate(user, _context) && authenticate(user, request, 
                response, trustRoot, realm, returnTo);
    }
    
    boolean authenticate(OpenIdUser user, HttpServletRequest request, HttpServletResponse response,
            String trustRoot, String realm, String returnTo) throws IOException
    {
        UrlEncodedParameterMap params = RelyingParty.getAuthUrlMap(user, trustRoot, realm, returnTo);
        
        _listener.onPreAuthenticate(user, request, params);      

        if(!response.isCommitted())
            _manager.saveUser(user, request, response);
        
        response.sendRedirect(params.toString());
        return true;
    }    
    
    public boolean invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        return _manager.invalidate(request, response);
    }
    
    public RelyingParty addListener(Listener listener)
    {
        _listener.addListener(listener);
        return this;
    }
    
    public RelyingParty addResolver(Resolver resolver)
    {
        _resolver.addResolver(resolver);
        return this;
    }
    
    public void destroy()
    {
        if(_destroyed)
            return;
        
        _destroyed = true;
        // TODO
    }
    
    
    public interface Listener
    {
        // the authentication process (in order)
        public void onDiscovery(OpenIdUser user, HttpServletRequest request);
        
        public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request, 
                UrlEncodedParameterMap params);        
        
        public void onAuthenticate(OpenIdUser user, HttpServletRequest request);
        
        public void onAccess(OpenIdUser user, HttpServletRequest request);
    }
    
    public static class ListenerCollection implements Listener
    {      

        private Listener[] _listeners = new Listener[]{};
        
        public ListenerCollection addListener(Listener listener)
        {
            if(listener==null || indexOf(listener)!=-1)
                return this;
            
            Listener[] oldListeners = _listeners;
            Listener[] listeners = new Listener[oldListeners.length+1];
            System.arraycopy(oldListeners, 0, listeners, 0, oldListeners.length);
            listeners[oldListeners.length] = listener;
            _listeners = listeners;
            
            return this;
        }
        
        public int indexOf(Listener listener)
        {
            if(listener!=null)
            {
                Listener[] listeners = _listeners;
                for(int i=0; i<listeners.length; i++)
                {
                    if(listeners[i].equals(listener))
                        return i;
                }
            }
            return -1;
        }
        
        public Listener[] getListeners()
        {
            return _listeners;
        }

        public void onDiscovery(OpenIdUser user, HttpServletRequest request)
        {            
            for(Listener l : getListeners())
                l.onDiscovery(user, request);       
        }
        
        public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request, 
                UrlEncodedParameterMap params)
        {
            for(Listener l : getListeners())
                l.onPreAuthenticate(user, request, params);
        }
        
        public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
        {
            for(Listener l : getListeners())
                l.onAuthenticate(user, request);
        }
        
        public void onAccess(OpenIdUser user, HttpServletRequest request)
        {
            for(Listener l : getListeners())
                l.onAccess(user, request);
        }
        
    }

}
