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

package com.dyuproject.web.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author David Yu
 */

public class WebServiceContext 
{
    
    public static final String RPC = "RPC";
    public static final String REST = "REST";    
    
    private ServletContext _servletContext;
    private WebServiceFilter _filter;
    private Map<String,WebServiceHandler> _handlers = new HashMap<String,WebServiceHandler>();
    private Map<String,Generator> _generators = new HashMap<String,Generator>();
    private List<WebServiceProvider> _providers = new ArrayList<WebServiceProvider>();
    
    private Generator _defaultGenerator;
    private boolean _allowMethodOverride = false;
    
    public final void init() throws ServletException
    {
        if(_defaultGenerator==null)
            throw new ServletException("defaultGenerator not set.");
        for(WebServiceProvider wsp : _providers)
            wsp.init(this);
        for(WebServiceHandler wsh : _handlers.values())
            wsh.init();
        for(Generator g : _generators.values())
            g.init(this);
    }
    
    public void setAllowMethodOverride(boolean allow)
    {
        _allowMethodOverride = allow;
    }
    
    public boolean isAllowMethodOverride()
    {
        return _allowMethodOverride;
    }    
    
    public void setFilter(WebServiceFilter filter)
    {
        _filter = filter;
        _filter.init(this);
    }
    
    public WebServiceFilter getFilter()
    {
        return _filter;
    }
    
    public void setProviders(List<WebServiceProvider> providers)
    {
        _providers.addAll(providers);
    }
    
    public void addProvider(WebServiceProvider provider)
    {
        _providers.add(provider);
    }
    
    public List<WebServiceProvider> getProviders()
    {
        return _providers;
    }
    
    void setServletContext(ServletContext servletContext)
    {
        if(_servletContext==null && servletContext!=null)
            _servletContext = servletContext;
    }
    
    public ServletContext getServletContext()
    {
        return _servletContext;
    }
    
    public void setHandlers(List<WebServiceHandler> handlers)
    {
        for(WebServiceHandler handler : handlers)
            _handlers.put(handler.getName(), handler);
    }
    
    public WebServiceContext addHandler(WebServiceHandler handler)
    {
        _handlers.put(handler.getName(), handler);
        return this;
    }
    
    public WebServiceHandler getHandler(String name)
    {
        return _handlers.get(name);
    }
    
    public void setGenerators(List<Generator> generators)
    {
        for(Generator generator : generators)
            _generators.put(generator.getFormat(), generator);
    }
    
    public WebServiceContext addGenerator(Generator generator)
    {
        _generators.put(generator.getFormat(), generator);
        return this;
    }
    
    public Generator getGenerator(String format)
    {
        return _generators.get(format);
    }
    
    public void setDefaultGeneratorClassName(String defaultGeneratorClassName)
    {
        try
        {
            Class clazz = getClass().getClassLoader().loadClass(defaultGeneratorClassName);
            _defaultGenerator = (Generator)clazz.newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }
    
    public void setDefaultGenerator(Generator defaultGenerator)
    {
        _defaultGenerator = defaultGenerator;
    }
    
    public Generator getDefaultGenerator()
    {
        return _defaultGenerator;
    }
    
    public boolean isFormatSupported(String format)
    {
        return _generators.containsKey(format);
    }
    
}
