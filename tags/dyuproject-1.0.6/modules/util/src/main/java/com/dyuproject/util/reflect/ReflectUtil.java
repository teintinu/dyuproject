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
import java.util.HashMap;
import java.util.Map;


/**
 * Util for java reflections
 * 
 * @author David Yu
 * @created Mar 14, 2008
 */

public abstract class ReflectUtil
{    
    private static final String GET = "get";
    private static final String IS = "is";
    private static final String SET = "set";
    private static final int ROOT_OBJECT = "java.lang.Object".hashCode();   
    
    private static final Map<Class, ParameterType> __simpleParameterMap = new HashMap<Class, ParameterType>();
    static
    {
        __simpleParameterMap.put(ParameterType.STRING.getTypeClass(), ParameterType.STRING);        
        __simpleParameterMap.put(ParameterType.PRIMITIVE_BOOLEAN.getTypeClass(), ParameterType.PRIMITIVE_BOOLEAN);
        __simpleParameterMap.put(ParameterType.BOOLEAN.getTypeClass(), ParameterType.BOOLEAN);
        __simpleParameterMap.put(ParameterType.PRIMITIVE_INTEGER.getTypeClass(), ParameterType.PRIMITIVE_INTEGER);
        __simpleParameterMap.put(ParameterType.INTEGER.getTypeClass(), ParameterType.INTEGER);
        __simpleParameterMap.put(ParameterType.PRIMITIVE_LONG.getTypeClass(), ParameterType.PRIMITIVE_LONG);
        __simpleParameterMap.put(ParameterType.LONG.getTypeClass(), ParameterType.LONG);
        __simpleParameterMap.put(ParameterType.PRIMITIVE_FLOAT.getTypeClass(), ParameterType.PRIMITIVE_FLOAT);
        __simpleParameterMap.put(ParameterType.FLOAT.getTypeClass(), ParameterType.FLOAT);
        __simpleParameterMap.put(ParameterType.PRIMITIVE_DOUBLE.getTypeClass(), ParameterType.PRIMITIVE_DOUBLE);
        __simpleParameterMap.put(ParameterType.DOUBLE.getTypeClass(), ParameterType.DOUBLE);
        __simpleParameterMap.put(ParameterType.PRIMITIVE_SHORT.getTypeClass(), ParameterType.PRIMITIVE_SHORT);
        __simpleParameterMap.put(ParameterType.SHORT.getTypeClass(), ParameterType.SHORT);
    }
    
    public static final ParameterType getParameterType(Class clazz)
    {
        return __simpleParameterMap.get(clazz);
    }
    
    public static Map<String,Method> getGetterMethods(Class beanClass) 
    {
        return fillGetterMethods(beanClass, new HashMap<String,Method>());
    }
    
    private static Map<String,Method> fillGetterMethods(Class beanClass, Map<String,Method> baseMap) 
    {      
        Class parentClass = beanClass.getSuperclass();
        if(parentClass.getName().hashCode()!=ROOT_OBJECT) 
            fillGetterMethods(parentClass, baseMap);
        
        Method[] methods = beanClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++) 
        {          
            if(0==methods[i].getParameterTypes().length)
            {
                String name = methods[i].getName();
                if(name.startsWith(GET))                
                    baseMap.put(toProperty(GET.length(), name), methods[i]);                
                else if(name.startsWith(IS))                
                    baseMap.put(toProperty(IS.length(), name), methods[i]);
            }
        }
        return baseMap; 
    }
    
    public static Map<String,Method> getSimpleSetterMethods(Class beanClass) 
    {
        return fillSimpleSetterMethods(beanClass, new HashMap<String,Method>());
    }
    
    private static Map<String,Method> fillSimpleSetterMethods(Class beanClass, Map<String,Method> baseMap) 
    {      
        Class parentClass = beanClass.getSuperclass();
        if(parentClass.getName().hashCode()!=ROOT_OBJECT) 
            fillSimpleSetterMethods(parentClass, baseMap);
        
        Method[] methods = beanClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++) 
        {          
            if(1==methods[i].getParameterTypes().length)
            {
                String name = methods[i].getName();
                if(name.startsWith(SET) && __simpleParameterMap.containsKey(methods[i].getParameterTypes()[0]))                   
                    baseMap.put(toProperty(SET.length(), name), methods[i]);         
            }
        }
        return baseMap; 
    }
    
    public static Method getSimpleSetterMethod(String property, Class beanClass)
    {
        Method[] methods = beanClass.getMethods();
        for(Method m : methods)
        {
            if(m.getParameterTypes().length==1 && __simpleParameterMap.containsKey(m.getParameterTypes()[0]))
                return m;
        }
        return null;
    }
    
    public static Map<Method,Method> getSetterMethods(Class beanClass) 
    {
        return fillSetterMethods(beanClass, new HashMap<Method,Method>());
    }
    
    private static Map<Method,Method> fillSetterMethods(Class beanClass, Map<Method,Method> baseMap) 
    {      
        Class parentClass = beanClass.getSuperclass();
        if(parentClass.getName().hashCode()!=ROOT_OBJECT) 
            fillSetterMethods(parentClass, baseMap);
        
        Method[] methods = beanClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++) 
        {          
            if(1==methods[i].getParameterTypes().length)
            {
                String name = methods[i].getName();
                if(name.startsWith(SET))                
                    baseMap.put(methods[i], methods[i]);
            }
        }
        return baseMap; 
    }    
    
    /*private static void putMethodProp(Map<String,Method> baseMap, int start, String name, Method method)
    {
        char[] prop = name.toCharArray();
        int firstLetter = prop[start];
        prop[start] = (char)(firstLetter<91 ? firstLetter + 32 : firstLetter);
        baseMap.put(new String(prop, start, prop.length-start), method);
    }*/
    
    public static String toProperty(int start, String name)
    {
        char[] prop = name.toCharArray();
        int firstLetter = prop[start];
        prop[start] = (char)(firstLetter<91 ? firstLetter + 32 : firstLetter);
        return new String(prop, start, prop.length-start);
    }
    
    public static void applySimpleSetters(Object obj, Map<String,Method> methods, Map<String,String> params)
    throws Exception
    {
        for(Map.Entry<String, Method> entry : methods.entrySet())
        {
            String value = params.get(entry.getKey());
            Method m = entry.getValue();
            if(value!=null)
            {                
                ParameterType type = __simpleParameterMap.get(m.getParameterTypes()[0]);
                if(type!=null)                
                    m.invoke(obj, new Object[]{type.create(value)});
            }
        }
    }

}
