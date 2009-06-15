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
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.Constants;
import com.dyuproject.util.ClassLoaderUtil;

/**
 * @author David Yu
 * @created Jun 15, 2009
 */

@SuppressWarnings("serial")
public class AuthorizeTokenServlet extends AbstractServiceProviderServlet
{
    
    private Properties _users = new Properties();
    
    public void init() throws ServletException
    {
        super.init();
        
        URL resource = ClassLoaderUtil.getResource("users.properties", 
                AuthorizeTokenServlet.class);
        
        if(resource==null)
            throw new ServletException("user.properties not found in classpath.");
        try
        {
            _users.load(resource.openStream());
        }
        catch(IOException ioe)
        {
            throw new ServletException(ioe);
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String requestToken = request.getParameter(Constants.OAUTH_TOKEN);
        if(requestToken==null)
        {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        request.setAttribute("requestToken", requestToken);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {        
        String username = request.getParameter("username");
        if(username==null || username.length()==0)
        {
            request.setAttribute("msg", "Username is required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }        
        
        String password = request.getParameter("password");
        if(password==null || password.length()==0)
        {
            request.setAttribute("msg", "Password is required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        String pw = _users.getProperty(username);
        if(pw==null)
        {
            request.setAttribute("msg", "User not found.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        if(!pw.equals(password))
        {
            request.setAttribute("msg", "Incorrect password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;            
        }
        
        String requestToken = request.getParameter(Constants.OAUTH_TOKEN);
        if(requestToken==null || requestToken.length()==0)
        {
            request.getSession().setAttribute("user", username);
            response.sendRedirect(request.getContextPath() + "/home/");
            return;
        }
        
        String callbackOrVerifier = getServiceProvider().getAuthCallbackOrVerifier(requestToken, 
                username);
        
        if(callbackOrVerifier.startsWith("http"))
            response.sendRedirect(callbackOrVerifier);
        else
        {
            request.setAttribute("verifier", callbackOrVerifier);
            request.getRequestDispatcher("/verifier.jsp").forward(request, response);
        }
        
    }

}
