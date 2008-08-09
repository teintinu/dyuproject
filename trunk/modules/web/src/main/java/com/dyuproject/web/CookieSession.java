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

package com.dyuproject.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.B64Code;
import com.dyuproject.util.Delim;
import com.dyuproject.util.digest.MD5;

/**
 * @author David Yu
 * @created May 19, 2008
 */

public class CookieSession
{
    
    public static final String TIMESTAMP_ATTR = "cs.ts";
    public static final String SIG_ATTR = "cs.sig";
    
    static CookieSession get(String secretKey, String cookieName, HttpServletRequest request, 
            boolean includeRemoteAddr)
    {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
        {
            for(Cookie c : cookies)
            {
                if(c.getName().equals(cookieName))
                    return parse(secretKey, c, request, includeRemoteAddr);
            }
        }
        return null;
    }
    
    private static CookieSession parse(String secretKey, Cookie cookie, HttpServletRequest request,
            boolean includeRemoteAddr)
    {
        String value = B64Code.decode(cookie.getValue());
        String[] pairs = Delim.AMPER.split(value);
        if(pairs.length<2)
            return null;
        
        String[] sigParam = Delim.EQUALS.split(pairs[pairs.length-1]);
        if(sigParam.length!=2 || !sigParam[0].equals(SIG_ATTR))
            return null;
        
        String sig = sigParam[1];

        StringBuilder toDigest = new StringBuilder();
        for(int i=0,len=pairs.length-1; i<len ;i++)
            toDigest.append(pairs[i]);
        
        toDigest.append(secretKey);
        
        //remote address, prevents session hijacking
        String remoteAddr = includeRemoteAddr ? request.getRemoteAddr() : null;
        if(remoteAddr!=null)
            toDigest.append(remoteAddr);        

        // verify signature        
        if(!sig.equals(MD5.digest(toDigest.toString())))
            return null;
        
        CookieSession session = new CookieSession(secretKey, cookie, remoteAddr);
        // exclude signature on attributes map
        for(int i=0,len=pairs.length-1; i<len ;i++)
        {
            String[] param = Delim.EQUALS.split(pairs[i]);
            if(param.length==2)
                session._attributes.internalPut(param[0], param[1]);
        }            
        return session;
    }
    
    static CookieSession create(String secretKey, String cookieName, HttpServletRequest request, 
            int maxAgeSeconds, String path, String domain, boolean includeRemoteAddr)
    {        
        return new CookieSession(secretKey, cookieName, includeRemoteAddr ? 
                request.getRemoteAddr() : null, maxAgeSeconds, path, domain);
    }    
    
    private boolean _parsed = false, _modified = false, _written = false;
    private AttributesMap<String,String>_attributes = new AttributesMap<String,String>();
    private Cookie _cookie;
    private String _secretKey, _cookieName, _domain, _path, _remoteAddr, _hashValue;
    private int _maxAge;
    private Object _writeLock = new Object();
    
    private CookieSession(String secretKey, Cookie cookie, String remoteAddr)
    {
        _parsed = true;
        _secretKey = secretKey;
        _cookieName = cookie.getName();        
        _remoteAddr = remoteAddr;
        _cookie = cookie;
    }
    
    private CookieSession(String secretKey, String cookieName, String remoteAddr, 
            int maxAgeSeconds, String path, String domain)
    {
        _parsed = false;
        _secretKey = secretKey;
        _cookieName = cookieName;
        _remoteAddr = remoteAddr;
        _maxAge = maxAgeSeconds;        
        _path = path;
        _domain = domain;        
    }
    
    public boolean isModified()
    {
        return _modified;
    }
    
    public boolean isWritten()
    {
        return _written;
    }
    
    public void setAttribute(String name, String value)
    {        
        _attributes.put(name, value);
    }
    
    public String getAttribute(String name)
    {
        return _attributes.get(name);
    }
    
    public void setMaxAge(int maxAge)
    {
        _maxAge = maxAge;
        _modified = true;
    }
    
