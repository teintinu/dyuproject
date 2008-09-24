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
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Relying party which discovers, associates and verifies the authentication of a user. 
 * 
 * @author David Yu
 * @created Sep 21, 2008
 */

public class RelyingParty
{
    
    public static final String DEFAULT_RESOURCE_PATH = "openid.properties";
    public static final String DEFAULT_PARAMETER = "openid_identifier";
    
    private static final Pattern PREFIX = Pattern.compile("^https?://");
    
    private static RelyingParty __instance = null;
    
    public static RelyingParty getInstance()
    {
        if(__instance==null)
        {
            synchronized(DEFAULT_RESOURCE_PATH)
            {
                if(__instance==null)
                    __instance = newInstance(DEFAULT_RESOURCE_PATH);
            }
        }
        return __instance;
    }
    
    public static RelyingParty newInstance(String properties)
    {        
        URL resource = RelyingParty.class.getClassLoader().getResource(properties);
        if(resource==null)
            resource = Thread.currentThread().getContextClassLoader().getResource(properties);
        if(resource==null)
            throw new IllegalStateException(properties + " could not be resolved in classpath.");
        try
        {
            return newInstance(resource);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public static RelyingParty newInstance(URL properties) throws IOException
    {
        return newInstance(properties.openStream());
    }
    
    public static RelyingParty newInstance(InputStream properties) throws IOException
    {
        Properties props = new Properties();
        props.load(properties);
        return newInstance(props);
    }
    
    public static RelyingParty newInstance(Properties properties) throws IOException
    {
        // required
        String cookieName = properties.getProperty("openid.cookie.name");
        String secretKey = properties.getProperty("openid.cookie.security.secretKey");
        
        // optional
        String cookiePath = properties.getProperty("openid.cookie.path");
        String cookieDomain = properties.getProperty("openid.cookie.domain");
        String cookieMaxAge = properties.getProperty("openid.cookie.maxAge");
        String securityType = properties.getProperty("openid.cookie.security.type");
        
        String openIdParameter = properties.getProperty("openid.parameter");
        boolean encrypt = "encrypted".equals(securityType);
        
        if(cookieName==null)
            throw new IllegalStateException("openid.cookie.name must be set.");
        
        if(secretKey==null)
            throw new IllegalStateException("openid.cookie.security.secretKey must be set.");
        
        OpenIdUserManager manager = new OpenIdUserManager(cookieName, secretKey, encrypt);
        manager.setCookiePath(cookiePath);
        manager.setCookieDomain(cookieDomain);
        if(cookieMaxAge!=null)
            manager.setMaxAge(Integer.parseInt(cookieMaxAge));
        
        OpenIdContext context = new OpenIdContext();
        context.setAssociation(new DiffieHellmanAssociation());
        context.setDiscovery(new DefaultDiscovery());
        context.setHttpConnector(new SimpleHttpConnector());
        
        RelyingParty relyingParty = new RelyingParty();
        relyingParty._manager = manager;
        relyingParty._context = context;
        if(openIdParameter!=null)
            relyingParty._openIdParameter = openIdParameter;
        
        return relyingParty;
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
    
    public static UrlEncodedParameterMap getAuthUrlMap(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
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
        
        if(identity!=null)
        {
            map.put(Constants.OPENID_IDENTITY, identity);
            map.put(Constants.OPENID_CLAIMED_ID, identity);
        }
        else
        {
            map.put(Constants.OPENID_IDENTITY, user.getOpenIdServer());
            map.put(Constants.OPENID_CLAIMED_ID, user.getOpenIdServer());
        }
        
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
        
        if(identity!=null)
        {
            buffer.append('&').append(Constants.OPENID_IDENTITY).append('=').append(
                    UrlEncodedParameterMap.encode(identity));
            buffer.append('&').append(Constants.OPENID_CLAIMED_ID).append('=').append(
                    UrlEncodedParameterMap.encode(identity));
        }
        else
        {
            buffer.append('&').append(Constants.OPENID_IDENTITY).append('=').append(
                    UrlEncodedParameterMap.encode(user.getOpenIdServer()));
            buffer.append('&').append(Constants.OPENID_CLAIMED_ID).append('=').append(
                    UrlEncodedParameterMap.encode(user.getOpenIdServer()));
        }       
        
        return buffer;
    }
    
    public static String getAuthUrlString(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        return getAuthUrlBuffer(user, trustRoot, realm, returnTo).toString();
    }
    
    private OpenIdUserManager _manager;
    private OpenIdContext _context;
    private String _openIdParameter = DEFAULT_PARAMETER;
    
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
    
    public OpenIdUser discover(HttpServletRequest request) 
    throws Exception
    {
        OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.class.getName());
        if(user!=null)
            return user;

        user = _manager.getUser(request);
        if(user!=null)
            return user;
        
        String claimedId = request.getParameter(_openIdParameter);
        if(claimedId==null || !PREFIX.matcher(claimedId).find())
            return null;
        user = _context.getDiscovery().discover(claimedId, _context);
        if(user!=null)
            request.setAttribute(OpenIdUser.class.getName(), user);
        
        return user;
    }
    
    public boolean verifyAuth(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws Exception
    {
        boolean verified = _context.getAssociation().verifyAuth(user, getAuthParameters(request), 
                _context);
        if(!response.isCommitted())
            _manager.saveUser(user, response);
        
        return verified;
    }
    
    public boolean associate(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws Exception
    {
        if(_context.getAssociation().associate(user, _context))
        {
            if(!response.isCommitted())
                _manager.saveUser(user, response);
            return true;
        }        
        return false;
    }
    
    public boolean invalidate(HttpServletResponse response) throws IOException
    {
        return _manager.invalidate(response);
    }

}
