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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dyuproject.util.reflect.ParameterType;
import com.dyuproject.util.reflect.ReflectUtil;
import com.dyuproject.util.reflect.ParameterType.SimpleField;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidationException;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public class SimpleParameterConsumer extends AbstractConsumer
{
    
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "text/html";
    
    static final String CACHE_KEY = SimpleParameterConsumer.class + ".cache";

    public static String generateDefaultPojoAttrName(Class<?> clazz)
    {
        return ReflectUtil.toProperty(0, clazz.getSimpleName());
    }    
    
    static Map<String,Included> newIncludedFields(int size, Map<String,SimpleField> simpleFields, Map<?,?> fieldParams)
    {
        Map<String,Included> _includedFields = new HashMap<String,Included>(size);
        for(Map.Entry<String,SimpleField> entry : simpleFields.entrySet())
        {
            String field = entry.getKey();
            boolean included = !"false".equalsIgnoreCase((String)fieldParams.get(field+".included"));
            if(!included)
                continue;
            
            boolean required = !"false".equalsIgnoreCase((String)fieldParams.get(field+".required"));
            String errorMsg = (String)fieldParams.get(field + ".error_msg");
            String validator = (String)fieldParams.get(field + ".validator");
            FieldValidator fv = null;
            if(validator!=null)
            {
                try
                {
                    fv = (FieldValidator)newObjectInstance(validator);
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            if(errorMsg==null || errorMsg.length()==0)
                errorMsg = AbstractConsumer.getDefaultErrorMsg(field);
            
            _includedFields.put(field, new Included(entry.getValue(), required, fv, errorMsg));
        }
        return _includedFields;
    }
    
    private Map<String,Included> _includedFields;
    private int _initialSize;
    
    public SimpleParameterConsumer()
    {
        
    }

    protected String getDefaultContentType()
    {        
        return DEFAULT_CONTENT_TYPE;
    }

    protected String getDefaultResponseContentType()
    {
        return DEFAULT_RESPONSE_CONTENT_TYPE;
    }

    protected void init()
    {
        Map<String, CacheEntry> cache = 
            (Map<String,CacheEntry>)getWebContext().getAttribute(CACHE_KEY);
        CacheEntry ce = null;
        if(cache==null)
        {
            cache = new HashMap<String,CacheEntry>();            
            getWebContext().addAttribute(CACHE_KEY, cache);
        }
        
        if(ce==null)
        {
            ce = new CacheEntry();            
            ce._simpleFields = ParameterType.getSimpleFieldSetters(_pojoClass);
            ce._fieldParams = _fieldParams;
            ce._includedFields = newIncludedFields(16, ce._simpleFields, _fieldParams);            
            cache.put(_pojoClass.getName(), ce);
            _includedFields = ce._includedFields;
            _initialSize = (int)(ce._includedFields.size()/.75 + 1);
        }
        else if(ce._fieldParams.hashCode()==_fieldParams.hashCode())
            _includedFields = ce._includedFields;
        else
            _includedFields = newIncludedFields(_initialSize, ce._simpleFields, _fieldParams);
    }

    
    public boolean merge(Object pojo, RequestContext rc) throws IOException, ValidationException
    {
        return setProps(pojo, rc)!=0;
    }

    public Object consume(RequestContext rc) throws IOException, ValidationException
    {
        return getPojo(rc);
    }
    
    int setProps(Object pojo, RequestContext requestContext) throws IOException, ValidationException
    {
        int count = 0;
        HttpServletRequest request = requestContext.getRequest();
        for(Map.Entry<String, Included> entry : _includedFields.entrySet())
        {
            String field = entry.getKey();
            Included included = entry.getValue();
            String value = request.getParameter(field);
            if(value==null)
            {
                if(!included.isRequired())
                    continue;
                
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            value = value.trim();
            if(value.length()==0)
            {
                if(!included.isRequired())
                    continue;
                
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            
            String errorMsg = included.getErrorMsg(actualValue);
            if(errorMsg!=null)
                throw new ValidationException(errorMsg, field, pojo);
            try
            {                
                included.getSimpleField().getMethod().invoke(pojo, new Object[]{actualValue});
                count++;
            }
            catch(IllegalArgumentException e)
            {
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            } 
            catch (InvocationTargetException e)
            {
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }            
        }
        return count;
    }
    
    Object getPojo(RequestContext requestContext) throws IOException, ValidationException
    {        
        HttpServletRequest request = requestContext.getRequest();
        Object pojo = null;
        for(Map.Entry<String, Included> entry : _includedFields.entrySet())
        {
            String field = entry.getKey();
            Included included = entry.getValue();
            String value = request.getParameter(field);
            if(value==null)
            {
                if(!included.isRequired())
                    continue;
                
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            value = value.trim();
            if(value.length()==0)
            {
                if(!included.isRequired())
                    continue;
                
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            
            String errorMsg = included.getErrorMsg(actualValue);
            if(errorMsg!=null)
                throw new ValidationException(errorMsg, field, pojo);
            // lazy
            if(pojo==null)
            {
                try
                {
                    pojo = _pojoClass.newInstance();
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            try
            {                
                included.getSimpleField().getMethod().invoke(pojo, new Object[]{actualValue});
            }
            catch(IllegalArgumentException e)
            {
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            } 
            catch (InvocationTargetException e)
            {
                throw new ValidationException(included.getErrorMsg(), field, pojo);
            }            
        }
        return pojo;
    }
    
    static class Included
    {
        
        private SimpleField _simpleField;        
        private boolean _required;
        private FieldValidator _validator;
        private String _errorMsg;
        
        Included(SimpleField simpleField, boolean required, FieldValidator validator, 
                String errorMsg)
        {
            _simpleField = simpleField;
            _required = required;
            _validator = validator;
            _errorMsg = errorMsg;
        }
        
        SimpleField getSimpleField()
        {
            return _simpleField;
        }
        
        boolean isRequired()
        {
            return _required;
        }
        
        String getErrorMsg()
        {
            return _errorMsg;
        }
        
        String getErrorMsg(Object value)
        {
            return value==null || _validator==null ? null : _validator.validate(value);
        }
        
    }
    
    static class CacheEntry
    {
        Map<String,SimpleField> _simpleFields;
        Map<String,Included> _includedFields;
        Map<?,?> _fieldParams;
    }

}
