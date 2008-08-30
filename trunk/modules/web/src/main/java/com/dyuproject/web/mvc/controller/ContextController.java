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

package com.dyuproject.web.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.web.mvc.AbstractController;
import com.dyuproject.web.mvc.Controller;
import com.dyuproject.web.mvc.WebContext;


/**
 * @author David Yu
 * @created Jun 5, 2008
 */

public class ContextController extends AbstractController
{
    
    private static final Log log = LogFactory.getLog(ContextController.class);
    
    private Controller _defaultController;
    private Map<String,Controller> _controllers = new HashMap<String,Controller>();
    
    public ContextController()
    {
        this(null);
    }
    
    public ContextController(String identifier)
    {
        setIdentifier(identifier);
        setIdentifierAttribute("*");
    }
    
    protected void init()
    {
        // check in case user had unintentionally changed the value
        if(!"*".equals(getIdentifierAttribute()))
            setIdentifierAttribute("*");
        
        if(_defaultController!=null)
            _defaultController.init(getWebContext());
        
        for(Controller c : _controllers.values())
            c.init(getWebContext());        
    }
    
    public void setDefaultController(Controller defaultController)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _defaultController = defaultController;
    }
    
    public Controller getDefaultController()
    {
        return _defaultController;
    }
    
    public void setControllers(Controller[] controllers)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        for(Controller c : controllers)
        {
            if(c.getIdentifier()==null)
                throw new IllegalStateException("Controller's resourceName must not be null");
            _controllers.put(c.getIdentifier(), c);
        }
    }
    
    public void setControllers(List<Controller> controllers)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        for(Controller c : controllers)
        {
            if(c.getIdentifier()==null)
                throw new IllegalStateException("Controller's resourceName must not be null");
            _controllers.put(c.getIdentifier(), c);
        }
    }
    
    public void addController(Controller c)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        if(c.getIdentifier()==null)
            throw new IllegalStateException("Controller's resourceName must not be null");
        _controllers.put(c.getIdentifier(), c);
    }
    
    public Controller getController(String resourceName)
    {
        return _controllers.get(resourceName);
    }
    
    public final int getIndex(HttpServletRequest request)
    {
        Integer idx = (Integer)request.getAttribute(WebContext.PATHINFO_INDEX_ATTR);
        return idx!=null ? idx.intValue() : -1;
    }
    
    /* ================================================================================= */

    public void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        int pathIndex = getIndex(request);
        // must be root context
        if(pathIndex==-1)
        {
            handleRoot(mime, request, response);
            return;
        }
        if(pathIndex!=0)
        {
            response.sendError(404);
            return;
        }
        String[] pathInfo = (String[])request.getAttribute(WebContext.PATHINFO_ARRAY_ATTR);
        if(pathInfo.length==1)
            handleRoot(mime, request, response);
        else        
            handle(pathIndex+1, pathInfo, mime, request, response);
    }
    
    protected void handleRoot(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        if(_defaultController==null)
            getWebContext().getDefaultDispatcher().dispatch(null, request, response);
        else
            WebContext.handle(_defaultController, mime, request, response);
    }
    
    private void handle(int sub, String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        Controller c = _controllers.get(pathInfo[sub]);
        if(c==null)
        {
            log.warn("No controller matched on: " + pathInfo[sub]);
            response.sendError(404);
            return;
        }            
        if(sub+1==pathInfo.length)
        {            
            WebContext.handle(c, mime, request, response);
            return;
        }
        String verbOrIdAttr = c.getIdentifierAttribute();
        if(verbOrIdAttr==null)
        {
            log.warn(pathInfo[sub+1] + " is not a verb nor an id of " + pathInfo[sub]);
            response.sendError(404);
            return;
        }
        // support wildcard
        if(verbOrIdAttr.charAt(0)=='*')
        {
            //request.setAttribute(WebContext.PATHINFO_ARRAY_ATTR, pathInfo);
            request.setAttribute(WebContext.PATHINFO_INDEX_ATTR, new Integer(sub));
            WebContext.handle(c, mime, request, response);
            return;
        }
        
        request.setAttribute(verbOrIdAttr, pathInfo[sub+1]);
        if(sub+2==pathInfo.length)
        {
            WebContext.handle(c, mime, request, response);
            return;
        }
        handle(sub+2, pathInfo, mime, request, response);
    }

}
