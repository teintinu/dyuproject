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

/**
 * @author David Yu
 * @created Jun 28, 2008
 */

public abstract class Authentication
{
    
    public static final String AUTHORIZATION = "Authorization";
    public static final String WWW_AUTHENTICATE= "WWW-Authenticate";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String REALM = "realm";
    
    private CredentialSource _authDataSource;
    
    public void setAuthDataSource(CredentialSource authDataSource)
    {
        _authDataSource = authDataSource;
    }
    
    public CredentialSource getAuthDataSource()
    {
        return _authDataSource;
    }
    
    public abstract boolean authenticate(String realm, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException;
    
    public abstract String getType();

}
