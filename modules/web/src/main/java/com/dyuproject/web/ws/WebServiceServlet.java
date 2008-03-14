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

package com.dyuproject.web.ws;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dyuproject.web.RequestUtil;
import com.dyuproject.web.ws.rest.RESTService;
import com.dyuproject.web.ws.rpc.RPCService;
import com.dyuproject.util.Delim;
import com.dyuproject.util.FormatConverter;

/**
 * @author David Yu
 */

public class WebServiceServlet extends HttpServlet
{

    public static final String WS_CONTEXT_ATTR = WebServiceContext.class.getName();    
    
    protected WebServiceContext _context;
    protected String _cookiePath;
    protected FormatConverter _defaultFormatConverter;

    public void init() throws ServletException
    {
        _context = (WebServiceContext)getServletContext().getAttribute(WS_CONTEXT_ATTR);
        if(_context==null)
        {
            String providerClasses = getInitParameter("providerClasses");
            if(providerClasses==null)
                throw new ServletException("WebServiceContext and providerClasses both null");            
            String[] providers = Delim.COMMA.split(providerClasses);
            _context = new WebServiceContext();
            try
            {                
                for(int i=0; i<providers.length; i++)
                {
                    Class clazz = getClass().getClassLoader().loadClass(providers[i].trim());
                    if(WebServiceProvider.class.isAssignableFrom(clazz))                                            
                        _context.addProvider((WebServiceProvider)clazz.newInstance());
                }
            }
            catch(Exception e)
            {
                throw new ServletException(e);
            }
        }
        String defaultFormat = getInitParameter("defaultFormat");
        _defaultFormatConverter = defaultFormat==null ? FormatConverter.getDefault() : 
            FormatConverter.getConverter(defaultFormat);
        _context.setServletContext(getServletContext());
        _context.init();
    }
    
    protected void addCookie(HttpServletResponse response, AuthToken token, String path)
    {        
        Cookie cookie = new Cookie(token.getName(), token.getValue());
        cookie.setMaxAge(token.getMaxAge());
        cookie.setPath(path);
        response.addCookie(cookie);        
    }
    
    protected Object handle(HttpServletRequest request, Map<String,String> params) throws Exception
    {
        String path = request.getPathInfo();        
        return path!=null && path.length()>1 ? RESTService.getInstance().handle(_context, request, 
                params) : RPCService.getInstance().handle(_context, request, params);
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        Map<String,String> params = RequestUtil.getParams(request);                        
        Object resource = null;
        try
        {
            resource = handle(request, params);
            if(resource instanceof AuthToken)
            {
                if(_cookiePath==null)
                {
                    _cookiePath = getServletContext().getContextPath().length()<2 ? request.getServletPath() : 
                        getServletContext().getContextPath() + request.getServletPath();
                }
                addCookie(response, (AuthToken)resource, _cookiePath);
            }
        }
        catch(WebServiceException wse)
        {
            resource = wse;
        }
        catch(NullPointerException npe)
        {
            npe.printStackTrace();
            resource = null;
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            resource = new ExceptionWrapper(e);
        }
        writeResponse(response, resource, params);
    }
    
    protected void writeResponse(HttpServletResponse response, Object resource, 
            Map<String, String> params) throws ServletException, IOException
    {
        FormatConverter converter = FormatConverter.getConverter(params.get("format"));
        response.setContentType(converter.getContentType());
        ServletOutputStream out = response.getOutputStream();
        out.write(converter.toString(resource, params.get("callback")).getBytes());
    }
    
}
