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

package com.dyuproject.demos.helloworld;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.auth.Authentication;
import com.dyuproject.web.auth.DigestAuthentication;
import com.dyuproject.web.auth.SimpleCredentialSource;
import com.dyuproject.web.mvc.AbstractFilter;

/**
 * @author David Yu
 * @created Jun 29, 2008
 */

public class DigestAuthFilter extends AbstractFilter
{
    
    private Authentication _authentication = new DigestAuthentication("secret");
    
    public DigestAuthFilter()
    {
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        props.setProperty("hello", "world");        
        _authentication.setAuthDataSource(new SimpleCredentialSource(props));  
    }

    @Override
    protected void init()
    {
      
    }

    public void postHandle(boolean handled, String mime,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {       
        
    }

    public boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {        
        return _authentication.authenticate(ProtectedController.IDENTIFIER, request, response);
    }

}
