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
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.dao.TodoDao;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class MainService extends AbstractService
{
    
    private UserDao _userDao;
    private TodoDao _todoDao;

    @Override
    protected void init()
    {
        _userDao = (UserDao)getWebContext().getAttribute("userDao");
        _todoDao = (TodoDao)getWebContext().getAttribute("todoDao");
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
            request.setAttribute(Constants.MSG, "Required: Username, Password");
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("login/index.jsp", request, 
                    response); 
            return;
        }
        User user = _userDao.get(username, password);
        if(user==null)
        {
            request.setAttribute(Constants.MSG, Feedback.USER_NOT_FOUND.getMsg());
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("login/index.jsp", request, 
                    response); 
            return;
        }
        CookieSession session = getWebContext().getSession(request, true);
        session.setAttribute(Constants.USER, user);
        getWebContext().persistSession(session, request, response);
        response.setContentType(Constants.TEXT_HTML);
        response.sendRedirect(request.getContextPath() + "/overview");        
    }
    
    @HttpResource(location="/login")
    @Get
    public void login(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        CookieSession session = getWebContext().getSession(request);        
        if(session!=null && session.getAttribute(Constants.USER)!=null)
        {
            response.sendRedirect(request.getContextPath() + "/overview");
            return;
        }
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("login/index.jsp", request, 
                response);
    }
    
    @HttpResource(location="/logout")
    @Get
    public void logout(RequestContext rc) throws IOException
    {        
        getWebContext().invalidateSession(rc.getResponse());
        rc.getResponse().sendRedirect(rc.getRequest().getContextPath() + "/overview");
    }
    
    @HttpResource(location="/overview")
    @Get
    public void overview(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        CookieSession session = getWebContext().getSession(request);
        User user = (User)session.getAttribute(Constants.USER);
        request.setAttribute(Constants.USER, user);
        request.setAttribute(Constants.TODOS, _todoDao.getByUser(user.getId()));
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("overview/index.jsp", request, 
                response);
    }
    

}
