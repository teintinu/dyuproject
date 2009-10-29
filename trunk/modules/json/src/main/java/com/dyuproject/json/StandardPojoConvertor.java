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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mortbay.log.Log;
import org.mortbay.util.ajax.JSONPojoConvertor;

/**
 * The standard pojo convertor. If overloading is needed, use
 * {@link OverloadPojoConvertor} instead.
 * 
 * @author David Yu
 * @created Feb 21, 2009
 */

public class StandardPojoConvertor extends JSONPojoConvertor
{

    public StandardPojoConvertor(Class<?> pojoClass)
    {
        super(pojoClass);        
    }
    
    public StandardPojoConvertor(Class<?> pojoClass, String[] excluded)
    {
        super(pojoClass, excluded);
    }
    
    public StandardPojoConvertor(Class<?> pojoClass, String[] excluded, boolean fromJSON)
    {
        super(pojoClass, excluded);
        _fromJSON = fromJSON;
    }
    
    public StandardPojoConvertor(Class<?> pojoClass, boolean fromJSON)
    {
        super(pojoClass, fromJSON);
    }
    
    public StandardPojoConvertor(Class<?> pojoClass, boolean fromJSON, Set<String> excluded)
    {
        super(pojoClass, excluded, fromJSON);
    }
    
    @SuppressWarnings("unchecked")
    protected void addSetter(String name, Method method)
    {
        _setters.put(name, newSetter(name, method));
    }
    
    protected StandardSetter newSetter(String name, Method method)
    {
        return new StandardSetter(name, method);
    }    
    
    /**
     * The standard setter.  If overloading is needed, use 
     * {@link OverloadPojoConvertor.OverloadSetter} instead.
     */
    public static class StandardSetter extends Setter
    {
        
        protected static final int LIST = 1, SET = 2;
        protected int _collectionType;

        public StandardSetter(String propertyName, Method method)
        {
            super(propertyName, method);
            if(Collection.class.isAssignableFrom(_type))
            {
                if(List.class.isAssignableFrom(_type))
                    _collectionType = LIST;
                else if(Set.class.isAssignableFrom(_type))
                    _collectionType = SET;
            }
        }
        
        public void invoke(Object obj, Object value) throws IllegalArgumentException, 
                IllegalAccessException, InvocationTargetException
        {
            if(value==null)
                _method.invoke(obj, JSONPojoConvertor.NULL_ARG);
            else
                invokeObject(obj, value, this);
        }
        
        @SuppressWarnings("unchecked")
        public static boolean invokeObject(Object obj, Object value, StandardSetter setter)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            switch(setter._collectionType)
            {
                case 0:
                    setter.invokeObject(obj, value);
                    return true;
                case 1:
                    if(value instanceof List)
                    {
                        setter._method.invoke(obj, new Object[]{value});
                        return true;
                    }
                    if(value.getClass().isArray())
                    {
                        int len = Array.getLength(value);
                        if(len==0)
                        {
                            setter._method.invoke(obj, new Object[]{Collections.EMPTY_LIST});
                            return true;
                        }
                        Object first = Array.get(value, 0);
                        NumberType numberType = JSONPojoConvertor.getNumberType(first.getClass());
                        if(numberType!=null)
                        {
                            try
                            {
                                first = numberType.getActualValue((Number)first);
                                //Object[] old = (Object[])value;
                                ArrayList list = new ArrayList(len);
                                list.add(first);
                                for(int i=1; i<len; i++)
                                {
                                    //list.add(numberType.getActualValue((Number)old[i]));
                                    list.add(numberType.getActualValue((Number)Array.get(value, i)));
                                }
                                setter._method.invoke(obj, new Object[]{list});
                                return true;
                            }                        
                            catch(Exception e)
                            {                            
                                Log.ignore(e);
                                return false;
                            }
                        }
                        try
                        {
                            ArrayList list = new ArrayList(len);
                            list.add(first);
                            for(int i=1; i<len; i++)
                                list.add(Array.get(value, i));
                            
                            setter._method.invoke(obj, list);
                            return true;
                        }
                        catch(Exception e)
                        {
                            Log.ignore(e);
                            return false;
                        }
                    }
                    return false;
                case 2:
                    if(value instanceof Set)
                    {
                        setter._method.invoke(obj, new Object[]{value});
                        return true;
                    }
                    if(value.getClass().isArray())
                    {
                        int len = Array.getLength(value);
                        if(len==0)
                        {
                            setter._method.invoke(obj, new Object[]{Collections.EMPTY_LIST});
                            return true;
                        }
                        Object first = Array.get(value, 0);
                        NumberType numberType = JSONPojoConvertor.getNumberType(first.getClass());
                        if(numberType!=null)
                        {
                            try
                            {                                
                                first = numberType.getActualValue((Number)first);
                                //Object[] old = (Object[])value;
                                HashSet set = new HashSet(len);
                                set.add(first);
                                for(int i=1; i<len; i++)
                                {
                                    //list.add(numberType.getActualValue((Number)old[i]));
                                    set.add(numberType.getActualValue((Number)Array.get(value, i)));
                                }
                                setter._method.invoke(obj, new Object[]{set});
                                return true;
                            }                        
                            catch(Exception e)
                            {                            
                                Log.ignore(e);
                                return false;
                            }
                        }
                        try
                        {
                            HashSet set = new HashSet(len);
                            set.add(first);
                            for(int i=1; i<len; i++)
                                set.add(Array.get(value, i));
                            
                            setter._method.invoke(obj, set);
                            return true;
                        }
                        catch(Exception e)
                        {
                            Log.ignore(e);
                            return false;
                        }
                    }
                    return false;
            }
            return false;
        }        
    }

}
