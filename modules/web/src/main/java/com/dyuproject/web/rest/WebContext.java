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

package com.dyuproject.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.Delim;
import com.dyuproject.web.CookieSession;
import com.dyuproject.web.CookieSessionManager;

/**
 * The REST web application context.
 * 
 * @author David Yu
 * @created May 11, 2008
 */

public abstract class WebContext
{    
    public static final String DISPATCH_ATTR = "com.dyuproject.web.dispatch";

    public static final String PATH_SUFFIX_ATTR = "pathSuffix";
    public static final String DEFAULT_MIME_LOCATION = "/WEB-INF/mime.properties";
    public static final String DEFAULT_ENV_LOCATION = "/WEB-INF/env.properties"; 
    
    public static final String SESSION_ENABLED = "session.enabled";
    
    public static final String PATHINFO_ARRAY_ATTR = "rest.pathInfo.array";
    public static final String PATHINFO_INDEX_ATTR = "rest.pathInfo.index";
    
    protected static final String CONSUMER_PROPERTIES_CACHE = "consumer.properties.cache";
    
    private static final Logger log = LoggerFactory.getLogger(WebContext.class);   
    
    private boolean _initialized, _destroyed, _sessionEnabled;
    private ServletContext _servletContext;
    
    private final DefaultDispatcher _defaultDispatcher = new DefaultDispatcher();
    private final JSPDispatcher _jspDispatcher = new JSPDispatcher();
    private final Map<String,ViewDispatcher> _viewDispatchers = new HashMap<String,ViewDispatcher>();
    
    private final Map<String,Object> _attributes = new HashMap<String,Object>();
    
    private final Properties _mime = new Properties();
    private final Properties _env = new Properties();
    private CookieSessionManager _cookieSessionManager;
    
    private static final RequestContext.Local __currentContext = new RequestContext.Local();    
    public static RequestContext getCurrentRequestContext()
    {
        return __currentContext.get();
    }
    
    public RequestContext getRequestContext()
    {
        return getCurrentRequestContext();
    }
    
    public static CookieSession getCurrentSession()
    {
        return CookieSessionManager.getCurrentSession();
    } 
    
    public WebContext()
    {
        
    }
    
    public boolean isInitialized()
    {
        return _initialized;
    }
    
    public CookieSession getSession(HttpServletRequest request, boolean create)
    {
        return _sessionEnabled ? _cookieSessionManager.getSession(request, create) : null;
    }
    
    public CookieSession getSession(HttpServletRequest request)
    {
        return _sessionEnabled ? _cookieSessionManager.getSession(request, false) : null;
    }
    
    public boolean persistSession(CookieSession session, HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {     
        return _sessionEnabled && _cookieSessionManager.persistSession(session, request, response);
    }

    public boolean invalidateSession(HttpServletResponse response) throws IOException
    {
        return _sessionEnabled && _cookieSessionManager.invalidateSession(response);
    }
    
    public boolean isSessionEnabled()
    {
        return _sessionEnabled;
    }    
    
    void destroy(ServletContext servletContext)
    {
        if(_destroyed || !_initialized)
            return;
        
        _destroyed = true;
        destroy();
        log.info("destroyed.");
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
        
        try
        {
            URL resource = _servletContext.getResource(DEFAULT_MIME_LOCATION);
            if(resource!=null)
            {
                setMime(resource.openStream());
                log.info("loaded: " + DEFAULT_MIME_LOCATION);
            }
            else
                log.warn("no mime.properties found");
        }
        catch(Exception e)
        {                
            //ignore
        }
        
        _sessionEnabled = Boolean.parseBoolean(_env.getProperty(SESSION_ENABLED, "false"));
        if(_sessionEnabled)
        {
            _cookieSessionManager = new CookieSessionManager();
            _cookieSessionManager.init(_env);
        }
        
        _viewDispatchers.put("default", _defaultDispatcher);
        _viewDispatchers.put("jsp", _jspDispatcher);
        
        init();
        
        for(ViewDispatcher vd : _viewDispatchers.values())
            vd.init(this);                    
        
        _initialized = true;        
        
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
    
    public void setViewDispatcher(String name, ViewDispatcher dispatcher)
    {
        addViewDispatcher(name, dispatcher);
    }
    
    public void addViewDispatcher(String name, ViewDispatcher dispatcher)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _viewDispatchers.put(name, dispatcher);
    }    
    
    public ViewDispatcher getViewDispatcher(String name)
    {
        return _viewDispatchers.get(name);
    }    
    
    public void setMime(Properties mimes)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
        _mime.putAll(mimes);
    }
    
