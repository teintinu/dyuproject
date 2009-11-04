//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.openid;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Forwards the user to an html page that contains a form where the user will be 
 * automatically redirected upon page load.
 * 
 * @author David Yu
 * @created Mar 17, 2009
 */

public final class FormRedirection implements AuthRedirection
{
    
    public static final String DEFAULT_TITLE = System.getProperty("fr.title","");
    
    private final String _title;
    
    public FormRedirection()
    {
        this(DEFAULT_TITLE);
    }
    
    public FormRedirection(String title)
    {
        _title = title;
    }

    public String getTitle()
    {
        return _title;
    }
    
    public void redirect(UrlEncodedParameterMap params,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.append("<html>");
        writeHead(pw, params, request);
        writeBody(pw, params, request);
        pw.append("</html>");
    }
    
    protected void writeHead(Writer w, UrlEncodedParameterMap params, 
            HttpServletRequest request) throws IOException
    {
        w.append("<head><title>").append(getTitle()).append("</title>")
            .append("<meta http-equiv=\"Pragma\" content=\"no-cache\"/>")
            .append("<meta http-equiv=\"Cache-Control\" content=\"no-cache,no-store\"/></head>");     
    }
    
    protected void writeBody(Writer w, UrlEncodedParameterMap params, 
            HttpServletRequest request) throws IOException
    {
        w.append("<body onload=\"document.forms['auth'].submit();\">")
            .append("<form id=\"auth\" name=\"auth\" method=\"POST\" accept-charset=\"utf-8\"")
            .append(" action=\"").append(params.getUrl()).append("\">");
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            w.append("<input type=\"hidden\" name=\"").append(entry.getKey()).append("\" value=\"")
                .append(entry.getValue()).append("\"/>");
        }
        w.append("</form></body>");
    }

}
