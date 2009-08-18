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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.openid.OpenIdServletFilter;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Home Servlet. If authenticated, goes to the home page. If not, goes to the login page.
 * 
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet
{

    RelyingParty _relyingParty = RelyingParty.getInstance()
        .addListener(new SRegExtension()
            .addExchange("email")
            .addExchange("country")
            .addExchange("language")
        )
        .addListener(new AxSchemaExtension()
            .addExchange("email")
            .addExchange("country")
            .addExchange("language")
        )
        .addListener(new RelyingParty.Listener()
        {
            public void onDiscovery(OpenIdUser user, HttpServletRequest request)
            {
                System.err.println("discovered user: " + user.getClaimedId());
            }            
            public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
                    UrlEncodedParameterMap params)
            {
                System.err.println("pre-authenticate user: " + user.getClaimedId());
            }            
            public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
            {
                System.err.println("newly authenticated user: " + user.getIdentity());
                Map<String,String> sreg = SRegExtension.remove(user);
                Map<String,String> axschema = AxSchemaExtension.remove(user);
                if(sreg!=null && !sreg.isEmpty())
                {
                    System.err.println("sreg: " + sreg);
                    user.setAttribute("info", sreg);
                }
                else if(axschema!=null && !axschema.isEmpty())
                {                    
                    System.err.println("axschema: " + axschema);
                    user.setAttribute("info", axschema);
                }          
            }            
            public void onAccess(OpenIdUser user, HttpServletRequest request)
            {        
                System.err.println("user access: " + user.getIdentity());
                System.err.println("info: " + user.getAttribute("info"));
            }   
        });
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
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
                    // set error msg if the openid_identifier is not resolved.
                    if(request.getParameter(_relyingParty.getIdentifierParameter())!=null)
                        request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
                    
                    // new user
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            if(user.isAuthenticated())
            {
                // user already authenticated                
                request.setAttribute(OpenIdUser.ATTR_NAME, user);
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
                    response.sendRedirect(request.getContextPath() + "/home/");
                }
                else
                {
                    // failed verification
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            // associate and authenticate user
            StringBuffer url = request.getRequestURL();
            String trustRoot = url.substring(0, url.indexOf("/", 9));
            String realm = url.substring(0, url.lastIndexOf("/"));
            String returnTo = url.toString();            
            if(_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm, 
                    returnTo))
            {
                // successful association
                return;
            }          
        }
        catch(UnknownHostException uhe)
        {
            System.err.println("not found");
            errorMsg = OpenIdServletFilter.ID_NOT_FOUND_MSG;
        }
        catch(FileNotFoundException fnfe)
        {
            System.err.println("could not be resolved");
            errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
        }
        request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

}
