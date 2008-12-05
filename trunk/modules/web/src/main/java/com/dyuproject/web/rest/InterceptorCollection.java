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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;


/**
 * @author David Yu
 * @created May 18, 2008
 */

public class InterceptorCollection extends AbstractInterceptor
{
    
    private List<Interceptor> _interceptors = new ArrayList<Interceptor>();
    
    public void setInterceptors(List<Interceptor> interceptor)
    {
        _interceptors.addAll(interceptor);
    }
    
    public void addInterceptor(Interceptor interceptor)
    {
        _interceptors.add(interceptor);
    }
    
    public List<Interceptor> getInterceptors()
    {
        return _interceptors;
    }
    
    protected void init()
    {
        for(Interceptor i : _interceptors)
            i.init(getWebContext());
    }

    public void postHandle(boolean handled, RequestContext requestContext) 
    throws ServletException, IOException
    {
        if(handled)        
            doPostHandleChain(_interceptors.size()-1, true, requestContext);       
    }

    public boolean preHandle(RequestContext requestContext) throws ServletException, IOException
    {        
        boolean success = false;
        int i = 0;
        try
        {
            for(;i<_interceptors.size(); i++)
            {
                // protect in case of exceptions
                success = false;
                if(_interceptors.get(i).preHandle(requestContext))                
                    success = true;                
                else                    
                    break;                                   
            }
        }
        finally
        {
            if(!success)            
                doPostHandleChain(i, false, requestContext);            
        }
        return success;
    }
    
    private void doPostHandleChain(int i, boolean handled, RequestContext requestContext) 
    throws ServletException, IOException
    {
        try
        {
            _interceptors.get(i).postHandle(handled, requestContext);
        }
        finally
        {
            if(i>0)
                doPostHandleChain(i-1, handled, requestContext);
        }
    }
    
    public static class ThreadLocal extends java.lang.ThreadLocal<InterceptorCollection>
    {
        public InterceptorCollection initialValue()
        {
            return new InterceptorCollection();
        }
    }

}