    public void setMime(InputStream stream)
    {
        if(_initialized)
            throw new IllegalStateException("already initialized");
        
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
        if(_initialized)
            throw new IllegalStateException("already started");
        
        _attributes.putAll(attributes);
    }
    
    public void setAttribute(String name, Object value)
    {
        addAttribute(name, value);
    }
    
    public void addAttribute(String name, Object value)
    {
        if(_initialized)
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
    
    public static Object newObjectInstance(String className) throws Exception
    {
        return ClassLoaderUtil.newInstance(className, WebContext.class);
    }
    
    public static URL getResource(String resource)
    {
        return ClassLoaderUtil.getResource(resource, WebContext.class);
    }
    
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
        
        String uri = request.getPathInfo();
        int last = uri==null ? 0 : uri.length()-1;
        // root context /
        if(last<1)
        {
            RequestContext requestContext = getCurrentRequestContext();                        
            try
            {
                handleRoot(requestContext.init(request, response, null, null));
            }
            catch(IllegalArgumentException e)
            {                
                if(!response.isCommitted())
                {
                    log.debug(e.getMessage(), e);
                    response.sendError(404);
                }
                else
                    log.warn(e.getMessage(), e);
            }
            catch(RuntimeException e)
            {
                log.info(e.getMessage(), e);
                if(!response.isCommitted())
                    response.sendError(500);
            }
            finally
            {
                requestContext.clear();
                if(isSessionEnabled())
                    _cookieSessionManager.postHandle(request, response);
            }                 
            return;
        }
        
        String mime = null;        
        String lastWord = null;        
        int dot = -1;
        int lastSlash = uri.lastIndexOf('/');
        // ends with /
        if(lastSlash==last)
        {
            /*if(pathInfo.indexOf('.')!=-1)
            {
                response.sendError(404);
                return;
            }*/
            uri = uri.substring(1, last);
        }
        else
        {           
            lastWord = uri.substring(lastSlash+1);            
            dot = lastWord.lastIndexOf('.');            
            if(dot!=-1)
            {
                mime = lastWord.substring(dot+1);                
                if(!isMimeSupported(mime))
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
            uri = uri.substring(1);
        }
        String[] pathInfo = Delim.SLASH.split(uri);
        /*if(dot!=-1 && tokens.length%2!=0)
        {
            response.sendError(404);
            return;
        }*/
        if(lastWord!=null)
            pathInfo[pathInfo.length-1] = lastWord;
        else if(pathInfo[pathInfo.length-1].indexOf('.')!=-1)
        {
            response.sendError(404);
            return;
        }            
        RequestContext requestContext = getCurrentRequestContext();
        try
        {
            handlePath(requestContext.init(request, response, pathInfo, mime));            
        }
        catch(IllegalArgumentException e)
        {                
            if(!response.isCommitted())
            {
                log.debug(e.getMessage(), e);
                response.sendError(404);
            }
            else
                log.warn(e.getMessage(), e);
        }
        catch(RuntimeException e)
        {
            log.info(e.getMessage(), e);
            if(!response.isCommitted())
                response.sendError(500);
        }
        finally
        {
            requestContext.clear();
            if(isSessionEnabled())
                _cookieSessionManager.postHandle(request, response);
        }        
    }
    
    /* ================================================================================= */
    
    protected abstract void preConfigure(ServletConfig config) throws Exception;
    protected abstract void init();    
    protected abstract void destroy();
    
    protected abstract void handleRoot(RequestContext requestContext) 
    throws ServletException, IOException;  
    
    protected abstract void handlePath(RequestContext requestContext) 
    throws ServletException, IOException;

}
