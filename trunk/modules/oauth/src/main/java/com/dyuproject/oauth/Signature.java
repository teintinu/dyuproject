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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.dyuproject.util.B64Code;
import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.Delim;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Signature - for signing and verification of oauth requests
 * 
 * @author David Yu
 * @created May 30, 2009
 */

public abstract class Signature
{
    
    public static final String[] REQUIRED_OAUTH_HEADERS_TO_SIGN = new String[]{
        Constants.OAUTH_CONSUMER_KEY,
        Constants.OAUTH_NONCE,
        Constants.OAUTH_TIMESTAMP,
        Constants.OAUTH_SIGNATURE_METHOD
    };
    
    static final String ENCODED_EQ = "%3D";
    static final String ENCODED_AMP = "%26";
    
    private static final Map<String,Signature> __defaults = new HashMap<String,Signature>(5);
    
    public static void register(Signature sig)
    {
        __defaults.put(sig.getMethod(), sig);
    }
    
    public static Signature get(String method)
    {
        return __defaults.get(method);
    }
    
    public static String encode(String value)
    {
        return UrlEncodedParameterMap.encodeRFC3986(value, Constants.ENCODING.name());
    }
    
    public static String decode(String value)
    {
        return UrlEncodedParameterMap.decode(value, Constants.ENCODING.name());
    }
    
    public static String getKey(String consumerSecret, String secret)
    {   
        StringBuilder buffer = new StringBuilder()
            .append(encode(consumerSecret))
            .append('&');
        
        return secret==null ? buffer.toString() : buffer.append(encode(secret)).toString();
    }
    
    public static String getBase(UrlEncodedParameterMap params, String method)
    {
        List<String> base = new ArrayList<String>();
        
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            String key = entry.getKey();
            String value = encode(entry.getValue());
            base.add(new StringBuilder()
                .append(key)
                .append(ENCODED_EQ)
                .append(encode(value))
                .toString());
        }
        
        Collections.sort(base);
        StringBuilder buffer = new StringBuilder()
            .append(method)
            .append('&')
            .append(encode(params.getUrl()))
            .append('&');
        
        for(String b : base)
            buffer.append(b).append(ENCODED_AMP);
        
