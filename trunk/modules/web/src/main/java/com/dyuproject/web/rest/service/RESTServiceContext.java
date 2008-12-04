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
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.codehaus.jra.HttpResource;

import com.dyuproject.util.Delim;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Dec 4, 2008
 */

public class RESTServiceContext extends WebContext
{
    
    private PathHandler _pathHandler = new PathHandler("/");
    private List<Service> _services = new ArrayList<Service>();
    
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

    protected void init()
    {
        for(Service s : _services)
            initService(s);        
    }
    
    void initService(Service service)
    {
        service.init(this);
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
                            throw new IllegalStateException("multiple declared HttpMethod annotations");
                    }                   
                    else
                        httpMethod = method;                    
                }
            }

            if(location!=null && httpMethod!=null)
            {
                if(m.getParameterTypes().length!=0)
                    throw new IllegalStateException("annotated method should have no args.");
                
                location = location.trim();                
                if(location.length()==0)
                    throw new IllegalStateException("invalid location/uri");
                
                if(location.endsWith("/"))
                    location = location.substring(0, location.length()-1);
                
                if(location.startsWith("/"))
                    location = location.substring(1);
                
                ResourceHandler handler = new ResourceHandler(service, m, httpMethod);
                if(location.length()==0)
                    _pathHandler.addDefaultHandler(handler);
                else
                    _pathHandler.addPathHandler(0, Delim.SLASH.split(location), handler);                
            }
        }
    }
    
    protected void destroy()
    {
        for(Service s : _services)
            s.destroy(this);        
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
