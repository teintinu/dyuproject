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

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.format.FormatConverter;
import com.dyuproject.util.format.JSONConverter;

/**
 * @author David Yu
 * @created Mar 15, 2008
 */

public class JSONGenerator implements Generator
{
    
    public void init(WebServiceContext context)
    {        
        
    }

    public void generateResponse(HttpServletRequest request, HttpServletResponse response, 
            Object resource, Map<String, String> params) throws IOException
    {
        FormatConverter converter = JSONConverter.getInstance();
        response.setContentType(converter.getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.write(converter.toString(resource, params.get("callback")).getBytes());        
    }

    public String getFormat()
    {        
        return FormatConverter.JSON;
    }    

}
