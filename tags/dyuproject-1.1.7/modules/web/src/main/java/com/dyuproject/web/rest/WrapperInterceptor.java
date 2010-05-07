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

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Wraps an interceptor for pre-handling and post-handling.  This is generally subclassed to
 * determine which interceptor executes first on pre-handle and post-handle.
 * 
 * @author David Yu
 * @created Dec 4, 2008
 */

public class WrapperInterceptor extends AbstractLifeCycle implements Interceptor
{
    
    private Interceptor _interceptor;
    
    public void setInterceptor(Interceptor interceptor)
    {
        if(_interceptor==null)
            _interceptor = interceptor;
    }
    
    public Interceptor getInterceptor()
    {
        return _interceptor;
    }

    protected void init()
    {        
        if(_interceptor!=null)
            _interceptor.init(getWebContext());
    }

    public void postHandle(boolean handled, RequestContext requestContext)
    {
        if(_interceptor!=null)
            _interceptor.postHandle(handled, requestContext);        
    }

    public boolean preHandle(RequestContext requestContext)
            throws ServletException, IOException
    {        
        return _interceptor==null || _interceptor.preHandle(requestContext);
    }    

}
