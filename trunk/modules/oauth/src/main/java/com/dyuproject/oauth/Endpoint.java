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
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import com.dyuproject.util.ClassLoaderUtil;

/**
 * Endpoint which contains all the information for the target webservice.
 * 
 * @author David Yu
 * @created Jun 2, 2009
 */

@SuppressWarnings("serial")
public final class Endpoint implements Serializable
{
    
    public static Endpoint load(String resource) throws IOException
    {
        return load(ClassLoaderUtil.getResource(resource, Endpoint.class));
    }
    
    public static Endpoint load(URL resource) throws IOException
    {
        return load(resource.openStream());
    }
    
    public static Endpoint load(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);
        return load(props);
    }
    
    public static Endpoint load(Properties props)
    {
        String domain = props.getProperty("domain");
        if(domain==null)
            throw new IllegalStateException("domain not found.");
        
        String consumerKey = props.getProperty("consumer_key");
        if(consumerKey==null)
            throw new IllegalStateException("consumer_key not found.");
        
        String consumerSecret = props.getProperty("consumer_secret");
        if(consumerSecret==null)
            throw new IllegalStateException("consumer_secret not found.");
        
        String requestTokenUrl = props.getProperty("request_token_url");
        if(requestTokenUrl==null)
            throw new IllegalStateException("request_token_url not found.");
        
        String authorizationUrl = props.getProperty("authorization_url");
        if(authorizationUrl==null)
            throw new IllegalStateException("authorization_url not found.");
        
        String accessTokenUrl = props.getProperty("access_token_url");
        if(accessTokenUrl==null)
            throw new IllegalStateException("access_token_url not found.");

        // optional
        String signatureMethod = props.getProperty("signature_method");
        String transportName = props.getProperty("transport_name");        
        boolean secure = "true".equals(props.getProperty("secure"));
        
        return new Endpoint(consumerKey, consumerSecret, secure, domain, requestTokenUrl, 
                authorizationUrl, accessTokenUrl, signatureMethod, transportName);
    }
    
    public static Endpoint load(Properties props, String domain)
    {
        String consumerKey = props.getProperty(domain + ".consumer_key");
        if(consumerKey==null)
            throw new IllegalStateException(domain + ".consumer_key not found.");
        
        String consumerSecret = props.getProperty(domain+".consumer_secret");
        if(consumerSecret==null)
            throw new IllegalStateException(domain + ".consumer_secret not found.");
        
        String requestTokenUrl = props.getProperty(domain+".request_token_url");
        if(requestTokenUrl==null)
            throw new IllegalStateException(domain + ".request_token_url not found.");
        
        String authorizationUrl = props.getProperty(domain+".authorization_url");
        if(authorizationUrl==null)
            throw new IllegalStateException(domain + ".authorization_url not found.");
        
        String accessTokenUrl = props.getProperty(domain+".access_token_url");
        if(accessTokenUrl==null)
            throw new IllegalStateException(domain + ".access_token_url not found.");

        // optional
        String signatureMethod = props.getProperty(domain+".signature_method");
        String transportName = props.getProperty(domain+".transport_name");        
        boolean secure = "true".equals(props.getProperty(domain + ".secure"));
        
        return new Endpoint(consumerKey, consumerSecret, secure, domain, requestTokenUrl, 
                authorizationUrl, accessTokenUrl, signatureMethod, transportName);
    }

    private final boolean _secure;
    private final String _consumerKey;
    private final String _consumerSecret;
    private final String _domain;
    private final String _root;
    private final String _requestTokenUrl;
    private final String _authorizationUrl;
    private final String _accessTokenUrl;
    private final Signature _signature;
    private final Transport _transport;
    
    public Endpoint(String consumerKey, String consumerSecret, boolean secure, String domain,
            String requestTokenUrl, String authorizationUrl, String accessTokenUrl, 
            String sigMethod, String transportName)
    {
        this(consumerKey, consumerSecret, secure, domain, requestTokenUrl, authorizationUrl, 
                accessTokenUrl, Signature.get(sigMethod), Transport.get(transportName));
    }
    
    public Endpoint(String consumerKey, String consumerSecret, boolean secure, String domain,
            String requestTokenUrl, String authorizeUrl, String accessTokenUrl, 
            Signature signature, Transport transport)
    {
        _consumerKey = consumerKey;
        
        _consumerSecret = consumerSecret;
        
        _secure = secure;
        
        _domain = domain;
        
        _root = secure ? "https://" + _domain : "http://" + _domain;
        
        _requestTokenUrl = requestTokenUrl.charAt(0)=='/' ? _root + requestTokenUrl : 
                requestTokenUrl;
        
        _authorizationUrl = authorizeUrl.charAt(0)=='/' ? _root + authorizeUrl : 
                authorizeUrl;
        
        _accessTokenUrl = accessTokenUrl.charAt(0)=='/' ? _root + accessTokenUrl : 
                accessTokenUrl;
        
        _signature = signature==null ? (_secure ? Signature.PLAINTEXT : Signature.HMACSHA1) : 
                signature;
        
        _transport = transport==null ? HttpAuthTransport.DEFAULT : transport;
    }
    
    public String getConsumerKey()
    {
        return _consumerKey;
    }
    
    public String getConsumerSecret()
    {
        return _consumerSecret;
    }
    
    public String getDomain()
    {
        return _domain;
    }
    
    public String getRoot()
    {
        return _root;
    }
    
    public boolean isSecure()
    {
        return _secure;
    }
    
    public String getRequestTokenUrl()
    {
        return _requestTokenUrl;
    }
    
    public String getAuthorizationUrl()
    {
        return _authorizationUrl;
    }
    
    public String getAccessTokenUrl()
    {
        return _accessTokenUrl;
    }
    
    public Signature getSignature()
    {
        return _signature;
    }
    
    public Transport getTransport()
    {
        return _transport;
    }
    
}
