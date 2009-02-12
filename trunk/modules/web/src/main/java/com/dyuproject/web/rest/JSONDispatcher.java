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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.rest.consumer.JSONConsumer.CachedJSON;
import com.dyuproject.web.rest.consumer.JSONConsumer.PojoConvertor;

/**
 * @author David Yu
 * @created Feb 13, 2009
 */

public class JSONDispatcher extends CachedJSON implements ViewDispatcher
{
    
    public static final String JSON_DATA = "json_data";
    public static final String ERROR_MSG = "error_msg";
    
    public static final Generator EMPTY_RESPONSE_MAP = new Generator()
    {
        public void addJSON(StringBuffer buffer)
        {            
            buffer.append('{').append('}');
        }        
    };    
    
    public JSONDispatcher()
    {
        super(new ConcurrentHashMap<String,Convertor>());
    }
    
    public JSONDispatcher(ConcurrentMap<String,Convertor> cache)
    {
        super(cache);
    }

    public void dispatch(String errorMsg, HttpServletRequest request, HttpServletResponse response) 
    throws ServletException,
            IOException
    {
        Object data = request.getAttribute(JSON_DATA);
        if(data==null)
            writeErrorMsg(errorMsg, request, response);
        else
            writeData(data, request, response);
    }
    
    public void writeErrorMsg(String errorMsg, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        response.getWriter().write(toJSON(errorMsg==null ? EMPTY_RESPONSE_MAP : 
            new ErrorResponse(errorMsg)));
    }
    
    public void writeData(Object data, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        response.getWriter().write(toJSON(data==null ? EMPTY_RESPONSE_MAP : data));
    }
    
    protected Convertor getConvertor(Class clazz)
    {
        Convertor convertor = _cache.get(clazz.getName());
        if(convertor==null)
        {
            convertor = new PojoConvertor(clazz);
            addConvertor(clazz, convertor);
        }
        return convertor;
    }

    public void destroy(WebContext webContext)
    {            
        
    }

    public void init(WebContext webContext)
    {            
        
    }
    
    public static class ErrorResponse implements Generator
    {
        private String _msg;
        
        public ErrorResponse(String msg)
        {
            _msg = msg;
        }
        
        public void addJSON(StringBuffer buffer)
        {            
            buffer.append('{').append(ERROR_MSG).append(':').append(_msg).append('}');
        }        
    }
}