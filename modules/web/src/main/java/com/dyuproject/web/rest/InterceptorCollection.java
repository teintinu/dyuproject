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

import com.dyuproject.util.ArrayUtil;


/**
 * Wraps an arry of interceptors and does the handle chain.
 * 
 * @author David Yu
 * @created May 18, 2008
 */

public class InterceptorCollection extends AbstractLifeCycle implements Interceptor
{
    
    private Interceptor[] _interceptors = new Interceptor[]{};
    
    public InterceptorCollection addInterceptor(Interceptor interceptor)
    {
        if(interceptor==null || indexOf(interceptor)!=-1)
            return this;
        
        synchronized(this)
        {
            if(interceptor instanceof InterceptorCollection)
            {
                _interceptors = (Interceptor[])ArrayUtil.append(_interceptors, 
                        ((InterceptorCollection)interceptor).getInterceptors());
            }
            else
                _interceptors = (Interceptor[])ArrayUtil.append(_interceptors, interceptor);
        }
        
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
    {
        if(handled)
        {
            Interceptor[] interceptors = _interceptors;
            doPostHandleChain(interceptors, interceptors.length-1, true, requestContext);
        }
    }

    public boolean preHandle(RequestContext requestContext) throws ServletException, IOException
    {
        int i = 0;
        boolean success = true;
        Interceptor[] interceptors = _interceptors;
        try
        {
            for(; i<interceptors.length; i++)
            {
                if(!interceptors[i].preHandle(requestContext))                
                    break;                              
            }
        }
        finally
        {
            if(i!=interceptors.length)
            {
                success = false;
                doPostHandleChain(interceptors, i, false, requestContext);
            }                        
        }
        return success;
    }
    
    static void doPostHandleChain(Interceptor[] interceptors, int i, boolean handled, 
            RequestContext requestContext)
    {
        while(i!=-1)
            interceptors[i--].postHandle(handled, requestContext);
        /*while(i!=-1)
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
        }*/
    }

}
