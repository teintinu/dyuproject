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

package com.dyuproject.oauth.manager;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON.StringSource;

import com.dyuproject.json.StandardJSON;
import com.dyuproject.oauth.Token;
import com.dyuproject.oauth.TokenManager;
import com.dyuproject.util.Cryptography;

/**
 * CookieBasedTokenManager - stores the token in the cookie encrypted.
 * 
 * @author David Yu
 * @created May 30, 2009
 */

public class CookieBasedTokenManager implements TokenManager
{
    private boolean _initialized = false;
    
    private StandardJSON _json = new StandardJSON();
    
    private String _secretKey;
    private String _cookieDomain;
    private String _cookiePath;
    
    private Cryptography _crypto;

    /**
     * 10 minutes for the authenticated token's session to expire
     */
    private int _maxAge = 600; 
    /**
     * 1 minute for the discovered token to finish authentication on his oauth provider
     */
    private int _loginTimeout = 60;
    
    
    public CookieBasedTokenManager()
    {
        
    }
    
    public CookieBasedTokenManager(String secretKey)
    {
        setSecretKey(secretKey);        
    }
    
    public CookieBasedTokenManager(String secretKey, String cookiePath, String cookieDomain, int loginTimeout, int maxAge)
    {
        setSecretKey(secretKey);
        setCookiePath(cookiePath==null ? "/" : cookiePath);
        setCookieDomain(cookieDomain);
        setLoginTimeout(loginTimeout);
        _loginTimeout = loginTimeout;
        _maxAge = maxAge;
        init();
        _initialized = true;
    }
    
    public StandardJSON getJSON()
    {
        return _json;
    }
    
    public void init(Properties properties)
    {
        if(_initialized)
            return;
        
        _secretKey = properties.getProperty("oauth.token.manager.cookie.security.secret_key");
        
        // optional
        _cookiePath = properties.getProperty("oauth.token.manager.cookie.path", "/");
        _cookieDomain = properties.getProperty("oauth.token.manager.cookie.domain");
        String cookieMaxAge = properties.getProperty("oauth.token.manager.cookie.max_age");
        if(cookieMaxAge!=null)
            setMaxAge(Integer.parseInt(cookieMaxAge));
        
        String loginTimeout = properties.getProperty("oauth.token.manager.cookie.login_timeout");
        if(loginTimeout!=null)
            _loginTimeout = Integer.parseInt(loginTimeout);
        
        if(_secretKey==null)
            throw new IllegalStateException("oauth.token.manager.cookie.security.secret_key must be set.");
        
        init();
        
        _initialized = true;
    }
    
    public void setSecretKey(String secretKey)
    {
        if(_secretKey!=null)
            throw new IllegalArgumentException("secretKey has already been set.");
            
        _secretKey = secretKey;            
    }
    
    public void setCookiePath(String cookiePath)
    {
        if(_cookiePath!=null)
            throw new IllegalArgumentException("cookiePath has already been set.");
        
        _cookiePath = cookiePath;            
    }
    
    public void setCookieDomain(String cookieDomain)
    {
        if(_cookieDomain!=null)
            throw new IllegalArgumentException("cookieDomain has already been set.");
        
        _cookieDomain = cookieDomain;            
    }
    
    public void setMaxAge(int maxAge)
    {
        _maxAge = maxAge;
    }
    
    public void setLoginTimeout(int loginTimeout)
    {
        _loginTimeout = loginTimeout;
    }
    
    protected void init()
    {
        if(_crypto==null)
        {
            if(_secretKey.length()!=24)
            {
                if(_secretKey.length()>24)
                    throw new IllegalArgumentException("supported secret keys are limited to 24 bytes long.");
                
                // padding
                for(int i=0,len=24-_secretKey.length(); i<len; i++)
                    _secretKey+=".";
            }
            try
            {
                _crypto = Cryptography.createDESede(_secretKey);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Token getToken(String ck, HttpServletRequest request) 
    throws IOException
    {
        Cookie[] cookies = request.getCookies();
        if(cookies==null)
            return null;
        
        for(Cookie c : cookies)
        {
            if(ck.equals(c.getName()))
                return getToken(c);
        }        
        return null;
    }
    
    Token getToken(Cookie cookie) 
    throws IOException
    {
        String value = null;
        try
        {
            value = _crypto.decryptDecode(cookie.getValue());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }        
        return (Token)_json.parse(new StringSource(value));
    }
    
    public boolean saveToken(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        if(response.isCommitted())
            return false;
        String u = _json.toJSON(token);
        String value = null;
        try
        {
            value = _crypto.encryptEncode(u);
        }
        catch(Exception e)
        {
            // shouldn't happen
            e.printStackTrace();
            return false;
        }
        return write(token.getCk(), value, token.isAuthentic() ? _maxAge : _loginTimeout, response);
    }
 
    
    public boolean invalidate(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        return write(token.getCk(), "0", 0, response);
    }
    
    private boolean write(String name, String value, int maxAge, HttpServletResponse response) 
    throws IOException
    {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(_cookiePath);
        if(_cookieDomain!=null)
            cookie.setDomain(_cookieDomain);
        response.addCookie(cookie);
        return true;
    }
}
