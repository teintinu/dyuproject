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

import javax.servlet.http.HttpServletRequest;


/**
 * The base controller to serve REST requests.
 * 
 * @author David Yu
 * @created May 16, 2008
 */

public abstract class AbstractController implements Controller
{
    
    private boolean _initialized = false, _destroyed = false;
    
    private String _identifier, _identifierAttribute;
    private WebContext _webContext;
    private Filter _filter;

    public final void init(WebContext webContext)
    {
        if(_initialized)
            return;
        
        _webContext = webContext;
        _initialized = true;
        if(getFilter()!=null)
            getFilter().init(webContext);
        init();
    }
    
    protected abstract void init();
    protected void destroy()
    {}
    
    protected boolean isInitialized()
    {
        return _initialized;
    }
    
    public WebContext getWebContext()
    {
        return _webContext;
    }
    
    public void setIdentifier(String identifier)
    {
        if(_initialized)
            return;
        _identifier = identifier;
    }
    
    public String getIdentifier()
    {
        return _identifier;
    }
    
    public void setIdentifierAttribute(String identifierAttribute)
    {
        _identifierAttribute = identifierAttribute;
    }
    
    public String getIdentifierAttribute()
    {
        return _identifierAttribute;
    }
    
    public void setFilter(Filter filter)
    {
        _filter = filter;
    }
    
    public Filter getFilter()
    {
        return _filter;
    }
    
    public String getVerbOrId(HttpServletRequest request)
    {
        return getIdentifierAttribute()==null ? null : 
            (String)request.getAttribute(getIdentifierAttribute());
    }
    
    public void destroy(WebContext webContext)
    {
        if(_destroyed || !_initialized)
            return;
        
        _destroyed = true;
        if(getFilter()!=null)
            getFilter().destroy(webContext);
        destroy();
    }

}
