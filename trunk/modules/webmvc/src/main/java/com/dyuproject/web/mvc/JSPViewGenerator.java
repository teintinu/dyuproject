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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author David Yu
 * @created May 11, 2008
 */

public class JSPViewGenerator implements ContentGenerator
{
    
    private static final String INCLUDE_ATTR = "javax.servlet.include.servlet_path";
    
    private String _format, _contentType;
    private RequestDispatcher _jsp;
    
    public void init(WebContext context)
    {
        if(_format==null)
            _format = DEFAULT_FORMAT;
        if(_contentType==null)
            _contentType =DEFAULT_CONTENT_TYPE;

        _jsp = context.getJspDispatcher();
    }
    
    public void setFormat(String format)
    {
        _format = format;
    }
    
    public String getFormat()
    {
        return _format;
    }
    
    public void setContentType(String contentType)
    {
        _contentType = contentType;
    }
    
    public String getContentType()
    {
        return _contentType;
    }

    public void generateContent(Object data, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        if(!(data instanceof String))
            throw new IllegalArgumentException("data arg must be a uri(String)");
        
        
        String uri = (String)data;
        request.setAttribute(RestfulMVCServlet.DISPATCH_ATTR, "true");
        request.setAttribute(RestfulMVCServlet.DISPATCH_SUFFIX_ATTR, "jsp");
        request.setAttribute(INCLUDE_ATTR, uri);
        response.setContentType(getContentType());
        _jsp.include(request, response);        
    }

}
