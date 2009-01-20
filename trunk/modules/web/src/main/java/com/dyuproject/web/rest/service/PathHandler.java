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
import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.InterceptorCollection;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * Contains all the logic to handle the REST requests.
 * Wraps the Resource and Interceptor.
 * Wildcard interceptor supported.
 * 
 * @author David Yu
 * @created Dec 3, 2008
 */

public class PathHandler extends AbstractLifeCycle
{
    
    public static final String ROOT = "/", PARAM = "$";
    
    private static Log _log = LogFactory.getLog(PathHandler.class);
    
    public static boolean isPathParameter(String id)
    {
        char first = id.charAt(0);
        return first=='$' || first=='{';
    }
    
    private String _id;
    private PathHandler _parent, _parameterHandler;
    private Map<String,PathHandler> _pathHandlers = new HashMap<String,PathHandler>(3);
    private Map<String,Resource> _resources = new HashMap<String,Resource>(3);
    
    // /path/   /path/* and  /path/**
    
    private Interceptor _interceptor;
    private Interceptor[] _mappedInterceptors = new Interceptor[3];
    
    public PathHandler()
    {
        this(ROOT);
    }
    
    PathHandler(String id)
    {
        _id = id;
    }
    
    PathHandler(String id, PathHandler parent)
    {
        this(id);
        _parent = parent;
        _parent.addPathHandler(this);        
    }
    
    PathHandler setParent(PathHandler parent)
    {
        _parent = parent;
        return this;
    }
    
    protected void init()
    {        
        for(PathHandler ph : _pathHandlers.values())
            ph.init(getWebContext());
        
        if(_parameterHandler!=null)
            _parameterHandler.init(getWebContext());
        
        if(_parent!=null)
        {
            loadInterceptors(this, _parent);
            appendInterceptor(this, _parent._mappedInterceptors[1]);
        }
        appendInterceptor(this, _mappedInterceptors[2]);
        appendInterceptor(this, _mappedInterceptors[1]);
        appendInterceptor(this, _mappedInterceptors[0]);
        
        if(_interceptor!=null)
            _interceptor.init(getWebContext());
        
        
    }
    
    protected void destroy()
    {
        for(PathHandler ph : _pathHandlers.values())
            ph.destroy(getWebContext());
        
        if(_parameterHandler!=null)
            _parameterHandler.destroy(getWebContext());
        
        if(_interceptor!=null)
            _interceptor.destroy(getWebContext());
        
        _resources.clear();        
        _pathHandlers.clear();
        _resources = null;
        _pathHandlers = null;
        _parent = null;
        _parameterHandler = null;
        _id = null;
        _mappedInterceptors = null;
        _interceptor = null;
    }
    
    static void loadInterceptors(PathHandler toConfigure, PathHandler pathHandler)
    {
        PathHandler parent = pathHandler._parent;
        if(parent!=null)
            loadInterceptors(toConfigure, parent);
        
        appendInterceptor(toConfigure, pathHandler._mappedInterceptors[2]);  
    }
    
    static void appendInterceptor(PathHandler ph, Interceptor i)
    {
        if(i!=null)
        {
            if(ph._interceptor==null)
                ph._interceptor = i;
            else if(ph._interceptor instanceof InterceptorCollection)
                ((InterceptorCollection)ph._interceptor).addInterceptor(i);
            else
            {
                InterceptorCollection ic = new InterceptorCollection();
                ic.addInterceptor(ph._interceptor);
                ic.addInterceptor(i);
                ph._interceptor = ic;
            }
        }
    }

    public String getId()
    {
        return _id;
    }
    
    void addMappedInterceptor(Interceptor interceptor, int wildcard)
    {
        Interceptor existing = _mappedInterceptors[wildcard];
        if(existing==null)
            _mappedInterceptors[wildcard] = interceptor;
        else if(existing instanceof InterceptorCollection)
            ((InterceptorCollection)existing).addInterceptor(interceptor);
        else
        {
            InterceptorCollection ic = new InterceptorCollection();
            ic.addInterceptor(existing);
            ic.addInterceptor(interceptor);
            _mappedInterceptors[wildcard] = ic;
        }        
    }
    
    void addPathHandler(PathHandler child)
    {
        PathHandler last = _pathHandlers.put(child.getId(), child);
        if(last!=null)
            _log.warn("path overridden: " + last.getId() + " | " + last);
    }
    
    void addResource(Resource resource)
    {
        Resource last = _resources.put(resource.getHttpMethod(), resource);
        if(last!=null)
            _log.warn("resource overridden: " + last.getHttpMethod() + " | " + last);
    }
    
