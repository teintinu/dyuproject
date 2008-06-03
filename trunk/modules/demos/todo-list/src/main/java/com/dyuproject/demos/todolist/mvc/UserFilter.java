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
import com.dyuproject.web.mvc.AbstractFilter;
import com.dyuproject.web.mvc.CookieSession;

/**
 * @author David Yu
 * @created Jun 3, 2008
 */

public class UserFilter extends AbstractFilter
{

    @Override
    protected void init()
    {
        // TODO Auto-generated method stub
        
    }

    public void postHandle(boolean handled, String mime,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

    }

    public boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        CookieSession session = getWebContext().getSession(request);        
        if(session!=null && session.getAttribute(Constants.ID)!=null)
            return true;
        getWebContext().getJSPDispatcher().dispatch("/WEB-INF/jsp/login/index.jsp", request, 
                response);
        return false;
    }

}
