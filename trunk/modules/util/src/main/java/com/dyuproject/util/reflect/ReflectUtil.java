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
 * Util for java reflections
 * 
 * @author David Yu
 * @created Mar 14, 2008
 */

public abstract class ReflectUtil
{    
    static final String GET = "get";
    static final String IS = "is";
    static final String SET = "set";

    public static Map<String,Method> getGetterMethods(Class<?> pojoClass) 
    {
        HashMap<String,Method> methods = new HashMap<String,Method>();
        fillGetterMethods(pojoClass, methods);
        return methods;
    }
    
    private static void fillGetterMethods(Class<?> pojoClass, Map<String,Method> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillGetterMethods(pojoClass.getSuperclass(), baseMap);

        Method[] methods = pojoClass.getDeclaredMethods();
        for (int i=0;i<methods.length;i++)
        {
            Method m=methods[i];
            if (!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==0 && 
                    m.getReturnType()!=null)
            {
                String name=m.getName();
                if (name.startsWith(IS))
                    baseMap.put(toProperty(IS.length(), name), m);
                else if (name.startsWith(GET))
                    baseMap.put(toProperty(GET.length(), name), m);
            }
        }
    }



    public static Map<String,Method> getSetterMethods(Class<?> pojoClass) 
    {
        HashMap<String,Method> methods = new HashMap<String,Method>();
        fillSetterMethods(pojoClass, methods);
        return methods;
    }
    
    private static void fillSetterMethods(Class<?> pojoClass, Map<String,Method> baseMap) 
    {
        if(pojoClass.getSuperclass()!=Object.class)
            fillSetterMethods(pojoClass.getSuperclass(), baseMap);
        
        Method[] methods = pojoClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++)
        {
            Method m = methods[i];
            if(!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length==1 && 
                    m.getName().startsWith(SET))
            {
                baseMap.put(toProperty(SET.length(), m.getName()), m);
            }
        }
    }
    
    public static String toProperty(int start, String methodName)
    {
        char[] prop = new char[methodName.length()-start];
        methodName.getChars(start, methodName.length(), prop, 0);
        int firstLetter = prop[0];
        prop[0] = (char)(firstLetter<91 ? firstLetter + 32 : firstLetter);
        return new String(prop);
    }

}
