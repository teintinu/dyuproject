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

package com.dyuproject.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mortbay.log.Log;
import org.mortbay.util.ajax.JSONPojoConvertor;

import com.dyuproject.json.StandardPojoConvertor.StandardSetter;

/**
 * Sets the correct values for pojos with overloaded setters except when 
 * 2 methods overload a number type (limitation).
 * 
 * @author David Yu
 * @created Feb 21, 2009
 */

public class OverloadPojoConvertor extends JSONPojoConvertor
{
    
    public OverloadPojoConvertor(Class<?> pojoClass)
    {
        super(pojoClass);
    }
    
    // TODO remove this when 6.1.15 is out.
    protected void init()
    {
        Method[] methods = _pojoClass.getMethods();
        for (int i=0;i<methods.length;i++)
        {
            Method m=methods[i];
            if (!java.lang.reflect.Modifier.isStatic(m.getModifiers()) && m.getDeclaringClass()!=Object.class)
            {
                String name=m.getName();
                switch(m.getParameterTypes().length)
                {
                    case 0:
                        
                        if(m.getReturnType()!=null)
                        {
                            if (name.startsWith("is") && name.length()>2)
                                name=name.substring(2,3).toLowerCase()+name.substring(3);
                            else if (name.startsWith("get") && name.length()>3)
                                name=name.substring(3,4).toLowerCase()+name.substring(4);
                            else 
                                break;
                            if(includeField(name, m))
                                addGetter(name, m);
                        }
                        break;
                    case 1:
                        if (name.startsWith("set") && name.length()>3)
                        {
                            name=name.substring(3,4).toLowerCase()+name.substring(4);
                            if(includeField(name, m))
                                addSetter(name, m);
                        }
                        break;                
                }
            }
        }
    }
    
    protected void addSetter(String name, Method method)
    {
        OverloadSetter ds = newSetter(name, method);
        OverloadSetter last = (OverloadSetter)_setters.put(name, ds);
        if(last!=null)
            ds._next = last;
    }
    
    protected OverloadSetter newSetter(String name, Method method)
    {
        return new OverloadSetter(name, method);
    }
    
    public static class OverloadSetter extends StandardSetter
    {        
        protected OverloadSetter _next;

        public OverloadSetter(String propertyName, Method method)
        {
            super(propertyName, method);
        }
        
        public void invoke(Object obj, Object value) throws IllegalArgumentException, 
        IllegalAccessException, InvocationTargetException
        {
            if(_next==null)
            {
                if(value==null)
                    _method.invoke(obj, JSONPojoConvertor.NULL_ARG);
                else
                    invokeObject(obj, value, this);
            }
            else if(value==null)
                _method.invoke(obj, JSONPojoConvertor.NULL_ARG);
            else
                tryInvoke(obj, value, this);
        }
        
        static void tryInvoke(Object obj, Object value, OverloadSetter rs)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            try
            {
                if(invokeObject(obj, value, rs))
                    return;
            }
            catch(IllegalArgumentException e)
            {
                if(rs._next==null)
                    throw e;
                
                Log.ignore(e);
                tryInvoke(obj, value, rs._next);
                return;
            }
            if(rs._next!=null)
                tryInvoke(obj, value, rs._next);
        }        
    }

}
