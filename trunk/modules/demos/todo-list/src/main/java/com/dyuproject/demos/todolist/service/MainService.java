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

package com.dyuproject.demos.todolist.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.dao.CredentialDao;
import com.dyuproject.demos.todolist.dao.TodoDao;
import com.dyuproject.demos.todolist.model.Credential;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class MainService extends AbstractService
{    
    
    private TodoDao _todoDao;
    private CredentialDao _credentialDao;

    @Override
    protected void init()
    {        
        _todoDao = (TodoDao)getWebContext().getAttribute("todoDao");
        _credentialDao = (CredentialDao)getWebContext().getAttribute("credentialDao");
    }
    
    @HttpResource(location="/")
    @Get
    public void root(RequestContext rc) throws IOException, ServletException
    {
        rc.getResponse().setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/auth")
    @Post
    public void auth(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String username = request.getParameter(Constants.USERNAME);
        String password = request.getParameter(Constants.PASSWORD);
        
        if(username==null || password==null)
        {
            request.setAttribute(Constants.MSG, Feedback.AUTH_REQUIRED.getMsg());
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("login/index.jsp", request, response); 
            return;
        }
        Credential credential = _credentialDao.get(username, password);
        if(credential==null)
        {
            request.setAttribute(Constants.MSG, Feedback.USER_NOT_FOUND.getMsg());
            dispatchToLogin(rc, getWebContext());
            return;
        }
        User user = credential.getUser();
        saveUser(user, rc, getWebContext());
        redirectToOverview(rc, getWebContext());
    }
    
    @HttpResource(location="/login")
    @Get
    public void login(RequestContext rc) throws IOException, ServletException
    {   
        if(getUser(rc, getWebContext())!=null)
            rc.getResponse().sendRedirect(rc.getRequest().getContextPath() + "/overview");
        else
            dispatchToLogin(rc, getWebContext());
    }
    
    @HttpResource(location="/logout")
    @Get
    public void logout(RequestContext rc) throws IOException
    {        
        getWebContext().invalidateSession(rc.getResponse());
        redirectToOverview(rc, getWebContext());
    }
    
    @HttpResource(location="/overview")
    @Get
    public void overview(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();

        User user = getUser(rc, getWebContext());
        request.setAttribute(Constants.USER, user);
        request.setAttribute(Constants.TODOS, _todoDao.getByUser(user.getId()));
        dispatchToOverview(rc, getWebContext());
    }
    
    /* ============================================================================= */
    
    static void dispatchToLogin(RequestContext rc, WebContext wc) 
    throws ServletException, IOException
    {
        rc.getResponse().setContentType(Constants.TEXT_HTML);
        wc.getJSPDispatcher().dispatch("login/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    static void dispatchToOverview(RequestContext rc, WebContext wc) 
    throws ServletException, IOException
    {
        rc.getResponse().setContentType(Constants.TEXT_HTML);
        wc.getJSPDispatcher().dispatch("overview/index.jsp", rc.getRequest(), rc.getResponse());
    }
    
    static void redirectToOverview(RequestContext rc, WebContext wc) throws IOException
    {
        rc.getResponse().sendRedirect(rc.getRequest().getContextPath() + "/overview");
    }
    
    static void redirectToLogin(RequestContext rc) throws IOException
    {
        rc.getResponse().sendRedirect(rc.getRequest().getContextPath() + "/login");
    }
    
    static User getUser(RequestContext rc, WebContext wc)
    {
        CookieSession session = wc.getSession(rc.getRequest());
        return session==null ? null : (User)session.getAttribute(Constants.USER);
    }
    
    static void saveUser(User user, RequestContext rc, WebContext wc) throws IOException
    {
        HttpServletRequest request = rc.getRequest();
        CookieSession session = wc.getSession(request, true);
        session.setAttribute(Constants.USER, user);
        wc.persistSession(session, request, rc.getResponse());        
    }
    

}
