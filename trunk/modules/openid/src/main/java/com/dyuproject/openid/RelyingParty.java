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

import com.dyuproject.openid.Discovery.UserCache;
import com.dyuproject.openid.Identifier.Resolver;
import com.dyuproject.openid.Identifier.ResolverCollection;
import com.dyuproject.openid.manager.HttpSessionUserManager;
import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.SimpleHttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Relying party which discovers, associates and verifies the authentication of a user.
 * An implementation of RelyingParty.Listener will enable you to listen to events 
 * during a user's authentication lifecycle.
 * 
 * <blockquote>
 * <pre>
 *   OpenIdUser user = _relyingParty.discover(request);
 *   if(user==null)
 *   {                
 *       if(RelyingParty.isAuthResponse(request))
 *       {
 *           // authentication timeout                    
 *           response.sendRedirect(request.getRequestURI());
 *       }
 *       else
 *       {
 *           // set error msg if the openid_identifier is not resolved.
 *           if(request.getParameter(_relyingParty.getIdentifierParameter())!=null)
 *               request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
 *           
 *           // new user
 *           request.getRequestDispatcher("/login.jsp").forward(request, response);
 *       }
 *       return;
 *   }
 *   
 *   if(user.isAuthenticated())
 *   {
 *       // user already authenticated
 *       request.getRequestDispatcher("/home.jsp").forward(request, response);
 *       return;
 *   }
 *   
 *   if(user.isAssociated() && RelyingParty.isAuthResponse(request))
 *   {
 *       // verify authentication
 *       if(_relyingParty.verifyAuth(user, request, response))
 *       {
 *           // authenticated                    
 *          // redirect to home to remove the query params instead of doing:
 *           // request.setAttribute("user", user); request.getRequestDispatcher("/home.jsp").forward(request, response);
 *           response.sendRedirect(request.getContextPath() + "/home/");
 *       }
 *       else
 *       {
 *           // failed verification
 *           request.getRequestDispatcher("/login.jsp").forward(request, response);
 *       }
 *       return;
 *   }
 *   
 *   StringBuffer url = request.getRequestURL();
 *   String trustRoot = url.substring(0, url.indexOf("/", 9));
 *   String realm = url.substring(0, url.lastIndexOf("/"));
 *   String returnTo = url.toString();            
 *   if(_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm, returnTo))
 *   {
 *       // successful association
 *       return;
 *   } 
 * </pre>
 * </blockquote>
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
        RelyingParty instance = __instance;
        if(instance==null)
        {
            synchronized(RelyingParty.class)
            {
                instance = __instance;
                if(instance==null)
                {
                    URL resource = getResource(DEFAULT_RESOURCE_PATH);
                    try
                    {                        
                        // configure defaults if openid.properties is not available
                        __instance = instance = resource==null ? newInstance(new Properties()) : 
                            newInstance(resource);
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }    
                }
            }
        }
        return instance;
    }
    
    public static RelyingParty newInstance(String resourceLoc)
    {        
        URL resource = getResource(resourceLoc);
        if(resource==null)
            throw new RuntimeException(resourceLoc + " not found in the classpath.");
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
        HttpConnector httpConnector = httpConnectorParam==null ? SimpleHttpConnector.getDefault() : 
            (HttpConnector)newObjectInstance(httpConnectorParam);       
        
        // auth redirection
        String authRedirectionParam = properties.getProperty("openid.authredirection");
        AuthRedirection authRedirection = authRedirectionParam==null ? SimpleRedirection.DEFAULT_INSTANCE : 
            (AuthRedirection)newObjectInstance(authRedirectionParam);
        
        // user manager
        String managerParam = properties.getProperty("openid.user.manager");            
        OpenIdUserManager manager = managerParam == null ? HttpSessionUserManager.getDefault() :
            (OpenIdUserManager)newObjectInstance(managerParam);        
        manager.init(properties);        
        
        OpenIdContext context = new OpenIdContext(discovery, association, httpConnector);        
        RelyingParty relyingParty = new RelyingParty(context, manager);
        relyingParty.setAuthRedirection(authRedirection);
        
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
        
        // openid user cache
        String userCacheParam = properties.getProperty("openid.user.cache");
        relyingParty._userCache = userCacheParam==null ? new IdentifierSelectUserCache() : 
            (UserCache)newObjectInstance(userCacheParam);
        
        // openid automatic redirect
        // when the user is redirected to his provider and he somehow navigates away from his
        // provider and returns to your site ... the relying party will do an automatic redirect
        // back to his provider for authentication (if set to true)
        String automaticRedirectParam = properties.getProperty("openid.automatic_redirect");
        if(automaticRedirectParam!=null)
            relyingParty._automaticRedirect = Boolean.parseBoolean(automaticRedirectParam);
        
        return relyingParty;
    }    
    
    static URL getResource(String resource)
    {
        return ClassLoaderUtil.getResource(resource, RelyingParty.class);
    }
    
    static Object newObjectInstance(String className)
    {        
        try
        {
            return ClassLoaderUtil.newInstance(className, RelyingParty.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
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
    
    public static boolean isAuthCancel(HttpServletRequest request)
    {
        return Constants.Mode.CANCEL.equals(request.getParameter(Constants.OPENID_MODE));
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
        char separator = user.getOpenIdServer().indexOf('?')==-1 ? '?' : '&';
        buffer.append(separator).append(Constants.OPENID_NS).append('=').append(Constants.DEFAULT_NS);
        
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
    private AuthRedirection _authRedirection = SimpleRedirection.DEFAULT_INSTANCE;
    private final ListenerCollection _listener = new ListenerCollection();
    private final ResolverCollection _resolver = new ResolverCollection();
    private UserCache _userCache;
    
    private boolean _destroyed = false;
    private boolean _automaticRedirect = true;
    
    public RelyingParty()
    {
        
    }    

    public RelyingParty(OpenIdContext context, OpenIdUserManager manager)
    {
        _context = context;
        _manager = manager;
    }
    
    public RelyingParty(OpenIdContext context, OpenIdUserManager manager, 
            AuthRedirection authRedirection, UserCache userCache)
    {
        _context = context;
        _manager = manager;
        _authRedirection = authRedirection;
        _userCache = userCache;
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
    
    public String getIdentifierParameter()
    {
        return _identifierParameter;
    }
    
    public boolean isDestroyed()
    {
        return _destroyed;
    }
    
    public boolean isAutomaticRedirect()
    {
        return _automaticRedirect;
    }
    
    public void setAutomaticRedirect(boolean automaticRedirect)
    {
        _automaticRedirect = automaticRedirect;
    }
    
    public AuthRedirection getAuthRedirection()
    {
        return _authRedirection;
    }     
    
    public void setAuthRedirection(AuthRedirection authRedirection)
    {
        if(authRedirection!=null)
            _authRedirection = authRedirection;
    }
    
    public UserCache getUserCache()
    {
        return _userCache;
    }
    
    public void setUserCache(UserCache userCache)
    {
        if(_userCache!=null)
            throw new IllegalArgumentException("userCache already set.");
        
        _userCache = userCache;
    }
    
    public OpenIdUser discover(HttpServletRequest request) 
    throws Exception
    {
        OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
        if(user==null)
        {
            user = _manager.getUser(request);
            String id = null;
            if(user!=null)
            {
                if(user.isAuthenticated())
                {
                    _listener.onAccess(user, request);
                    request.setAttribute(OpenIdUser.ATTR_NAME, user);
                    return user;
                }
                if((id=request.getParameter(_identifierParameter))==null)
                    return (user.isAssociated() && !_automaticRedirect) || isAuthCancel(request) ? null : user;
                else if((id=id.trim()).length()!=0)
                {
                    Identifier identifier = Identifier.getIdentifier(id, _resolver, _context);
                    if(identifier.isResolved())
                    {                    
                        if(!identifier.getId().equals(user.getIdentifier()))
                        {
                            // new user or ... the user cancels authentication
                            // and provides a different openid identifier
                            return discover(identifier, request);
                        }
                    }
                }
            }
            else if((id=request.getParameter(_identifierParameter))!=null && (id=id.trim()).length()!=0)
            {
                Identifier identifier = Identifier.getIdentifier(id, _resolver, _context);
                if(identifier.isResolved())
                    return discover(identifier, request);
            }
        }
        return user;
    }
    
    protected OpenIdUser discover(Identifier identifier, HttpServletRequest request) 
    throws Exception
    {
        OpenIdUser user = _userCache.get(identifier.getUrl(), true);
        if(user==null)
        {
            if((user=_context.getDiscovery().discover(identifier, _context))==null)
                return null;
            
            _userCache.put(identifier.getUrl(), user);
        }
        
        _listener.onDiscovery(user, request);
        request.setAttribute(OpenIdUser.ATTR_NAME, user);   
        return user;
    }
    
    public boolean verifyAuth(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws Exception
    {
        if(_context.getAssociation().verifyAuth(user, getAuthParameters(request), _context))
        {
            _listener.onAuthenticate(user, request);
            _manager.saveUser(user, request, response);
            return true;
        }        
        return false;
    }
    
    public boolean associate(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws Exception
    {
        if(_context.getAssociation().associate(user, _context))
        {
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
    
    public boolean authenticate(OpenIdUser user, HttpServletRequest request, HttpServletResponse response,
            String trustRoot, String realm, String returnTo) throws IOException
    {
        UrlEncodedParameterMap params = getAuthUrlMap(user, trustRoot, realm, returnTo);
        
        _listener.onPreAuthenticate(user, request, params);
        
        _manager.saveUser(user, request, response);        
        
        _authRedirection.redirect(params, request, response);
        
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
            
            synchronized(this)
            {
                Listener[] oldListeners = _listeners;
                Listener[] listeners = new Listener[oldListeners.length+1];
                System.arraycopy(oldListeners, 0, listeners, 0, oldListeners.length);
                listeners[oldListeners.length] = listener;
                _listeners = listeners;
            }
            
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
