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
import com.dyuproject.web.RequiredParametersValidator;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class UserService extends AbstractService
{
    
    private UserDao _userDao;
    private RequiredParametersValidator _validator = new RequiredParametersValidator(new String[]{
            Constants.FIRST_NAME,
            Constants.LAST_NAME,
            Constants.EMAIL,
            Constants.USERNAME,
            Constants.PASSWORD,
            Constants.CONFIRM_PASSWORD
    });

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
        
        String id = rc.getPathElement(1);
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        
        String firstName = request.getParameter(Constants.FIRST_NAME);
        String lastName = request.getParameter(Constants.LAST_NAME);
        String email = request.getParameter(Constants.EMAIL);
        //String username = request.getParameter(Constants.USERNAME);
        //String password = request.getParameter(Constants.PASSWORD);
        
        boolean updated = false;
        if(firstName!=null)
        {
            user.setFirstName(firstName);
            updated = true;
        }
        if(lastName!=null)
        {
            user.setLastName(lastName);
            updated = true;
        }
        if(email!=null)
        {
            user.setEmail(email);
            updated = true;
        }
        
        /*if(username!=null)
        {
            user.setUsername(username);
            changed = true;
        }
        if(password!=null)
        {
            user.setPassword(password);
            changed = true;
        }*/
        updated = updated ? _userDao.update(user) : false;
        
        request.setAttribute(Constants.MSG, updated ? Feedback.USER_UPDATED : 
            UserDao.getCurrentFeedback()!=null ? UserDao.getCurrentFeedback().getMsg() : 
                Feedback.COULD_NOT_UPDATE_USER.getMsg());
        dispatchToFormView(user, request, response);        
    }
    
    @HttpResource(location="/users")
    @Post
    public void create(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        boolean paramsComplete = _validator.validate(request);
        if(!paramsComplete)
        {
            request.setAttribute(Constants.MSG, Feedback.REQUIRED_PARAMS_USER_CREATE.getMsg());
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);  
            dispatchToFormView(null, request, response);
            return;
        }
        
        String[] validatedParams = _validator.getValidatedParams(request);
        
        String firstName = validatedParams[0];
        String lastName = validatedParams[1];
        String email = validatedParams[2];
        String username = validatedParams[3];
        String password = validatedParams[4];
        String confirmPassword = validatedParams[5];        
        // password confirm/validation
        if(!password.equals(confirmPassword))
        {
            request.setAttribute(Constants.MSG, Feedback.PASSWORD_DID_NOT_MATCH.getMsg());
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);  
            dispatchToFormView(null, request, response);
            return;
        }
        
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        
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
    public void form_edit(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
        updateById(rc);  
    }
    
    @HttpResource(location="/users/new")
    @Get
    public void verb_create(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
        dispatchToFormView((User)null, rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/users/new")
    @Post
    public void form_create(RequestContext rc) throws IOException, ServletException
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
        
        if(feedback==null)
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
