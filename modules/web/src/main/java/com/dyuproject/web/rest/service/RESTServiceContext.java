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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jra.HttpResource;

import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Dec 4, 2008
 */

public class RESTServiceContext extends WebContext
{
    
    private static Log _log = LogFactory.getLog(RESTServiceContext.class);
    
    private PathHandler _pathHandler = new PathHandler("/");
    private List<Service> _services = new ArrayList<Service>();
    private Map<String,Interceptor> _interceptors = new HashMap<String,Interceptor>();
    
    public void addService(Service service)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _services.add(service);
    }
    
    public void setServices(Service[] services)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        for(Service s : services)
            _services.add(s);
    }
    
    public void setServices(List<Service> services)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        for(Service s : services)
            _services.add(s);        
    }
    
    public void addInterceptor(String path, Interceptor interceptor)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _interceptors.put(path, interceptor);        
    }
    
    public void setInterceptors(Map<String,Interceptor> interceptors)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _interceptors.putAll(interceptors);
    }

    protected void init()
    {
        for(Service s : _services)
            initService(s);
        
        for(Map.Entry<String, Interceptor> entry : _interceptors.entrySet())
            initInterceptor(entry.getKey(), entry.getValue());
    }    
    
    protected void destroy()
    {
        for(Service s : _services)
            s.destroy(this);        
        
        for(Interceptor i : _interceptors.values())
            i.destroy(this);
        
        _services.clear();
        _interceptors.clear();
    }
    
    void initInterceptor(String path, Interceptor interceptor)
    {
        _pathHandler.map(path.trim(), interceptor);
        interceptor.init(this);
    }
    
    void initService(Service service)
    {        
        Method[] methods = service.getClass().getDeclaredMethods();
        for(Method m : methods)
        {
            String location = null;
            String httpMethod = null;
            Annotation[] annotations = m.getDeclaredAnnotations();
            if(annotations!=null)
            {                
                for(Annotation a : annotations)
                {
                    if(location==null && a instanceof HttpResource)
                    {
                        location = ((HttpResource)a).location();                        
                        continue;
                    }
                    String method = ResourceHandler.getHttpMethod(a.annotationType());
                    if(httpMethod!=null)
                    {
                        if(method!=null)
                            throw new IllegalStateException("multiple declared Http method annotations");
                    }                   
                    else
                        httpMethod = method;                    
                }
            }
            
            if(location==null)
                continue;
            
            if(httpMethod==null)
            {
                _log.warn(location + " not mapped.  Http method annotation is required");
                continue;
            }
            
            if(m.getParameterTypes().length!=0)
            {
                _log.warn(location + " not mapped.  Annotated method should have no args.");
                continue;
            }

            if(_pathHandler.map(location.trim(), new ResourceHandler(service, m, httpMethod))==null)
                _log.warn(location + " not mapped.");
        }
        service.init(this);
    }
    
    protected void preConfigure(ServletConfig config) throws Exception
    {
        String servicesParam = config.getInitParameter("services");
        if(servicesParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(servicesParam, ",;");
            while(tokenizer.hasMoreTokens())
                addService((Service)newObjectInstance(tokenizer.nextToken().trim()));
        }
        
        String interceptorsParam = config.getInitParameter("interceptors");
        if(interceptorsParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(interceptorsParam, ",;");
            while(tokenizer.hasMoreTokens())
            {
                String next = tokenizer.nextToken().trim();
                int idx = next.indexOf('@');
                if(idx==-1)
                {
                    _log.warn("invalid interceptor mapping: " + next);
                    continue;
                }
                String interceptorClass = next.substring(0, idx);
                String path = next.substring(idx+1);
                addInterceptor(path, (Interceptor)newObjectInstance(interceptorClass));
            }
        }
    }

    protected void handleRoot(RequestContext requestContext)
            throws ServletException, IOException
    {
        _pathHandler.resourceHandle();
    }

    protected void handlePath(RequestContext requestContext)
            throws ServletException, IOException
    {
        _pathHandler.handle(0, requestContext.getPathInfo());        
    }

}
