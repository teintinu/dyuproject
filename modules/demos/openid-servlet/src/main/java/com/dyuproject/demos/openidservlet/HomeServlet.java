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

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;

/**
 * Home Servlet. If authenticated, goes to the home page. If not, goes to the login page.
 * 
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet
{
    
    RelyingParty _relyingParty;
    
    public void init()
    {
        _relyingParty = RelyingParty.getInstance();
    }    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {        
        try
        {
            OpenIdUser user = _relyingParty.discover(request);
            if(user==null)
            {                
                if(RelyingParty.isAuthResponse(request))
                {
                    // authentication timeout                    
                    response.sendRedirect(request.getRequestURI());
                }
                else
                {
                    // new user
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            if(user.isAuthenticated())
            {
                // user already authenticated                
                request.setAttribute("user", user);
                request.getRequestDispatcher("/home.jsp").forward(request, response);
                
                return;
            }
            
            if(user.isAssociated() && RelyingParty.isAuthResponse(request))
            {
                // verify authentication
                if(_relyingParty.verifyAuth(user, request, response))
                {
                    // authenticated                    
                    // redirect to home to remove the query params instead of doing:
                    // request.setAttribute("user", user); request.getRequestDispatcher("/home.jsp").forward(request, response);
                    response.sendRedirect("/home/");
                }
                else
                {
                    // failed verification ... re-authenticate
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            // associate user
            if(_relyingParty.associate(user, request, response))
            {
                // authenticate user to his/her openid provider
                StringBuffer url = request.getRequestURL();
                String trustRoot = url.substring(0, url.indexOf("/", 9));
                String realm = url.substring(0, url.lastIndexOf("/"));
                String returnTo = url.toString();
                response.sendRedirect(RelyingParty.getAuthUrlString(user, trustRoot, realm, 
                        returnTo)); 
            }
            else
            {
                // failed association
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }           
        }
        catch(Exception e)
        {
            e.printStackTrace();
            response.sendError(401);
        }
    }

}
