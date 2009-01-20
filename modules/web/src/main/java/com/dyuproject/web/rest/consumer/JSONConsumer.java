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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.util.ajax.JSON;

import com.dyuproject.util.reflect.ReflectUtil;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidatingConsumer;
import com.dyuproject.web.rest.WebContext;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public class JSONConsumer extends JSON implements ValidatingConsumer
{
    
    public static final String CONTENT_TYPE = "text/json";
    
    public static final String STRIP_OUTER_COMMENT_KEY = "jsonc.strip_outer_comment";
    
    public static final String DEFAULT_ERROR_MSG_KEY = "jsonc.default_error_msg";
    
    static final String CACHE_KEY = SimpleParameterConsumer.class + ".cache";

    private static final Object[] __getterArg = new Object[]{};
    
    private static final Map<Class<?>, NumberType> __numberTypes = new HashMap<Class<?>, NumberType>();
    
    private static final Log _log = LogFactory.getLog(JSONConsumer.class);
    
    public static NumberType getNumberType(Class<?> clazz)
    {
        return __numberTypes.get(clazz);
    }
    
    private static String __defaultErrorMsg = "Please enter the fields correctly.";
    
    public static void setDefaultErrorMsg(String errorMsg)
    {
        __defaultErrorMsg = errorMsg;
    }
    
    private String _httpMethod;
    private Class<?> _pojoClass;
    private String _outputType;
    private Map<?,?> _initParams;
    
    private WebContext _webContext;
    
    private Map<Class<?>,Convertor> _cache;
    private Convertor _pojoConvertor, _mapConvertor;
    
    private boolean _stripOuterComment;
    private String _defaultErrorMsg;
    
    public JSONConsumer()
    {
        
    }
    
    public String getContentType()
    {
        return CONTENT_TYPE;
    }
    
    public String getHttpMethod()
    {
        return _httpMethod;
    }
    
    public String getDefaultErrorMsg()
    {
        return _defaultErrorMsg;
    }
    
    public void preConfigure(String httpMethod, Class<?> pojoClass, String outputType, 
            Map<?, ?> initParams)
    {
        if(_pojoClass!=null)
            throw new IllegalStateException("pojoClass already set.");
        if(pojoClass==null)
            throw new IllegalStateException("pojoClass must be provided.");
        
        _httpMethod = httpMethod;
        _pojoClass = pojoClass;
        _outputType = outputType;
        _initParams = initParams;
    }
    
    public void destroy(WebContext webContext)
    {        
        
    }
    
    public void init(WebContext webContext)
    {
        if(_webContext!=null || webContext==null)
            return;
        
        _webContext = webContext;
        
        _cache = (Map<Class<?>,Convertor>)_webContext.getAttribute(CACHE_KEY);
        if(_cache==null)
        {
            _cache = new HashMap<Class<?>,Convertor>();
            webContext.setAttribute(CACHE_KEY, _cache);
        }
        
        if("map".equals(_outputType))
        {
            _mapConvertor = new JSON.Convertor()
            {
                public Object fromJSON(Map map)
                {                    
                    return map;
                }
                public void toJSON(Object obj, Output out)
                {                    
                    _pojoConvertor.toJSON(obj, out);
                }                
            };
        }
        
        _pojoConvertor = _cache.get(_pojoClass);
        if(_pojoConvertor!=null)
            return;
        
        if(_initParams==null || _initParams.isEmpty())
        {
            _pojoConvertor = new PojoConvertor(_pojoClass);
            return;
        }
        
        _stripOuterComment = "true".equals(_initParams.get(STRIP_OUTER_COMMENT_KEY));
        _defaultErrorMsg = (String)_initParams.get(DEFAULT_ERROR_MSG_KEY);
        if(_defaultErrorMsg==null)
            _defaultErrorMsg = __defaultErrorMsg;
        
        Map<String,Method> methods = ReflectUtil.getSetterMethods(_pojoClass);
        Map<String,ValidatingSetter> vSetters = new HashMap<String,ValidatingSetter>(1 + 
                new Double(methods.size()/.75).intValue());
        
        for(Map.Entry<String, Method> entry : methods.entrySet())
        {
            String field = entry.getKey();
            String errorMsg = (String)_initParams.get(field);
            boolean required = true;
            if(errorMsg!=null)
            {
                if(errorMsg.length()==0)
                {
                    errorMsg = AbstractConsumer.getDefaultErrorMsg(field);
                    required = false;
                }
                String validator = (String)_initParams.get(field + ".validator");
                FieldValidator fv = null;
                if(validator!=null)
                {
                    try
                    {
                        fv = (FieldValidator)AbstractConsumer.newObjectInstance(validator);
                    }
                    catch(Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                vSetters.put(field, new ValidatingSetter(field, entry.getValue(), required, fv, 
                        errorMsg));
            }            
        }
        
        _pojoConvertor = new ValidatingPojoConvertor(_pojoClass, vSetters, _mapConvertor==null);
        methods.clear();
        methods = null;        
    }
    
    public boolean consume(RequestContext requestContext) throws ServletException, IOException
    {
        Object result = null;
        try
        {
            result = parse(new ReaderSource(requestContext.getRequest().getReader()), 
                    _stripOuterComment);
            if(result instanceof Map && _mapConvertor==null)
                result = _pojoConvertor.fromJSON((Map)result);
        }
        catch(RequiredFieldException e)
        {
            generateResponse(e.getSetter().getErrorMsg(), requestContext);
        }
        catch(ValidationException e)
        {
            generateResponse(e.getSetter().getErrorMsg(), requestContext);
        }
        catch(Exception e)
        {
            generateResponse(_defaultErrorMsg, requestContext);
            _log.warn("Consume error.", e);
        }
        if(result==null)
            return false;
        
        requestContext.getRequest().setAttribute(OUTPUT_KEY, result);
        return true;
    }
    
    protected void generateResponse(String message, RequestContext rc)
    throws IOException, ServletException
    {
        HashMap<String,String> map = new HashMap<String,String>(2);
        map.put(ERROR_MSG_KEY, message);
        rc.getResponse().setContentType(getContentType());
        rc.getResponse().getWriter().write(toJSON(map));
    }
    
    protected Convertor getConvertor(Class clazz)
    {
        if(_pojoClass==clazz)
            return _mapConvertor==null ? _pojoConvertor : _mapConvertor;
        
        Convertor convertor = _cache.get(clazz);
        if(convertor==null)
            throw new IllegalArgumentException("unregistered pojo: " + clazz.getName());
        
        return convertor;
    }
    
    @SuppressWarnings("serial")
    static class ValidationException extends RuntimeException
    {        
        ValidatingSetter _setter;
        
        ValidationException(String msg, ValidatingSetter setter)
        {
            super(msg);
            _setter = setter;
        }
        
        public ValidatingSetter getSetter()
        {
            return _setter;
        }
    }
    
    @SuppressWarnings("serial")
    static class RequiredFieldException extends ValidationException
    {
        RequiredFieldException(String msg, ValidatingSetter setter)
        {
            super(msg, setter);            
        }        
    }
    
    public static class PojoConvertor implements JSON.Convertor
    {
        
        protected Class<?> _pojoClass;
        protected Map<String,Method> _getters;
        protected Map<String,Setter> _setters;
        protected boolean _fromJSON;
        
        public PojoConvertor(Class<?> pojoClass)
        {
            this(pojoClass, true);
        }
        
        public PojoConvertor(Class<?> pojoClass, boolean fromJSON)
        {
            _pojoClass = pojoClass;
            _fromJSON = fromJSON;
        }
        
        protected void init()
        {
            Method[] methods = _pojoClass.getMethods();
            for (int i=0;i<methods.length;i++)
            {
                Method m=methods[i];
                if (!Modifier.isStatic(m.getModifiers()) && m.getDeclaringClass()!=Object.class)
                {
                    String name=m.getName();
                    switch(m.getParameterTypes().length)
                    {
                        case 0:
                            
                            if(m.getReturnType()!=null)
                            {
                                if (name.startsWith("is"))
                                    name=name.substring(2,3).toLowerCase()+name.substring(3);
                                else if (name.startsWith("get"))
                                    name=name.substring(3,4).toLowerCase()+name.substring(4);
                                else 
                                    break;
                                addGetter(name, m);
                            }
                            break;
                        case 1:
                            if (name.startsWith("set"))
                            {
                                name=name.substring(3,4).toLowerCase()+name.substring(4);
                                addSetter(name, m);
                            }
                            break;                
                    }
                }
            }
        }
        
        protected void addGetter(String name, Method method)
        {
            if(_getters==null)
                _getters = new HashMap<String,Method>();
            
            _getters.put(name, method);
        }
        
        protected void addSetter(String name, Method method)
        {
            if(_setters==null)
                _setters = new HashMap<String,Setter>();
            
            _setters.put(name, new Setter(name, method));
        }
        
        protected Setter getSetter(String name)
        {
            return _setters.get(name);
        }

        public Object fromJSON(Map map)
        {
            Object obj = null;
            try
            {
                obj = _pojoClass.newInstance();
            }
            catch(Exception e)
            {
                // TODO return Map instead?
                throw new RuntimeException(e);
            }
            
            for(Iterator<Map.Entry<?, ?>> iterator = map.entrySet().iterator(); iterator.hasNext();)
            {
                Map.Entry<?, ?> entry = iterator.next();
                Setter setter = getSetter((String)entry.getKey());
                if(setter!=null)
                {
                    try
                    {
                        setter.invoke(obj, entry.getValue());                    
                    }
                    catch(Exception e)
                    {
                        // TODO throw exception?
                        _log.warn(_pojoClass.getName() + " property '"+setter.getPropertyName() + 
                                "' not set.", e);
                    }
                }
            }
            
            return obj;
        }

        public void toJSON(Object obj, Output out)
        {
            if(_fromJSON)
                out.addClass(_pojoClass);
            for(Map.Entry<String,Method> entry : _getters.entrySet())
            {            
                try
                {
                    out.add(entry.getKey(), entry.getValue().invoke(obj, __getterArg));                    
                }
                catch(Exception e)
                {
                    // TODO throw exception?
                    _log.warn(_pojoClass.getName() + " property '" + entry.getKey() + 
                            "' excluded.", e);               
                }
            }
        }        
    }
    
    public static class ValidatingPojoConvertor extends PojoConvertor
    {
        
        private Map<String,ValidatingSetter> _vSetters;
        
        public ValidatingPojoConvertor(Class<?> pojoClass, Map<String, ValidatingSetter> vSetters)
        {
            this(pojoClass, vSetters, true);
        }
        
        public ValidatingPojoConvertor(Class<?> pojoClass, Map<String, ValidatingSetter> vSetters, 
                boolean fromJSON)
        {
            super(pojoClass, fromJSON);
            _vSetters = vSetters;
        }
        
        protected void addGetter(String name, Method method)
        {
            if(_getters==null)
                _getters = new HashMap<String,Method>(_vSetters.size());
            
            _getters.put(name, method);
        }
        
        protected void addSetter(String name, Method method)
        {
            // not needed
        }
        
        public Object fromJSON(Map map)
        {
            Object obj = null;
            
            for(Map.Entry<String, ValidatingSetter> entry : _vSetters.entrySet())
            {
                String field = entry.getKey();
                ValidatingSetter setter = entry.getValue();
                Object value = map.get(field);
                if(value==null && !map.containsKey(field) && setter.isRequired())
                    throw new RequiredFieldException(setter.getErrorMsg(), setter);
                
                try
                {
                    setter.invoke(obj, entry.getValue());                    
                }
                catch(Exception e)
                {
                    // TODO throw exception?
                    _log.warn(_pojoClass.getName() + " property '"+setter.getPropertyName() + 
                            "' not set.", e);
                }
            }            
            return obj;
        }
        
    }
    
    public static class Setter
    {
        private String _propertyName;
        private Method _method;
        private NumberType _numberType;
        
        public Setter(String propertyName, Method method)
        {
            _propertyName = propertyName;
            _method = method;
            _numberType = __numberTypes.get(method.getParameterTypes()[0]);
        }
        
        public String getPropertyName()
        {
            return _propertyName;
        }
        
        public Method getMethod()
        {
            return _method;
        }
        
        public NumberType getNumberType()
        {
            return _numberType;
        }
        
        public boolean isPropertyNumber()
        {
            return _numberType!=null;
        }
        
        public void invoke(Object obj, Object value) throws IllegalArgumentException, 
            IllegalAccessException, InvocationTargetException
        {
            if(_numberType!=null && value instanceof Number)
                _method.invoke(obj, new Object[]{_numberType.getActualValue((Number)value)});
            else
                _method.invoke(obj, new Object[]{value});
        }
    }
    
    public static class ValidatingSetter extends Setter
    {
        private boolean _required;
        private FieldValidator _validator;
        private String _errorMsg;

        public ValidatingSetter(String propertyName, Method method, boolean required, 
                FieldValidator validator, String errorMsg)
        {
            super(propertyName, method);
            _required = required;
            _validator = validator;
            _errorMsg = errorMsg;
        }
        
        public boolean isRequired()
        {
            return _required;
        }
        
        public FieldValidator getValidator()
        {
            return _validator;
        }
        
        public String getErrorMsg()
        {
            return _errorMsg;
        }
        
        public void invoke(Object obj, Object value) throws IllegalArgumentException, 
        IllegalAccessException, InvocationTargetException
        {
            try
            {
                if(_validator==null)
                    super.invoke(obj, value);
                else
                {
                    String errorMsg = _validator.getErrorMsg(value);
                    if(errorMsg==null)
                        super.invoke(obj, value);
                    else
                        throw new ValidationException(errorMsg, this);
                }                    
            }
            catch(Exception e)
            {
                throw new ValidationException(getErrorMsg(), this);
            }
        }
    }
    
    public interface NumberType
    {        
        public Object getActualValue(Number number);     
    }
    
    public static final NumberType SHORT = new NumberType()
    {
        public Object getActualValue(Number number)
        {            
            return new Short(number.shortValue());
        } 
    };

    public static final NumberType INTEGER = new NumberType()
    {
        public Object getActualValue(Number number)
        {            
            return new Integer(number.intValue());
        }
    };
    
    public static final NumberType FLOAT = new NumberType()
    {
        public Object getActualValue(Number number)
        {            
            return new Float(number.floatValue());
        }      
    };

    public static final NumberType LONG = new NumberType()
    {
        public Object getActualValue(Number number)
        {            
            return number instanceof Long ? number : new Long(number.longValue());
        }     
    };

    public static final NumberType DOUBLE = new NumberType()
    {
        public Object getActualValue(Number number)
        {            
            return number instanceof Double ? number : new Double(number.doubleValue());
        }       
    };

    static
    {
        __numberTypes.put(Short.class, SHORT);
        __numberTypes.put(Short.TYPE, SHORT);
        __numberTypes.put(Integer.class, INTEGER);
        __numberTypes.put(Integer.TYPE, INTEGER);
        __numberTypes.put(Long.class, LONG);
        __numberTypes.put(Long.TYPE, LONG);
        __numberTypes.put(Float.class, FLOAT);
        __numberTypes.put(Float.TYPE, FLOAT);
        __numberTypes.put(Double.class, DOUBLE);
        __numberTypes.put(Double.TYPE, DOUBLE);
    }
    
}
