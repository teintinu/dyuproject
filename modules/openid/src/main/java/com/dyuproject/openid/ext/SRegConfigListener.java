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

package com.dyuproject.openid.ext;

import javax.servlet.http.HttpServletRequest;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.UrlEncodedParameterMap;

/**
 * Sets the sreg parameters
 * Deprecated.  Use SRegExtension instead.
 *
 * @author David Yu
 * @created Jan 8, 2009
 */
@Deprecated
public class SRegConfigListener implements RelyingParty.Listener
{

    public void onDiscovery(OpenIdUser user, HttpServletRequest request)
    {        
        
    }
    
    public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
            UrlEncodedParameterMap params)
    {
        SReg.put(params);
    }
    
    public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
    {
        SReg.set(user, SReg.parse(request));
    }

    public void onAccess(OpenIdUser user, HttpServletRequest request)
    {        
        
    }

    


}
