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

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.util.Delim;
import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.InterceptorCollection;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Dec 3, 2008
 */

public class PathHandler
{
    
    private static Log _log = LogFactory.getLog(PathHandler.class);
    private static final InterceptorCollection.ThreadLocal __currentInterceptors = 
        new InterceptorCollection.ThreadLocal();
    
    public static boolean isPathParameter(String id)
    {
        char first = id.charAt(0);
        return first=='{' || first=='$';
    }
    
    private String _id;
    private PathHandler _parent, _parameterHandler;
    private Map<String,PathHandler> _pathHandlers = new HashMap<String,PathHandler>(3);
    private Map<String,ResourceHandler> _resourceHandlers = new HashMap<String,ResourceHandler>(3);
    
    // /path/   /path/* and  /path/**
    private Interceptor[] _interceptors = new Interceptor[3];
    
    public PathHandler(String id)
    {
        _id = id;
    }
    
    PathHandler(String id, PathHandler parent)
    {
        this(id);
        _parent = parent;
        _parent.addPathHandler(this);        
    }
    
    public String getId()
    {
        return _id;
    }
    
    PathHandler setOtherParent(PathHandler parent)
    {
        _parent = parent;
        return this;
    }
    
    void addPathHandler(PathHandler child)
    {
        PathHandler last = _pathHandlers.put(child.getId(), child);
        if(last!=null)
            _log.warn("path overridden: " + last.getId() + " | " + last);
    }
    
    void addResourceHandler(ResourceHandler resourceHandler)
    {
        ResourceHandler last = _resourceHandlers.put(resourceHandler.getHttpMethod(), resourceHandler);
        if(last!=null)
            _log.warn("resource overridden: " + last.getHttpMethod() + " | " + last);
    }
    
    void addInterceptor(Interceptor interceptor, int wildcard)
    {
        if(_interceptors[wildcard]!=null)
            _log.warn("interceptor overriden: " + _interceptors[wildcard]);
                
        _interceptors[wildcard] = interceptor;        
    }
    
    static void appendWildcardToCollection(PathHandler pathHandler, 
            InterceptorCollection interceptors)
    {
        PathHandler parent = pathHandler._parent;
        if(parent!=null)
            appendWildcardToCollection(parent, interceptors);
        
        Interceptor interceptor = pathHandler._interceptors[2];
        if(interceptor!=null)
            interceptors.addInterceptor(interceptor);   
    }
    
    static void appendInterceptor(Interceptor i, InterceptorCollection interceptors)
    {
        if(i!=null)
            interceptors.addInterceptor(i);
    }
    
    void appendToCollection(InterceptorCollection interceptors)
    {
        if(_parent!=null)
        {
            appendWildcardToCollection(_parent, interceptors);            
            appendInterceptor(_parent._interceptors[1], interceptors);
        }
        //appendInterceptor(_interceptors[2], interceptors);
        //appendInterceptor(_interceptors[1], interceptors);
        appendInterceptor(_interceptors[0], interceptors);        
    }
    
    void resourceHandle() throws ServletException, IOException
    {
        RequestContext requestContext = WebContext.getCurrentRequestContext();
        ResourceHandler resourceHandler = _resourceHandlers.get(requestContext.getRequest().getMethod());
        if(resourceHandler==null)
        {
            requestContext.getResponse().sendError(404);
            return;
        }        
        
        InterceptorCollection interceptor = __currentInterceptors.get();
        appendToCollection(interceptor);        
        if(interceptor.getInterceptors().size()==0)
        {
            resourceHandler.handle();
            return;
        }

        boolean success = false;
        try
        {
            success = interceptor.preHandle(requestContext);
        }
        finally
        {            
            if(success)
            {
                try
                {
                    resourceHandler.handle();
                }
                finally
                {
                    interceptor.postHandle(true, requestContext);
                }
            }
            else
                interceptor.postHandle(false, requestContext);
        }
        
        /*int i=3;
        while(i-->0)
        {            
            if(_interceptors[i]!=null)
                _interceptors[i].preHandle(requestContext);
        }
        resourceHandler.handle();
        while(3>++i)
        {            
            if(_interceptors[i]!=null)
                _interceptors[i].postHandle(true, requestContext);
        }*/        
    }    
    
