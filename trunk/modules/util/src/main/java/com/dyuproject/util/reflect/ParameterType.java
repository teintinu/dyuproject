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
    
    public static final ParameterType BOOLEAN_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Boolean(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Boolean.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    
    public static final ParameterType SHORT = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Short(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Short.class;
        }
    };
    
    public static final ParameterType SHORT_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Short(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Short.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
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
    
    public static final ParameterType INTEGER_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Integer(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Integer.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
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
    
    public static final ParameterType LONG_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Long(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Long.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
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
    
    public static final ParameterType FLOAT_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Float(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Float.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
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
    
    public static final ParameterType DOUBLE_P = new ParameterType(){
        public Object getActualValue(String value)
        {
            return new Double(value);
        }
        public Class<? extends Object> getTypeClass()
        {
            return Double.TYPE;
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    
    static
    {
        fillWithSimpleType(__simpleTypes);
    }
    
    public static void fillWithSimpleType(Map<Class<?>, ParameterType> map)
    {
        map.put(STRING.getTypeClass(), STRING);
        map.put(BOOLEAN.getTypeClass(), BOOLEAN);
        map.put(BOOLEAN_P.getTypeClass(), BOOLEAN_P);
        map.put(SHORT.getTypeClass(), SHORT);
        map.put(SHORT_P.getTypeClass(), SHORT_P);
        map.put(INTEGER.getTypeClass(), INTEGER);
        map.put(INTEGER_P.getTypeClass(), INTEGER_P);
        map.put(LONG.getTypeClass(), LONG);
        map.put(LONG_P.getTypeClass(), LONG_P);
        map.put(FLOAT.getTypeClass(), FLOAT);
        map.put(FLOAT_P.getTypeClass(), FLOAT_P);
        map.put(DOUBLE.getTypeClass(), DOUBLE);
        map.put(DOUBLE_P.getTypeClass(), DOUBLE_P);
    }
    
    public static ParameterType getSimpleType(Class<?> clazz)
    {
        return __simpleTypes.get(clazz);
    }
    
    public static Map<String,SimpleField> getSimpleFieldSetters(Class<?> pojoClass) 
    {
        HashMap<String,SimpleField> baseMap = new HashMap<String,SimpleField>();
        fillSimpleFieldSetters(pojoClass, baseMap);
        return baseMap;
    }
    
    private static void fillSimpleFieldSetters(Class<?> pojoClass, Map<String,SimpleField> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillSimpleFieldSetters(pojoClass.getSuperclass(), baseMap);
        
        Method[] methods = pojoClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++)
        {
            Method m = methods[i];
            if(!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==1 && 
                    m.getName().startsWith(ReflectUtil.SET))
            {
                ParameterType pt = getSimpleType(m.getParameterTypes()[0]);
                if(pt!=null)
                {
                    String name = ReflectUtil.toProperty(ReflectUtil.SET.length(), m.getName());
                    baseMap.put(name, new SimpleField(name, m, pt));
                }
            }
        }
    }
    
    public static Map<String,SimpleField> getSimpleFieldGetters(Class<?> pojoClass) 
    {
        HashMap<String,SimpleField> baseMap = new HashMap<String,SimpleField>();
        fillSimpleFieldGetters(pojoClass, baseMap);
        return baseMap;
    }
    
    private static void fillSimpleFieldGetters(Class<?> pojoClass, Map<String,SimpleField> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillSimpleFieldGetters(pojoClass.getSuperclass(), baseMap);
        
        Method[] methods = pojoClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++)
        {
            Method m = methods[i];
            if (!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==0 && 
                    m.getReturnType()!=null)
            {
                String methodName = m.getName();
                if(methodName.startsWith(ReflectUtil.IS))
                {
                    ParameterType pt = getSimpleType(m.getReturnType());
                    if(pt!=null)
                    {
                        String name = ReflectUtil.toProperty(ReflectUtil.IS.length(), methodName);
                        baseMap.put(name, new SimpleField(name, m, pt));
                    }
                }
                else if(methodName.startsWith(ReflectUtil.GET))
                {
                    ParameterType pt = getSimpleType(m.getReturnType());
                    if(pt!=null)
                    {
                        String name = ReflectUtil.toProperty(ReflectUtil.GET.length(), methodName);
                        baseMap.put(name, new SimpleField(name, m, pt));
                    }
                }
            }
        }
    }
    
    public abstract Object getActualValue(String value);
    public abstract Class<?> getTypeClass();
    
    public int hashCode()
    {
        return getTypeClass().hashCode();
    }
    
    public boolean isPrimitive()
    {
        return false;
    }
    
    public static class SimpleField
    {
        private String _name;
        private Method _method;
        private ParameterType _type;
        
        SimpleField(String name, Method method, ParameterType type)
        {
            _name = name;
            _method = method;
            _type = type;
        }
        
        public String getName()
        {
            return _name;
        }
        
        public Method getMethod()
        {
            return _method;
        }
        
        public ParameterType getType()
        {
            return _type;
        }
        
    }
    
}
