//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public abstract class AbstractLifeCycle implements LifeCycle
{

    private boolean _initialized = false, _destroyed = false;
    private WebContext _webContext;    
    
    public final void init(WebContext webContext)
    {
        if(_initialized || webContext==null)
            return;
        
        _initialized = true;
        _webContext = webContext;        
        init();
    }
    
    public boolean isInitialized()
    {
        return _initialized;
    }
    
    public boolean isDestroyed()
    {
        return _destroyed;
    }
    
    public final void destroy(WebContext webContext)
    {
        if(_destroyed || !_initialized)
            return;
        
        destroy();
        _webContext = null;
        _destroyed = true;
    }
    
    protected abstract void init();
    protected void destroy()
    {}
    
    public WebContext getWebContext()
    {
        return _webContext;
    }
    
    public static Object newObjectInstance(String className) throws Exception
    {
        return WebContext.newObjectInstance(className);
    }

}
