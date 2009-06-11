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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON.StringSource;

import com.dyuproject.json.StandardJSON;
import com.dyuproject.util.Cryptography;

/**
 * Manages the CookieSession.
 * The environment is setup from a java.util.Properties 
 * with config parameters that starts with session.cookie.*
 * 
 * @author David Yu
 * @created Jun 2, 2008
 */

public class CookieSessionManager
{
    
    public static final String SESSION_COOKIE_SECRET_KEY = "session.cookie.secret_key";
    public static final String SESSION_COOKIE_NAME = "session.cookie.name";
    public static final String SESSION_COOKIE_MAX_AGE = "session.cookie.max_age";
    public static final String SESSION_COOKIE_DOMAIN = "session.cookie.domain";
    public static final String SESSION_COOKIE_PATH = "session.cookie.path";
    public static final String SESSION_COOKIE_INCLUDE_REMOTE_ADDRESS = "session.cookie.include_remote_address";
    
    private static final ThreadLocal<CookieSession> __session = new ThreadLocal<CookieSession>();

    private String _secretKey, _cookieName, _cookiePath, _cookieDomain;
    private int _maxAge = 3600, _updateMs = 3600*500;
    private boolean _started = false, _includeRemoteAddr = false;
    private StandardJSON _json = new StandardJSON();
    private Cryptography _crypto;
    
    public static CookieSession getCurrentSession()
    {
        return __session.get();
    }
    
    public CookieSessionManager()
    {
        
    }
    
    public StandardJSON getJSON()
    {
        return _json;
    }

    public void init(Properties props)
    {
        if(_started)
            return;
        
        _cookieName = props.getProperty(SESSION_COOKIE_NAME);
        _secretKey = props.getProperty(SESSION_COOKIE_SECRET_KEY);
        if(_cookieName==null || _secretKey==null)
        {
            throw new IllegalStateException(SESSION_COOKIE_NAME + " and " + 
                    SESSION_COOKIE_SECRET_KEY + " env property must be set.");
        }
        _secretKey = Cryptography.pad(_secretKey, '.');

        _cookiePath = props.getProperty(SESSION_COOKIE_PATH, "/");
        _cookieDomain = props.getProperty(SESSION_COOKIE_DOMAIN);
        
        String maxAge = props.getProperty(SESSION_COOKIE_MAX_AGE);
        if(maxAge!=null)
        {
            _maxAge = Integer.parseInt(maxAge);
            _updateMs = _maxAge * 500;
        }
        
        String includeRemoteAddr = props.getProperty(SESSION_COOKIE_INCLUDE_REMOTE_ADDRESS);
        if(includeRemoteAddr!=null)
            _includeRemoteAddr = Boolean.parseBoolean(includeRemoteAddr);
        
        try
        {
            _crypto = _secretKey.length()==24 ? Cryptography.createDESede(_secretKey) : 
                Cryptography.createDES(_secretKey);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        _started = true;
    }

    
    public CookieSession getSession(HttpServletRequest request, boolean create)
    {
        CookieSession session = __session.get();
        if(session!=null)
            return session;
        
        session = (CookieSession)request.getAttribute(CookieSession.ATTR_NAME);
        if(session!=null)
            return session;
        
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
        {
            for(Cookie c : cookies)
            {
                if(_cookieName.equals(c.getName()))
                    return read(c, request);
            }  
        }      
        return create ? create(request) : null;
    }
    
    public boolean persistSession(CookieSession session, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        return !session.isPersisted() && persist(session, request, response);
    }
    
    public boolean invalidateSession(HttpServletResponse response) throws IOException
    {
        return write("0", 0, response);
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse response)
    throws IOException
    {
        CookieSession session = __session.get();
        __session.set(null);

        if(session==null || session.isPersisted())
            return;

        if(System.currentTimeMillis() > session.getTimeUpdated()+_updateMs)
            persist(session, request, response);
    }
        
    private boolean persist(CookieSession session, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        if(_includeRemoteAddr)
            session.setIP(request.getRemoteAddr());
        
        session.markPersisted();
        String value = null;
        try
        {
            value = _crypto.encryptEncode(_json.toJSON(session));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        write(value, _maxAge, response);
        return true;
    }
    
    private CookieSession create(HttpServletRequest request)
    {
        CookieSession session = new CookieSession(new HashMap<String,Object>(3));
        __session.set(session);
        request.setAttribute(CookieSession.ATTR_NAME, session);
        return session;
    }
    
    @SuppressWarnings("unchecked")
    private CookieSession read(Cookie cookie, HttpServletRequest request)
    {
        try
        {
            Map map = (Map)_json.parse(new StringSource(_crypto.decryptDecode(cookie.getValue())));
            if(_includeRemoteAddr)
            {
                String ip = (String)map.get("i");
                if(ip!=null && !ip.equals(request.getRemoteAddr()))
                    return null;
            }
            CookieSession session = new CookieSession();
            session.fromJSON(map);
            request.setAttribute(CookieSession.ATTR_NAME, session);
            return session;
        }
        catch(Exception e)
        {
            return null;
        }
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
