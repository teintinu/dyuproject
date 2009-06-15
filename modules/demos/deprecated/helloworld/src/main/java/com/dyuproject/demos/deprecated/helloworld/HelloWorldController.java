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

package com.dyuproject.demos.deprecated.helloworld;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.web.rest.mvc.AbstractController;

/**
 * @author David Yu
 * @created Jun 7, 2008
 */

public class HelloWorldController extends AbstractController
{
    
    /**
     * unique identifier
     * handles http://localhost:8080/helloworld
     */
    public static final String IDENTIFIER = "helloworld";
    
    /**
     * /helloworld/${token}
     * To get the value, you call request.getAttribute(IDENTIFIER_ATTR);
     */    
    public static final String IDENTIFIER_ATTR = "helloworld.verbOrId";
    
    public HelloWorldController()
    {
        // set the identifier (required)
        setIdentifier(IDENTIFIER);
        
        // (optional)
        setIdentifierAttribute(IDENTIFIER_ATTR);
    }

    /**
     * initialize your controller
     */
    protected void init()
    {
        // Object a = getWebContext().getAttribute("someAttribute");
        // String b = getWebContext().getProperty("someEnvProperty");
        
    }

    /**
     * handle the request
     * you can handle the request depending on the request method.(GET, POST, PUT, DELETE, etc)
     */
    public void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        // you can allow certain mimetypes, like json w/c are common for webservices
        // WEB-INF/mime.properties will be parsed
        // format would be: 
        //   json = text/json
        
        // /helloworld/${verbOrId}
        String verbOrId = getVerbOrId(request);        

        if("json".equals(mime))
        {
            response.setContentType("text/plain");
            // generate json response
            ServletOutputStream out = response.getOutputStream();
            out.print(JSON.toString(new HelloWorldBean(verbOrId)));
        }
        else
        {
            response.setContentType("text/html");            
            request.setAttribute("helloWorldBean", new HelloWorldBean(verbOrId));
            
            
            // for demo purposes, 2 templating engines are used.
            // normally, you'd only have to choose one.
            // velocity is configured via: WebContext.addViewDispatcher("velocity", new VelocityDispatcher());
            if("vm".equals(mime))
            {
                getWebContext().getViewDispatcher("velocity").dispatch("helloworld/index.vm", 
                        request, response);
            }
            else
                getWebContext().getJSPDispatcher().dispatch("helloworld/index.jsp", 
                    request, response);
        }        
        
    }
    
    // POJO to xml/json string
    public static class HelloWorldBean implements JSON.Convertible
    {
        
        private long _timestamp = System.currentTimeMillis();
        private String _message;
        private String _verbOrId;
        
        public HelloWorldBean(String verbOrId)
        {
            _message = "Hello World from controller! @ " + new Date(_timestamp);
            _verbOrId = verbOrId;
        }
        
        public long getTimestamp()
        {
            return _timestamp;
        }
        
        public String getMessage()
        {
            return _message;
        }
        
        public String getVerbOrId()
        {
            return _verbOrId;
        }

        public void fromJSON(Map map)
        {
            // TODO Auto-generated method stub
            
        }

        public void toJSON(Output out)
        {
            out.add("message", getMessage());
            out.add("verbOrId", getVerbOrId());
            out.add("timestamp", getTimestamp());            
        }
        
    }

}