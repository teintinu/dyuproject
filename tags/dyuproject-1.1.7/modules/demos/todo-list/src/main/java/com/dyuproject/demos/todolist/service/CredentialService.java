//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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
import com.dyuproject.demos.todolist.model.Credential;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidationException;
import com.dyuproject.web.rest.WebContext;
import com.dyuproject.web.rest.annotation.Consume;
import com.dyuproject.web.rest.consumer.SimpleParameterConsumer;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Feb 25, 2009
 */

public class CredentialService extends AbstractService
{
    
    private CredentialDao _credentialDao;


    protected void init()
    {
        _credentialDao = (CredentialDao)getWebContext().getAttribute("credentialDao");
        
    }
    
    @HttpResource(location="/account")
    @Get
    public void get(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.USER, MainService.getUser(rc, getWebContext()));
        dispatchToView(null, rc, getWebContext());
    }
    
    @HttpResource(location="/account")
    @Post
    public void create(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        String username = request.getParameter(Constants.USERNAME);
        String password = request.getParameter(Constants.PASSWORD);
        String confirmPassword = request.getParameter(Constants.CONFIRM_PASSWORD);
        if(username==null || password==null || confirmPassword==null)
        {
            request.setAttribute(Constants.MSG, Feedback.ACCOUNT_CREATE_REQUIRED);
            dispatchToFormView(null, rc, getWebContext());
            return;
        }        
        if(!password.equals(confirmPassword))
        {
            request.setAttribute(Constants.MSG, Feedback.PASSWORD_DID_NOT_MATCH.getMsg());
            dispatchToFormView(null, rc, getWebContext());
            return;
        }
        
        Credential cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(password);
        cred.setRole(Integer.valueOf(0));
        User user = null;
        try
        {
            user = (User)rc.getConsumer().consume(rc);
        }
        catch(ValidationException ve)
        {
            cred.setUser((User)ve.getPojo());
            request.setAttribute(Constants.MSG, ve.getMessage());
            dispatchToFormView(cred, rc, getWebContext());
            return;
        }
        cred.setUser(user);
        boolean created = _credentialDao.create(cred);
        if(created)
        {
            MainService.saveUser(user, rc, getWebContext());
            MainService.redirectToOverview(rc, getWebContext());
        }
        else
        {
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            request.setAttribute(Constants.MSG, Feedback.COULD_NOT_CREATE_ACCOUNT.getMsg());
            dispatchToFormView(cred, rc, getWebContext());
        }        
    }
    
    @HttpResource(location="/account/new")
    @Get
    public void verb_new(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
        dispatchToFormView((Credential)null, rc, getWebContext());
    }
    
    @HttpResource(location="/account/new")
    @Post
    @Consume(consumers={SimpleParameterConsumer.class}, pojoClass=User.class)
    public void form_new(RequestContext rc) throws IOException, ServletException
    {
        create(rc);
    }
    
    @HttpResource(location="/account/change_password")
    @Get
    public void verb_change_password(RequestContext rc) throws IOException, ServletException
    {
        dispatchToFormChangePassword(rc, getWebContext());
    }
    
    @HttpResource(location="/account/change_password")
    @Post
    public void form_change_password(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        User user = MainService.getUser(rc, getWebContext());
        Credential cred = _credentialDao.get(user.getId());
        if(cred==null)
        {            
            response.sendError(404);
            return;
        }
        
        String oldPassword = request.getParameter(Constants.OLD_PASSWORD);
        String newPassword = request.getParameter(Constants.NEW_PASSWORD);
        String confirmPassword = request.getParameter(Constants.CONFIRM_PASSWORD);

        Feedback feedback = null;
        
        if(oldPassword==null || newPassword==null || confirmPassword==null)
            feedback = Feedback.CHANGE_PASSWORD_REQUIRED;
        else if(oldPassword.equals(newPassword))
            feedback = Feedback.NO_CHANGES_MADE;
        else if(newPassword.equals(confirmPassword))
        {
            if(cred.getPassword().equals(oldPassword))
            {
                cred.setPassword(newPassword);
                if(CredentialDao.executeUpdate())
                {
                    request.setAttribute(Constants.MSG, Feedback.PASSWORD_CHANGED.getMsg());
                    dispatchToView(null, rc, getWebContext());
                    return;
                }
            }
            else
                feedback = Feedback.PASSWORD_INCORRECT;
        }
        else
            feedback = Feedback.PASSWORD_DID_NOT_MATCH;
            
        
        if(feedback==null)
            feedback = Feedback.COULD_NOT_UPDATE_ACCOUNT;
        
        request.setAttribute(Constants.MSG, feedback.getMsg());
        dispatchToFormChangePassword(rc, getWebContext());
    }
    
    /* ============================================================================= */
    
    static void dispatchToFormView(Credential account, RequestContext rc, WebContext wc)
    throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        request.setAttribute(Constants.ACCOUNT, account);
        response.setContentType(Constants.TEXT_HTML);
        wc.getJSPDispatcher().dispatch("account/form.jsp", request, response);
    }
    
    static void dispatchToView(Credential account, RequestContext rc, WebContext wc)
    throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        request.setAttribute(Constants.ACCOUNT, account);
        response.setContentType(Constants.TEXT_HTML);
        wc.getJSPDispatcher().dispatch("account/id.jsp", request, response);
    }
    
    static void dispatchToFormChangePassword(RequestContext rc, WebContext wc)
    throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        response.setContentType(Constants.TEXT_HTML);
        wc.getJSPDispatcher().dispatch("account/change_password.jsp", request, response);
    }
    
    

}
