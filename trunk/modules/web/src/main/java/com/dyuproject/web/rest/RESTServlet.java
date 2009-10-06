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

package com.dyuproject.web.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.ClassLoaderUtil;


/**
 * The REST servlet to handle all the requests dispatched to the webapp.
 * 
 * @author David Yu
 * @created May 10, 2008
 */

@SuppressWarnings("serial")
public final class RESTServlet extends HttpServlet
{
    
    private WebContext _webContext;
    
    public void init() throws ServletException
    {        
        _webContext = (WebContext)getServletContext().getAttribute(WebContext.class.getName());
        if(_webContext==null)
        {
            String webContextClass = getServletConfig().getInitParameter("webContext");            
            if(webContextClass==null)
                throw new ServletException("*webContext* is missing from the servlet context's attributes/init-parameter");
            try
            {
                _webContext = (WebContext)ClassLoaderUtil.newInstance(webContextClass, RESTServlet.class);                
            }
            catch(Exception e)
            {
                throw new ServletException(e);
            }
        }
        try
        {            
            _webContext.preConfigure(getServletConfig());
            _webContext.init(getServletContext());
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }        
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        _webContext.service(request, response);
    }
    
    public void destroy()
    {
        if(_webContext!=null)
            _webContext.destroy(getServletContext());
    }

}