    void resourceHandle(RequestContext rc) throws ServletException, IOException
    {
        Resource resource = _resources.get(rc.getRequest().getMethod());
        if(resource==null)
        {
            rc.getResponse().sendError(405);
            return;
        }        
        
        if(_interceptor==null)
        {
            resource.handle(rc);
            return;
        }

        boolean success = false;
        try
        {
            success = _interceptor.preHandle(rc);
        }
        finally
        {            
            if(success)
            {
                try
                {
                    resource.handle(rc);
                }
                finally
                {
                    _interceptor.postHandle(true, rc);
                }
            }
            else
                _interceptor.postHandle(false, rc);
        }      
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
            pathHandler.addMappedInterceptor(interceptor, wildcard);
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
                pathHandler._parameterHandler.addMappedInterceptor(interceptor, wildcard);
                return true;
            }            
            return pathHandler._parameterHandler.map(index, pathInfo, interceptor, wildcard);
        }
        
        return pathHandler.map(--index, pathInfo, interceptor, wildcard);
    }
    
    PathHandler map(int index, String[] pathInfo, Resource resource)
    {        
        String id = pathInfo[index++];
        PathHandler pathHandler = _pathHandlers.get(id);
        if(pathHandler==null)
            pathHandler = new PathHandler(id, this);     
        
        if(index==pathInfo.length)
        {            
            pathHandler.addResource(resource);
            return pathHandler;
        }
        
        String next = pathInfo[index++];
        if(isPathParameter(next))
        {
            if(pathHandler._parameterHandler==null)
                pathHandler._parameterHandler = new PathHandler(PARAM).setParent(pathHandler);
            
            if(index==pathInfo.length)
            {
                pathHandler._parameterHandler.addResource(resource);
                return pathHandler._parameterHandler;
            }            
            
            return pathHandler._parameterHandler.map(index, pathInfo, resource);
        }
        
        return pathHandler.map(--index, pathInfo, resource);
    }
    
    public boolean map(String path, Interceptor interceptor)
    {
        path = path.trim();
        if(path.length()==0)
            throw new IllegalStateException("invalid path: " + path);
        
        int wildcard = 0;
        int lastSlashIdx = path.lastIndexOf('/');
        if(lastSlashIdx==path.length()-1)
        {
            if(path.length()==1)
            {
                addMappedInterceptor(interceptor, 0);
                return true;
            }
            
            path = path.substring(0, path.length()-1);
            lastSlashIdx = path.lastIndexOf('/');
        }
        
        int asteriskIdx = path.lastIndexOf('*');
        if(asteriskIdx!=-1)
        {
            if(asteriskIdx<lastSlashIdx)
                throw new IllegalStateException("invalid path: " + path);
            
            int len = asteriskIdx - lastSlashIdx;
            if(len>2 || asteriskIdx+1!=path.length())
                throw new IllegalArgumentException("wild card: " + path + " must end with /* or /**");
         
            if(len==2)
            {
                if(path.charAt(asteriskIdx-1)!='*')
                    throw new IllegalArgumentException("wild card: " + path + " must end with /* or /**");
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
    
    public PathHandler map(String path, Resource resource)
    {
        path = path.trim();
        if(path.length()==0 || path.indexOf('*')!=-1)
            throw new IllegalStateException("invalid path: " + path);
        
        int lastSlashIdx = path.lastIndexOf('/');
        if(lastSlashIdx==path.length()-1)
        {
            if(path.length()==1)
            {
                addResource(resource);
                return this;
            }
            path = path.substring(0, path.length()-1);
        }
        
        if(path.charAt(0)=='/')
            path = path.substring(1);        
     
        return map(0, Delim.SLASH.split(path), resource);
    }
    
    public void handle(String[] pathInfo, RequestContext rc) 
    throws ServletException, IOException
    {
        handle(this, 0, pathInfo, rc);
    }
    
    static void handle(PathHandler parentHandler, int index, String[] pathInfo, RequestContext rc) 
    throws ServletException, IOException
    {
        String id = pathInfo[index++];
        PathHandler pathHandler = parentHandler._pathHandlers.get(id);
        if(pathHandler==null)
        {
            if(parentHandler._parameterHandler==null)
                WebContext.getCurrentRequestContext().getResponse().sendError(404);
            else if(index==pathInfo.length)
                parentHandler._parameterHandler.resourceHandle(rc);
            else
                handle(parentHandler._parameterHandler, index, pathInfo, rc);            
        }
        else if(index==pathInfo.length)
            pathHandler.resourceHandle(rc);
        else
            handle(pathHandler, index, pathInfo, rc);
    }

}