    boolean map(int index, String[] pathInfo, Interceptor interceptor, int wildcard)
    {        
        String id = pathInfo[index++];        
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
        {
            _log.warn("unmapped interceptor: " + interceptor);
            return false;
        }
        
        if(index==pathInfo.length)
        {            
            pathHandler.addInterceptor(interceptor, wildcard);
            return true;
        }
        
        String next = pathInfo[index++];
        if(isPathParameter(next))
        {
            if(pathHandler._parameterHandler==null)
            {
                _log.warn("unmapped interceptor: " + interceptor);
                return false;
            }
            
            if(index==pathInfo.length)
            {               
                pathHandler._parameterHandler.addInterceptor(interceptor, wildcard);
                return true;
            }            
            return pathHandler._parameterHandler.map(index, pathInfo, interceptor, wildcard);
        }
        
        return pathHandler.map(--index, pathInfo, interceptor, wildcard);
    }
    
    PathHandler map(int index, String[] pathInfo, ResourceHandler resourceHandler)
    {        
        String id = pathInfo[index++];
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
            pathHandler = new PathHandler(id, this);     
        
        if(index==pathInfo.length)
        {            
            pathHandler.addResourceHandler(resourceHandler);
            return pathHandler;
        }
        
        String next = pathInfo[index++];
        if(isPathParameter(next))
        {
            if(pathHandler._parameterHandler==null)
                pathHandler._parameterHandler = new PathHandler("$").setOtherParent(pathHandler);
            
            if(index==pathInfo.length)
            {
                pathHandler._parameterHandler.addResourceHandler(resourceHandler);
                return pathHandler._parameterHandler;
            }            
            
            return pathHandler._parameterHandler.map(index, pathInfo, resourceHandler);
        }
        
        return pathHandler.map(--index, pathInfo, resourceHandler);
    }
    
    public boolean map(String path, Interceptor interceptor)
    {
        int wildcard = 0;
        int lastSlashIdx = path.lastIndexOf('/');
        if(lastSlashIdx==path.length()-1)
        {
            if(path.length()==1)
            {
                addInterceptor(interceptor, 0);
                return true;
            }
            
            path = path.substring(0, path.length()-1);
            lastSlashIdx = path.lastIndexOf('/');
        }
        
        int asteriskIdx = path.lastIndexOf('*');
        if(asteriskIdx!=-1)
        {
            if(asteriskIdx<lastSlashIdx)
                throw new IllegalArgumentException("illegal path: " + path);
            
            int len = asteriskIdx - lastSlashIdx;
            if(len>2 || asteriskIdx+1!=path.length())
                throw new IllegalArgumentException("wild card path must end with /* or /**");
         
            if(len==2)
            {
                if(path.charAt(asteriskIdx-1)!='*')
                    throw new IllegalArgumentException("wild card path must end with /* or /**");
                wildcard = 2;
            }
            else
                wildcard = 1;
            
            path = path.substring(0, lastSlashIdx);
        }
        if(path.charAt(0)=='/')
            path = path.substring(1);

        return map(0, Delim.SLASH.split(path), interceptor, wildcard);        
    }
    
    public PathHandler map(String path, ResourceHandler resourceHandler)
    {
        if(path.length()==0 || path.indexOf('*')!=-1)
            throw new IllegalStateException("invalid location/uri");
        
        int lastSlashIdx = path.lastIndexOf('/');
        if(lastSlashIdx==path.length()-1)
        {
            if(path.length()==1)
            {
                addResourceHandler(resourceHandler);
                return this;
            }
            path = path.substring(0, path.length()-1);
        }
        
        if(path.charAt(0)=='/')
            path = path.substring(1);        
     
        return map(0, Delim.SLASH.split(path), resourceHandler);
    }
    
    public void handle(int index, String[] pathInfo) throws ServletException, IOException
    {
        try
        {
            handle(this, 0, pathInfo);
        }
        finally
        {
            __currentInterceptors.get().getInterceptors().clear();
        }

        /*String id = pathInfo[index++];
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
            pathHandler.handle(index, pathInfo);*/
    }
    
    static void handle(PathHandler parentHandler, int index, String[] pathInfo) 
    throws ServletException, IOException
    {
        String id = pathInfo[index++];
        PathHandler pathHandler = parentHandler._pathHandlers.get(id);
        if(pathHandler==null)
        {
            if(parentHandler._parameterHandler==null)
                WebContext.getCurrentRequestContext().getResponse().sendError(404);
            else if(index==pathInfo.length)
                parentHandler._parameterHandler.resourceHandle();
            else
                handle(parentHandler._parameterHandler, index, pathInfo);            
        }
        else if(index==pathInfo.length)
            pathHandler.resourceHandle();
        else
            handle(pathHandler, index, pathInfo);
    }

}
