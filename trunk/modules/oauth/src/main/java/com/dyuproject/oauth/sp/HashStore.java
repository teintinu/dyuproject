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

import com.dyuproject.oauth.Constants;
import com.dyuproject.oauth.Signature;
import com.dyuproject.util.Cryptography;
import com.dyuproject.util.Delim;
import com.dyuproject.util.validate.IPDomainValidator;

/**
 * The hashed tokens are generated and parsed using encryption and mac signatures. 
 * 
 * @author David Yu
 * @created Jun 8, 2009
 */

public abstract class HashStore implements ServiceToken.Store
{
    
    /**
     * the default access timeout (60*60*1000 or the defined 
     * property "hashstore.access_timeout")
     */
    public static final long DEFAULT_ACCESS_TIMEOUT = 
        Long.getLong("hashstore.access_timeout", 60*60*1000); // defaults to 1 hr  
    
    /**
     * the default exchange timeout (60*10*1000 or the defined 
     * property "hashstore.exchange_timeout")
     */
    public static final long DEFAULT_EXCHANGE_TIMEOUT = 
        Long.getLong("hashstore.exchange_timeout", 60*10*1000); // defaults to 10 mins
    
    /**
     * the default login timeout ({@link #DEFAULT_EXCHANGE_TIMEOUT}/2 or 
     * the defined property "hashstore.login_timeout")
     */
    public static final long DEFAULT_LOGIN_TIMEOUT = 
        Long.getLong("hashstore.login_timeout", DEFAULT_EXCHANGE_TIMEOUT/2);
    
    /**
     * the default mac algorithm ("HMACSHA1")
     */
    public static final String DEFAULT_MAC_ALGORITHM = "HMACSHA1";
    
    static final String CHECKED_PREFIX = "http";
    static final String ASSIGNED_PREFIX = "http://";  
    
    private final Cryptography _crypto;
    private final String _secretKey;
    private final String _macSecretKey;
    private final String _macAlgorithm;
    
    private final long _accessTimeout;
    private final long _exchangeTimeout;
    private final long _loginTimeout;
    
    public HashStore(String secretKey, String macSecretKey)
    {
        this(secretKey, macSecretKey, DEFAULT_MAC_ALGORITHM, DEFAULT_ACCESS_TIMEOUT, 
                DEFAULT_EXCHANGE_TIMEOUT, DEFAULT_LOGIN_TIMEOUT);
    }
    
