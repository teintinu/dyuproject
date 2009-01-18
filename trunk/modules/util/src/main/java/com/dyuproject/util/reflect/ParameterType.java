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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
        public Object getActualValue(String value)
        {
            return value;
        }
        public Class<? extends Object> getTypeClass()
        {
            return String.class;
        }
    };
    
    public static final ParameterType BOOLEAN = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Boolean(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Boolean.class;
        }
    };    
    
    public static final ParameterType SHORT = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Short(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Short.TYPE;
        }
    };
    
    public static final ParameterType INTEGER = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Integer(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Integer.class;
        }
    };
    
    public static final ParameterType LONG = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Long(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Long.class;
        }
    };
    
    public static final ParameterType FLOAT = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Float(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Float.class;
        }
    };
    
    public static final ParameterType DOUBLE = new ParameterType(){
        public Object getActualValue(String value)
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
        fillWithSimpleType(__simpleTypes);
    }
    
    public static void fillWithSimpleType(Map<Class<?>, ParameterType> map)
    {
        map.put(String.class, STRING);
        map.put(Boolean.class, BOOLEAN);
        map.put(Boolean.TYPE, BOOLEAN);
        map.put(Short.class, SHORT);
        map.put(Short.TYPE, SHORT);
        map.put(Integer.class, INTEGER);
        map.put(Integer.TYPE, INTEGER);
        map.put(Long.class, LONG);
        map.put(Long.TYPE, LONG);
        map.put(Float.class, FLOAT);
        map.put(Float.TYPE, FLOAT);
        map.put(Double.class, DOUBLE);
        map.put(Double.TYPE, DOUBLE);
    }
    
    public static ParameterType getSimpleType(Class<?> clazz)
    {
        return __simpleTypes.get(clazz);
    }
    
    public static Map<String,ParameterType> getSimpleFields(Class<?> pojoClass) 
    {
        HashMap<String,ParameterType> baseMap = new HashMap<String,ParameterType>();
        fillSimpleFields(pojoClass, baseMap);
        return baseMap;
    }
    
    private static void fillSimpleFields(Class<?> pojoClass, Map<String,ParameterType> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillSimpleFields(pojoClass.getSuperclass(), baseMap);
        
        Method[] methods = pojoClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++)
        {
            Method m = methods[i];
            if(!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==1 && 
                    m.getName().startsWith(ReflectUtil.SET))
            {
                ParameterType pt = getSimpleType(m.getParameterTypes()[0]);
                if(pt!=null)
                    baseMap.put(ReflectUtil.toProperty(ReflectUtil.SET.length(), m.getName()), pt);
            }
        }
    }
    
    public abstract Object getActualValue(String value);
    public abstract Class<?> getTypeClass();
    
    public int hashCode()
    {
        return getTypeClass().hashCode();
    }
    
}
