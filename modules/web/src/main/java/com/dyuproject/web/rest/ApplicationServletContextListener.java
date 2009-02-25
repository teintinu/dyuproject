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

package com.dyuproject.web.rest;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.ioc.ApplicationContext;

/**
 * @author David Yu
 * @created Feb 25, 2009
 */

public class ApplicationServletContextListener implements ServletContextListener
{
    
    public static final String RESOURCE_LOCATION = "/WEB-INF/application.json";
    public static final String WEBCONTEXT_KEY = "webContext";
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationServletContextListener.class);
    
    private ApplicationContext _appContext;
    
    public ApplicationContext getAppContext()
    {
        return _appContext;
    }

    public void contextDestroyed(ServletContextEvent event)
    {
        if(_appContext!=null)
        {
            _appContext.destroy();            
            log.info(_appContext + " destroyed.");
            _appContext = null;
        }        
    }

    public void contextInitialized(ServletContextEvent event)
    {
        if(_appContext==null)
        {
            File file = new File(event.getServletContext().getRealPath(RESOURCE_LOCATION));
            if(file.exists())
                _appContext = ApplicationContext.load(file);
            else
            {
                _appContext = ApplicationContext.load(event.getServletContext().getResourceAsStream(
                        RESOURCE_LOCATION));
            }
            WebContext webContext = (WebContext)_appContext.findPojo(WEBCONTEXT_KEY);
            if(webContext==null)
                throw new IllegalStateException(WEBCONTEXT_KEY + " not found.");
            
            event.getServletContext().setAttribute(WebContext.class.getName(), webContext);
            log.info(_appContext + " initialized.");
        }        
    }

}
