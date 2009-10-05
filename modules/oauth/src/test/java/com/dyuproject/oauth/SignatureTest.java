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

import junit.framework.TestCase;

import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * @author David Yu
 * @created Jun 16, 2009
 */

public class SignatureTest extends TestCase
{
    
    static Endpoint newEndpoint(String key, String secret)
    {
        return new Endpoint(key, secret, false, "localhost", "/request_token", 
                "/authorize_token", "/access_token", "HMAC-SHA1", "Authorization");
    }
    
    static String sign(Signature signature, UrlEncodedParameterMap params, Endpoint ep, Token token)
    {
        
        TokenExchange.REQUEST_TOKEN.put(params, ep, token);
        SimpleNonceAndTimestamp.DEFAULT.put(params, ep.getConsumerKey());
        params.put(Constants.OAUTH_SIGNATURE_METHOD, signature.getMethod());
        return signature.sign(ep.getConsumerSecret(), token.getSecret(), 
                Signature.getBase(params, ep.getTransport().getMethod()));
    }
    
    public void testHMACSHA1()
    {        
        Endpoint ep = newEndpoint("foo", "foo_secret");
        Token token = new Token(ep.getConsumerKey());
        
        UrlEncodedParameterMap params = new UrlEncodedParameterMap()
            .add(Constants.OAUTH_CALLBACK, "http://localhost:8080/callback");
        
        Signature signature = Signature.HMACSHA1;
        
        String sig = sign(signature, params, ep, token);
        
        System.err.println(sig);
        
        params.put(Constants.OAUTH_SIGNATURE, sig);
        
        boolean verified = signature.verify(ep.getConsumerSecret(), token.getSecret(), 
                ep.getTransport().getMethod(), params);
        
        assert(verified);
    }
    
    public void testPLAINTEXT()
    {        
        Endpoint ep = newEndpoint("foo", "foo_secret");
        Token token = new Token(ep.getConsumerKey());
        
        UrlEncodedParameterMap params = new UrlEncodedParameterMap()
            .add(Constants.OAUTH_CALLBACK, "http://localhost:8080/callback");
        
        Signature signature = Signature.PLAINTEXT;
        
        String sig = sign(signature, params, ep, token);
        
        System.err.println(sig);
        
        params.put(Constants.OAUTH_SIGNATURE, sig);
        
        boolean verified = signature.verify(ep.getConsumerSecret(), token.getSecret(), 
                ep.getTransport().getMethod(), params);
        
        assert(verified);
    }

}
