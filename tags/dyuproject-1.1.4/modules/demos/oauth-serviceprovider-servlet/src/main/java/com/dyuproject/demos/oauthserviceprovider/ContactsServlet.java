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

package com.dyuproject.demos.oauthserviceprovider;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.sp.ServiceToken;

/**
 * @author David Yu
 * @created Jun 15, 2009
 */

@SuppressWarnings("serial")
public class ContactsServlet extends AbstractServiceProviderServlet
{
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        ServiceToken accessToken = getServiceProvider().getAccessToken(request);
        if(accessToken==null)
        {            
            response.setStatus(401);
            return;
        }
        
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            .append("<user>")
            .append("<id>").append(accessToken.getId()).append("</id>")
            .append("<contacts>")
            .append("contacts goes here ...")
            .append("</contacts>")
            .append("</user>");
        
    }

}
