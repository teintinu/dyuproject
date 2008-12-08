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

package com.dyuproject.web.rest.mvc;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.WebContext;

/**
 * The application context using REST Controllers 
 * 
 * @author David Yu
 * @created Dec 4, 2008
 */
@Deprecated
public class RESTControllerContext extends WebContext
{    
    private static final Log _log = LogFactory.getLog(RESTControllerContext.class);    
    
    private Controller _defaultController;
    private Map<String, Controller> _controllers = new HashMap<String,Controller>();
    
    public RESTControllerContext()
    {
        
    }
    
    protected void preConfigure(ServletConfig config) throws Exception
    {
        String defaultControllerParam = config.getInitParameter("defaultController");
        if(defaultControllerParam!=null)
            setDefaultController((Controller)newObjectInstance(defaultControllerParam));
        
        String controllersParam = config.getInitParameter("controllers");
        if(controllersParam!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(controllersParam, ",;");
            while(tokenizer.hasMoreTokens())
                addController((Controller)newObjectInstance(tokenizer.nextToken().trim()));
        }
    }
    
    protected void init()
    {
        try
        {
            URL resource = getServletContext().getResource(DEFAULT_ENV_LOCATION);
            if(resource!=null)
            {
                setEnv(resource.openStream());
                _log.info("loaded: " + DEFAULT_ENV_LOCATION);
            }
        }
        catch(Exception e)
        {                
            //ignore
        }
                
        if(_defaultController==null)
            throw new IllegalStateException("default controller must be specified");      
        
        _defaultController.init(this);        
        for(Controller c : _controllers.values())
            c.init(this);

        _log.info(1+_controllers.size() + " controllers initialized.");
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

    protected void handleRoot(RequestContext requestContext)
            throws ServletException, IOException
    {
        _defaultController.handle(requestContext.getMime(), requestContext.getRequest(), 
                requestContext.getResponse());        
    }

    protected void handlePath(RequestContext requestContext)
            throws ServletException, IOException
    {        
        handle(0, requestContext.getPathInfo(), requestContext.getMime(), 
                requestContext.getRequest(), requestContext.getResponse());
    }
    
    private void handle(int idx, String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        Controller c = _controllers.get(pathInfo[idx]);
        if(c==null)
        {
            _log.warn("No controller mapped on: " + pathInfo[idx]);
            response.sendError(404);
            return;
        }            
        if(idx+1==pathInfo.length)
        {            
            handle(c, mime, request, response);
            return;
        }
        String verbOrIdAttr = c.getIdentifierAttribute();
        if(verbOrIdAttr==null)
        {
            _log.warn(pathInfo[idx+1] + " is not a verb nor an id of " + pathInfo[idx]);
            response.sendError(404);
            return;
        }
        // support wildcard
        if(verbOrIdAttr.charAt(0)=='*')
        {
            request.setAttribute(PATHINFO_ARRAY_ATTR, pathInfo);
            request.setAttribute(PATHINFO_INDEX_ATTR, new Integer(idx));
            handle(c, mime, request, response);
            return;
        }
        
        request.setAttribute(verbOrIdAttr, pathInfo[idx+1]);
        if(idx+2==pathInfo.length)
        {
            handle(c, mime, request, response);
            return;
        }
        handle(idx+2, pathInfo, mime, request, response);
    }
    
    public void handle(String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        handle(0, pathInfo, mime, request, response);
    }
    
    public static void handle(Controller controller, String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        Interceptor interceptor = controller.getInterceptor();
        if(interceptor==null)
        {
            controller.handle(mime, request, response);
            return;
        }
        boolean success = false;
        try
        {
            success = interceptor.preHandle(getCurrentRequestContext());
        }
        finally
        {            
            if(success)
            {
                try
                {
                    controller.handle(mime, request, response);
                }
                finally
                {
                    interceptor.postHandle(true, getCurrentRequestContext());
                }
            }
            else
                interceptor.postHandle(false, getCurrentRequestContext());
        }       
    }
    
    protected void destroy()
    {
        int destroyed = 0;
        if(_defaultController!=null)
        {
            _defaultController.destroy(this);
            destroyed++;
        }                
        for(Controller c : _controllers.values())
            c.destroy(this);
        
        _log.info(destroyed+_controllers.size() + " controllers destroyed.");
    }
    
}
