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
import java.util.HashMap;
import java.util.Map;

import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Dec 3, 2008
 */

public class ResourceHandler
{
    
    public static final String GET = "GET", POST = "POST", PUT = "PUT", DELETE = "DELETE";
    
    private static final Map<Class<?>,String> __httpMethods = new HashMap<Class<?>,String>();
    static
    {
        __httpMethods.put(org.codehaus.jra.Get.class, GET);
        __httpMethods.put(org.codehaus.jra.Post.class, POST);
        __httpMethods.put(org.codehaus.jra.Put.class, PUT);
        __httpMethods.put(org.codehaus.jra.Delete.class, DELETE);        
    }
    
    static String getHttpMethod(Class<?> clazz)
    {
        return __httpMethods.get(clazz);
    }
    
    private Service _service;
    private Method _serviceMethod;
    private String _httpMethod;
    
    public ResourceHandler(Service service, Method serviceMethod, String httpMethod)
    {
        _service = service;
        _serviceMethod = serviceMethod;
        _httpMethod = httpMethod;
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
    
    public void handle() throws IOException
    {
        try
        {
            _serviceMethod.invoke(_service, new Object[]{});
        } 
        catch (IllegalArgumentException e)
        {            
            e.printStackTrace();
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
        } 
        catch (IllegalAccessException e)
        {            
            e.printStackTrace();
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
        } 
        catch (InvocationTargetException e)
        {            
            e.printStackTrace();
            WebContext.getCurrentRequestContext().getResponse().sendError(404);
        }        
    }
    
    public String toString()
    {
        return getService().getClass().getSimpleName() + "." + getServiceMethod().getName() + "()";
    }

}
