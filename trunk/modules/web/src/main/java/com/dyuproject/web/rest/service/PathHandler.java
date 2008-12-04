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
    
    public static boolean isPathParameter(String id)
    {
        char first = id.charAt(0);
        return first=='{' || first=='$';
    }
    
    private String _id;
    private PathHandler _parent;
    
    private PathHandler _parameterHandler = null;
    private Map<String,PathHandler> _pathHandlers = new HashMap<String,PathHandler>(3);
    private Map<String,ResourceHandler> _resourceHandlers = new HashMap<String,ResourceHandler>(3);
    
    public PathHandler(String id)
    {
        _id = id;
    }
    
    PathHandler(String id, PathHandler parent)
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
        PathHandler last = _pathHandlers.put(child.getId(), child);
        if(last!=null)
            _log.warn("path overridden: " + last.getId() + " | " + last);
    }
    
    void addDefaultHandler(ResourceHandler resourceHandler)
    {
        ResourceHandler last = _resourceHandlers.put(resourceHandler.getHttpMethod(), resourceHandler);
        if(last!=null)
            _log.warn("resource overridden: " + last.getHttpMethod() + " | " + last);
    }    
    
    void resourceHandle() throws IOException
    {
        ResourceHandler resourceHandler = _resourceHandlers.get(WebContext.getCurrentRequestContext().getRequest().getMethod());
        if(resourceHandler==null)
        {
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
            return;
        }
        resourceHandler.handle();
    }
    
    public PathHandler map(int index, String[] pathInfo, ResourceHandler resourceHandler)
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
        if(isPathParameter(next))
        {
            if(pathHandler._parameterHandler==null)
                pathHandler._parameterHandler = new PathHandler("/");
            
            if(index==pathInfo.length)
            {
                pathHandler._parameterHandler.addDefaultHandler(resourceHandler);
                return pathHandler._parameterHandler;
            }            
            
            return pathHandler._parameterHandler.map(index, pathInfo, resourceHandler);
        }
        
        return pathHandler.map(--index, pathInfo, resourceHandler);
    }
    
    public void handle(int index, String[] pathInfo) throws IOException
    {
        String id = pathInfo[index++];
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
        {
            if(_parameterHandler==null)
                WebContext.getCurrentRequestContext().getResponse().sendError(404);
            else if(index==pathInfo.length)
                _parameterHandler.resourceHandle();
            else
                _parameterHandler.handle(index, pathInfo);            
        }
        else if(index==pathInfo.length)
            pathHandler.resourceHandle();
        else
            pathHandler.handle(index, pathInfo);
    }

}
