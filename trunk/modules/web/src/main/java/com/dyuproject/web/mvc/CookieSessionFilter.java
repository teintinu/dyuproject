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

package com.dyuproject.web.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author David Yu
 * @created May 19, 2008
 */

public class CookieSessionFilter extends AbstractFilter
{
    
    public static final String ENV_SECRET_KEY = "session.cookie.secretKey";
    public static final String ENV_COOKIE_NAME = "session.cookie.name";
    public static final String ENV_COOKIE_MAX_AGE = "session.cookie.maxAge";
    public static final String ENV_COOKIE_DOMAIN = "session.cookie.domain";
    public static final String ENV_COOKIE_PATH = "session.cookie.path";
    
    private static ThreadLocal<CookieSession> __cookieSession = new ThreadLocal<CookieSession>();
    
    static void setCurrentSession(CookieSession session)
    {
        __cookieSession.set(session);
    }    
    
    public static CookieSession getCurrentSession()
    {
        return __cookieSession.get();
    }
    
    private String _secretKey, _cookieName;
    
    public CookieSessionFilter()
    {
        CookieSession.init();
    }
    
    protected void init()
    {
        _secretKey = _webContext.getProperty(ENV_SECRET_KEY);
        _cookieName = _webContext.getProperty(ENV_COOKIE_NAME);     
        
        if(getCookieName()==null)
            throw new IllegalStateException("cookieName must be specified.");
        if(getSecretKey()==null)
            throw new IllegalStateException("secretKey must be specified.");
    }
    
    public String getCookieName()
    {
        return _cookieName;
    }
    
    public String getSecretKey()
    {
        return _secretKey;
    }

    public final void postHandle(boolean handled, String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        CookieSession session = getCurrentSession();
        setCurrentSession(null);
        if(session!=null)
        {
            session.writeIfNecessary(response);
            onSessionPassivated(session);
        }        
    }

    public final boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        CookieSession session = getSession(request);
        if(session==null)
        {
            sendError(mime, request, response);
            return false;
        }
        setCurrentSession(session);
        onSessionActivated(session);
        return true;
    }
    
    protected CookieSession getSession(HttpServletRequest request)
    {
        return CookieSession.get(getSecretKey(), getCookieName(), request);
    }
    
    protected void onSessionActivated(CookieSession session)
    {
        
    }
    
    protected void onSessionPassivated(CookieSession session)
    {
        
    }
    
    protected void sendError(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        response.sendError(401);
    }
}