        return buffer.substring(0, buffer.length()-ENCODED_AMP.length());
    }
    
    public static String getMacSignature(String secretKey, String base, String algorithm)
    {
        try
        {
            SecretKeySpec sks = new SecretKeySpec(secretKey.getBytes(Constants.ENCODING), algorithm);
            Mac m = Mac.getInstance(sks.getAlgorithm());
            m.init(sks);
            return new String(B64Code.encode(m.doFinal(base.getBytes(Constants.ENCODING))));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }            
    }
    
    public abstract String getMethod();
    
    public int hashCode()
    {
        return getMethod().hashCode();
    }
    
    protected final String getBaseAndPutDefaults(UrlEncodedParameterMap params, String httpMethod, 
            Listener listener, StringBuilder oauthBuffer, StringBuilder requestBuffer)
    {        
        String url = params.getUrl();
        int idx = url.indexOf('?');
        if(idx!=-1)
        {
            String[] pairs = Delim.AMPER.split(url.substring(idx+1));
            for(String pair : pairs)
            {
                int eq = pair.indexOf('=');
                if(eq==-1)
                    params.put(pair, "");
                else
                {
                    params.put(pair.substring(0, eq), UrlEncodedParameterMap.decode(
                            pair.substring(eq+1), Constants.ENCODING.name()));
                }
            }
            params.setUrl(url.substring(0, idx));
        }
        
        params.put(Constants.OAUTH_SIGNATURE_METHOD, getMethod());
        
        List<String> base = new ArrayList<String>();
        
        if(oauthBuffer!=null)
        {
            // oauth_token parameter
            String versionValue = params.remove(Constants.OAUTH_VERSION);
            if(versionValue!=null)
            {
                versionValue = encode(versionValue);
                listener.handleOAuthParameter(Constants.OAUTH_VERSION, versionValue, oauthBuffer);
                base.add(new StringBuilder()
                    .append(Constants.OAUTH_VERSION)
                    .append(ENCODED_EQ)
                    .append(encode(versionValue))
                    .toString());
            }
            
            // oauth_token parameter
            String tokenValue = params.remove(Constants.OAUTH_TOKEN);
            if(tokenValue!=null)
            {
                tokenValue = encode(tokenValue);
                listener.handleOAuthParameter(Constants.OAUTH_TOKEN, tokenValue, oauthBuffer);
                base.add(new StringBuilder()
                    .append(Constants.OAUTH_TOKEN)
                    .append(ENCODED_EQ)
                    .append(encode(tokenValue))
                    .toString());
            }
            
            // default oauth parameters
            for(String key : REQUIRED_OAUTH_HEADERS_TO_SIGN)
            {
                String value = encode(params.remove(key));
                listener.handleOAuthParameter(key, value, oauthBuffer);
                base.add(new StringBuilder()
                    .append(key)
                    .append(ENCODED_EQ)
                    .append(encode(value))
                    .toString());
            }
        }
        
        // request parameters
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            String key = entry.getKey();
            String value = encode(entry.getValue());
            listener.handleRequestParameter(key, value, requestBuffer);
            base.add(new StringBuilder()
                .append(key)
                .append(ENCODED_EQ)
                .append(encode(value))
                .toString());
        }
        
        Collections.sort(base);
        StringBuilder buffer = new StringBuilder()
            .append(httpMethod)
            .append('&')
            .append(encode(params.getUrl()))
            .append('&');
        
        for(String b : base)
            buffer.append(b).append(ENCODED_AMP);
        
        return buffer.substring(0, buffer.length()-ENCODED_AMP.length());
    }
    
    public abstract String sign(String consumerSecret, String tokenSecret, String base);
    
    public abstract boolean verify(String consumerSecret, String tokenSecret, String method, 
            UrlEncodedParameterMap params);
    
    public abstract void generate(UrlEncodedParameterMap params, String consumerSecret, Token token,
            String httpMethod, Listener listener, StringBuilder oauthBuffer, 
            StringBuilder requestBuffer);
    
    
    public static final Signature PLAINTEXT = new Signature()
    {
        
        public final String getMethod()
        {
            return "PLAINTEXT";
        }
        
        public final String sign(String consumerSecret, String tokenSecret, String base)
        {
            return getKey(consumerSecret, tokenSecret);
        }

        public final boolean verify(String consumerSecret, String tokenSecret, String method, 
                UrlEncodedParameterMap params)
        {
            String sig = params.get(Constants.OAUTH_SIGNATURE);
            if(sig==null)
                return false;
            
            int idx = sig.indexOf('&');
            if(idx==-1)
                return false;
            
            return consumerSecret.equals(sig.substring(0, idx)) && (tokenSecret==null ||
                    tokenSecret.equals(sig.substring(idx+1)));     
        }
        
        public final void generate(UrlEncodedParameterMap params, String consumerSecret, Token token,
                String httpMethod, Listener listener, StringBuilder oauthBuffer, 
                StringBuilder requestBuffer)
        {
            getBaseAndPutDefaults(params, httpMethod, listener, oauthBuffer, requestBuffer);
            String sig = sign(consumerSecret, token.getSecret(), null);
            if(oauthBuffer==null)
                listener.handleRequestParameter(Constants.OAUTH_SIGNATURE, encode(sig), requestBuffer);
            else
                listener.handleOAuthParameter(Constants.OAUTH_SIGNATURE, encode(sig), oauthBuffer);
        }
        
    };
    
    public static final Signature HMACSHA1 = new Signature()
    {

        public final String getMethod()
        {
            return "HMAC-SHA1";
        }
        
        public final String sign(String consumerSecret, String tokenSecret, String base)
        {
            return getMacSignature(getKey(consumerSecret, tokenSecret), base, "HMACSHA1");
        }

        public final boolean verify(String consumerSecret, String tokenSecret, String method, 
                UrlEncodedParameterMap params)
        {            
            String sig = params.remove(Constants.OAUTH_SIGNATURE);        
            return sig!=null && sig.equals(sign(consumerSecret, tokenSecret, getBase(params, method)));
        }
        
        public final void generate(UrlEncodedParameterMap params, String consumerSecret, Token token,
                String httpMethod, Listener listener, StringBuilder oauthBuffer, 
                StringBuilder requestBuffer)
        {
            String base = getBaseAndPutDefaults(params, httpMethod, listener, oauthBuffer, requestBuffer);
            String sig = sign(consumerSecret, token.getSecret(), base);
            if(oauthBuffer==null)
                listener.handleRequestParameter(Constants.OAUTH_SIGNATURE, encode(sig), requestBuffer);
            else
                listener.handleOAuthParameter(Constants.OAUTH_SIGNATURE, encode(sig), oauthBuffer);
        }
        
    };
    
    public interface Listener
    {
        void handleOAuthParameter(String key, String value, StringBuilder oauthBuffer);
        void handleRequestParameter(String key, String value, StringBuilder requestBuffer);
    }
    
    static
    {
        register(PLAINTEXT);
        register(HMACSHA1);
        String ext = System.getProperty("oauth.signatures.ext");
        if(ext!=null)
        {
            try
            {
                StringTokenizer tokenizer = new StringTokenizer(ext, ",;");
                while(tokenizer.hasMoreTokens())
                {
                    register((Signature)ClassLoaderUtil.newInstance(tokenizer.nextToken().trim(), 
                            Signature.class));
                }
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

}
