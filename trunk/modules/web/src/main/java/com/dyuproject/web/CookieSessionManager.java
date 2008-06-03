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

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author David Yu
 * @created Jun 2, 2008
 */

public class CookieSessionManager
{
    
    public static final String SESSION_COOKIE_SECRET_KEY = "session.cookie.secretKey";
    public static final String SESSION_COOKIE_NAME = "session.cookie.name";
    public static final String SESSION_COOKIE_MAX_AGE = "session.cookie.maxAge";
    public static final String SESSION_COOKIE_DOMAIN = "session.cookie.domain";
    public static final String SESSION_COOKIE_PATH = "session.cookie.path";
    
    public static final String COOKIE_SESSION_REQUEST_ATTR = "cs";
    
    private static LocalizedCookieSessionHolder __holder;    

    private String _secretKey, _cookieName, _path, _domain;
    private int _maxAge = 3600;
    private boolean _started = false;
    
    public static CookieSession getCurrentSession()
    {
        return __holder!=null ? __holder.get()._session : null;
    }
    
    public CookieSessionManager()
    {
        
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
        
        _path = props.getProperty(SESSION_COOKIE_PATH);
        _domain = props.getProperty(SESSION_COOKIE_DOMAIN);
        
        String maxAge = props.getProperty(SESSION_COOKIE_MAX_AGE);
        if(maxAge!=null)
            _maxAge = Integer.parseInt(maxAge);           
        
        init();
        CookieSession.init();
        _started = true;
    }
    
    private static synchronized void init()
    {
        if(__holder==null)
            __holder = new LocalizedCookieSessionHolder();
    }
    
    public CookieSession getSession(HttpServletRequest request, boolean create)
    {
        CookieSessionHolder holder = __holder.get();
        if(holder._initialized)
            return holder._session;
        holder._initialized = true;
        CookieSession cs = CookieSession.get(_secretKey, _cookieName, request);
        if(cs==null)
        {
            if(!create)
                return null;
            cs = CookieSession.create(_secretKey, _cookieName, request, _maxAge, _path, _domain);
        }
        request.setAttribute(COOKIE_SESSION_REQUEST_ATTR, cs);
        holder._session = cs;
        return cs;
    }
    
    public void updateIfNecessary(HttpServletResponse response)
    {
        CookieSessionHolder holder = __holder.get();
        CookieSession old = holder._session;
        holder._session = null;
        holder._initialized = false;        
        if(old!=null)
            old.updateIfNecessary(response);        
    }
    
    private static class CookieSessionHolder
    {
        private CookieSession _session;
        private boolean _initialized = false;
        
        private CookieSessionHolder()
        {
            
        }
    }
    
    private static class LocalizedCookieSessionHolder extends ThreadLocal<CookieSessionHolder>
    {
        protected CookieSessionHolder initialValue()
        {
            return new CookieSessionHolder();
        }   
    }

}
