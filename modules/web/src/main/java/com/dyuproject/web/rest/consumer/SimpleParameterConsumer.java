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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.util.reflect.ParameterType;
import com.dyuproject.util.reflect.ReflectUtil;
import com.dyuproject.util.reflect.ParameterType.SimpleField;
import com.dyuproject.web.rest.RequestContext;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public class SimpleParameterConsumer extends AbstractConsumer
{
    
    public static final String DEFAULT_REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_DISPATCHER_NAME = "jsp";
    
    public static final String STRING_ATTRIBUTES = "spc.string_attributes";
    
    public static final String POJO_ATTR_NAME = "spc.pojo_attr_name";
    public static final String DISPATCHER_NAME = "spc.dispatcher_name";
    public static final String DISPATCH_URI = "spc.dispatch_uri";
    
    static final String CACHE_KEY = SimpleParameterConsumer.class + ".cache";
    
    private static final Logger log = LoggerFactory.getLogger(SimpleParameterConsumer.class);
    
    private Map<String,Included> _includedFields;
    private String _pojoAttrName;
    private int _initialSize;
    private boolean _pojo;
    
    public SimpleParameterConsumer()
    {
        
    }

    protected String getDefaultDispatcherName()
    {        
        return DEFAULT_DISPATCHER_NAME;
    }

    protected String getDefaultRequestContentType()
    {        
        return DEFAULT_REQUEST_CONTENT_TYPE;
    }

    protected String getDefaultResponseContentType()
    {
        return DEFAULT_RESPONSE_CONTENT_TYPE;
    }

    public static String generateDefaultPojoAttrName(Class<?> clazz)
    {
        return ReflectUtil.toProperty(0, clazz.getSimpleName());
    }

    protected void init()
    {
        initDefaults();
        _dispatchUri = getInitParam(DISPATCH_URI);
        if(_dispatchUri==null)
            throw new IllegalStateException(DISPATCH_URI + " must be provided.");
        
        _pojo = !"map".equalsIgnoreCase(getConsumeType());
        
        _pojoAttrName = getInitParam(POJO_ATTR_NAME);
        if(_pojoAttrName==null)
            _pojoAttrName = generateDefaultPojoAttrName(_pojoClass);

        Map<Class<?>,Map<String,SimpleField>> cache = 
            (Map<Class<?>,Map<String,SimpleField>>)getWebContext().getAttribute(CACHE_KEY);
        if(cache==null)
        {
            cache = new HashMap<Class<?>,Map<String,SimpleField>>();
            getWebContext().addAttribute(CACHE_KEY, cache);
        }
        
        Map<String,SimpleField> simpleFields = cache.get(_pojoClass);
        if(simpleFields==null)
        {
            simpleFields = ParameterType.getSimpleFieldSetters(_pojoClass);
            cache.put(_pojoClass, simpleFields);
        }
        
        _includedFields = new HashMap<String,Included>(_initialSize);
        for(Map.Entry<String,SimpleField> entry : simpleFields.entrySet())
        {
            String field = entry.getKey();
            boolean included = !"false".equalsIgnoreCase(getFieldParam(field+".included"));
            if(!included)
                continue;
            
            boolean required = !"false".equalsIgnoreCase(getFieldParam(field+".required"));            
            String errorMsg = getFieldParam(field + "." + MSG);
            String validator = getFieldParam(field + ".validator");
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
    }
    
    public boolean merge(Object pojo, RequestContext rc)
    {
        if(_pojo)
            throw new IllegalStateException("Cannot merge. init-param '" + CONSUME_TYPE + "' must be 'map'.");
        
        if(pojo==null || pojo.getClass()!=_pojoClass)
            throw new IllegalArgumentException("The pojo must be of type: " + _pojoClass.getName());
        
        return merge(pojo, (Map<?,?>)rc.getRequest().getAttribute(CONSUMED_OBJECT));
    }
    
    public boolean merge(Object pojo, Map<?,?> props)
    {
        int mergeCount = 0;
        for(Iterator<?> iterator = props.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
            Included included = _includedFields.get(entry.getKey());
            if(included!=null)
            {
                try
                {
                    included.getSimpleField().getMethod().invoke(pojo, new Object[]{entry.getValue()});
                    mergeCount++;
                }
                catch(Exception e)
                {
                    log.warn(e.getMessage(), e);
                }
            }
        }        
        return mergeCount!=0;
    }

    protected void dispatch(String errorMsg, RequestContext rc, Object pojo) 
    throws ServletException, IOException
    {
        if(pojo!=null)
            rc.getRequest().setAttribute(_pojoAttrName, pojo);
        dispatch(errorMsg, rc, _dispatchUri);
    }
    
    public boolean consume(RequestContext requestContext) throws ServletException, IOException
    {        
        return _pojo ? consumeToPojo(requestContext) : consumeToMap(requestContext);
    }
    
    public boolean consumeToPojo(RequestContext requestContext) 
    throws ServletException, IOException
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
                dispatch(included.getErrorMsg(), requestContext, pojo);
                return false;
            }
            value = value.trim();
            if(value.length()==0)
            {
                if(!included.isRequired())
                    continue;
                dispatch(included.getErrorMsg(), requestContext, pojo);
                return false;
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            
            String validationErrorMsg = included.getErrorMsg(actualValue);
            if(validationErrorMsg!=null)
            {
                dispatch(validationErrorMsg, requestContext, pojo);
                return false;
            }
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
                dispatch(included.getErrorMsg(), requestContext, pojo);
                return false;
            }
            catch (IllegalAccessException e)
            {
                log.warn(field + " not set.", e);
            } 
            catch (InvocationTargetException e)
            {
                log.warn(field + " not set.", e);
            }            
        }
        
        request.setAttribute(CONSUMED_OBJECT, pojo);
        return true;
    }
    
    public boolean consumeToMap(RequestContext requestContext) 
    throws ServletException, IOException
    {
        HttpServletRequest request = requestContext.getRequest();
        HashMap<String,Object> output = null;
        for(Map.Entry<String, Included> entry : _includedFields.entrySet())
        {
            String field = entry.getKey();
            Included included = entry.getValue();
            String value = request.getParameter(field);
            if(value==null)
            {
                if(!included.isRequired())
                    continue;
                dispatch(included.getErrorMsg(), requestContext, output);
                return false;
            }
            value = value.trim();
            if(value.length()==0)
            {
                if(!included.isRequired())
                    continue;
                dispatch(included.getErrorMsg(), requestContext, output);
                return false;
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            String validationErrorMsg = included.getErrorMsg(actualValue);
            if(validationErrorMsg!=null)
            {
                dispatch(validationErrorMsg, requestContext, output);
                return false;
            }
            if(output==null)
                output = new HashMap<String,Object>(_initialSize);
            
            output.put(field, actualValue);
        }
        request.setAttribute(CONSUMED_OBJECT, output);
        return true;
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

}
