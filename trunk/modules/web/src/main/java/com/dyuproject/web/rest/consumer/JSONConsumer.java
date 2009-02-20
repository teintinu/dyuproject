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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Convertor;
import org.mortbay.util.ajax.JSON.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.util.reflect.ReflectUtil;
import com.dyuproject.web.rest.JSONDispatcher;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ViewDispatcher;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

@SuppressWarnings("serial")
public class JSONConsumer extends AbstractConsumer
{   
    
    public static final String DEFAULT_DISPATCHER_NAME = "json";    

    public static final Object[] GETTER_ARG = new Object[]{}, NULL_ARG = new Object[]{null};
    
    static final String CACHE_ATTR = SimpleParameterConsumer.class + ".cache";
    
    private static final Map<Class<?>, NumberType> __numberTypes = new HashMap<Class<?>, NumberType>();
    
    private static final Logger log = LoggerFactory.getLogger(JSONConsumer.class);
    
    private static String __defaultContentType = "text/json";
    
    private static String __defaultErrorMsg = "Please enter the fields correctly.";
    
    public static void setDefaultContentType(String defaultContentType)
    {
        if(defaultContentType!=null && defaultContentType.length()!=0)
            __defaultContentType = defaultContentType;
    }
        
    public static void setDefaultErrorMsg(String errorMsg)
    {
        if(errorMsg!=null && errorMsg.length()!=0)
            __defaultErrorMsg = errorMsg;
    }  
    
    public static NumberType getNumberType(Class<?> clazz)
    {
        return __numberTypes.get(clazz);
    }    

    private Convertor _mapConvertor;
    private PojoConvertor _pojoConvertor;
    private CachedJSON _json;
    
    public JSONConsumer()
    {
        
    }

    protected String getDefaultDispatcherName()
    {        
        return DEFAULT_DISPATCHER_NAME;
    }

    protected String getDefaultRequestContentType()
    {        
        return __defaultContentType;
    }

    protected String getDefaultResponseContentType()
    {        
        return __defaultContentType;
    }
    
