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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * Wraps the methods of a service to be invoked for request handling.
 * 
 * @author David Yu
 * @created Dec 3, 2008
 */

public final class AnnotatedMethodResource implements Resource
{    
    
    private static final Object[] __arg = new Object[]{};
    
    private static final Map<Class<?>,String> __httpMethods = new HashMap<Class<?>,String>();
    static
    {
        __httpMethods.put(org.codehaus.jra.Get.class, GET);
        __httpMethods.put(org.codehaus.jra.Post.class, POST);
        __httpMethods.put(org.codehaus.jra.Put.class, PUT);
        __httpMethods.put(org.codehaus.jra.Delete.class, DELETE);        
    }
    
    private static final Logger log = LoggerFactory.getLogger(AnnotatedMethodResource.class);
    
    static String getHttpMethod(Class<?> clazz)
    {
        return __httpMethods.get(clazz);
    }
    
    private final Service _service;
    private final Method _serviceMethod;
    private final String _httpMethod;
    private final int _len;
    
    public AnnotatedMethodResource(Service service, Method serviceMethod, String httpMethod)
    {
        _service = service;
        _serviceMethod = serviceMethod;
        _httpMethod = httpMethod;
        _len = serviceMethod.getParameterTypes().length;
        if(!Modifier.isPublic(_serviceMethod.getModifiers()))
            _serviceMethod.setAccessible(true);
    }
    
    public void init(WebContext webContext)
    {
        
    }
    
    public void destroy(WebContext webContext)
    {
        
    }
    
    public Service getService()
    {
        return _service;
    }
    
    public Method getServiceMethod()
    {
        return _serviceMethod;
    }
    
    public String getHttpMethod()
    {
        return _httpMethod;
    }
    
    public void handle(RequestContext rc) throws ServletException, IOException
    {
        try
        {
            _serviceMethod.invoke(_service, _len==0 ? __arg : new Object[]{rc});
        } 
        catch (IllegalArgumentException e)
        {            
            log.warn(e.getMessage(), e);
            rc.getResponse().sendError(404);
        } 
        catch (IllegalAccessException e)
        {            
            log.warn(e.getMessage(), e);
            rc.getResponse().sendError(404);
        } 
        catch (InvocationTargetException e)
        {            
            Throwable cause = e.getCause();
            if(cause instanceof IOException)
                throw (IOException)cause;
            if(cause instanceof ServletException)
                throw (ServletException)cause;
            if(cause instanceof RuntimeException)
                throw (RuntimeException)cause;
            
            log.info(cause.getMessage(), cause);
            rc.getResponse().sendError(500);
        }
    }
    
    public String toString()
    {
        return getService().getClass().getSimpleName() + "." + getServiceMethod().getName() + "()";
    }

}
