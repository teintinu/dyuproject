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
 * @created May 20, 2008
 */

public abstract class AbstractSessionAuthController extends AbstractController
{
    
    private String _secretKey, _cookieName, _path, _domain;
    private int _maxAge = 3600;
    
    protected void init()
    {
        _secretKey = _webContext.getProperty(CookieSessionFilter.ENV_SECRET_KEY);
        _cookieName = _webContext.getProperty(CookieSessionFilter.ENV_COOKIE_NAME);
        _path = _webContext.getProperty(CookieSessionFilter.ENV_COOKIE_PATH);
        _domain = _webContext.getProperty(CookieSessionFilter.ENV_COOKIE_DOMAIN);
        
        if(_secretKey==null)
            throw new IllegalStateException("secretKey is required");
        if(_cookieName==null)
            throw new IllegalStateException("cookieName is required");
    }
    
    public final void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        try
        {
            handleAuth(mime, request, response);
        }
        finally
        {
            CookieSession session = CookieSessionFilter.getCurrentSession();
            CookieSessionFilter.setCurrentSession(null);
            session.writeIfNecessary(response);
        }
    }
    
    protected CookieSession newSession(HttpServletRequest request)
    {
        CookieSession session = CookieSession.create(_secretKey, _cookieName, request, _maxAge, 
                _path, _domain);
        CookieSessionFilter.setCurrentSession(session);
        return session;        
    }
    
    protected CookieSession createSession(HttpServletRequest request)
    {
        return createSession(request);
    }
    
    protected abstract void handleAuth(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;

}
