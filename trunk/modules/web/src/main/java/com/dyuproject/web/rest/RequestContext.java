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
 * @author David Yu
 * @created Dec 4, 2008
 */

public class RequestContext
{
    private HttpServletRequest _request;
    private HttpServletResponse _response;
    private String _mime;
    private String[] _pathInfo;
    
    void setRequest(HttpServletRequest request)
    {
        _request = request;
    }
    
    public HttpServletRequest getRequest()
    {
        return _request;
    }
    
    void setResponse(HttpServletResponse response)
    {
        _response = response;
    }
    
    public HttpServletResponse getResponse()
    {
        return _response;
    }
    
    void setMime(String mime)
    {
        _mime = mime;
    }
    
    public String getMime()
    {
        return _mime;
    }
    
    void setPathInfo(String[] pathInfo)
    {
        _pathInfo = pathInfo;
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
