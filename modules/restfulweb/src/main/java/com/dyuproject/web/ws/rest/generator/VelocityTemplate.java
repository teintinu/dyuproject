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

package com.dyuproject.web.ws.rest.generator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import com.dyuproject.web.ws.AbstractWebServiceError;

/**
 * @author David Yu
 * @created Mar 15, 2008
 */

public class VelocityTemplate implements TemplateSource
{
    
    private static final ThreadLocal<VelocityContext> __velocityContext = new ThreadLocal<VelocityContext>();
    
    public static VelocityContext getContext()
    {
        VelocityContext vc = __velocityContext.get();
        if(vc==null)
        {
            vc = new VelocityContext();
            __velocityContext.set(vc);            
        }
        return vc;
    }
    
    private Map<String,Template> _mappings;
    private VelocityEngine _engine;
    private String _contentType;    
    
    public VelocityTemplate()
    {
        
    }

    public void init(File dir, String baseDir, String format, String contentType) throws IOException
    {        
        Properties props = new Properties();
        props.put("file.resource.loader.path", dir.getCanonicalPath());
        try
        {
            _engine = new VelocityEngine(props);
            _mappings = VelocityUtil.getTemplateMapping(_engine, dir, baseDir, format);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        _contentType = contentType;
    }
    
    public void writeTo(HttpServletResponse response, Object resource, String pathInfo,
            Map<String, String> params) throws IOException
    {
        if(resource==null || resource instanceof AbstractWebServiceError)
        {
            response.sendError(404);
            return;
        }
        Template template = _mappings.get(pathInfo);
        if(template==null)
        {
            response.sendError(404);
            return;
        }
        response.setContentType(_contentType);
        VelocityContext context = getContext();
        context.put("resource", resource);
        template.merge(context, response.getWriter());
        context.put("resource", null);
    }
    


}
