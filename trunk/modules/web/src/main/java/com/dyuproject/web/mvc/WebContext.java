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

import com.dyuproject.util.Delim;

/**
 * @author David Yu
 * @created May 11, 2008
 */

public class WebContext
{
    
    public static final String DISPATCH_ATTR = "com.dyuproject.web.dispatch";
    public static final String DISPATCH_SUFFIX_ATTR = "com.dyuproject.web.dispatch.suffix";
    public static final String PATH_SUFFIX_ATTR = "pathSuffix";
    public static final String DEFAULT_MIMES_LOCATION = "/WEB-INF/mimes.properties";
    
    private boolean _initialized = false, _initializing = false;
    private ServletContext _servletContext;
    
    private DefaultDispatcher _defaultDispatcher = new DefaultDispatcher();
    private JSPDispatcher _jspDispatcher = new JSPDispatcher();
    private Map<String,ViewDispatcher> _viewDispatchers = new HashMap<String,ViewDispatcher>();
    
    private Controller _defaultController;
    private Map<String, Controller> _controllers = new HashMap<String,Controller>();
    
    private Map<String,Object> _attributes = new HashMap<String,Object>();
    
    private Properties _mimes;
    
    public WebContext()
    {
        
    }
    
    void init(ServletContext servletContext)
    {
        if(_initialized || servletContext==null)
            return;
        
        _initialized = true;
        _initializing = true;
        _servletContext = servletContext;
        
        _defaultDispatcher.init(this);
        _jspDispatcher.init(this);
        
        for(ViewDispatcher vd : _viewDispatchers.values())
            vd.init(this);
        
        if(_viewDispatchers.get("default")==null)
            _viewDispatchers.put("default", _defaultDispatcher);
        if(_viewDispatchers.get("jsp")==null)
            _viewDispatchers.put("jsp", _jspDispatcher);
        
        if(_defaultController==null)
            throw new IllegalStateException("default controller must be specified");        
        _defaultController.init(this);
        
        for(Controller c : _controllers.values())
            c.init(this);
        
        if(_mimes==null)
        {
            try
            {
                URL resource = _servletContext.getResource(DEFAULT_MIMES_LOCATION);
                if(resource!=null)
                    setMimes(resource);
            }
            catch(Exception e)
            {
                //ignore
            }
            if(_mimes==null)
                _mimes = new Properties();
        }
        _initializing = false;
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
    
    public void setMimes(Properties mimes)
    {
        if(_mimes!=null)
            throw new IllegalStateException("mimes already set");
        
        _mimes = mimes;
    }
    
    public void setMimes(String resource)
    {
        setMimes(_servletContext.getResourceAsStream(resource));
    }
    
    public void setMimes(InputStream stream)
    {
        if(_mimes!=null)
            throw new IllegalStateException("mimes already set");
        
        _mimes = new Properties();
        try
        {            
            _mimes.load(stream);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setMimes(File location) throws IOException
    {
        setMimes(new FileInputStream(location));
    }
    
    public void setMimes(URL location) throws IOException
    {
        setMimes(location.openStream());
    }
    
    public boolean isMimeSupported(String mime)
    {
        return _mimes.containsKey(mime);
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
    
    /* ================================================================================= */
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {        
        String pathInfo = request.getPathInfo();        
        
        if(request.getAttribute(DISPATCH_ATTR)!=null)
        {
            System.err.println("DISPATCHED");
            if("jsp".equals(request.getAttribute(DISPATCH_SUFFIX_ATTR)))
                _jspDispatcher._jsp.include(request, response);
            else
                _defaultDispatcher._default.forward(request, response);
            return;
        }       

        int last = pathInfo.length()-1;
        // root context /
        if(last<1)
        {
            _defaultController.handle(null, request, response);            
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
                if(!_mimes.containsKey(mime))
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
        
        handle(0, tokens, mime, request, response); 
    }
    
    private void handle(int sub, String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        Controller c = _controllers.get(pathInfo[sub]);
        if(c==null)
        {
            System.err.println("No controller matched on: " + pathInfo[sub]);
            response.sendError(404);
            return;
        }            
        if(sub+1==pathInfo.length)
        {            
            c.handle(mime, request, response);
            return;
        }
        String verbOrIdAttr = c.getIdentifierAttribute();
        if(verbOrIdAttr!=null)
            request.setAttribute(verbOrIdAttr, pathInfo[sub+1]);
        if(sub+2==pathInfo.length)
        {
            c.handle(mime, request, response);
            return;
        }
        handle(sub+2, pathInfo, mime, request, response);
    }
    
    
    public void handle(String[] pathInfo, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        handle(0, pathInfo, mime, request, response);
    }

}
