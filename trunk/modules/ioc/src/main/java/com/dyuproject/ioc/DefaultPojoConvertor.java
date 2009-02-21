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

package com.dyuproject.ioc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mortbay.util.ajax.JSONPojoConvertor;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class DefaultPojoConvertor extends OverloadPojoConvertor
{
    
    public DefaultPojoConvertor(Class<?> pojoClass)
    {
        super(pojoClass);            
    }
    
    protected OverloadSetter newSetter(String name, Method method)
    {
        return new DefaultSetter(name, method);
    }
    
    public static class DefaultSetter extends OverloadSetter
    {

        public DefaultSetter(String propertyName, Method method)
        {
            super(propertyName, method);
        }
        
        public void invokeObject(Object obj, Object value) throws IllegalArgumentException, 
                IllegalAccessException, InvocationTargetException
        {
            if(value instanceof Reference)
            {
                Object ref = ((Reference)value).getRef();
                if(ref==null)
                    _method.invoke(obj, JSONPojoConvertor.NULL_ARG);
                else
                    super.invokeObject(obj, ref);
            }
            else
                super.invokeObject(obj, value);
        }
        
    }

}
