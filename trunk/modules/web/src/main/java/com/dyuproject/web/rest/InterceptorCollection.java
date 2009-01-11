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
 * Wraps an arry of interceptors and does the handle chain.
 * 
 * @author David Yu
 * @created May 18, 2008
 */

public class InterceptorCollection extends AbstractInterceptor
{
    
    private Interceptor[] _interceptors = new Interceptor[]{};
    
    public InterceptorCollection addInterceptor(Interceptor interceptor)
    {
        if(interceptor==null || indexOf(interceptor)!=-1)
            return this;
        
        Interceptor[] oldInterceptors = _interceptors;
        Interceptor[] interceptors = new Interceptor[oldInterceptors.length+1];
        System.arraycopy(oldInterceptors, 0, interceptors, 0, oldInterceptors.length);
        interceptors[oldInterceptors.length] = interceptor;
        _interceptors = interceptors;
        
        return this;
    }
    
    public int indexOf(Interceptor interceptor)
    {
        if(interceptor!=null)
        {
            Interceptor[] interceptors = _interceptors;
            for(int i=0; i<interceptors.length; i++)
            {
                if(interceptors[i].equals(interceptor))
                    return i;
            }
        }        
        return -1;
    }
    
    public void setInterceptors(Interceptor[] interceptors)
    {
        _interceptors = interceptors;
    }
    
    public Interceptor[] getInterceptors()
    {
        return _interceptors;
    }
    
    protected void init()
    {
        for(Interceptor i : getInterceptors())
            i.init(getWebContext());
    }

    public void postHandle(boolean handled, RequestContext requestContext) 
    throws ServletException, IOException
    {
        if(handled)
        {
            Interceptor[] interceptors = _interceptors;
            doPostHandleChain(interceptors, interceptors.length-1, true, requestContext);
        }
    }

    public boolean preHandle(RequestContext requestContext) throws ServletException, IOException
    {        
        boolean success = false;
        int i = 0;
        Interceptor[] interceptors = _interceptors;
        try
        {
            for(; i<interceptors.length; i++)
            {
                // protect in case of exceptions
                success = false;
                if(interceptors[i].preHandle(requestContext))                
                    success = true;                
                else                    
                    break;                                   
            }
        }
        finally
        {
            if(!success)            
                doPostHandleChain(interceptors, i, false, requestContext);            
        }
        return success;
    }
    
    static void doPostHandleChain(Interceptor[] interceptors, int i, boolean handled, 
            RequestContext requestContext) throws ServletException, IOException
    {
        // minimize recursion
        while(i!=-1)
        {
            try
            {
                interceptors[i--].postHandle(handled, requestContext);
            }
            finally
            {
                if(true)
                    continue;
            }
        }
        
        /*try
        {
            interceptors[i].postHandle(handled, requestContext);
        }
        finally
        {
            if(i>0)
                doPostHandleChain(interceptors, i-1, handled, requestContext);
        }*/
    }

}
