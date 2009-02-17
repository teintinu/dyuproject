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
import java.util.Map;
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
            writeSimpleResponse(errorMsg, true, request, response);
        else
            write(data, request, response);
    }
    
    public void write(Object data, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        response.getWriter().write(toJSON(data));
    }
    
    public void writeSimpleResponse(String msg, boolean error, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        write(new SimpleResponse(msg, error), request, response);
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
    
    public static class SimpleResponse implements Generator
    {
        public static final String MSG = "msg";
        public static final String ERROR = "error";
        
        protected String _msg;
        protected boolean _error;
        
        public SimpleResponse(String msg)
        {
            _msg = msg;
        }
        
        public SimpleResponse(String msg, boolean error)
        {
            _msg = msg;
            _error = error;
        }
        
        public String getMsg()
        {
            return _msg;
        }
        
        public boolean isError()
        {
            return _error;
        }
        
        public void addJSON(StringBuffer buffer)
        {            
            buffer.append('{').append(MSG).append(':').append(_msg);
            if(_error)
                buffer.append(',').append(ERROR).append(':').append(_error);
            buffer.append('}');
        }
        
        public String toString()
        {
            return _msg;
        }
    }
}
