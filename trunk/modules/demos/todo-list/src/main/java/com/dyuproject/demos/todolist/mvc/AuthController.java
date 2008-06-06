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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.mvc.AbstractController;

/**
 * @author David Yu
 * @created Jun 3, 2008
 */

public class AuthController extends AbstractController
{
    
    public static final String IDENTIFIER = "auth";
    
    private UserDao _userDao;
    
    public AuthController()
    {
        setIdentifier(IDENTIFIER);
    }
    
    protected void init()
    {
        _userDao = (UserDao)getWebContext().getAttribute("userDao");        
    }

    public void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        if(!request.getMethod().equals(POST))
        {
            response.sendError(404);
            return;
        }
        String username = request.getParameter(Constants.USERNAME);
        String password = request.getParameter(Constants.PASSWORD);
        
        if(username==null || password==null)
        {
            request.setAttribute(Constants.MSG, "Required: Username, Password");
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/login/index.jsp", request, 
                    response); 
            return;
        }
        User user = _userDao.get(username, password);
        if(user==null)
        {
            request.setAttribute(Constants.MSG, Constants.USER_NOT_FOUND);
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/login/index.jsp", request, 
                    response); 
            return;
        }
        CookieSession session = getWebContext().getSession(request, true);
        session.setAttribute(Constants.ID, user.getId().toString());
        session.saveIfNew(response);
        response.setContentType(Constants.TEXT_HTML);
        response.sendRedirect(request.getContextPath() + "/overview");
    }

}
