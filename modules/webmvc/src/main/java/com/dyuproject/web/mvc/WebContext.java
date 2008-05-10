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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * @author David Yu
 * @created May 11, 2008
 */

public class WebContext
{
    
    private ServletContext _servletContext;
    private RequestDispatcher _default, _jsp;
    
    public WebContext()
    {
        
    }
    
    void setServletContext(ServletContext servletContext)
    {
        _servletContext = servletContext;
    }
    
    public ServletContext getServletContext()
    {
        return _servletContext;
    }
    
    void setDefaultDispatcher(RequestDispatcher dispatcher)
    {
        _default = dispatcher;
    }
    
    RequestDispatcher getDefaultDispatcher()
    {
        return _default;
    }
    
    void setJspDispatcher(RequestDispatcher dispatcher)
    {
        _jsp = dispatcher;
    }
    
    RequestDispatcher getJspDispatcher()
    {
        return _jsp;
    }   

}
