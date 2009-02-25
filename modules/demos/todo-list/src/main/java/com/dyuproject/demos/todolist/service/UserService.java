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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jra.Delete;
import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;
import org.codehaus.jra.Put;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidationException;
import com.dyuproject.web.rest.annotation.Consume;
import com.dyuproject.web.rest.consumer.SimpleParameterConsumer;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class UserService extends AbstractService
{
    
    private UserDao _userDao;

    @Override
    protected void init()
    {
        _userDao = (UserDao)getWebContext().getAttribute("userDao");
    }
    
    @HttpResource(location="/users")
    @Get
    public void get(RequestContext rc) throws IOException, ServletException
    {
        dispatchToView(_userDao.get(), rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/users/$")
    @Get
    public void getById(RequestContext rc) throws IOException, ServletException
    {
        String id = rc.getPathElement(1);
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {
            rc.getResponse().sendError(404);
            return;
        }
        dispatchToView(user, rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/users/$")
    @Delete
    public void deleteById(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        
        boolean deleted = _userDao.delete(user);
        
        request.setAttribute(Constants.MSG, deleted ? Feedback.USER_DELETED.getMsg() : 
            Feedback.COULD_NOT_DELETE_USER.getMsg());
        
        dispatchToView(user, request, response);
    }    
    
    @HttpResource(location="/users/$")
    @Put
    public void updateById(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        User user = _userDao.get(Long.valueOf(rc.getPathElement(1)));
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        
        boolean updated = false;
        try
        {
            updated = rc.getConsumer().merge(user, rc);
        }
        catch(ValidationException ve)
        {
            request.setAttribute(Constants.MSG, ve.getMessage());
            dispatchToFormView(user, request, response);
            return;
        }
        
        if(updated && UserDao.executeUpdate())
        {
            String sub = request.getParameter("sub");
            if(sub!=null)
            {
                String[] pi = rc.getPathInfo();
                int len = pi.length - Integer.parseInt(sub);
                StringBuilder buffer = new StringBuilder().append(request.getContextPath());
                for(int i=0; i<len; i++)
                    buffer.append('/').append(pi[i]);
                response.sendRedirect(buffer.toString());
                return;
            }
            request.setAttribute(Constants.MSG, Feedback.USER_UPDATED.getMsg());
        }
        else
            request.setAttribute(Constants.MSG, UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback().getMsg() : Feedback.COULD_NOT_UPDATE_USER.getMsg());
        
        dispatchToFormView(user, request, response);        
    }
    
    @HttpResource(location="/users")
    @Post
    public void create(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String password = request.getParameter(Constants.PASSWORD);
        String confirmPassword = request.getParameter(Constants.CONFIRM_PASSWORD);        
        if(!password.equals(confirmPassword))
        {
            request.setAttribute(Constants.MSG, Feedback.PASSWORD_DID_NOT_MATCH.getMsg());
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);  
            dispatchToFormView(null, request, response);
            return;
        }
        
        User user = null;
        try
        {
            user = (User)rc.getConsumer().consume(rc);
        }
        catch(ValidationException ve)
        {
            request.setAttribute(Constants.MSG, ve.getMessage());
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            dispatchToFormView((User)ve.getPojo(), request, response);
            return;
        }
        
        boolean created = _userDao.create(user);
        
        if(created)
        {       
            //request.setAttribute(Constants.MSG, Feedback.USER_CREATED.getMsg());                
            //dispatchToView(user, request, response);
            response.sendRedirect(request.getContextPath() + "/users/" +user.getId());
        }
        else
        {                
            request.setAttribute(Constants.MSG, UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback().getMsg() : Feedback.COULD_NOT_CREATE_USER);
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            dispatchToFormView(user, request, response);
        }
        
    }
    
    /* ====================== VIEW VERBS(GET) and FORMS(POST) ====================== */
    
    @HttpResource(location="/users/$/delete")
    @Get
    public void verb_delete(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_DELETE);
        deleteById(rc);
    }
    
    @HttpResource(location="/users/$/edit")
    @Get
    public void verb_edit(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
            request.setAttribute(Constants.MSG, Feedback.USER_NOT_FOUND.getMsg());
        else
            request.setAttribute(Constants.USER, user);
        request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
        dispatchToFormView(user, request, response);
    }
    
    @HttpResource(location="/users/$/edit")
    @Post
    @Consume(consumers={SimpleParameterConsumer.class}, pojoClass=User.class)
    public void form_edit(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
        updateById(rc);  
    }
    
    @HttpResource(location="/users/new")
    @Get
    public void verb_new(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
        dispatchToFormView((User)null, rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/users/new")
    @Post
    @Consume(consumers={SimpleParameterConsumer.class}, pojoClass=User.class)
    public void form_new(RequestContext rc) throws IOException, ServletException
    {
        create(rc);
    }
    
    @HttpResource(location="/users/$/change_password")
    @Post
    public void verb_change_password(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {            
            response.sendError(404);
            return;
        }
        
        String oldPassword = request.getParameter(Constants.OLD_PASSWORD);
        String newPassword = request.getParameter(Constants.NEW_PASSWORD);

        Feedback feedback = null;
        
        if(oldPassword==null || newPassword==null)
        {
            feedback = Feedback.REQUIRED_PARAMS_CHANGE_PASSWORD;
        }
        else if(oldPassword.equals(newPassword))
        {
            feedback = Feedback.PASSWORD_DID_NOT_MATCH;
        }
        
        if(feedback==null && UserDao.executeUpdate())
            feedback = Feedback.PASSWORD_CHANGED;
        
        request.setAttribute(Constants.MSG, feedback.getMsg());
        dispatchToView(user, request, response);
    }
    
    /* ============================================================================= */
    
    private void dispatchToFormView(User user, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USER, user);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("users/form.jsp", request, response);
    }
    
    private void dispatchToView(User user, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USER, user);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("users/id.jsp", request, response);
    }

    private void dispatchToView(List<?> users, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USERS, users);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("users/list.jsp", request, 
                response);
    }

}
