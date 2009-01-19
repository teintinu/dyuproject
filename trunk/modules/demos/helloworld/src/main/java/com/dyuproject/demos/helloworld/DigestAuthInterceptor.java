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

import com.dyuproject.web.auth.Authentication;
import com.dyuproject.web.auth.SimpleCredentialSource;
import com.dyuproject.web.auth.SmartDigestAuthentication;
import com.dyuproject.web.rest.AbstractInterceptor;
import com.dyuproject.web.rest.RequestContext;

/**
 * @author David Yu
 * @created Jun 29, 2008
 */

public class DigestAuthInterceptor extends AbstractInterceptor
{
    
    private Authentication _authentication;
    
    public DigestAuthInterceptor()
    {
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        props.setProperty("hello", "world");        
        _authentication = new SmartDigestAuthentication(new SimpleCredentialSource(props), 
                "secret", 30);
    }

    protected void init()
    {
      
    }

    public void postHandle(boolean handled, RequestContext requestContext)
    {        
        
    }

    public boolean preHandle(RequestContext requestContext)
            throws ServletException, IOException
    {        
        return _authentication.authenticate("protected", 
                requestContext.getRequest(), requestContext.getResponse());
    }

}
