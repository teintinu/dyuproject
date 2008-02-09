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

package com.dyuproject.web.ws;

import java.util.Map;
import javax.servlet.http.Cookie;

/**
 * @author David Yu
 */

public class WebServiceFilterCollection implements WebServiceFilter 
{
    
    private WebServiceFilter[] _filters;
    
    public void init(WebServiceContext context)
    {
        
    }
    
    public void setFilters(WebServiceFilter[] filters)
    {
        _filters = filters;
    }
    
    public WebServiceFilter[] getFilters()
    {
        return _filters;
    }

    public boolean preHandle(int type, Cookie[] cookies, String[] pathInfo, 
            Map<String, String> params, int batchIndex, int batchLength) throws Exception
    {
        for(int i=0; i<_filters.length; i++)
        {
            if(!_filters[i].preHandle(type, cookies, pathInfo, params, batchIndex, batchLength))
                return false;
        }
        return true;
    }
    
    public void postHandle(int type, int batchLength)
    {
        for(int i=0; i<_filters.length; i++)
            _filters[i].postHandle(type, batchLength);
    }       

}