    protected void init()
    {        
        ConcurrentMap<String,Convertor> cache = 
            (ConcurrentMap<String,Convertor>)getWebContext().getAttribute(CACHE_ATTR);
        if(cache==null)
        {
            ViewDispatcher vd = getWebContext().getViewDispatcher(getDefaultDispatcherName());
            if(vd==null)
            {
                cache = new ConcurrentHashMap<String,Convertor>();
                getWebContext().setViewDispatcher(getDefaultDispatcherName(), 
                        new JSONDispatcher(cache));
            }
            else if(vd instanceof JSONDispatcher)
                cache = ((JSONDispatcher)vd)._cache;
            else
                cache = new ConcurrentHashMap<String,Convertor>();
            
            getWebContext().setAttribute(CACHE_ATTR, cache);
        }
        
        initDefaults();
        
        _json = new CachedJSON(cache)
        {
            protected Convertor getConvertor(Class clazz)
            {
                if(_pojoClass==clazz)
                    return _mapConvertor==null ? _pojoConvertor : _mapConvertor;
                return super.getConvertor(clazz);
            }
        };
        
        if("map".equalsIgnoreCase(getConsumeType()))
        {
            _mapConvertor = new Convertor()
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
        
        _pojoConvertor = (PojoConvertor)cache.get(_pojoClass.getName());
        if(_pojoConvertor!=null)
            return;
        
        /*if(_initParams.isEmpty())
        {
            _pojoConvertor = new PojoConvertor(_pojoClass);
            return;
        }*/
        
        Map<String,Method> methods = ReflectUtil.getSetterMethods(_pojoClass);
        Map<String,ValidatingSetter> vSetters = new HashMap<String,ValidatingSetter>(1 + 
                new Double(methods.size()/.75).intValue());
        
        for(Map.Entry<String,Method> entry : methods.entrySet())
        {
            String field = entry.getKey();
            boolean included = !"false".equalsIgnoreCase(getFieldParam(field+".included"));
            if(!included)
            {
                log.info(field + " excluded");
                continue;
            }
            
            boolean required = !"false".equalsIgnoreCase(getFieldParam(field+".required"));   
            String errorMsg = getFieldParam(field + "." + MSG);
            String validator = getFieldParam(field + ".validator");
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
            if(errorMsg==null || errorMsg.length()==0)
                errorMsg = AbstractConsumer.getDefaultErrorMsg(field);
            
            vSetters.put(field, new ValidatingSetter(field, entry.getValue(), required, fv, 
                    errorMsg));
        }        
        _pojoConvertor = new ValidatingPojoConvertor(_pojoClass, vSetters, _mapConvertor==null);
        cache.putIfAbsent(_pojoClass.getName(), _pojoConvertor);    
    }
    
    public boolean merge(Object pojo, RequestContext rc)
    {
        if(_mapConvertor==null)
            throw new IllegalStateException("Cannot merge. init-param '" + CONSUME_TYPE + "' must be 'map'.");
        
        if(pojo==null || pojo.getClass()!=_pojoClass)
            throw new IllegalArgumentException("The pojo must be of type: " + _pojoClass.getName());
        
        return merge(pojo, (Map<?,?>)rc.getRequest().getAttribute(CONSUMED_OBJECT));
    }
    
    public boolean merge(Object pojo, Map<?,?> props)
    {        
        return _pojoConvertor.setProps(pojo, props)!=0;
    }
    
    public boolean consume(RequestContext requestContext) throws ServletException, IOException
    {
        Object result = null;
        try
        {
            result = _json.parse(new JSON.ReaderSource(requestContext.getRequest().getReader()));
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
            generateResponse(__defaultErrorMsg, requestContext);
            log.warn("Consume error.", e);
        }
        if(result==null)
            return false;
        
        requestContext.getRequest().setAttribute(CONSUMED_OBJECT, result);
        return true;
    }
    
    protected void generateResponse(String message, RequestContext rc)
    throws IOException, ServletException
    {
        if(_dispatcher instanceof JSONDispatcher)
        {
            rc.getResponse().setContentType(_responseContentType);
            ((JSONDispatcher)_dispatcher).writeSimpleResponse(message, true, rc.getRequest(), 
                    rc.getResponse());
        }
        else
            dispatch(message, rc, message);
    }
    
    public static class CachedJSON extends JSON
    {
        protected ConcurrentMap<String,Convertor> _cache;
        
        public CachedJSON(ConcurrentMap<String,Convertor> cache)
        {
            _cache = cache;
        }
        
        public void addConvertor(Class clazz, Convertor convertor)
        {
            _cache.putIfAbsent(clazz.getName(), convertor);
        }
        
        protected Convertor getConvertor(Class clazz)
        {
            Convertor convertor = _cache.get(clazz.getName());
            if(convertor==null)
                throw new IllegalArgumentException("unregistered pojo: " + clazz.getName());
            
            return convertor;
        }
    }

    public static class ValidationException extends RuntimeException
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
            
            setProps(obj, map);            
            return obj;
        }
        
        protected int setProps(Object obj, Map<?,?> props)
        {
            int count = 0;
            for(Iterator<?> iterator = props.entrySet().iterator(); iterator.hasNext();)
            {
                Map.Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
                Setter setter = getSetter((String)entry.getKey());
                if(setter!=null)
                {
                    try
                    {
                        setter.invoke(obj, entry.getValue());
                        count++;
                    }
                    catch(Exception e)
                    {
                        // TODO throw exception?
                        log.warn(_pojoClass.getName() + " property '"+setter.getPropertyName() + 
                                "' not set.", e);
                    }
                }
            }
            return count;
        }

        public void toJSON(Object obj, Output out)
        {
            if(_fromJSON)
                out.addClass(_pojoClass);
            for(Map.Entry<String,Method> entry : _getters.entrySet())
            {            
                try
                {
                    out.add(entry.getKey(), entry.getValue().invoke(obj, GETTER_ARG));                    
                }
                catch(Exception e)
                {
                    // TODO throw exception?
                    log.warn(_pojoClass.getName() + " property '" + entry.getKey() + 
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
                    log.warn(_pojoClass.getName() + " property '"+setter.getPropertyName() + 
                            "' not set.", e);
                }
            }            
            return obj;
        }
        
    }
    
    public static class Setter
    {
        protected String _propertyName;
        protected Method _method;
        protected NumberType _numberType;
        
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
        protected boolean _required;
        protected FieldValidator _validator;
        protected String _errorMsg;

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
            String errorMsg = null;
            try
            {                
                if(value==null)
                {
                    _method.invoke(obj, NULL_ARG);
                    return;
                }
                if(_validator==null)
                {
                    super.invoke(obj, value);
                    return;
                }
                errorMsg = _validator.validate(value);
                if(errorMsg==null)
                {
                    super.invoke(obj, value);
                    return;
                }
            }
            catch(Exception e)
            {
                throw new ValidationException(getErrorMsg(), this);
            }
            
            throw new ValidationException(errorMsg, this);
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
