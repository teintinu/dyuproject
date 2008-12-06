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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Wraps the request and response plus the bits and pieces needed in handling a REST requset.
 * 
 * @author David Yu
 * @created Dec 4, 2008
 */

public class RequestContext
{
    private HttpServletRequest _request;
    private HttpServletResponse _response;
    private String _mime;
    private String[] _pathInfo;
    
    RequestContext init(HttpServletRequest request, HttpServletResponse response, String[] pathInfo, 
            String mime)
    {
        _request = request;
        _response = response;
        _pathInfo = pathInfo;
        _mime = mime;
        return this;
    }
    
    public HttpServletRequest getRequest()
    {
        return _request;
    }
    
    public HttpServletResponse getResponse()
    {
        return _response;
    }
    
    public String getMime()
    {
        return _mime;
    }
    
    public String[] getPathInfo()
    {
        return _pathInfo;
    }
    
    public String getPathElement(int idx)
    {
        return _pathInfo[idx];
    }
    
    void clear()
    {
        _request = null;
        _response = null;
        _pathInfo = null;
    }

}
