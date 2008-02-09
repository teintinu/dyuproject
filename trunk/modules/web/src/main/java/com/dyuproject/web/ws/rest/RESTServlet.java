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

package com.dyuproject.web.ws.rest;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.dyuproject.web.ws.WebServiceServlet;
import com.dyuproject.web.ws.error.ResourceUnavailable;
/*import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.dyuproject.util.FormatConverter;*/

/**
 * @author David Yu
 */

public class RESTServlet extends WebServiceServlet
{
    /*
    public static final String DEFAULT_ROOT_DIR = "/WEB-INF/rest";
    
    private boolean _enableView = false;
    private String _resourceDir; 
    
    public void init() throws ServletException
    {
        String enableViewParam = getInitParameter("enableView");
        if(enableViewParam!=null)
        {
            _enableView = Boolean.parseBoolean(enableViewParam);
            String rootDirParam = getInitParameter("resourceDir");
            _resourceDir = rootDirParam!=null ? rootDirParam : DEFAULT_ROOT_DIR;
        }
        super.init();
    }
    
    protected void writeResponse(HttpServletResponse response, Object resource, 
            Map<String, String> params) throws ServletException, IOException
    {
        String format = params.get("format");
        if(format==null && _enableView)
        {
            
            return;
        }
        FormatConverter converter = FormatConverter.getConverter(format);
        response.setContentType(converter.getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.write(converter.toString(resource, params.get("callback")).getBytes());
    }*/

    protected Object handle(HttpServletRequest request, Map<String,String> params) throws Exception
    {
        String path = request.getPathInfo();
        return path!=null && path.length()>1 ? RESTService.getInstance().handle(_context, request,
                params) : ResourceUnavailable.getInstance();
    }
    

}
