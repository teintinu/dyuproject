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

package com.dyuproject.openid.manager;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON.StringSource;

import com.dyuproject.json.StandardJSON;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.OpenIdUserManager;
import com.dyuproject.util.B64Code;
import com.dyuproject.util.Cryptography;
import com.dyuproject.util.Delim;
import com.dyuproject.util.DigestUtil;

/**
 * The OpenIdUser is serialized and persisted in an http cookie.
 * 
 * @author David Yu
 * @created Jan 5, 2009
 */

public final class CookieBasedUserManager implements OpenIdUserManager
{
    
    private boolean _initialized = false;
    
    private final StandardJSON _json = new StandardJSON();
    
    private String _secretKey;
    private String _cookieName;    
    private String _cookieDomain;
    private String _cookiePath;
    
    private Cryptography _crypto;

    /**
     * 10 minutes for the authenticated user's session to expire
     */
    private int _maxAge = 600; 
    /**
     * 1 minute for the discovered user to finish authentication on his openid provider
     */
    private int _loginTimeout = 60;
    
    
    public CookieBasedUserManager()
    {
        
    }
    
    public CookieBasedUserManager(String cookieName, String secretKey)
    {
        setCookieName(cookieName);
        setSecretKey(secretKey);        
    }
    
    public CookieBasedUserManager(String cookieName, String secretKey, boolean encrypted)
    {
        setCookieName(cookieName);
        setSecretKey(secretKey);
        setEncrypted(encrypted);
    }
    
    public StandardJSON getJSON()
    {
        return _json;
    }
    
    public void init(Properties properties)
    {
        if(_initialized)
            return;
        
        // required
        _cookieName = properties.getProperty("openid.user.manager.cookie.name");
        _secretKey = properties.getProperty("openid.user.manager.cookie.security.secret_key");
        
        // optional
        _cookiePath = properties.getProperty("openid.user.manager.cookie.path", "/");
        _cookieDomain = properties.getProperty("openid.user.manager.cookie.domain");
        String cookieMaxAge = properties.getProperty("openid.user.manager.cookie.max_age");
        if(cookieMaxAge!=null)
            setMaxAge(Integer.parseInt(cookieMaxAge));
        
        String loginTimeout = properties.getProperty("openid.user.manager.cookie.login_timeout");
        if(loginTimeout!=null)
            _loginTimeout = Integer.parseInt(loginTimeout);
        
        String securityType = properties.getProperty("openid.user.manager.cookie.security_type");
        
        setEncrypted(securityType==null || "encrypted".equalsIgnoreCase(securityType));
        
        if(_cookieName==null)
            throw new IllegalStateException("openid.user.manager.cookie.name must be set.");
        
        if(_secretKey==null)
            throw new IllegalStateException("openid.user.manager.cookie.security.secret_key must be set.");
        
        _initialized = true;
    }
    
    public void setSecretKey(String secretKey)
    {
        if(_secretKey!=null)
            throw new IllegalArgumentException("secretKey has already been set.");
            
        _secretKey = secretKey;            
    }    
    
    public void setCookieName(String cookieName)
    {
        if(_cookieName!=null)
            throw new IllegalArgumentException("cookieName has already been set.");
        
        _cookieName = cookieName;            
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
    
    public void setEncrypted(boolean encrypted)
    {
        if(encrypted && _crypto==null)
        {
            _secretKey = Cryptography.pad(_secretKey, '.');
            try
            {
                _crypto = _secretKey.length()==24 ? Cryptography.createDESede(_secretKey) : 
                    Cryptography.createDES(_secretKey);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public OpenIdUser getUser(HttpServletRequest request) 
    throws IOException
    {
        Cookie[] cookies = request.getCookies();
        if(cookies==null)
            return null;
        
        for(Cookie c : cookies)
        {
            if(_cookieName.equals(c.getName()))
                return getUser(c);
        }        
        return null;
    }
    
    OpenIdUser getUser(Cookie cookie) throws IOException
    {       
        return _crypto==null ? getUserVerifiedBySignature(cookie) : getUserByDecryption(cookie);
    }
    
    @SuppressWarnings("unchecked")
    OpenIdUser getUserVerifiedBySignature(Cookie cookie) 
    throws IOException
    {
        String[] values = Delim.AMPER.split(cookie.getValue());
        if(values.length!=2)
        {
            // invalid cookie.
            return null;
        }
        String sig = values[0];
        String u = values[1];
        if(!DigestUtil.digestMD5(u + _secretKey).equals(sig))
        {
            // invalid cookie.
            return null;
        }
        
        Map map = (Map)_json.parse(new StringSource(B64Code.decode(u)));
        OpenIdUser user = new OpenIdUser();
        user.fromJSON(map);
        return user;
    }
    
    @SuppressWarnings("unchecked")
    OpenIdUser getUserByDecryption(Cookie cookie) 
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
        
        Map map = (Map)_json.parse(new StringSource(value));
        OpenIdUser user = new OpenIdUser();
        user.fromJSON(map);
        return user;
    }
    
    public boolean saveUser(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        return _crypto==null ? saveUserWithSignature(user, response) : saveUserWithEncryption(user, 
                response);
    }
    
    boolean saveUserWithSignature(OpenIdUser user, HttpServletResponse response) throws IOException
    {
        String u = B64Code.encode(_json.toJSON(user));
        String sig = DigestUtil.digestMD5(u + _secretKey);
        StringBuilder buffer = new StringBuilder().append(sig).append('&').append(u);
        return write(buffer.toString(), user.isAuthenticated() ? _maxAge : _loginTimeout, response);
    }
    
    boolean saveUserWithEncryption(OpenIdUser user, HttpServletResponse response) throws IOException
    {
        String value = null;
        try
        {
            value = _crypto.encryptEncode(_json.toJSON(user));
        }
        catch(Exception e)
        {
            // shouldn't happen
            e.printStackTrace();
            return false;
        }
        return write(value, user.isAuthenticated() ? _maxAge : _loginTimeout, response);
    }    
    
    public boolean invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        return write("0", 0, response);
    }
    
    private boolean write(String value, int maxAge, HttpServletResponse response) 
    throws IOException
    {
        Cookie cookie = new Cookie(_cookieName, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(_cookiePath);
        if(_cookieDomain!=null)
            cookie.setDomain(_cookieDomain);
        response.addCookie(cookie);
        return true;
    }
}
