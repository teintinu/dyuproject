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


/**
 * Base interceptor to pre-handle and post-handle requests
 * 
 * @author David Yu
 * @created May 18, 2008
 */

public abstract class AbstractInterceptor implements Interceptor
{
    
    private boolean _initialized = false, _destroyed = false;
    private WebContext _webContext;    
    
    public final void init(WebContext webContext)
    {
        if(_initialized)
            return;
        
        _webContext = webContext;
        _initialized = true;
        init();
    }
    
    
    
    public final void destroy(WebContext webContext)
    {
        if(_destroyed || !_initialized)
            return;
        
        _destroyed = true;
        destroy();
    }
    
    protected abstract void init();
    protected void destroy()
    {}
    
    public WebContext getWebContext()
    {
        return _webContext;
    }    

}