    public boolean isNew()
    {
        return !_parsed;
    }
    
    public boolean saveIfNew(HttpServletResponse response)
    {
        if(!_parsed)
        {
            synchronized(_writeLock)
            {
                _written = true;
            }
            Cookie cookie = new Cookie(_cookieName, getHashValue());            
            cookie.setMaxAge(_maxAge);
            cookie.setPath(_path);
            if(_domain!=null)
                cookie.setDomain(_domain);
            _cookie = cookie;
            response.addCookie(cookie);            
            return true;
        }
        return false;
    }
    
    boolean updateIfNecessary(HttpServletResponse response)
    {
        synchronized(_writeLock)
        {
            if(_written)
                return false;
            _written = true;
        }
        
        if(_parsed)
        {
            if(!_modified)
            {
                _written = false;
                return false;
            }            
            Cookie cookie = new Cookie(_cookie.getName(), getHashValue());            
            cookie.setMaxAge(cookie.getMaxAge());
            cookie.setPath(cookie.getPath());
            if(_cookie.getDomain()!=null)
                cookie.setDomain(_cookie.getDomain());
            _cookie = cookie;
            response.addCookie(cookie);            
            return true;
        }        
        
        Cookie cookie = new Cookie(_cookieName, getHashValue());            
        cookie.setMaxAge(_maxAge);
        cookie.setPath(_path);
        if(_domain!=null)
            cookie.setDomain(_domain);
        _cookie = cookie;
        response.addCookie(cookie);        
        return true;
    }
    
    public String getHashValue()
    {
        if(_hashValue==null)
            _hashValue = generateHashValue();
        return _hashValue;
    }
    
    private String generateHashValue()
    {
        StringBuilder toDigest = new StringBuilder();
        StringBuilder output = new StringBuilder();
        
        _attributes.internalPut(TIMESTAMP_ATTR, String.valueOf(System.currentTimeMillis()));
        
        for(Map.Entry<String, String> entry : _attributes.entrySet())
        {
            String key = entry.getKey();
            if(!SIG_ATTR.equals(key))
            {
                String value = entry.getValue();
                output.append(key).append('=').append(value).append('&');
                toDigest.append(key).append('=').append(value);
            }
        }        
        
        toDigest.append(_secretKey);
        
        //remote address, prevents session hijacking
        if(_remoteAddr!=null)
            toDigest.append(_remoteAddr);        
        
        output.append(SIG_ATTR).append('=').append(MD5.digest(toDigest.toString()));
        return B64Code.encode(output.toString());
    }
    
    public boolean invalidate(HttpServletResponse response)
    {
        if(!_parsed)
            return false;
        
        synchronized(_writeLock)
        {
            _written = true;                
        }        
        //String oldSecretKey = _secretKey;
        //_secretKey = String.valueOf(System.currentTimeMillis());
        Cookie cookie = new Cookie(_cookie.getName(), getHashValue());
        //_secretKey = oldSecretKey;
        cookie.setMaxAge(0);
        cookie.setPath(cookie.getPath());
        if(_cookie.getDomain()!=null)
            cookie.setDomain(_cookie.getDomain());
        _cookie = cookie;
        _modified = false;
        response.addCookie(cookie);
        return true;
    }
    
    public Map<String,String> getAttributes()
    {
        return getAttrs();
    }
    
    public Map<String,String> getAttrs()
    {
        return _attributes;
    }
    
    private class AttributesMap<K,V> extends HashMap<K,V>
    {

        private static final long serialVersionUID = 20080520L;

        AttributesMap()
        {
            super(8);
        }
        
        private V internalPut(K key, V value)
        {            
            return super.put(key, value);
        }
        
        public V put(K key, V value)
        {
            _modified = true;
            return super.put(key, value);
        }
        
        public V remove(Object key)
        {
            _modified = true;
            return super.remove(key);
        }        
        
    }

}
