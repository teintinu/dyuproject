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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dyuproject.oauth.sp.PropertiesHashStore;
import com.dyuproject.oauth.sp.ServiceProvider;
import com.dyuproject.util.ClassLoaderUtil;

/**
 * @author David Yu
 * @created Jun 15, 2009
 */

public class ContextListener implements ServletContextListener
{

    public void contextInitialized(ServletContextEvent event)
    {
        Properties consumers = new Properties();
        URL resource = ClassLoaderUtil.getResource("consumers.properties", 
                AuthorizeTokenServlet.class);
        if(resource==null)
            throw new IllegalStateException("consumer.properties not found in classpath.");
        try
        {
            consumers.load(resource.openStream());
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
        
        PropertiesHashStore store = new PropertiesHashStore("secret", "macSecret", consumers);
        ServiceProvider serviceProvider = new ServiceProvider(store);
        event.getServletContext().setAttribute(ServiceProvider.class.getName(), serviceProvider);
        System.err.println("ServiceProvider initialized.");
    }
    
    public void contextDestroyed(ServletContextEvent event)
    {
        
    }
}
