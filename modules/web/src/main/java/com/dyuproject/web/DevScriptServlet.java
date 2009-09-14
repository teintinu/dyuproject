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

package com.dyuproject.web;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.util.ResourceUtil;

/**
 * A utility servlet that serves a javascript file that is external from the webapp.
 * Refreshes the content-served when content changes.
 * 
 * @author David Yu
 * @created Feb 14, 2008
 */

@SuppressWarnings("serial")
public class DevScriptServlet extends HttpServlet 
{
    
    public static final String CONTENT_TYPE = "application/x-javascript";
    
    private static final Logger log = LoggerFactory.getLogger(DevScriptServlet.class);
    private byte[] _scriptBytes;
    private final Object _lock = new Object();
    private File _scriptFile;
    private long _lastModified = 0;
    private long _interval = 5000;
    
    public void init() throws ServletException
    {
        String scriptLocation = getInitParameter("scriptLocation");
        if(scriptLocation==null)
            throw new IllegalStateException("scriptLocation not specified");
        _scriptFile = new File(scriptLocation);
        if(!_scriptFile.exists())
            throw new IllegalStateException("script does not exist");
        String intervalParam = getInitParameter("interval");
        if(intervalParam!=null)
            _interval = Long.parseLong(intervalParam);        
        Timer timer = new Timer();        
        timer.schedule(new TimerTask(){            
            public void run()
            {                
                long modified = _scriptFile.lastModified();                
                if(_lastModified==0)
                {                    
                    _lastModified = modified;                   
                    return;
                }
                if(_lastModified<modified)
                {                    
                    try
                    {
                        log.info("loading: " + _scriptFile);
                        setScript(ResourceUtil.readBytes(_scriptFile));
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    _lastModified = modified;
                }
            }            
        }, _interval, _interval);
        try
        {
            setScript(ResourceUtil.readBytes(_scriptFile));
        }
        catch(IOException ioe)
        {
            throw new ServletException(ioe);
        } 
    } 
    
    private void setScript(byte[] script)
    {
        synchronized(_lock)
        {
            _scriptBytes = script;
        } 
        System.err.println("script updated @ " + System.currentTimeMillis());
    }
    
    public byte[] getScript()
    {
        synchronized(_lock)
        {
            return _scriptBytes;
        }        
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException 
    {
        response.setContentType(CONTENT_TYPE);
        ServletOutputStream out = response.getOutputStream();        
        out.write(getScript());        
    }
}
