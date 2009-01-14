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

package com.dyuproject.demos.deprecated.todolist.mvc;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dyuproject.demos.deprecated.todolist.Constants;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.rest.AbstractInterceptor;
import com.dyuproject.web.rest.RequestContext;

/**
 * @author David Yu
 * @created Jun 3, 2008
 */

public class UserInterceptor extends AbstractInterceptor
{

    @Override
    protected void init()
    {
        // TODO Auto-generated method stub
        
    }

    public void postHandle(boolean handled, RequestContext requestContext)
    {
        
    }

    public boolean preHandle(RequestContext requestContext)
            throws ServletException, IOException
    {
        CookieSession session = getWebContext().getSession(requestContext.getRequest());        
        if(session!=null && session.getAttribute(Constants.ID)!=null)
            return true;
        requestContext.getResponse().setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("login/index.jsp", requestContext.getRequest(), 
                requestContext.getResponse());
        return false;
    }

}
