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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dyuproject.util.reflect.ParameterType;
import com.dyuproject.util.reflect.ParameterType.SimpleField;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ViewDispatcher;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public class SimpleParameterConsumer extends AbstractConsumer
{
    
    public static final String REQUEST_CONTENT_TYPE = "application/x-www-url-form-encoded";
    public static final String RESPONSE_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_DISPATCHER = "jsp";
    
    
    public static final String DISPATCHER_NAME = "spc.dispatcher_name";
    public static final String DISPATCH_URI = "spc.dispatch_uri";
    
    static final String CACHE_KEY = SimpleParameterConsumer.class + ".cache";
    
    private static final Log _log = LogFactory.getLog(SimpleParameterConsumer.class);
    
    private Map<String,Included> _includedFields;
    private String _dispatcherName, _dispatchUri;
    private ViewDispatcher _dispatcher;
    private int _initialSize;
    private boolean _pojo;
    
    public SimpleParameterConsumer()
    {
        
    }

    protected void init()
    {        
        _dispatcher = getWebContext().getViewDispatcher(_dispatcherName);
        if(_dispatcher==null)
            throw new IllegalStateException("dispatcher *" + _dispatcherName + "* not found.");
        
        _dispatcherName = (String)_initParams.get(DISPATCHER_NAME);
        if(_dispatcherName==null)
            _dispatcherName = DEFAULT_DISPATCHER;

        _pojo = !"map".equals(_outputType);
        
        _dispatchUri = (String)_initParams.get(DISPATCH_URI);
        if(_dispatchUri==null)
            throw new IllegalStateException(DISPATCH_URI + " must be provided.");
        
        Map<Class<?>,Map<String,Included>> cache = 
            (Map<Class<?>,Map<String,Included>>)getWebContext().getAttribute(CACHE_KEY);
        if(cache==null)
        {
            cache = new HashMap<Class<?>,Map<String,Included>>();
            getWebContext().addAttribute(CACHE_KEY, cache);
        }
        
        _includedFields = cache.get(_pojoClass);
        if(_includedFields!=null)
        {
            _initialSize = 1 + new Double(_includedFields.size()/.75).intValue();
            return;
        }
        
        Map<String,SimpleField> simpleFields = ParameterType.getSimpleFieldSetters(_pojoClass);
        
        _includedFields = new HashMap<String,Included>(_initialSize);
        for(Map.Entry<String, SimpleField> entry : simpleFields.entrySet())
        {
            String field = entry.getKey();
            String errorMsg = (String)_initParams.get(field);
            boolean required = true;
            if(errorMsg!=null)
            {
                if(errorMsg.length()==0)
                {
                    errorMsg = getDefaultErrorMsg(field);
                    required = false;
                }
                String validator = (String)_initParams.get(field + ".validator");
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
                _includedFields.put(field, new Included(entry.getValue(), required, fv, errorMsg));
            }
        }
        cache.put(_pojoClass, _includedFields);
        simpleFields.clear();
        simpleFields = null;
    }

    public boolean consume(RequestContext requestContext) throws ServletException, IOException
    {        
        return _pojo ? consumeToPojo(requestContext) : consumeToMap(requestContext);
    }
    
    public boolean consumeToPojo(RequestContext requestContext) 
    throws ServletException, IOException
    {
        HttpServletRequest request = requestContext.getRequest();
        Object output = null;
        for(Map.Entry<String, Included> entry : _includedFields.entrySet())
        {
            String field = entry.getKey();
            Included included = entry.getValue();
            String value = request.getParameter(field);
            if(value==null)
            {
                if(!included.isRequired())
                    continue;
                dispatch(_dispatcher, _dispatchUri, RESPONSE_CONTENT_TYPE, included.getErrorMsg(), 
                        requestContext);
                return false;
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            String validationErrorMsg = included.getErrorMsg(actualValue);
            if(validationErrorMsg!=null)
            {
                dispatch(_dispatcher, _dispatchUri, RESPONSE_CONTENT_TYPE, validationErrorMsg, 
                        requestContext);
                return false;
            }
            if(output==null)
            {
                try
                {
                    output = _pojoClass.newInstance();
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            try
            {                
                included.getSimpleField().getMethod().invoke(output, new Object[]{actualValue});
            }
            catch(IllegalArgumentException e)
            {
                dispatch(_dispatcher, _dispatchUri, RESPONSE_CONTENT_TYPE, included.getErrorMsg(), 
                        requestContext);
                return false;
            }
            catch (IllegalAccessException e)
            {
                _log.warn(field + " not set.", e);
            } 
            catch (InvocationTargetException e)
            {
                _log.warn(field + " not set.", e);
            }            
        }
        request.setAttribute(OUTPUT_KEY, output);
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
                dispatch(_dispatcher, _dispatchUri, RESPONSE_CONTENT_TYPE, included.getErrorMsg(), 
                        requestContext);
                return false;
            }
            Object actualValue = included.getSimpleField().getType().getActualValue(value);
            String validationErrorMsg = included.getErrorMsg(actualValue);
            if(validationErrorMsg!=null)
            {
                dispatch(_dispatcher, _dispatchUri, RESPONSE_CONTENT_TYPE, validationErrorMsg, 
                        requestContext);
                return false;
            }
            if(output==null)
                output = new HashMap<String,Object>(_initialSize);
            
            output.put(field, actualValue);
        }
        request.setAttribute(OUTPUT_KEY, output);
        return true;
    }

    public String getRequestContentType()
    {        
        return REQUEST_CONTENT_TYPE;
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
            return _validator==null ? null : _validator.getErrorMsg(value);
        }
        
    }

}
