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

package com.dyuproject.web.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.util.Delim;

/**
 * @author David Yu
 * @created May 10, 2008
 */

public class RestfulMVCServlet extends HttpServlet
{
    
    private static Log log = LogFactory.getLog(RestfulMVCServlet.class);
    
    private static final int GET = "GET".hashCode();
    private static final int POST = "POST".hashCode();
    private static final int PUT = "PUT".hashCode();
    private static final int DELETE = "DELETE".hashCode();
    
    public static final String DISPATCH_ATTR = "com.dyuproject.web.dispatch";
    public static final String DISPATCH_SUFFIX_ATTR = "com.dyuproject.web.dispatch.suffix";
    
    private RequestDispatcher _default, _jsp;
    private Controller _defaultController;
    private ContentGenerator _defaultGenerator;
    private Map<String,Controller> _controllers;
    private Map<String,ContentGenerator> _generators;
    private String _defaultFormat = ContentGenerator.DEFAULT_FORMAT;
    private WebContext _webContext;
    
    public void init()
    {
        _default = getServletContext().getNamedDispatcher("default");
        _jsp = getServletContext().getNamedDispatcher("jsp");
        
        _webContext = new WebContext();
        _webContext.setServletContext(getServletContext());
        _webContext.setDefaultDispatcher(_default);
        _webContext.setJspDispatcher(_jsp);        
        
        _controllers = new HashMap<String,Controller>();
        _generators = new HashMap<String,ContentGenerator>();        
        
        try{tester();}catch(Exception e){e.printStackTrace();throw new RuntimeException(e);}
        
        _defaultController.init(_webContext);
        _defaultGenerator.init(_webContext);
        if(_defaultGenerator.getFormat()==null)
            throw new IllegalStateException("Generator's format must not be null");
        if(_defaultGenerator.getContentType()==null)
            throw new IllegalStateException("Generator's contentType must not be null");
        
        
        if(!_generators.containsKey(_defaultGenerator.getFormat()))
            _generators.put(_defaultGenerator.getFormat(), _defaultGenerator);
        
        for(ContentGenerator cg : _generators.values())
        {
            if(cg.getFormat()==null)
                throw new IllegalStateException("Generator's format must not be null");
            if(cg.getContentType()==null)
                throw new IllegalStateException("Generator's contentType must not be null");
            cg.init(_webContext);
        }
        for(Controller c : _controllers.values())
        {
            if(c.getResourceName()==null)
                throw new IllegalStateException("Controller's resourceName must not be null");
            c.init(_webContext);
        }            
    }
    
    public void tester() throws Exception
    {
        String defaultController = getInitParameter("defaultController");
        if(defaultController==null)
            throw new IllegalStateException("defaultController must be provided.");
        
        _defaultController = (Controller)getInstance(defaultController);
        
        String defaultGenerator = getInitParameter("defaultGenerator");
        if(defaultGenerator==null)
            throw new IllegalStateException("defaultGenerator must be provided.");
        
        _defaultGenerator = (ContentGenerator)getInstance(defaultGenerator);
        
        String controllerClasses = getInitParameter("controllers");
        if(controllerClasses!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(controllerClasses, ",;");
            while(tokenizer.hasMoreTokens())
            {
                Controller c = (Controller)getInstance(tokenizer.nextToken().trim());
                if(c.getResourceName()==null)
                    throw new IllegalStateException("Controller's resourceName must not be null");
                _controllers.put(c.getResourceName(), c);
            }
        }        

        String generatorClasses = getInitParameter("generators");
        if(generatorClasses!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(generatorClasses, ",;");
            while(tokenizer.hasMoreTokens())
            {
                ContentGenerator cg = (ContentGenerator)getInstance(tokenizer.nextToken().trim());
                if(cg.getFormat()==null)
                    throw new IllegalStateException("Generator's format must not be null");
                if(cg.getContentType()==null)
                    throw new IllegalStateException("Generator's contentType must not be null");
                _generators.put(cg.getFormat(), cg);
            }
        }        

    }
    
