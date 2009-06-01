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

package com.dyuproject.demos.openidservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON;

import com.dyuproject.openid.Constants;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Based from http://www.sociallipstick.com/2009/02/how-to-accept-openid-in-a-popup-without-leaving-the-page/
 * See http://wiki.openid.net/f/openid_ui_extension_draft01.html
 * 
 * 
 * @author David Yu
 * @created May 25, 2009
 */

@SuppressWarnings("serial")
public class PopupVerifyServlet extends HttpServlet
{
    
    RelyingParty _relyingParty = RelyingParty.getInstance()
        .addListener(new RelyingParty.Listener()
        {
            public void onAccess(OpenIdUser user, HttpServletRequest request)
            {                
            }
            public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
            {                
            }
            public void onDiscovery(OpenIdUser user, HttpServletRequest request)
            {                
            }
            public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request, 
                    UrlEncodedParameterMap params)
            {
                // the popup sign-in magic
                if("true".equals(request.getParameter("popup")))
                {
                    String returnTo = params.get(Constants.OPENID_TRUST_ROOT) + "/popup_verify.html";                    
                    params.put(Constants.OPENID_RETURN_TO, returnTo);
                    params.put(Constants.OPENID_REALM, returnTo);                    
                    params.put("openid.ns.ui", "http://specs.openid.net/extensions/ui/1.0");
                    params.put("openid.ui.mode", "popup");
                }
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
        if("true".equals(request.getParameter("logout")))
        {
            _relyingParty.invalidate(request, response);
            response.setStatus(200);
            return;
        }
        
        try
        {
            OpenIdUser user = _relyingParty.discover(request);
            if(user!=null)
            {
                if(user.isAuthenticated() || 
                        (user.isAssociated() && RelyingParty.isAuthResponse(request) && 
                                _relyingParty.verifyAuth(user, request, response)))
                {
                    response.setContentType("text/json");
                    response.getWriter().write(JSON.toString(user));
                    return;
                }   
            }         
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        response.setStatus(401);
    }

}
