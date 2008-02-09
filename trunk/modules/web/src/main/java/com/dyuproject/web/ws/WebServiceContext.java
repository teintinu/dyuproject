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
    private List<WebServiceProvider> _providers = new ArrayList<WebServiceProvider>();
    private boolean _allowMethodOverride = false;
    
    public final void init() throws ServletException
    {
        for(WebServiceProvider wsp : _providers)
            wsp.init(this);
        for(WebServiceHandler wsh : _handlers.values())
            wsh.init();
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
    
    public WebServiceContext addHandler(WebServiceHandler handler)
    {
        _handlers.put(handler.getName(), handler);
        return this;
    }
    
    public WebServiceHandler getHandler(String name)
    {
        return _handlers.get(name);
    }
    
}
