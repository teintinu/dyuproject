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


/**
 * @author David Yu
 * @created May 16, 2008
 */

public abstract class AbstractController implements Controller
{
    
    private boolean _initialized = false;
    
    protected String _identifier, _identifierAttribute;
    protected WebContext _webContext;
    protected Filter _filter;

    public void init(WebContext webContext)
    {
        if(_initialized || webContext==null)
            return;
        
        _webContext = webContext;
        _initialized = true;
    }
    
    protected WebContext getWebContext()
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

}
