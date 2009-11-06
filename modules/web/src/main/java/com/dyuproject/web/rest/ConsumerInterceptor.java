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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

/**
 * An interceptor that checks sets the mapped {@link Consumer} in the 
 * {@link RequestContext} for use with the REST handlers.  
 * 
 * @author David Yu
 * @created Jan 18, 2009
 */

public final class ConsumerInterceptor extends AbstractLifeCycle implements Interceptor
{
    
    private final Map<String,ValidatingConsumer> _consumers = new HashMap<String,ValidatingConsumer>(3);
    
    protected void init()
    {        
        for(ValidatingConsumer vc : _consumers.values())
            vc.init(getWebContext());
    }
    
    public void addConsumer(ValidatingConsumer consumer)
    {
        if(consumer==null || isInitialized())
            return;
        
        _consumers.put(consumer.getHttpMethod(), consumer);
    }

    public void postHandle(boolean handled, RequestContext requestContext)
    {        
        //requestContext.setConsumer(null);
    }

    public boolean preHandle(RequestContext requestContext)
            throws ServletException, IOException
    {
        ValidatingConsumer consumer = _consumers.get(requestContext.getRequest().getMethod());
        if(consumer!=null)
        {
            String contentType = requestContext.getRequest().getContentType();
            if(contentType.startsWith(consumer.getContentType()))
            {
                requestContext.setConsumer(consumer);
                return true;
            }
            requestContext.getResponse().sendError(404);
            return false;            
        }        
        return true;
    }

}
