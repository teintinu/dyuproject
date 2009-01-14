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

package com.dyuproject.util.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * The java.lang.* primitive and primitive wrapper types
 * 
 * @author David Yu
 * @created Mar 14, 2008
 */

public abstract class ParameterType
{
    
    private static final Map<Class<?>, ParameterType> __simpleTypes = new HashMap<Class<?>, ParameterType>();
    
    public static final ParameterType STRING = new ParameterType(){
        public Object create(String value)
        {
            return value;
        }
        public Class<? extends Object> getTypeClass()
        {
            return String.class;
        }
    };
    
    public static final ParameterType BOOLEAN = new ParameterType(){
        public Object create(String value)
        {
            return new Boolean(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Boolean.class;
        }
    };    
    
    public static final ParameterType SHORT = new ParameterType(){
        public Object create(String value)
        {
            return new Short(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Short.TYPE;
        }
    };
    
    public static final ParameterType INTEGER = new ParameterType(){
        public Object create(String value)
        {
            return new Integer(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Integer.class;
        }
        public Object create(Object value)
        {
            return value instanceof Integer ? value : new Integer(((Number)value).intValue());
        }
    };
    
    public static final ParameterType LONG = new ParameterType(){
        public Object create(String value)
        {
            return new Long(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Long.class;
        }
        public Object create(Object value)
        {
            return value instanceof Long ? value : new Long(((Number)value).longValue());
        }
    };
    
    public static final ParameterType FLOAT = new ParameterType(){
        public Object create(String value)
        {
            return new Float(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Float.class;
        }
    };
    
    public static final ParameterType DOUBLE = new ParameterType(){
        public Object create(String value)
        {
            return new Double(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Double.class;
        }
    };
    
    static
    {
        __simpleTypes.put(String.class, STRING);
        __simpleTypes.put(Boolean.class, BOOLEAN);
        __simpleTypes.put(Boolean.TYPE, BOOLEAN);
        __simpleTypes.put(Short.class, SHORT);
        __simpleTypes.put(Short.TYPE, SHORT);
        __simpleTypes.put(Integer.class, INTEGER);
        __simpleTypes.put(Integer.TYPE, INTEGER);
        __simpleTypes.put(Long.class, LONG);
        __simpleTypes.put(Long.TYPE, LONG);
        __simpleTypes.put(Float.class, FLOAT);
        __simpleTypes.put(Float.TYPE, FLOAT);
        __simpleTypes.put(Double.class, DOUBLE);
        __simpleTypes.put(Double.TYPE, DOUBLE);
    }   
    
    public static ParameterType getSimpleType(Class<?> clazz)
    {
        return __simpleTypes.get(clazz);
    }
    
    public abstract Object create(String value);
    public abstract Class<?> getTypeClass();
    
    public int hashCode()
    {
        return getTypeClass().hashCode();
    }
    
}
