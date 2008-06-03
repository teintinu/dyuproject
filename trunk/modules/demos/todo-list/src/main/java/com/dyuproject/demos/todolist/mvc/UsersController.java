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

package com.dyuproject.demos.todolist.mvc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.util.format.JSONConverter;
import com.dyuproject.util.format.XMLConverter;
import com.dyuproject.web.mvc.controller.CRUDController;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class UsersController extends CRUDController
{
    
    public static final String IDENTIFIER = "users";
    public static final String IDENTIFIER_ATTR = "users.verbOrId";
    
    private UserDao _userDao;
    
    public UsersController()
    {
        setIdentifier(IDENTIFIER);
        setIdentifierAttribute(IDENTIFIER_ATTR);
        setAllowMethodOverride(true);
    }
    
    protected void init()
    {
        super.init();
        _userDao = (UserDao)getWebContext().getAttribute("userDao");
    }


    @Override
    protected void create(HttpServletRequest request,
            HttpServletResponse response, String mime) throws IOException,
            ServletException
    {
        String firstName = request.getParameter(Constants.FIRST_NAME);
        String lastName = request.getParameter(Constants.LAST_NAME);
        String email = request.getParameter(Constants.EMAIL);
        String username = request.getParameter(Constants.USERNAME);
        String password = request.getParameter(Constants.PASSWORD);        

        User user = null;
        
        if(firstName==null || firstName.length()==0 || 
                lastName==null || lastName.length()==0 || 
                email==null || email.length()==0 ||
                username==null || username.length()==0 ||
                password==null || password.length()==0)
        {
            if(Constants.XML.equals(mime))
            {
                writeXML(Feedback.REQUIRED_PARAMS_USER_CREATE, request, response);
            }
            else if(Constants.JSON.equals(mime))
            {
                writeJSON(Feedback.REQUIRED_PARAMS_USER_CREATE, request, response);
            }
            else
            {
                request.setAttribute(Constants.MSG, Constants.REQUIRED_PARAMS_USER_CREATE);
                request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);  
                dispatchToFormView(user, request, response);
            }
            return;
        }
        
        user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        
        boolean created = _userDao.create(user);
        
        if(Constants.XML.equals(mime))
        {
            writeXML(created ? user : UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback() : Feedback.COULD_NOT_CREATE_USER, request, 
                    response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(created ? user : UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback() : Feedback.COULD_NOT_CREATE_USER, request, 
                    response);
        }
        else
        {
            if(created)
            {       
                request.setAttribute(Constants.MSG, Constants.USER_CREATED);
                dispatchToView(user, request, response);                
            }
            else
            {                
                request.setAttribute(Constants.MSG, UserDao.getCurrentErrorMessage()!=null ? 
                        UserDao.getCurrentErrorMessage() : Constants.COULD_NOT_CREATE_USER);
                request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
                dispatchToFormView(user, request, response);
            }
            
        }        
        
    }

    @Override
    protected void delete(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        boolean deleted = _userDao.delete(user);
        
        if(Constants.XML.equals(mime))
        {
            writeXML(deleted ? user : Feedback.COULD_NOT_DELETE_USER, request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(deleted ? user : Feedback.COULD_NOT_DELETE_USER, request, response);
        }
        else
        {
            request.setAttribute(Constants.MSG, deleted ? Constants.USER_DELETED : 
                Constants.COULD_NOT_DELETE_USER);
            
            dispatchToView(user, request, response);
        }
    }

    protected void read(HttpServletRequest request,
            HttpServletResponse response, String mime)
            throws IOException, ServletException
    {
        if(Constants.XML.equals(mime))        
            writeXML(_userDao.get(), request, response);        
        else if(Constants.JSON.equals(mime))        
            writeXML(_userDao.get(), request, response);        
        else        
            dispatchToView(_userDao.get(), request, response);        
    }
    
    protected void read(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {        
        User user = _userDao.get(Long.valueOf(id));
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        
        if(Constants.XML.equals(mime))        
            writeXML(user, request, response);        
        else if(Constants.JSON.equals(mime))        
            writeXML(user, request, response);        
        else
            dispatchToView(user, request, response);
        
    }

    @Override
    protected void update(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {
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
        
        if(Constants.XML.equals(mime))
        {
            writeXML(updated ? user : UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback() : Feedback.COULD_NOT_UPDATE_USER, 
                    request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(updated ? user : UserDao.getCurrentFeedback()!=null ? 
                    UserDao.getCurrentFeedback() : Feedback.COULD_NOT_UPDATE_USER, 
                    request, response);
        }
        else
        {            
            request.setAttribute(Constants.MSG, updated ? Constants.USER_UPDATED : 
                UserDao.getCurrentErrorMessage()!=null ? UserDao.getCurrentErrorMessage() : 
                    Constants.COULD_NOT_UPDATE_USER);
            dispatchToFormView(user, request, response);            
        }
        
    }
    
    /* ============================================================================= */
    
    private void dispatchToFormView(User user, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USER, user);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/users/form.jsp", request, response);
    }
    
    private void dispatchToView(User user, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USER, user);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/users/id.jsp", request, response);
    }

    private void dispatchToView(List<?> users, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.USERS, users);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/users/list.jsp", request, 
                response);
    }
    
    private void writeXML(Object data, HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        response.setContentType(Constants.TEXT_XML);
        ServletOutputStream out = response.getOutputStream();
        out.write(XMLConverter.getInstance().toString(data, null).getBytes());
        out.close();
    }
    
    private void writeJSON(Object data, HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        response.setContentType(Constants.TEXT_PLAIN);
        ServletOutputStream out = response.getOutputStream();
        out.write(JSONConverter.getInstance().toString(data, 
                request.getParameter(Constants.CALLBACK)).getBytes());
        out.close();
    }
    
    /* ============================================================================= */
    // VERBS
    /* ============================================================================= */
    
    protected void change_password(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        String id = request.getParameter(Constants.ID);
        String oldPassword = request.getParameter(Constants.OLD_PASSWORD);
        String newPassword = request.getParameter(Constants.NEW_PASSWORD);
        
        String msg = null;
        Feedback feedback = null;
        
        if(oldPassword==null || newPassword==null)
        {
            msg = Constants.REQUIRED_PARAMS_CHANGE_PASSWORD;
            feedback = Feedback.REQUIRED_PARAMS_CHANGE_PASSWORD;
        }
        else if(oldPassword.equals(newPassword))
        {
            msg = Constants.PASSWORD_DID_NOT_MATCH;
            feedback = Feedback.PASSWORD_DID_NOT_MATCH;
        }
        User user = id!=null ? _userDao.get(Long.valueOf(id)) : null;
        if(user==null)
        {
            msg = Constants.USER_NOT_FOUND;
            feedback = Feedback.USER_NOT_FOUND;
        }
        
        if(Constants.XML.equals(mime))
        {
            writeXML(feedback==null ? user : feedback, request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(feedback==null ? user : feedback, request, response);
        }
        else
        {
            request.setAttribute(Constants.MSG, msg==null ? Constants.PASSWORD_CHANGED : msg);
            dispatchToView(user, request, response);
        }        
        
    }
    
    protected void create(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        String method = request.getMethod();
        if(method.equals(GET))            
        {
            User user = null;
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            dispatchToFormView(user, request, response);
        }
        else if(method.equals(POST))
            create(request, response, mime);
        else
            response.sendError(404);
    }
    
    protected void edit(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {        
        String id = request.getParameter(Constants.ID);
        if(id==null)
        {            
            response.sendError(404);
            return;
        }
        
        String method = request.getMethod();
        if(method.equals(GET))
        {
            User user = _userDao.get(Long.valueOf(id));
            if(user==null)
                request.setAttribute(Constants.MSG, Constants.USER_NOT_FOUND);
            else
                request.setAttribute(Constants.USER, user);
            request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
            dispatchToFormView(user, request, response);
            return;
        }
        if(method.equals(POST))
        {
            request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
            update(request, response, mime, id);            
            return;
        }
        
        response.sendError(404);        
    }
    
    protected void delete(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {    
        String id = request.getParameter(Constants.ID);        
        if(id==null)
        {
            response.sendError(404);
            return;
        }
        request.setAttribute(Constants.ACTION, Constants.ACTION_DELETE);
        delete(request, response, mime, id);        
    }

}
