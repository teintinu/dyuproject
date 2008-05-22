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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author David Yu
 * @created May 18, 2008
 */

public class FilterCollection extends AbstractFilter
{
    
    private Filter[] _filters;
    private List<Filter> _temp;
    
    public void setFilters(Filter[] filters)
    {
        _filters = filters;
    }
    
    public void setFilters(List<Filter> filters)
    {
        _temp = filters;
    }
    
    public void addFilter(Filter filter)
    {
        if(_temp==null)
            _temp = new ArrayList<Filter>();
        _temp.add(filter);
    }
    
    protected void init()
    {        
        if(_filters==null)
        {
            if(_temp==null)
                throw new IllegalStateException("no filter present");
            _filters = _temp.toArray(new Filter[_temp.size()]);
        }
        else if(_temp!=null)
        {
            for(Filter f : _filters)
                _temp.add(f);
            _filters = _temp.toArray(new Filter[_temp.size()]);
        }
        for(Filter f : _filters)
            f.init(getWebContext());
    }

    public void postHandle(boolean handled, String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        if(handled)        
            doPostHandleChain(_filters.length-1, true, mime, request, response);       
    }

    public boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {        
        boolean success = false;
        int i = 0;
        try
        {
            for(;i<_filters.length; i++)
            {
                // protect in case of exceptions
                success = false;
                if(_filters[i].preHandle(mime, request, response))                
                    success = true;                
                else                    
                    break;                                   
            }
        }
        finally
        {
            if(!success)            
                doPostHandleChain(i, false, mime, request, response);            
        }
        return success;
    }
    
    private void doPostHandleChain(int i, boolean handled, String mime,
            HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {        
        if(i==-1)
            return;
        try
        {
            _filters[i--].postHandle(false, mime, request, response);
        }
        finally
        {
            doPostHandleChain(i, handled, mime, request, response);
        }
    }

}
