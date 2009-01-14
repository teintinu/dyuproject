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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.UrlEncodedParameterMap;
import com.dyuproject.openid.ext.SReg;
import com.dyuproject.openid.ext.SRegConfigListener;

/**
 * @author David Yu
 * @created Jan 8, 2009
 */

public class MyContextListener implements ServletContextListener, RelyingParty.Listener
{
    
    private RelyingParty _relyingParty;

    public void contextDestroyed(ServletContextEvent event)
    {        
        
    }

    public void contextInitialized(ServletContextEvent event)
    {
        // use default. See the http://code.google.com/p/dyuproject/wiki/openid for more info.
        _relyingParty = RelyingParty.getInstance();
        
        // enable sreg
        _relyingParty.addListener(new SRegConfigListener());
        
        // listen to the authentication events
        _relyingParty.addListener(this);
        
        // this relying party will be used by OpenIdServletFilter
        event.getServletContext().setAttribute(RelyingParty.class.getName(), _relyingParty);   
    }
    
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
        System.err.print("newly authenticated user: " + user.getIdentity());
        SReg sreg = SReg.get(user);
        if(sreg!=null)
            System.err.print(" aka " + sreg.getNickname());
        System.err.print("\n");       
    }

    public void onAccess(OpenIdUser user, HttpServletRequest request)
    {
        System.err.print("user access: " + user.getIdentity());
    }





}
