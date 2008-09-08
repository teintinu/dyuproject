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

package com.dyuproject.web.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.util.Delim;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.CookieSessionManager;

/**
 * The REST web application context.
 * 
 * @author David Yu
 * @created May 11, 2008
 */

public class WebContext
{   
    
    public static final String DISPATCH_ATTR = "com.dyuproject.web.dispatch";

    public static final String PATH_SUFFIX_ATTR = "pathSuffix";
    public static final String DEFAULT_MIME_LOCATION = "/WEB-INF/mime.properties";
    public static final String DEFAULT_ENV_LOCATION = "/WEB-INF/env.properties";
    
    public static final String PATHINFO_ARRAY_ATTR = "rest.pathInfo.array";
    public static final String PATHINFO_INDEX_ATTR = "rest.pathInfo.index";    
    
    public static final String SESSION_ENABLED = "session.enabled";
    
    private static final Log log = LogFactory.getLog(WebContext.class);
    
    public static CookieSession getCurrentSession()
    {
        return CookieSessionManager.getCurrentSession();
    }    
    
    private boolean _initialized = false, _initializing = false, _sessionEnabled = false;
    private ServletContext _servletContext;
    
    private DefaultDispatcher _defaultDispatcher = new DefaultDispatcher();
    private JSPDispatcher _jspDispatcher = new JSPDispatcher();
    private Map<String,ViewDispatcher> _viewDispatchers = new HashMap<String,ViewDispatcher>();
    
    private Controller _defaultController;
    private Map<String, Controller> _controllers = new HashMap<String,Controller>();
    
    private Map<String,Object> _attributes = new HashMap<String,Object>();
    
    private Properties _mime, _env = new Properties();
    private CookieSessionManager _cookieSessionManager;
    
    public WebContext()
    {
        
    }
    
    public CookieSession getSession(HttpServletRequest request, boolean create)
    {
        return _sessionEnabled ? _cookieSessionManager.getSession(request, create) : null;
    }
    
    public CookieSession getSession(HttpServletRequest request)
    {
        return _sessionEnabled ? _cookieSessionManager.getSession(request, false) : null;
    }
    
    public boolean isSessionEnabled()
    {
        return _sessionEnabled;
    }
    
    void init(ServletContext servletContext)
    {
        if(_initialized || servletContext==null)
            return;        

        _servletContext = servletContext;
        
        _defaultDispatcher.init(this);
        _jspDispatcher.init(this);
        
        try
        {
            URL resource = _servletContext.getResource(DEFAULT_ENV_LOCATION);
            if(resource!=null)
            {
                setEnv(resource.openStream());
                log.info("loaded: " + DEFAULT_ENV_LOCATION);
            }
        }
        catch(Exception e)
        {                
            //ignore
        }
                
        if(_defaultController==null)
            throw new IllegalStateException("default controller must be specified");      
        _defaultController.init(this);
        //default controller can set objects during intialization
        _initialized = true;
        _initializing = true;        
        
        for(ViewDispatcher vd : _viewDispatchers.values())
            vd.init(this);
        
        if(_viewDispatchers.get("default")==null)
            _viewDispatchers.put("default", _defaultDispatcher);
        if(_viewDispatchers.get("jsp")==null)
            _viewDispatchers.put("jsp", _jspDispatcher);
        
        for(Controller c : _controllers.values())
            c.init(this);
        
        if(_mime==null)
        {
            try
            {
                URL resource = _servletContext.getResource(DEFAULT_MIME_LOCATION);
                if(resource!=null)
                {
                    setMime(resource.openStream());
                    log.info("loaded: " + DEFAULT_MIME_LOCATION);
                }
            }
            catch(Exception e)
            {                
                //ignore
            }
            if(_mime==null)
            {
                log.warn("no mime.properties found");
                _mime = new Properties();
            }
        }
        _sessionEnabled = Boolean.parseBoolean(_env.getProperty(SESSION_ENABLED, "false"));
        if(_sessionEnabled)
        {
            _cookieSessionManager = new CookieSessionManager();
            _cookieSessionManager.init(_env);
        }
        
        _initializing = false;
        log.info("initialized.");
    }
    
    public ServletContext getServletContext()
    {
        return _servletContext;
    }
    
    public JSPDispatcher getJSPDispatcher()
    {
        return _jspDispatcher;
    }
    
    public DefaultDispatcher getDefaultDispatcher()
    {
        return _defaultDispatcher;
    }
    
