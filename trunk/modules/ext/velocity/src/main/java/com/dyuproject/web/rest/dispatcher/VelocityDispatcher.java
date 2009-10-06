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

package com.dyuproject.web.rest.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.ViewDispatcher;

/**
 * @author David Yu
 * @created Jun 15, 2008
 */

public final class VelocityDispatcher extends AbstractLifeCycle implements ViewDispatcher
{

    public static final String DEFAULT_BASE_DIR = "/WEB-INF/views/velocity/";
    public static final String DEFAULT_FILE_EXTENSION = "vm";
    
    private static final String VELOCITY_RESOURCE_LOADER = "resource.loader";
    private static final String VELOCITY_FILE_RESOURCE_LOADER_DESCRIPTION = "file.resource.loader.description";    
    private static final String VELOCITY_FILE_RESOURCE_LOADER_CLASS = "file.resource.loader.class";
    private static final String VELOCITY_FILE_RESOURCE_LOADER_PATH = "file.resource.loader.path";
    
    private static final Logger log = LoggerFactory.getLogger(VelocityDispatcher.class);
    
    private VelocityEngine _engine;
    private String _baseDir, _fileExtension, _suffix;    
    private final Properties _properties = new Properties();
    
    public VelocityDispatcher()
    {
        
    }
    
    public String getFileExtension()
    {
        return _fileExtension;
    }
    
    protected void init()
    {        
        if(_baseDir==null)
            _baseDir = DEFAULT_BASE_DIR;
        else if(_baseDir.charAt(_baseDir.length()-1)!='/')
            _baseDir += "/";            

        if(_fileExtension==null)
        {
            String fileExtension = getWebContext().getProperty("velocity.file_extentsion");
            _fileExtension = fileExtension==null ? DEFAULT_FILE_EXTENSION : fileExtension;
        }
        else if(_fileExtension.charAt(0)=='.')
            _fileExtension = _fileExtension.substring(1);
        
        File dir = new File(getWebContext().getServletContext().getRealPath(_baseDir));
        if(!dir.isDirectory() || !dir.exists())
            throw new IllegalStateException("baseDir must be an existing directory");        
        
        // apply velocity file resource loader defaults
        _properties.setProperty(VELOCITY_RESOURCE_LOADER, "file");
        
        _properties.setProperty(VELOCITY_FILE_RESOURCE_LOADER_DESCRIPTION, 
                "Velocity File Resource Loader");
        
        _properties.setProperty(VELOCITY_FILE_RESOURCE_LOADER_CLASS, 
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        
        try
        {            
            _properties.setProperty(VELOCITY_FILE_RESOURCE_LOADER_PATH, dir.getCanonicalPath());            
            _engine = new VelocityEngine(_properties);            
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        log.info("baseDir: " + _baseDir);
        log.info("fileExtension: " + _fileExtension);    
        log.info("initialized.");
        
        _suffix = "." + _fileExtension;
    }

    public void dispatch(String uri, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        if(uri.charAt(0)=='/')
        {
            uri = uri.endsWith(_suffix) ? uri.substring(_baseDir.length()) : 
                uri.substring(_baseDir.length()) + _suffix;
        }
        if(!uri.endsWith(_suffix))
            uri += _suffix;
        
        Template template = null;
        try
        {
            template = _engine.getTemplate(uri);
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        template.merge(LocalizedVelocityContext.getCurrent().setRequest(request), 
                response.getWriter());
    }
    
    public void setBaseDir(String baseDir)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _baseDir = baseDir;
    }
    
    public void setFileExtension(String fileExtension)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _fileExtension = fileExtension;
    }
    
    public void setProperties(Properties props)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        _properties.putAll(props);        
    }
    
    public void setProperties(InputStream stream)
    {
        if(isInitialized())
            throw new IllegalStateException("already initialized");
        
        Properties props = new Properties();
        try
        {            
            props.load(stream);
            _properties.putAll(props);
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


}
