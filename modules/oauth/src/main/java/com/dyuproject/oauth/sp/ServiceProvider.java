//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.oauth.sp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.Constants;
import com.dyuproject.oauth.HttpAuthTransport;
import com.dyuproject.oauth.Signature;
import com.dyuproject.oauth.sp.ServiceToken.Store;
import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.Delim;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Service Provider helper class which manages the oauth service provider lifecycle.
 * 
 * @author David Yu
 * @created May 29, 2009
 */

public class ServiceProvider
{
    
    public static final String DEFAULT_RESOURCE_PATH = "oauth_serviceprovider.properties";
    
    private static ServiceProvider __instance;
    
    public static ServiceProvider getInstance()
    {
        if(__instance==null)
        {
            synchronized(ServiceProvider.class)
            {
                if(__instance==null)
                    __instance = newInstance(DEFAULT_RESOURCE_PATH);
            }
        }
        return __instance;
    }
    
    public static ServiceProvider newInstance(String resourceLoc)
    {        
        URL resource = ClassLoaderUtil.getResource(resourceLoc, ServiceProvider.class);
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
    
    public static ServiceProvider newInstance(URL resource) throws IOException
    {
        return newInstance(resource.openStream());
    }
    
    public static ServiceProvider newInstance(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);
        return newInstance(props);
    }
    
    public static ServiceProvider newInstance(Properties props) throws IOException
    {
        //TODO
        
        return null;
    }
    
