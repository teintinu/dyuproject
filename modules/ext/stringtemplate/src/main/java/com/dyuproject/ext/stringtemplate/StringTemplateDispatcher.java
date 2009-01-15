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

package com.dyuproject.ext.stringtemplate;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.web.rest.ViewDispatcher;
import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Jan 15, 2009
 */

public class StringTemplateDispatcher implements ViewDispatcher, StringTemplateErrorListener
{
    
    public static final String DEFAULT_BASE_DIR = "/WEB-INF/views/stringtemplate/";
    public static final String DEFAULT_FILE_EXTENSION = "st";
    
    private static final Log _log = LogFactory.getLog(StringTemplateDispatcher.class);
    
    private boolean _initialized = false;
    private String _baseDir, _fileExtension, _suffix, _groupName = "root";
    private CustomTemplateGroup _group;
    
    public String getFileExtension()
    {
        return _fileExtension;
    }

    public void init(WebContext context)
    {
        if(_initialized)
            return;
        
        _initialized = true;
        
        if(_baseDir==null)
            _baseDir = DEFAULT_BASE_DIR;
        else if(_baseDir.charAt(_baseDir.length()-1)!='/')
            _baseDir += "/";            

        if(_fileExtension==null)
        {
            String fileExtension = context.getProperty("stringtemplate.file_extentsion");
            _fileExtension = fileExtension==null ? DEFAULT_FILE_EXTENSION : fileExtension;
        }
        else if(_fileExtension.charAt(0)=='.')
            _fileExtension = _fileExtension.substring(1);
        
        String groupName = context.getProperty("stringtemplate.group.name");
        if(groupName!=null)
            _groupName = groupName;
        
        File dir = new File(context.getServletContext().getRealPath(_baseDir));
        if(!dir.isDirectory() || !dir.exists())
            throw new IllegalStateException("baseDir must be an existing directory");
        
        _group = new CustomTemplateGroup(_groupName, 
                context.getServletContext().getRealPath(_baseDir), this);
        
        _log.info("baseDir: " + _baseDir);
        _log.info("fileExtension: " + _fileExtension);    
        _log.info("initialized.");
        
        _suffix = "." + _fileExtension;
    }
    
    public void dispatch(String uri, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        if(uri.charAt(0)=='/')
        {
            uri = uri.endsWith(_suffix) ? uri.substring(_baseDir.length(), 
                    uri.length()-_suffix.length()) : uri.substring(_baseDir.length());
        }
        else if(uri.endsWith(_suffix))
            uri = uri.substring(0, uri.length()-_suffix.length());
        
        CustomTemplate  template = (CustomTemplate)_group.getInstanceOf(uri);
        if(template==null)
            throw new IOException("Template not found.");
        
        template.init(request, response, "\r\n");        
    }
    

    public void error(String message, Throwable t)
    {
        _log.warn(message, t);        
    }

    public void warning(String message)
    {
        _log.warn(message);        
    }


}
