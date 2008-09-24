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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON;

import com.dyuproject.util.B64Code;
import com.dyuproject.util.Cryptography;
import com.dyuproject.util.Delim;
import com.dyuproject.util.DigestUtil;

/**
 * Persists associated/authenticated users using cookies
 * 
 * @author David Yu
 * @created Sep 20, 2008
 */

public class OpenIdUserManager
{
    
    private String _secretKey;
    private String _cookieName;    
    private String _cookieDomain;
    private String _cookiePath;
    private int _maxAge = 600; //10 minutes
    private Cryptography _crypto;
    
    public OpenIdUserManager()
    {
        
    }
    
    public OpenIdUserManager(String cookieName, String secretKey)
    {
        setCookieName(cookieName);
        setSecretKey(secretKey);        
    }
    
    public OpenIdUserManager(String cookieName, String secretKey, boolean encrypted)
    {
        setCookieName(cookieName);
        setSecretKey(secretKey);
        setEncrypted(encrypted);
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
    
    public void setEncrypted(boolean encrypted)
    {
        if(encrypted && _crypto==null)
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
                _crypto = Cryptography.createDES(_secretKey);
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
        
        return (OpenIdUser)JSON.parse(B64Code.decode(u));
    }
    
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
        return (OpenIdUser)JSON.parse(value);
    }
    
    public boolean saveUser(OpenIdUser user, HttpServletResponse response) throws IOException
    {        
        return _crypto==null ? saveUserWithSignature(user, response) : saveUserWithEncryption(user, 
                response);
    }
    
    boolean saveUserWithSignature(OpenIdUser user, HttpServletResponse response) throws IOException
    {
        String u = B64Code.encode(JSON.toString(user));
        String sig = DigestUtil.digestMD5(u + _secretKey);
        StringBuilder buffer = new StringBuilder().append(sig).append('&').append(u);
        return write(buffer.toString(), _maxAge, response);
    }
    
    boolean saveUserWithEncryption(OpenIdUser user, HttpServletResponse response) throws IOException
    {
        String u = JSON.toString(user);
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
        return write(value, _maxAge, response);
    }    
    
    public boolean invalidate(HttpServletResponse response) throws IOException
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
