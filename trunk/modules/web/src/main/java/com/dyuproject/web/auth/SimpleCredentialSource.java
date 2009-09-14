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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The credentials are retrieved from java.util.Properties.
 * 
 * @author David Yu
 * @created Jun 29, 2008
 */

public class SimpleCredentialSource implements CredentialSource
{
    
    private final Properties _properties = new Properties();
    
    public SimpleCredentialSource()
    {
        
    }
    
    public SimpleCredentialSource(Properties properties)
    {
        setProperties(properties);
    }
    
    public void setProperties(Properties properties)
    {        
        _properties.putAll(properties);        
    }
    
    public void setProperties(InputStream stream)
    {        
        Properties properties = new Properties();
        try
        {            
            properties.load(stream);
            _properties.putAll(properties);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setProperties(File location) throws IOException
    {
        setProperties(new FileInputStream(location));
    }
    
    public void setProperties(URL location) throws IOException
    {
        setProperties(location.openStream());
    }

    public String getPassword(String realm, String username,
            HttpServletRequest request)
    {       
        return _properties.getProperty(username);
    }

    public void onAuthenticated(String realm, String username, String password,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {        
        
    }

}
