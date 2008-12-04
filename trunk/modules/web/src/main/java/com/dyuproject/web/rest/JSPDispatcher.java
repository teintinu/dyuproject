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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Efficiently dispatches to the jsp servlet.
 * 
 * @author David Yu
 * @created May 16, 2008
 */

public class JSPDispatcher implements ViewDispatcher
{    
     
    public static final String JSP = "jsp";
    public static final String DEFAULT_BASE_DIR = "/WEB-INF/views/jsp/";
    
    static final String INCLUDE_ATTR = "javax.servlet.include.servlet_path";
    
    private static final Log log = LogFactory.getLog(JSPDispatcher.class);
    
    private boolean _initialized = false, _jetty = false;
    private String _baseDir;
    RequestDispatcher _jsp;    
    
    public void init(WebContext context)
    {
        if(_jsp==null)
            _jsp = context.getServletContext().getNamedDispatcher(JSP);
        
        if(_baseDir==null)
            _baseDir = DEFAULT_BASE_DIR;
        else if(_baseDir.charAt(_baseDir.length()-1)!='/')
            _baseDir += "/";
        
        _jetty = context.getServletContext().getClass().getName().startsWith("org.mortbay.jetty");
        _initialized = true;
        log.info("baseDir: " + _baseDir);        
        log.info("initialized.");
    }
    
    public void dispatch(String uri, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        if(uri.charAt(0)!='/')
            uri = _baseDir + uri;
        
        request.setAttribute(WebContext.DISPATCH_ATTR, JSP);
        request.setAttribute(INCLUDE_ATTR, uri);
        // more efficient if in jetty
        if(_jetty)
        {               
            _jsp.include(request, response);
            return;
        }
        
        if(response.isCommitted())
            request.getRequestDispatcher(uri).include(request, response);
        else
            request.getRequestDispatcher(uri).forward(request, response);
    }
    
    public void setBaseDir(String baseDir)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _baseDir = baseDir;
    }

}