    public HashStore(String secretKey, String macSecretKey, String macAlgorithm, 
            long accessTimeout, long exchangeTimeout, long loginTimeout)
    {
        _secretKey = secretKey;
        _macSecretKey = macSecretKey;
        _macAlgorithm = macAlgorithm;
        _accessTimeout = accessTimeout;
        _exchangeTimeout = exchangeTimeout;
        _loginTimeout = loginTimeout;
        try
        {
            _crypto = Cryptography.create(_secretKey, '.');
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public final ServiceToken newRequestToken(String consumerKey, String callback)
    {   
        if(!Constants.OOB.equals(callback) && (callback=validateCallbackUrl(callback))==null)
            return null;
        
        String consumerSecret = getConsumerSecret(consumerKey);
        if(consumerSecret==null)
            return null;

        try
        {
            StringBuilder buffer = new StringBuilder()
                .append(System.currentTimeMillis())
                .append('&')
                .append(consumerKey)
                .append('&')
                .append(callback);
            String base = buffer.toString();
            String requestToken = _crypto.encryptEncode(base);
            return new SimpleServiceToken(consumerSecret, requestToken, 
                    Signature.getMacSignature(_macSecretKey, base, _macAlgorithm));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Gets the request token with secret to be verified by the caller; 
     * Returns null if the request token is invalid.
     * If the request token was obtained from {@link #newHybridRequestToken(String, String)}, 
     * the token will be identified and a different request token will be returned that 
     * is not associated with a secret key since we are relying on the security of the 
     * openid protocol.
     */
    public final ServiceToken getRequestToken(String consumerKey, String requestToken)
    {
        String consumerSecret = getConsumerSecret(consumerKey);
        if(consumerSecret==null)
            return null;
        
        try
        {
            String base = _crypto.decryptDecode(requestToken);
            String[] tokens = Delim.AMPER.split(base);
            if(tokens.length!=3)
            {
                if(tokens.length==1)
                {
                    // generated
                    // allow for oauth+openid hybrid
                    if((System.currentTimeMillis()-Long.parseLong(base.substring(0, 13))) > _exchangeTimeout)
                        return null;
                    
                    return new SimpleServiceToken(consumerSecret, requestToken, null, base.substring(13));
                }
                return null;
            }

            if((System.currentTimeMillis() - Long.parseLong(tokens[0])) > _exchangeTimeout)
                return null;
            
            if(!consumerKey.equals(tokens[1]))
                return null;

            return new SimpleServiceToken(consumerSecret, requestToken, 
                    Signature.getMacSignature(_macSecretKey, base, _macAlgorithm));
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public final String getAuthCallbackOrVerifier(String requestToken, String id)
    {
        try
        {
            String[] tokens = Delim.AMPER.split(_crypto.decryptDecode(requestToken));
            if(tokens.length!=3)
                return null;

            if((System.currentTimeMillis() - Long.parseLong(tokens[0])) > _loginTimeout)
                return null;
            
            //String consumerKey = base[1];
            String verifier = _crypto.encryptEncode(requestToken + id);
            
            String callback = tokens[2];
            // check if oob .. return verifier
            if(callback.length()==3)
                return verifier;
            
            
            char separator = callback.indexOf('?')==-1 ? '?' : '&';
            return new StringBuilder()
                .append(callback)
                .append(separator)
                .append(Constants.OAUTH_TOKEN)
                .append('=')
                .append(Signature.encode(requestToken))
                .append('&')
                .append(Constants.OAUTH_VERIFIER)
                .append('=')
                .append(Signature.encode(verifier))
                .toString();
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public final ServiceToken newHybridRequestToken(String consumerKey, String id)
    {
        String consumerSecret = getConsumerSecret(consumerKey);
        return consumerSecret==null ? null : generateToken(consumerKey, consumerSecret, 
                id);
    }
    
    public final ServiceToken generateToken(String consumerKey, String consumerSecret, 
            String id)
    {
        try
        {
            String base = System.currentTimeMillis() + id;
            String keyToken = _crypto.encryptEncode(base);
            return new SimpleServiceToken(consumerSecret, keyToken, 
                    Signature.getMacSignature(_macSecretKey, base, _macAlgorithm));
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public final ServiceToken newAccessToken(String consumerKey, String verifier, String requestToken)
    {
        try
        {
            String vBase = _crypto.decryptDecode(verifier);
            if(!vBase.startsWith(requestToken))
                return null;
            
            String consumerSecret = getConsumerSecret(consumerKey);
            if(consumerSecret==null)
                return null;
            
            String id = vBase.substring(requestToken.length());            
            return generateToken(consumerKey, consumerSecret, id);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public final ServiceToken newAccessToken(String consumerKey, String verifier, String requestToken, 
            ServiceToken verifiedRequestToken)
    {
        try
        {
            if(verifiedRequestToken.getId()!=null)
            {
                return generateToken(consumerKey, verifiedRequestToken.getConsumerSecret(), 
                        verifiedRequestToken.getId());
            }
            
            String vBase = _crypto.decryptDecode(verifier);
            if(!vBase.startsWith(requestToken))
                return null;
            
            String id = vBase.substring(requestToken.length());            
            return generateToken(consumerKey, verifiedRequestToken.getConsumerSecret(), id);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public final ServiceToken getAccessToken(String consumerKey, String accessToken)
    {
        try
        {
            String base = _crypto.decryptDecode(accessToken);
            if((System.currentTimeMillis()-Long.parseLong(base.substring(0, 13))) > _accessTimeout)
                return null;
            
            String consumerSecret = getConsumerSecret(consumerKey);
            if(consumerSecret==null)
                return null;
            
            return new SimpleServiceToken(consumerSecret, accessToken, 
                    Signature.getMacSignature(_macSecretKey, base, _macAlgorithm), 
                    base.substring(13));
        }
        catch(Exception e)
        {
            return null;
        }
    }

    protected abstract String getConsumerSecret(String consumerKey);
    
    protected String validateCallbackUrl(String callbackUrl)
    {
        return callbackUrl.startsWith(CHECKED_PREFIX) ? callbackUrl : null;
    }

    /**
     * Returns null if the given {@code url} is invalid;  This is a utility method.
     */
    public static String validateUrl(String url)
    {
        int start = 0, end = 0, len = url.length();
        boolean addPrefix = true;
        if(url.startsWith(CHECKED_PREFIX))
        {
            if(len<11)
                return null;
            
            char c = url.charAt(4);
            if(c=='s')
                start = 8;
            else if(c==':')
                start = 7;
            else
                return null;
            
            addPrefix = false;
        }        
        
        boolean appendSlash = false;
        int lastSlash = url.indexOf('/', start);        
        if(lastSlash==-1)
        {            
            appendSlash = true;
            end = len;
        }
        else
            end = lastSlash;
        
        int colon = url.indexOf(':', start);
        if(colon!=-1)
        {
            // port validation
            int portsLen = end-colon-1;
            if(portsLen<1 && portsLen>5)
                return null;
            
            char[] ch = new char[portsLen];
            url.getChars(colon+1, end, ch, 0);
            if(!isDigit(ch, 0, portsLen))
                return null;
            
            end = colon;
        }
        
        // must be at least 4 characters long (e.g. 'a.ph')
        int domainLen = end-start;
        if(domainLen<4)
            return null;

        char[] domain = new char[domainLen];
        url.getChars(start, end, domain, 0);        
        if(!IPDomainValidator.isValid(domain))
            return null;
        
        if(addPrefix)
            return appendSlash ? ASSIGNED_PREFIX + url + '/' : ASSIGNED_PREFIX + url;
        
        return appendSlash ? url + '/' : url;
    }
    
    static boolean isDigit(char[] ch, int start, int len)
    {
        for(int i=start; i<len; i++)
        {
            if(!Character.isDigit(ch[i]))
                return false;
        }
        return true;
    }

}
