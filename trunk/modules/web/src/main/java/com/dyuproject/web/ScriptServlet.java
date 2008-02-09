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

package com.dyuproject.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.ResourceUtil;

/**
 * @author David Yu
 */

public class ScriptServlet extends HttpServlet 
{

    public static final String CONTENT_TYPE = "application/x-javascript";
    private static byte[] __js; 
    
    public static byte[] getScript() 
    {
        return __js;
    }
    
    public synchronized void init() throws ServletException
    {
        if(__js==null)
        {
            try
            {
                __js = ResourceUtil.readBytes(getClass().getClassLoader().getResourceAsStream(
                        "com/dyuproject/web/scripts/dyuproject.js"));    
            }
            catch(Exception e)
            {
                throw new ServletException(e);
            }            
        }
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException 
    {
        response.setContentType(CONTENT_TYPE);
        ServletOutputStream out = response.getOutputStream();        
        out.write(getScript());        
    }
}