    public void setViewDispatchers(Map<String,ViewDispatcher> dispatchers)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _viewDispatchers.putAll(dispatchers);
    }
    
    public void addViewDispatcher(String mime, ViewDispatcher dispatcher)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _viewDispatchers.put(mime, dispatcher);
    }    
    
    public ViewDispatcher getViewDispatcher(String name)
    {
        return _viewDispatchers.get(name);
    }
    
    public void setDefaultController(Controller defaultController)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _defaultController = defaultController;
    }
    
    public Controller getDefaultController()
    {
        return _defaultController;
    }
    
    public void setControllers(Controller[] controllers)
    {
        if(_initialized)
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
        if(_initialized)
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
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        if(c.getIdentifier()==null)
            throw new IllegalStateException("Controller's resourceName must not be null");
        _controllers.put(c.getIdentifier(), c);
    }
    
    public Controller getController(String resourceName)
    {
        return _controllers.get(resourceName);
    }
    
    public void setMime(Properties mimes)
    {
        if(_mime!=null)
            throw new IllegalStateException("mime already set");
        
        _mime = mimes;
    }
    
    public void setMime(InputStream stream)
    {
        if(_mime!=null)
            throw new IllegalStateException("mime already set");
        
        _mime = new Properties();
        try
        {            
            _mime.load(stream);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setMime(File location) throws IOException
    {
        setMime(new FileInputStream(location));
    }
    
    public void setMime(URL location) throws IOException
    {
        setMime(location.openStream());
    }    
    
    public boolean isMimeSupported(String mime)
    {
        return _mime.containsKey(mime);
    }
    
    public void setEnv(Properties env)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _env.putAll(env);        
    }
    
    public void setEnv(InputStream stream)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        Properties env = new Properties();
        try
        {            
            env.load(stream);
            _env.putAll(env);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setEnv(File location) throws IOException
    {
        setEnv(new FileInputStream(location));
    }
    
    public void setEnv(URL location) throws IOException
    {
        setEnv(location.openStream());
    }
    
    public void setAttributes(Map<String,Object> attributes)
    {
        if(_initialized && !_initializing)
            throw new IllegalStateException("already started");
        
        _attributes.putAll(attributes);
    }
    
    public void addAttribute(String name, Object value)
    {
        if(_initialized && !_initializing)
            throw new IllegalStateException("already started");
        
        _attributes.put(name, value);
    }
    
    public Object getAttribute(String name)
    {
        return _attributes.get(name);
    }
    
    public String getProperty(String name)
    {
        return _env.getProperty(name);
    }
    
    /* ================================================================================= */
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {        
        Object dispatched = request.getAttribute(DISPATCH_ATTR);
        if(dispatched!=null)
        {            
            if(JSPDispatcher.JSP.equals(dispatched))
                _jspDispatcher._jsp.include(request, response);
            else
                _defaultDispatcher._default.forward(request, response);
            return;
        }
        
        String pathInfo = request.getPathInfo();
        int last = pathInfo.length()-1;
        // root context /
        if(last<1)
        {
            try
            {
                handle(_defaultController, null, request, response);
            }
            finally
            {
                if(_sessionEnabled)
                    _cookieSessionManager.updateIfNecessary(response);
            }                 
            return;
        }
        
        String mime = null;        
        String lastWord = null;        
        int dot = -1;
        int lastSlash = pathInfo.lastIndexOf('/');
        // ends with /
        if(lastSlash==last)
        {
            /*if(pathInfo.indexOf('.')!=-1)
            {
                response.sendError(404);
                return;
            }*/
            pathInfo = pathInfo.substring(1, last);
        }
        else
        {           
            lastWord = pathInfo.substring(lastSlash+1);            
            dot = lastWord.lastIndexOf('.');            
            if(dot!=-1)
            {
                mime = lastWord.substring(dot+1);                
                if(!_mime.containsKey(mime))
                {
                    _defaultDispatcher._default.forward(request, response);
                    return;
                }
                request.setAttribute(PATH_SUFFIX_ATTR, "."+mime);
                lastWord = lastWord.substring(0, dot);
            }
            else
            {
                lastWord = null;
                /* redirect /foo to /foo/ */
                //response.sendRedirect(request.getRequestURL().append('/').toString());
                //return;
            }
            pathInfo = pathInfo.substring(1);
        }
        String[] tokens = Delim.SLASH.split(pathInfo);
        /*if(dot!=-1 && tokens.length%2!=0)
        {
            response.sendError(404);
            return;
        }*/
        if(lastWord!=null)
            tokens[tokens.length-1] = lastWord;
        else if(tokens[tokens.length-1].indexOf('.')!=-1)
        {
            response.sendError(404);
            return;
        }            
        
        try
        {
            handle(0, tokens, mime, request, response); 
        }
        finally
        {
            if(_sessionEnabled)
                _cookieSessionManager.updateIfNecessary(response);
        }        
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
            handle(c, mime, request, response);
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
            request.setAttribute(PATHINFO_ARRAY_ATTR, pathInfo);
            request.setAttribute(PATHINFO_INDEX_ATTR, new Integer(sub));
            handle(c, mime, request, response);
            return;
        }
        
        request.setAttribute(verbOrIdAttr, pathInfo[sub+1]);
        if(sub+2==pathInfo.length)
        {
            handle(c, mime, request, response);
            return;
        }
        handle(sub+2, pathInfo, mime, request, response);
    }
    
    
    public void handle(String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        handle(0, pathInfo, mime, request, response);
    }
    
    public static void handle(Controller controller, String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        Filter filter = controller.getFilter();
        if(filter==null)
        {
            controller.handle(mime, request, response);
            return;
        }
        boolean success = false;
        try
        {
            success = filter.preHandle(mime, request, response);
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
                    filter.postHandle(true, mime, request, response);
                }
            }
            else
                filter.postHandle(false, mime, request, response);
        }       
    }
    
    void destroy()
    {
        for(Controller c : _controllers.values())
            c.destroy(this);
    }

}
