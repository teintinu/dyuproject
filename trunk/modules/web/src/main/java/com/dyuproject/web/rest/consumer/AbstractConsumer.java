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

package com.dyuproject.web.rest.consumer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidatingConsumer;
import com.dyuproject.web.rest.ViewDispatcher;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public abstract class AbstractConsumer extends AbstractLifeCycle implements ValidatingConsumer
{
    
    protected String _httpMethod;
    protected Class<?> _pojoClass;
    protected Map<?,?> _initParams;
    protected String _outputType;
    
    public Class<?> getPojoClass()
    {
        return _pojoClass;
    }
    
    public Map<?,?> getInitParams()
    {
        return _initParams;
    }
    
    public String getHttpMethod()
    {
        return _httpMethod;
    }

    public void preConfigure(String httpMethod, Class<?> pojoClass, String outputType, Map<?,?> initParams)
    {
        if(_pojoClass!=null)
            throw new IllegalStateException("pojoClass already set.");
        if(httpMethod==null)
            throw new IllegalStateException("httpMethod is required.");
        if(pojoClass==null)
            throw new IllegalStateException("pojoClass must be provided.");
        
        _httpMethod = httpMethod;
        _pojoClass = pojoClass;
        _outputType = outputType;
        _initParams = initParams;
    }
    
    public static String getDefaultErrorMsg(String field)
    {
        return getDisplayField(field).append(" must be correctly provided.").toString();
    }
    
    public static StringBuilder getDisplayField(String field)
    {
        StringBuilder buffer = new StringBuilder();
        char[] ch = field.toCharArray();
        char firstLetter = ch[0];
        buffer.append((char)(firstLetter<91 ? firstLetter : firstLetter-32));
        for(int i=1; i<ch.length; i++)
        {
            char c = ch[i];
            if(c<91)
                buffer.append(' ');
            
            buffer.append(c);
        }
        return buffer;
    }
    
    static void dispatch(ViewDispatcher dispatcher, String uri, String contentType, String errorMsg, 
            RequestContext rc) throws ServletException, IOException
    {
        rc.getRequest().setAttribute(ERROR_MSG_KEY, errorMsg);
        rc.getResponse().setContentType(contentType);
        dispatcher.dispatch(uri, rc.getRequest(), rc.getResponse());
    }

    
}