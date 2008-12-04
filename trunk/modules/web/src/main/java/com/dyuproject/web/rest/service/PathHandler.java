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

package com.dyuproject.web.rest.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Dec 3, 2008
 */

public class PathHandler
{
    
    private static Log _log = LogFactory.getLog(PathHandler.class);
    
    public static boolean isUriParameter(String id)
    {
        char first = id.charAt(0);
        return first=='{' || first=='$';
    }
    
    private String _id;
    private PathHandler _parent;
    private Map<String,ResourceHandler> _defaultHandlers = new HashMap<String,ResourceHandler>(7);
    private Map<String,ResourceHandler> _parameterHandlers = new HashMap<String,ResourceHandler>(7);
    private Map<String,PathHandler> _pathHandlers = new HashMap<String,PathHandler>(7);   
    
    public PathHandler(String id)
    {
        _id = id;
    }
    
    public PathHandler(String id, PathHandler parent)
    {
        this(id);
        setParent(parent);
    }
    
    public String getId()
    {
        return _id;
    }
    
    PathHandler setParent(PathHandler parent)
    {
        _parent = parent;
        _parent.addPathHandler(this);
        return this;
    }
    
    void addPathHandler(PathHandler child)
    {
        Object last = _pathHandlers.put(child.getId(), child);
        if(last!=null)
        {
            _log.warn("overridden: " + last);
        }
    }
    
    void addDefaultHandler(ResourceHandler invoker)
    {
        Object last = _defaultHandlers.put(invoker.getHttpMethod(), invoker);
        if(last!=null)
        {
            _log.warn("overridden: " + last);
        }
    }
    
    void addParameterHandler(ResourceHandler invoker)
    {
        Object last = _parameterHandlers.put(invoker.getHttpMethod(), invoker);
        if(last!=null)
        {
            _log.warn("overridden: " + last);
        }        
    }
    
    public PathHandler addPathHandler(int index, String[] pathInfo, ResourceHandler resourceHandler)
    {
        String id = pathInfo[index++];
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
            pathHandler = new PathHandler(id, this);     
        
        if(index==pathInfo.length)
        {            
            pathHandler.addDefaultHandler(resourceHandler);
            return pathHandler;
        }
        
        String next = pathInfo[index++];
        if(isUriParameter(next) && index==pathInfo.length)
        {
            pathHandler.addParameterHandler(resourceHandler);
            return pathHandler;
        }
        else
            index--;

        return pathHandler.addPathHandler(index, pathInfo, resourceHandler).setParent(pathHandler);
    }
    
    void handleDefault() throws IOException
    {
        ResourceHandler invoker = _defaultHandlers.get(WebContext.getCurrentRequestContext().getRequest().getMethod());
        if(invoker==null)
        {
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
            return;
        }
        invoker.invoke();
    }
    
    void handleParameter() throws IOException
    {
        ResourceHandler invoker = _parameterHandlers.get(WebContext.getCurrentRequestContext().getRequest().getMethod());
        if(invoker==null)
        {
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
            return;
        }
        invoker.invoke();
    }
    
    public void handle(int index, String[] pathInfo) throws IOException
    {
        String id = pathInfo[index++];
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
        {
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
            return;
        }
        if(index==pathInfo.length)
        {
            pathHandler.handleDefault();
            return;
        }
        String next = pathInfo[index++];
        PathHandler nextHandler = pathHandler._pathHandlers.get(next);        
        if(nextHandler==null)
        {
            //WebContext.getCurrentRequestContext().setUriParameter(id, next);
            if(index==pathInfo.length)
            {
                pathHandler.handleParameter();
                return;
            }
        }
        else if(index==pathInfo.length)
        {
            nextHandler.handleDefault();
            return;
        }
        nextHandler.handle(index, pathInfo);
    }

}
