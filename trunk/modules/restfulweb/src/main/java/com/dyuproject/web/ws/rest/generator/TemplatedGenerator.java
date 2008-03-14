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

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.dyuproject.web.ws.Generator;

/**
 * @author David Yu
 * @created Mar 15, 2008
 */

public class TemplatedGenerator implements Generator
{
    
    private String _format;
    
    public TemplatedGenerator()
    {
        
    }
    
    public void setFormat(String format)
    {
        _format = format;
    }
    
    public void init()
    {
        // TODO Auto-generated method stub
        
    }

    public void generateResponse(HttpServletResponse response, Object resource,
            String[] pathInfo, Map<String, String> params) throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    public String getFormat()
    {
        // TODO Auto-generated method stub
        return _format;
    }





}
