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

package com.dyuproject.web.rest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Jun 15, 2008
 */

public class SpringServletContextListener implements ServletContextListener
{
    
    public static final String RESOURCE_LOCATION = "/WEB-INF/webContext.xml";
    public static final String BEAN_NAME = "webContext";

    private AbstractXmlApplicationContext _applicationContext;

    public void contextDestroyed(ServletContextEvent event)
    {
        _applicationContext.destroy();       
    }

    public void contextInitialized(ServletContextEvent event)
    {
        ServletContext sc = event.getServletContext();

        _applicationContext = new ApplicationContext(sc);

        // This will be picked up by RESTfulMVCServlet            
        sc.setAttribute(WebContext.class.getName(), _applicationContext.getBean(BEAN_NAME));      
    }
    
    private static class ApplicationContext extends AbstractXmlApplicationContext
    {        
        ServletContext _servletContext;
        
        ApplicationContext(ServletContext servletContext)
        {
            _servletContext = servletContext;
            refresh();
        }
        
        public Resource[] getConfigResources()
        {
            try
            {
                return new Resource[]{new UrlResource(
                        _servletContext.getResource(RESOURCE_LOCATION))};
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }                    
        }
        
    }


}
