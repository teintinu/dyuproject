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

package com.dyuproject.web.ws.rest;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.dyuproject.util.Delim;
import com.dyuproject.web.HttpMethod;
import com.dyuproject.web.ws.WebService;
import com.dyuproject.web.ws.WebServiceContext;
import com.dyuproject.web.ws.WebServiceFilter;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.AccessDenied;
import com.dyuproject.web.ws.error.NotSupported;
import com.dyuproject.web.ws.error.ResourceUnavailable;

/**
 * @author David Yu
 */

public class RESTService implements WebService
{
    
    public static final String NUMBER_REGEX  = "^\\d*$";
    private static final RESTService __instance = new RESTService();
    
    public static RESTService getInstance()
    {
        return __instance;
    }
    
    static boolean isNumber(String str)
    {
        return str.matches(NUMBER_REGEX);
    }
    
    private RESTService()
    {
        
    }

    public Object handle(WebServiceContext context, HttpServletRequest request, Map<String, String> params) throws Exception
    {
        String pathInfo = request.getPathInfo();
        int last = pathInfo.length()-1;
        if(pathInfo.charAt(last)=='/')
            pathInfo = pathInfo.substring(1, last);
        else
            pathInfo = pathInfo.substring(1);
        
        String[] tokens = Delim.SLASH.split(pathInfo);
        
        if(tokens.length==0)
            return ResourceUnavailable.getInstance();
        
        // .json, .xml, .rss, or .atom
        String lastToken = tokens[tokens.length-1];
        String format = context.getDefaultGenerator().getFormat();
        int formatIdx = lastToken.lastIndexOf('.');
        if(formatIdx!=-1)
        {            
            tokens[tokens.length-1] = lastToken.substring(0, formatIdx);            
            pathInfo = pathInfo.substring(0, pathInfo.length()-lastToken.length()+formatIdx);
            format = lastToken.substring(formatIdx+1);
            lastToken = tokens[tokens.length-1];            
        }
        if(lastToken.length()==0)
            return ResourceUnavailable.getInstance();
        
        params.put("format", format);
        request.setAttribute("restPath", isNumber(lastToken) ? pathInfo.substring(0, 
                pathInfo.lastIndexOf('/')+1).concat("id.").concat(format) : pathInfo);
        request.setAttribute("pathInfo", tokens);
        //params.put("pathInfo", pathInfo);

        String root = tokens[0];
        WebServiceHandler handler = context.getHandler(root);
        if(handler==null)
            return ResourceUnavailable.getInstance();
        HttpMethod method = null;
        // script requests
        if(context.isAllowMethodOverride())
        {
            String m = params.get("_method");
            method = m==null ? HttpMethod.get(request.getMethod()) : HttpMethod.get(m);
        }
        else
            method = HttpMethod.get(request.getMethod());
        if(method==null)
            return NotSupported.getInstance();
        params.put("method", method.getName());
        Object result = null;
        WebServiceFilter filter = context.getFilter();
        try
        {
            result = filter==null ? handler.handle(tokens, params) : 
                (filter.preHandle(WebServiceFilter.REST, request.getCookies(), tokens, params, 0, 1) ? 
                        handler.handle(tokens, params) : AccessDenied.getInstance());
        }
        catch(NumberFormatException e)
        {
            result = ResourceUnavailable.getInstance();
        }
        catch(ArrayIndexOutOfBoundsException ae)
        {
            result = ResourceUnavailable.getInstance();
        }
        finally
        {
            if(filter!=null)
                filter.postHandle(WebServiceFilter.REST, 1);
        }        
        return result;
    }

}
