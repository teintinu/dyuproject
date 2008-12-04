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
    
    private Interceptor[] _interceptors;
    private List<Interceptor> _temp;
    
    public void setInterceptors(Interceptor[] intereptors)
    {
        _interceptors = intereptors;
    }
    
    public void setInterceptors(List<Interceptor> interceptor)
    {
        _temp = interceptor;
    }
    
    public void addInterceptor(Interceptor interceptor)
    {
        if(_temp==null)
            _temp = new ArrayList<Interceptor>();
        _temp.add(interceptor);
    }
    
    protected void init()
    {        
        if(_interceptors==null)
        {
            if(_temp==null)
                throw new IllegalStateException("no interceptor present");
            _interceptors = _temp.toArray(new Interceptor[_temp.size()]);
        }
        else if(_temp!=null)
        {
            for(Interceptor f : _interceptors)
                _temp.add(f);
            _interceptors = _temp.toArray(new Interceptor[_temp.size()]);
            _temp.clear();
            _temp = null;
        }
        for(Interceptor f : _interceptors)
            f.init(getWebContext());
    }

    public void postHandle(boolean handled, RequestContext requestContext) 
    throws ServletException, IOException
    {
        if(handled)        
            doPostHandleChain(_interceptors.length-1, true, requestContext);       
    }

    public boolean preHandle(RequestContext requestContext) throws ServletException, IOException
    {        
        boolean success = false;
        int i = 0;
        try
        {
            for(;i<_interceptors.length; i++)
            {
                // protect in case of exceptions
                success = false;
                if(_interceptors[i].preHandle(requestContext))                
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
        if(i==0)
        {
            _interceptors[0].postHandle(handled, requestContext);
            return;
        }
        try
        {
            _interceptors[i].postHandle(handled, requestContext);
        }
        finally
        {
            doPostHandleChain(i-1, handled, requestContext);
        }
    }

}
