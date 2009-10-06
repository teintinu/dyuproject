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

import java.util.Map;

import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.ValidatingConsumer;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public abstract class AbstractConsumer extends AbstractLifeCycle implements ValidatingConsumer
{
    
    protected String _httpMethod;
    protected String _contentType;
    protected Class<?> _pojoClass;
    protected Map<?,?> _fieldParams;    
    
    public final Class<?> getPojoClass()
    {
        return _pojoClass;
    }
    
    public final Map<?,?> getFieldParams()
    {
        return _fieldParams;
    }

    
    public final String getHttpMethod()
    {
        return _httpMethod;
    }
    
    public final String getContentType()
    {
        return _contentType;
    }
    
    public void preConfigure(String httpMethod, String contentType, Class<?> pojoClass, 
            Map<?,?> fieldParams)
    {
        if(_pojoClass!=null)
            throw new IllegalStateException("pojoClass already set.");
        if(httpMethod==null)
            throw new IllegalStateException("httpMethod is required.");
        if(contentType==null)
            throw new IllegalStateException("contentType is required.");
        if(pojoClass==null)
            throw new IllegalStateException("pojoClass must be provided.");
        if(fieldParams==null)
            throw new IllegalStateException("fieldParams must be provided.");
        
        _httpMethod = httpMethod;
        _contentType = contentType;
        _pojoClass = pojoClass;
        _fieldParams = fieldParams;
        if(_contentType.length()==0)
            _contentType = getDefaultContentType();
    }
    
    protected final String getFieldParam(String name)
    {
        return (String)_fieldParams.get(name);
    }
 
    protected abstract String getDefaultContentType();
    
    public static String getDefaultErrorMsg(String field)
    {
        return getDisplayField(field).insert(0, "Required field: ").toString();
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
    
}
