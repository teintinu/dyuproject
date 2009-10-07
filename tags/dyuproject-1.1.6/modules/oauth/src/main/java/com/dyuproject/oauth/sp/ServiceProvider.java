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
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.Constants;
import com.dyuproject.oauth.HttpAuthTransport;
import com.dyuproject.oauth.Signature;
import com.dyuproject.oauth.sp.ServiceToken.Store;
import com.dyuproject.util.Delim;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Service Provider helper class which manages the oauth service provider lifecycle.
 * 
 * @author David Yu
 * @created May 29, 2009
 */

public final class ServiceProvider
{
    
    public static final boolean DEFAULT_CHECK_TIMESTAMP = Boolean.getBoolean("serviceprovider.check_timestamp");
    
    public static int parseHeader(String auth, UrlEncodedParameterMap params)
    {
        int initSize = params.size();
        String[] pairs = Delim.COMMA.split(auth);
        String first = pairs[0].trim();
        int space = first.indexOf(' ');
        if(space==-1 || !"oauth".equalsIgnoreCase(first.substring(0, space)))
            return 400;
        
        int eqIdx = first.indexOf('=', space+1);
        if(eqIdx==-1)
            return 400;
        
        params.put(first.substring(space+1, eqIdx), Signature.decode(first.substring(eqIdx+2, 
                first.length()-1)));
        
        for(int i=1; i<pairs.length; i++)
        {
            String pair = pairs[i].trim();
            int eq = pair.indexOf('=', Constants.PREFIX.length());
            if(eq==-1)
                return 400;
            
            params.put(pair.substring(0, eq), Signature.decode(pair.substring(eq+2, 
                    pair.length()-1)));
        }
        
        int size = params.size()-initSize;
        return size>7 || size<Signature.REQUIRED_OAUTH_HEADERS_TO_SIGN.length ? 400 : 200;
    }
    
    @SuppressWarnings("unchecked")
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
            if(sig.verify(consumerSecret, tokenSecret, request.getMethod(), 
                    params.setUrl(request.getRequestURL().toString())))
            {
                return 200;
            }
        }
        catch(NullPointerException npe)
        {
            return 400;
        }

        return 401;
    }
    
    private final Store _store;
    private final boolean _checkTimestamp;

    public ServiceProvider(Store store)
    {
        this(store, DEFAULT_CHECK_TIMESTAMP);
    }
    
    public ServiceProvider(Store store, boolean checkTimestamp)
    {
        _store = store;
        _checkTimestamp = checkTimestamp;
    }
    
    public Store getStore()
    {
        return _store;
    }
    
    public boolean isCheckTimestamp()
    {
        return _checkTimestamp;
    }
    
    // authorization
    public String getAuthCallbackOrVerifier(String requestToken, String accessId)
    {
        return _store.getAuthCallbackOrVerifier(requestToken, accessId);
    }
    
    // for hybrid openid+oauth requests
    public ServiceToken newHybridRequestToken(String consumerKey, String id)
    {
        return _store.newHybridRequestToken(consumerKey, id);
    }
    
    // access validity
    public ServiceToken getAccessToken(HttpServletRequest request)
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
                params)==200 ? ast : null;
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
        ServiceToken st = _store.getRequestToken(consumerKey, requestToken);
        if(st==null)
        {
            response.setStatus(401);
            return false;
        }
        
        String verifier = params.get(Constants.OAUTH_VERIFIER);
        if(verifier==null && st.getId()==null)
        {
            response.setStatus(400);
            return false;
        }
        
        int status = 200;
        if((status=verifySignature(st.getConsumerSecret(), st.getSecret(), request, 
                params))==200)
        {
            ServiceToken ast = _store.newAccessToken(consumerKey, verifier, requestToken, st);
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
            
            if(_checkTimestamp)
            {
                try
                {
                    if(System.currentTimeMillis()/1000 < Long.parseLong(params.get(
                            Constants.OAUTH_TIMESTAMP)))
                    {
                        response.setStatus(401);
                        return false;
                    }
                }
                catch(Exception e)
                {
                    response.setStatus(401);
                    return false;
                }
            }
            
            String requestToken = params.get(Constants.OAUTH_TOKEN);
            return requestToken==null ? handleTokenRequest(params, consumerKey, request, response) :
                handleTokenExchange(params, consumerKey, requestToken, request, response);
        }
        
        response.setStatus(status);
        return false;
    }
    
    public boolean handleTokenRequest(HttpServletRequest request, HttpServletResponse response)
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
            
            if(_checkTimestamp)
            {
                try
                {
                    if(System.currentTimeMillis()/1000 < Long.parseLong(params.get(
                            Constants.OAUTH_TIMESTAMP)))
                    {
                        response.setStatus(401);
                        return false;
                    }
                }
                catch(Exception e)
                {
                    response.setStatus(401);
                    return false;
                }
            }
            
            return handleTokenRequest(params, consumerKey, request, response);
        }

        response.setStatus(status);
        return false;
    }
    
    public boolean handleTokenExchange(HttpServletRequest request, HttpServletResponse response)
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
            
            if(_checkTimestamp)
            {
                try
                {
                    if(System.currentTimeMillis()/1000 < Long.parseLong(params.get(
                            Constants.OAUTH_TIMESTAMP)))
                    {
                        response.setStatus(401);
                        return false;
                    }
                }
                catch(Exception e)
                {
                    response.setStatus(401);
                    return false;
                }
            }
            
            String requestToken = params.get(Constants.OAUTH_TOKEN);
            if(requestToken==null)
            {
                response.setStatus(400);
                return false;     
            }
            
            return handleTokenExchange(params, consumerKey, requestToken, request, response);                
        }
        
        response.setStatus(status);
        return false;
    }

}
