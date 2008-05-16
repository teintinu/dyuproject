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

package com.dyuproject.web.ws.rpc;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.dyuproject.util.Delim;
import com.dyuproject.util.format.FormatConverter;
import com.dyuproject.web.ws.WebService;
import com.dyuproject.web.ws.WebServiceContext;
import com.dyuproject.web.ws.WebServiceFilter;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.AccessDenied;
import com.dyuproject.web.ws.error.NoSuchMethod;
import com.dyuproject.web.ws.error.ParameterRequired;
import com.dyuproject.web.ws.error.ResourceUnavailable;

/**
 * @author David Yu
 */

public class RPCService implements WebService
{
    
    private static final RPCService __instance  = new RPCService();
    
    public static RPCService getInstance()
    {
        return __instance;
    }
    
    private RPCService()
    {
        
    }

    public Object handleBatch(WebServiceContext context, String[] methods, Cookie[] cookies, Map<String,String> params)
    throws Exception
    {
        ArrayList<Object> result = new ArrayList<Object>();
        int batchLength = methods.length;
        WebServiceFilter filter = context.getFilter();
        try
        {
            for(int i=0; i<batchLength; i++)
            {
                String methodParam = methods[i];
                int idx = methodParam.indexOf('.');
                String name = methodParam.substring(0, idx);                
                WebServiceHandler handler = context.getHandler(name);
                if(handler==null)
                    return NoSuchMethod.getInstance();
                String method = methodParam.substring(idx+1);
                String[] pathInfo = new String[]{name,method};
                
                result.add(filter==null ? handler.handle(pathInfo, params) : 
                (filter.preHandle(WebServiceFilter.RPC, cookies, pathInfo, params, 0, 1) ? 
                        handler.handle(pathInfo, params) : AccessDenied.getInstance()));
            }
        }
        finally 
        {
            if(filter!=null)
                filter.postHandle(WebServiceFilter.RPC, batchLength);
        }                
        return result;        
    }
    
    public Object handle(WebServiceContext context, HttpServletRequest request, Map<String,String> params) throws Exception    
    {
        String methodParam = params.get("method");
        if(methodParam==null)        
            return ParameterRequired.getInstance();
        String[] methods = Delim.COMMA.split(methodParam);
        if(methods.length>1)           
            return handleBatch(context, methods, request.getCookies(), params);
        int idx = methodParam.indexOf('.');
        String name = methodParam.substring(0, idx);       
        WebServiceHandler handler = context.getHandler(name);
        if(handler==null)
            return NoSuchMethod.getInstance(); 
        String method = methodParam.substring(idx+1);
        String[] pathInfo = new String[]{name,method};
        String format = params.get("format");
        if(format!=null)
        {
            if(format.hashCode()!=FormatConverter.JSON.hashCode() && 
                    format.hashCode()!=FormatConverter.XML.hashCode())
            {
                return ResourceUnavailable.getInstance();
            }
        }        
        
        Object result = null;
        WebServiceFilter filter = context.getFilter();
        try
        {
            result = filter==null ? handler.handle(pathInfo, params) : 
                (filter.preHandle(WebServiceFilter.RPC, request.getCookies(), pathInfo, params, 0, 1) ? 
                        handler.handle(pathInfo, params) : AccessDenied.getInstance());
        }
        finally
        {
            if(filter!=null)
                filter.postHandle(WebServiceFilter.RPC, 1); 
        }
        return result;
    }
    
}
