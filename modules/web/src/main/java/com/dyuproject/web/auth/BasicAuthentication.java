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

package com.dyuproject.web.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.util.B64Code;

/**
 * @author David Yu
 * @created Jun 28, 2008
 */

public class BasicAuthentication extends Authentication
{
    
    public static final String TYPE = "Basic";
    
    public BasicAuthentication()
    {
        
    }
    
    public String getType()
    {
        return TYPE;
    }

    public boolean authenticate(String realm, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        String authorization = request.getHeader(AUTHORIZATION);
        if(authorization==null)
        {
            sendChallenge(realm, request, response);
            return false;
        }
        String credentials = B64Code.decode(authorization.substring(authorization.indexOf(' ')+1));
        int idx = credentials.indexOf(':');
        String username = credentials.substring(0, idx);
        String password = credentials.substring(idx+1);
        if(password.equals(getAuthDataSource().getPassword(realm, username, request)))
        {
            getAuthDataSource().onAuthenticated(realm, username, password, request, response);
            return true;
        }            
        sendChallenge(realm, request, response);
        return false;
    }
    
    private void sendChallenge(String realm, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        response.setHeader(WWW_AUTHENTICATE, "Basic realm=\"".concat(realm).concat("\""));
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
