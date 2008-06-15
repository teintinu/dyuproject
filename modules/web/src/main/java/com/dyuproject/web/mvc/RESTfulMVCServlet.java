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

package com.dyuproject.web.mvc;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author David Yu
 * @created May 10, 2008
 */

public class RESTfulMVCServlet extends HttpServlet
{
    
    private WebContext _webContext;
    
    public void init() throws ServletException
    {
        
        _webContext = (WebContext)getServletContext().getAttribute(WebContext.class.getName());        
        if(_webContext==null)
        {
            _webContext = new WebContext();
            try
            {
                setupFromWebXml();
            }
            catch(Exception e)
            {
                throw new ServletException(e);
            }
        }
        _webContext.init(getServletContext());
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        _webContext.service(request, response);
    }
    
    private void setupFromWebXml() throws Exception
    {
        String defaultControllerParam = getInitParameter("defaultController");
        if(defaultControllerParam!=null)
            _webContext.setDefaultController((Controller)newObjectInstance(defaultControllerParam));
        
        String controllersParam = getInitParameter("controllers");
        if(controllersParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(controllersParam, ",;");
            while(tokenizer.hasMoreTokens())
                _webContext.addController((Controller)newObjectInstance(tokenizer.nextToken().trim()));
        }
    }
    
    private Object newObjectInstance(String className) throws Exception
    {
        return getClass().getClassLoader().loadClass(className).newInstance();        
    }
    
    public void destroy()
    {
        if(_webContext!=null)
            _webContext.destroy();
    }

}
