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

package com.dyuproject.web.ws.rest.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.ws.Generator;
import com.dyuproject.web.ws.WebServiceContext;

/**
 * @author David Yu
 * @created Mar 15, 2008
 */

public class TemplatingGenerator implements Generator
{
    
    public static final String DEFAULT_BASE_DIR = "/WEB-INF/rest-root";
    
    private String _format;
    private String _contentType;
    private TemplateSource _templateSource;
    
    public TemplatingGenerator()
    {
        
    }
    
    public void setTemplateSource(TemplateSource templateSource)
    {
        _templateSource = templateSource;
    }
    
    public void setFormat(String format)
    {
        _format = format;
    }
    
    public void setContentType(String contentType)
    {
        _contentType = contentType;
    }
    
    public void init(WebServiceContext context)
    {
        if(_templateSource==null)
            throw new IllegalStateException("templateSource not set.");
        if(_format==null)
            throw new IllegalStateException("format not set.");
        if(_contentType==null)
            throw new IllegalStateException("contentType not set.");
        
        String realPath = context.getServletContext().getRealPath(DEFAULT_BASE_DIR);
        File root = new File(realPath);        
        if(!root.exists() || !root.isDirectory())
            throw new IllegalStateException(DEFAULT_BASE_DIR + " must be an existing directory.");
        try
        {
            _templateSource.init(root, DEFAULT_BASE_DIR, _format, _contentType);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void generateResponse(HttpServletRequest request, HttpServletResponse response, 
            Object resource, Map<String, String> params) throws IOException
    {        
        _templateSource.writeTo(response, resource, (String)request.getAttribute("restPath"), params);        
    }

    public String getFormat()
    {        
        return _format;
    }

}