    private static Object getInstance(String className) throws Exception
    {
        return RestfulMVCServlet.class.getClassLoader().loadClass(className).newInstance();        
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        
        String pathInfo = request.getPathInfo();        
        System.err.println("pathInfo: " + pathInfo);
        if(request.getAttribute(DISPATCH_ATTR)!=null)
        {
            System.err.println("DISPATCHED");
            if("jsp".equals(request.getAttribute(DISPATCH_SUFFIX_ATTR)))
                _jsp.include(request, response);
            else
                _default.forward(request, response);
            return;
        }
        
        int method = getMethod(request);
        if(method==-1)
        {
            response.sendError(404);  
            return;
        }        
        

        int last = pathInfo.length()-1;
        // root context /
        if(last<1)
        {
            handle(_defaultController, method, request, response, _defaultGenerator);
            return;
        }
        
        String format = null;        
        String lastWord = null;
        ContentGenerator generator = null;
        int dot = -1;
        int lastSlash = pathInfo.lastIndexOf('/');
        // ends with /
        if(lastSlash==last)
        {
            format = _defaultFormat;
            generator = _defaultGenerator;
            pathInfo = pathInfo.substring(1, last);
        }
        else
        {           
            lastWord = pathInfo.substring(lastSlash+1);            
            dot = lastWord.lastIndexOf('.');                
            if(dot!=-1)
            {
                format = lastWord.substring(dot+1);
                generator = _generators.get(format);
                if(generator==null)
                {
                    _default.forward(request, response);
                    return;
                }                
                lastWord = lastWord.substring(0, dot);
            }
            else
            {
                response.sendRedirect(request.getRequestURL().append('/').toString());
                return;
                //format = _defaultFormat;
                //generator = _defaultGenerator;
            }
            pathInfo = pathInfo.substring(1);
        }
        String[] tokens = Delim.SLASH.split(pathInfo);
        if(dot!=-1 && tokens.length%2!=0)
        {
            response.sendError(404);
            return;
        }
        if(lastWord!=null)
            tokens[tokens.length-1] = lastWord;
        System.err.println("format: " + format);
        doHandle(0, method, tokens, request, response, generator); 
    }
    

    
    private void doHandle(int sub, int method, String[] tokens, HttpServletRequest request, 
            HttpServletResponse response, ContentGenerator generator) 
            throws ServletException, IOException
    {
        Controller c = _controllers.get(tokens[sub]);
        if(c==null)
        {
            System.err.println("No controller matched on: " + tokens[sub]);
            response.sendError(404);
            return;
        }            
        if(sub+1==tokens.length)
        {            
            handle(c, method, request, response, generator);
            return;
        }
        String verbOrIdAttr = c.getResourceIdAttribute();
        if(verbOrIdAttr!=null)
            request.setAttribute(verbOrIdAttr, tokens[sub+1]);
        if(sub+2==tokens.length)
        {
            handle(c, method, request, response, generator);
            return;
        }
        doHandle(sub+2, method, tokens, request, response, generator);
    }
    
    public static void handle(Controller controller, int method, HttpServletRequest request, 
            HttpServletResponse response, ContentGenerator generator) 
            throws ServletException, IOException
    {
        switch(method)
        {
            case 0:
            {
                controller.doGet(request, response, generator);
            }
            case 1:
            {
                controller.doPost(request, response,  generator);
            }
            case 2:
            {
                controller.doPut(request, response, generator);
            }
            case 3:
            {
                controller.doDelete(request, response, generator);
            }
        }
    }
    
    public static void handle(Controller controller, HttpServletRequest request, 
            HttpServletResponse response, ContentGenerator generator) 
            throws ServletException, IOException
    {
        handle(controller, getMethod(request), request, response, generator);
    }
    
    public static int getMethod(HttpServletRequest request)
    {
        int method = request.getMethod().hashCode();
        if(GET==method)
            return 0;
        if(POST==method)
            return 1;
        if(PUT==method)
            return 2;
        if(DELETE==method)
            return 3;
        return -1;
    } 


}
