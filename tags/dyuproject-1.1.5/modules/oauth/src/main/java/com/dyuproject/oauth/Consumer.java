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

package com.dyuproject.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.manager.HttpSessionTokenManager;
import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.SimpleHttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.util.http.HttpConnector.Response;

/**
 * OAuth Consumer helper class which manages the oauth consumer lifecycle
 * 
 * @author David Yu
 * @created May 29, 2009
 */

public class Consumer
{
    public static final String DEFAULT_RESOURCE_PATH = "oauth_consumer.properties";
    
    private static Consumer __instance;
    
    public static Consumer getInstance()
    {
        if(__instance==null)
        {
            synchronized(Consumer.class)
            {
                if(__instance==null)
                    __instance = newInstance(DEFAULT_RESOURCE_PATH);
            }
        }
        return __instance;
    }
    
    public static Consumer newInstance(String resourceLoc)
    {        
        URL resource = ClassLoaderUtil.getResource(resourceLoc, Consumer.class);
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
    
    public static Consumer newInstance(URL resource) throws IOException
    {
        return newInstance(resource.openStream());
    }
    
    public static Consumer newInstance(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);
        return newInstance(props);
    }
    
    public static Consumer newInstance(Properties props) throws IOException
    {
        String httpConnectorParam = props.getProperty("oauth.consumer.httpconnector");
        HttpConnector httpConnector = httpConnectorParam==null ? SimpleHttpConnector.getDefault() : 
            (HttpConnector)newObjectInstance(httpConnectorParam);
        
        String ntsParam = props.getProperty("oauth.consumer.nonce_and_timestamp");
        NonceAndTimestamp nonceAndTimestamp = ntsParam==null ? SimpleNonceAndTimestamp.getDefault() : 
            (NonceAndTimestamp)newObjectInstance(ntsParam);

        ConsumerContext context = new ConsumerContext(httpConnector, nonceAndTimestamp);
        
        String domains = props.getProperty("oauth.consumer.endpoint.domains");
        if(domains==null)
            throw new IllegalStateException("oauth.consumer.endpoint.domains not found.");
        
        StringTokenizer tokenizer = new StringTokenizer(domains, ",;");
        while(tokenizer.hasMoreTokens())
            context.addEndpoint(Endpoint.load(props, tokenizer.nextToken().trim()));
        
        String tokenManagerParam = props.getProperty("oauth.consumer.token.manager");
        TokenManager tokenManager = tokenManagerParam==null ? HttpSessionTokenManager.getDefault() : 
            (TokenManager)newObjectInstance(tokenManagerParam);
            
        tokenManager.init(props);
        
        return new Consumer(context, tokenManager);
    }
    
    static Object newObjectInstance(String className)
    {        
        try
        {
            return ClassLoaderUtil.newInstance(className, Consumer.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private ConsumerContext _context;
    private TokenManager _manager;
    
    public Consumer()
    {
        
    }
    
    public Consumer(ConsumerContext context, TokenManager manager)
    {
        _context = context;
        _manager = manager;
    }
    
    public ConsumerContext getConsumerContext()
    {
        return _context;
    }
    
    public void setConsumerContext(ConsumerContext context)
    {
        _context = context;
    }
    
    public TokenManager getTokenManager()
    {
        return _manager;
    }
    
    public void setTokenManager(TokenManager manager)
    {
        _manager = manager;
    }
    
    public Endpoint getEndpoint(String domain)
    {
        return _context.getEndpoint(domain);
    }
    
    public void addEndpoint(Endpoint ep)
    {
        _context.addEndpoint(ep);
    }    
    
    public Token getToken(String consumerKey, HttpServletRequest request)
    throws IOException
    {
        Token token = (Token)request.getAttribute(consumerKey);
        if(token==null)
        {
            token = _manager.getToken(consumerKey, request);
            if(token==null)
                token = new Token(consumerKey);
            request.setAttribute(token.getCk(), token);
        }
        return token;
    }
    
    public boolean saveToken(Token token, HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        return _manager.saveToken(token, request, response);
    }
    
    public boolean invalidate(Token token, HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        return _manager.invalidate(token, request, response);
    }

    public Response fetchToken(Endpoint ep, Token token)
    throws IOException
    {
        return fetchToken(ep, new UrlEncodedParameterMap(), TokenExchange.getExchange(token), token);
    }
    
    public Response fetchToken(Endpoint ep, TokenExchange exchange, Token token)
    throws IOException
    {
        return fetchToken(ep, new UrlEncodedParameterMap(), exchange, token);
    }
    
    public Response fetchToken(Endpoint ep, UrlEncodedParameterMap params, TokenExchange exchange, 
            Token token) throws IOException
    {
        return ep.getTransport().send(params, ep, token, exchange, _context.getNonceAndTimestamp(), 
                ep.getSignature(), _context.getHttpConnector());
    }
    
    

}