    static Object newObjectInstance(String className)
    {        
        try
        {
            return ClassLoaderUtil.newInstance(className, ServiceProvider.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static int parseHeader(String auth, UrlEncodedParameterMap params)
    {
        int initSize = params.size();
        String[] pairs = Delim.COMMA.split(auth);
        String first = pairs[0];
        int space = first.indexOf(' ');
        if(space==-1 || !"oauth".equalsIgnoreCase(first.substring(0, space)))
            return 400;
        
        int eqIdx = first.indexOf('=', space+1+Constants.PREFIX.length());
        if(eqIdx==-1)
            return 400;
        
        params.put(first.substring(space+1, eqIdx), Signature.decode(first.substring(eqIdx+2, first.length()-eqIdx-2)));
        
        for(int i=1; i<pairs.length; i++)
        {
            String pair = pairs[i];
            int eq = pair.indexOf('=', Constants.PREFIX.length());
            if(eq==-1)
                return 400;
            
            params.put(pair.substring(0, eq), Signature.decode(pair.substring(eq+2, 
                    pair.length()-eq-2)));
        }
        
        int size = params.size()-initSize;
        return size>7 || size<Signature.REQUIRED_HEADER_TO_SIGN.length ? 400 : 200;
    }
    
    public static int parse(HttpServletRequest request, UrlEncodedParameterMap params)
    {
        int status = 200;
        String auth = request.getHeader(HttpAuthTransport.NAME);
        if(auth!=null && (status=parseHeader(auth, params))!=200)
            return status;
        
        int size = params.size();
        Enumeration<String> names = (Enumeration<String>)request.getParameterNames();
        while(names.hasMoreElements())
        {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
            size++;
        }
        
        // check if the oauth_parameters are overriden
        if(size!=params.size())
            return 400;
        
        return status;
    }
    
    public static int verifySignature(String consumerSecret, String tokenSecret, 
            HttpServletRequest request, UrlEncodedParameterMap params)
    {        
        Signature sig = Signature.get(params.get(Constants.OAUTH_SIGNATURE_METHOD));
        if(sig==null)
            return 400;
        
        try
        {
            if(!sig.verify(consumerSecret, tokenSecret, request.getMethod(), 
                    params.setUrl(request.getRequestURL().toString())))
            {
                return 401;
            }
        }
        catch(NullPointerException npe)
        {
            return 400;
        }
        
        return 200;
    }
    
    private Store _store;
    
    public ServiceProvider()
    {
        
    }
    
    public ServiceProvider(Store store)
    {
        _store = store;
    }
    
    public Store getStore()
    {
        return _store;
    }
    
    // authorization
    public String getAuthCallbackOrVerifier(String requestToken, String accessId)
    {
        return _store.getAuthCallbackOrVerifier(requestToken, accessId);
    }
    
    // access validity
    public UrlEncodedParameterMap getAccessParams(HttpServletRequest request)
    {
        UrlEncodedParameterMap params = new UrlEncodedParameterMap();
        if(parse(request, params)!=200)
            return null;
        
        String consumerKey = params.get(Constants.OAUTH_CONSUMER_KEY);
        if(consumerKey==null)
            return null;
        
        String accessToken = params.get(Constants.OAUTH_TOKEN);
        if(accessToken==null)
            return null;
        
        ServiceToken ast = _store.getAccessToken(consumerKey, accessToken);            
        return ast!=null && verifySignature(ast.getConsumerSecret(), ast.getSecret(), request, 
                params)==200 ? params : null;
    }
    
    boolean handleTokenRequest(UrlEncodedParameterMap params, String consumerKey, 
            HttpServletRequest request, HttpServletResponse response) throws IOException
    {        
        String callback = params.get(Constants.OAUTH_CALLBACK);
        if(callback==null)
        {
            response.setStatus(400);
            return false;
        }
        ServiceToken st = _store.newRequestToken(consumerKey, callback);
        if(st==null)
        {
            response.setStatus(401);
            return false;
        }
        
        int status = 200;
        if((status=verifySignature(st.getConsumerSecret(), null, request, 
                params))==200)
        {                    
            response.getWriter()
                .append(Constants.OAUTH_TOKEN)
                .append('=')
                .append(Signature.encode(st.getKey()))
                .append('&')
                .append(Constants.OAUTH_TOKEN_SECRET)
                .append('=')
                .append(Signature.encode(st.getSecret()))
                .append('&')
                .append(Constants.OAUTH_CALLBACK_CONFIRMED)
                .append('=')
                .append("true");
            
            return true;
        }
        
        response.setStatus(status);
        return false;
    }    
    
    boolean handleTokenExchange(UrlEncodedParameterMap params, String consumerKey, 
            String requestToken, HttpServletRequest request, HttpServletResponse response) 
            throws IOException
    {        
        String verifier = params.get(Constants.OAUTH_VERIFIER);
        if(verifier==null)
        {
            response.setStatus(400);
            return false;
        }
        
        ServiceToken st = _store.getRequestToken(consumerKey, requestToken);
        if(st==null)
        {
            response.setStatus(401);
            return false;
        }
        
        int status = 200;
        if((status=verifySignature(st.getConsumerSecret(), st.getSecret(), request, 
                params))==200)
        {
            ServiceToken ast = _store.newAccessToken(consumerKey, verifier, requestToken);
            if(ast==null)
            {
                response.setStatus(401);
                return false;
            }
            
            Writer writer = response.getWriter();
            writer.append(Constants.OAUTH_TOKEN)
                .append('=')
                .append(Signature.encode(ast.getKey()))
                .append('&')
                .append(Constants.OAUTH_TOKEN_SECRET)
                .append('=')
                .append(Signature.encode(ast.getSecret()));
            
            return true;
        }
        
        response.setStatus(status);
        return false;
    }
    
    public boolean handle(HttpServletRequest request, HttpServletResponse response)
    throws IOException
    {
        UrlEncodedParameterMap params = new UrlEncodedParameterMap();
        int status = parse(request, params);
        if(status==200)
        {
            String consumerKey = params.get(Constants.OAUTH_CONSUMER_KEY);
            if(consumerKey==null)
            {
                response.setStatus(400);
                return false;                
            }
            
            String requestToken = params.get(Constants.OAUTH_TOKEN);
            return requestToken==null ? handleTokenRequest(params, consumerKey, request, response) :
                handleTokenExchange(params, consumerKey, requestToken, request, response);
        }
        
        response.setStatus(status);
        return false;
    }

}
