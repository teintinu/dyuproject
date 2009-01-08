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

package com.dyuproject.demos.openidservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.openid.OpenIdSreg;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;

/**
 * Logout Servlet
 * 
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet
{
    
    public LogoutServlet()
    {
        RelyingParty.getInstance().setListener(new RelyingParty.Listener()
        {

            public void onDiscovery(OpenIdUser user, HttpServletRequest request)
            {
                System.err.println("new user: " + user.getClaimedId());                
            }

            public void onAuthentication(OpenIdUser user, HttpServletRequest request)
            {
                System.err.print("just logged in: " + user.getIdentity());
                OpenIdSreg sreg = OpenIdSreg.get(request);
                if(sreg!=null)
                {
                    System.err.print(" aka " + sreg.getNickname());
                    user.setAttribute("sreg", sreg);
                }
                System.err.print("\n");
            }
            
            public void onAccess(OpenIdUser user, HttpServletRequest request)
            {
                System.err.print("user access: " + user.getIdentity());
                OpenIdSreg sreg = (OpenIdSreg)user.getAttribute("sreg");
                if(sreg!=null)
                    System.err.print(" aka " + sreg.getNickname());
                System.err.print("\n");
            }
            
        });
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        RelyingParty.getInstance().invalidate(request, response);
        response.sendRedirect("/home/");
    }

}