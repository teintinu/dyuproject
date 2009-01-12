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

package com.dyuproject.util.format;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.dyuproject.util.Delim;

/**
 * Converts a bean to a string in JSON format.
 * 
 * @author David Yu
 */

public class JSONConverter extends FormatConverter
{    
    
    private static final JSONConverter __instance = new JSONConverter();
    protected static final String P_NULL = "(null);";
    
    public static JSONConverter getInstance()
    {
        return __instance;
    }
    
    protected static String callbackNULL(String callback) 
    {
        return callback.concat(P_NULL);
    }
    
    private JSONConverter()
    {
        
    }
    
    public String getFormat()
    {
        return JSON;
    }
    
    public String getContentType()
    {
        return TEXT_PLAIN;
    }
    
    public String toString(Object obj, String callback)
    {
        if(obj==null)
            return callback==null ? NULL : callbackNULL(callback);
        return getBuffer(obj, callback).toString();
    }
    
    public String toString(Object[] objs, String callback)
    {
        if(objs==null)
            return callback==null ? NULL : callbackNULL(callback);
        return getBuffer(objs, callback).toString();
    }
    
    public String toString(Map<String, Object> m, String callback)
    {
        if(m==null)
            return callback==null ? NULL : callbackNULL(callback);
        return getBuffer(m, callback).toString();
    }
    
    public String toString(Map<String, Object>[] m, String callback)
    {
        if(m==null)
            return callback==null ? NULL : callbackNULL(callback);
        return getBuffer(m, callback).toString();
    }
    
    public String toString(Collection<Object> c, String callback)
    {
        if(c==null)
            return callback==null ? NULL : callbackNULL(callback);
        return getBuffer(c, callback).toString();
    }
    
    public String toString(FormatConverter.Bean bean, String callback)
    {
        return getBuffer(bean, callback).toString();
    }
    
    public StringBuilder getBuffer(Object obj, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        processObject(builder, obj);
        if(hasCallback)
            builder._buffer.append(')').append(';');
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(Object[] objs, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        builder._buffer.append('[');
        if(objs.length==0)
        {
            builder._buffer.append(']');
            if(hasCallback)
                builder._buffer.append(')').append(';');
            return builder._buffer;
        }
        processObject(builder, objs[0]);
        for(int i=1; i<objs.length; i++)
        {
            builder._buffer.append(',');
            processObject(builder, objs[i]);
        }
        builder._buffer.append(']');
        if(hasCallback)
            builder._buffer.append(')').append(';');        
        return builder._buffer;
    }    
    
    public StringBuilder getBuffer(Map<String, Object> m, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        processMap(builder, m);
        if(hasCallback)
            builder._buffer.append(')').append(';');
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(Map<String, Object>[] m, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        builder._buffer.append('[');
        if(m.length==0)
        {
            builder._buffer.append(']');
            if(hasCallback)
                builder._buffer.append(')').append(';');
            return builder._buffer;
        }            
        processMap(builder, m[0]);
        for(int i=1; i<m.length; i++)
        {
            builder._buffer.append(',');
            processMap(builder, m[i]);
        }
        builder._buffer.append(']');
        if(hasCallback)
            builder._buffer.append(')').append(';');
        return builder._buffer;
    }    
    
    public StringBuilder getBuffer(Collection<Object> c, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        processCollection(builder, c);
        if(hasCallback)
            builder._buffer.append(')').append(';');
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(FormatConverter.Bean bean, String callback)
    {
        JSONBuilder builder = new JSONBuilder(new StringBuilder());
        boolean hasCallback = hasCallback(builder._buffer, callback);
        processConverterBean(builder, bean);
        if(hasCallback)
            builder._buffer.append(')').append(';');
        return builder._buffer;
    }
    
    static boolean hasCallback(StringBuilder buffer, String callback)
    {
        if(callback!=null && callback.length()>0)
        {
            buffer.append(callback).append('(');
            return true;
        }
        return false;
    }
    
    static void addKey(StringBuilder buffer, String key)
    {
        buffer.append(key).append(':');
        //buffer.append('"').append(key).append('"').append(':');
    }
    
    static void processObject(JSONBuilder builder, Object obj)
    {
        if(obj==null)
        {
            builder._buffer.append(NULL);
            return;
        }
        if(obj instanceof FormatConverter.Bean)
            processConverterBean(builder, (FormatConverter.Bean)obj);
        else if(obj instanceof String)
            builder._buffer.append('"').append(obj.toString()).append('"');
        else if(obj instanceof Number || obj instanceof Boolean)                
            builder._buffer.append(obj.toString());
        else if(obj.getClass().isArray())
            processArray(builder, obj, obj.getClass().getComponentType());
        else if(obj.getClass().isPrimitive())
            builder._buffer.append(String.valueOf(obj));
        else if(obj instanceof Map)
            processMap(builder, (Map<String, Object>)obj);
        else if(obj instanceof Collection)
            processCollection(builder, (Collection<Object>)obj);
        else
            processBean(builder, obj);
    }
    
    static void processBean(JSONBuilder builder, Object obj)
    {
        Map<String, Method> methods = getMethods(obj.getClass());
        int size = methods.size();        
        if(size==0)
        {
            builder._buffer.append(obj.toString());
            return;
        }
        builder._buffer.append('{');
        //addKey(builder._buffer, CLASS);
        //builder._buffer.append('"').append(obj.getClass().getSimpleName()).append('"');
        String[] props = Delim.COMMA.split(getOrder(methods.keySet()));
        Method firstM = methods.get(props[0]);
        if(firstM!=null)
        {
            try
            {
                Object firstV = firstM.invoke(obj, EMPTY_ARRAY);
                addKey(builder._buffer, props[0]);
                processObject(builder, firstV);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }            
        for(int i=1; i<props.length; i++)
        {
            Method method = methods.get(props[i]);
            try
            {
                Object value = method.invoke(obj, EMPTY_ARRAY);
                builder._buffer.append(',');
                addKey(builder._buffer, props[0]);
                processObject(builder, value);          
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        builder._buffer.append('}');
    }
    
    static void processConverterBean(JSONBuilder builder, FormatConverter.Bean bean)
    {
        builder._buffer.append('{');
        addKey(builder._buffer, CLASS);
        builder._buffer.append('"').append(bean.getClass().getSimpleName()).append('"');
        bean.convert(builder, JSON);
        builder._buffer.append('}');
    }
    
    static void processMap(JSONBuilder builder, Map<String, Object> m)
    {
        builder._buffer.append('{');        
        if(m.size()==0)
        {
            builder._buffer.append('}');
            return;
        }
        Iterator<Map.Entry<String, Object>> iter = m.entrySet().iterator();
        if(!iter.hasNext())
        {
            builder._buffer.append('}');
            return;
        }
        Map.Entry<String, Object> first = iter.next();        
        addKey(builder._buffer, first.getKey().toString());
        processObject(builder, first.getValue());
        while(iter.hasNext())
        {
            Map.Entry<String, Object> entry = iter.next();
            builder._buffer.append(',');
            addKey(builder._buffer, entry.getKey());
            processObject(builder, entry.getValue());
        }
        builder._buffer.append('}');
    }
    
    static void processCollection(JSONBuilder builder, Collection<Object> c)
    {
        builder._buffer.append('[');
        Object[] array = c.toArray();
        int size = array.length;
        if(size==0)
        {
            builder._buffer.append(']');
            return;
        }
        processObject(builder, array[0]);
        for(int i=1; i<size; i++)
        {
            builder._buffer.append(',');
            processObject(builder, array[i]);
        }
        builder._buffer.append(']');
    }
    
    static void processArray(JSONBuilder builder, Object array, Class componentType)
    {
        builder._buffer.append('[');
        int size = Array.getLength(array);
        if(size==0)
        {
            builder._buffer.append(']');
            return;
        }
        processObject(builder, Array.get(array, 0));
        for(int i=1; i<size; i++)
        {
            builder._buffer.append(',');
            processObject(builder, Array.get(array, i));
        }
        builder._buffer.append(']');
        /*if(Converter.Bean.class.isAssignableFrom(componentType))
        {
            processConverterBean(builder, (Converter.Bean)Array.get(array, 0));
            for(int i=1; i<size; i++)
            {
                builder._buffer.append(',');
                processConverterBean(builder, (Converter.Bean)Array.get(array, i));
            }
        }
        else if(String.class.isAssignableFrom(componentType))
        {
            builder._buffer.append('"').append(Array.get(array, 0).toString()).append('"');
            for(int i=1; i<size; i++)
            {
                builder._buffer.append(',').append('"').append(String.valueOf(Array.get(array, 
                        i))).append('"');
            }                
        }
        else if(Number.class.isAssignableFrom(componentType) || 
                Boolean.class.isAssignableFrom(componentType))
        {
            builder._buffer.append(String.valueOf(Array.get(array, 0)));
            for(int i=1; i<size; i++)
                builder._buffer.append(',').append(String.valueOf(Array.get(array, i)));
        }
        else if(Map.class.isAssignableFrom(componentType))
        {
            processMap(builder, (Map<String, Object>)Array.get(array, 0));
            for(int i=1; i<size; i++)
            {
                builder._buffer.append(',');
                processMap(builder, (Map<String, Object>)Array.get(array, i));
            }
        }
        else if(Collection.class.isAssignableFrom(componentType))
        {
            processCollection(builder, (Collection<Object>)Array.get(array, 0));
            for(int i=1; i<size; i++)
            {
                builder._buffer.append(',');
                processCollection(builder, (Collection<Object>)Array.get(array, i));
            }
        }
        builder._buffer.append(']');*/
    }
    
    private static class JSONBuilder implements FormatConverter.Builder
    {
        StringBuilder _buffer;
        
        public JSONBuilder(StringBuilder buffer)
        {
            _buffer = buffer;
        }
        
        public String getContentType() 
        {        
            return FormatConverter.TEXT_PLAIN;
        }

        public String getFormat() 
        {        
            return FormatConverter.JSON;
        }
        
        public String toString()
        {
            return _buffer.toString();
        }

        public void put(String key, Object value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);     
            processObject(this, value);
        }

        public void put(String key, String value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            if(value==null)
                _buffer.append(NULL);
            else
                _buffer.append('"').append(value).append('"');          
        }

        public void put(String key, Number value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(value.toString());        
        }

        public void put(String key, Boolean value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(value.toString());
        }

        public void put(String key, int value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(String.valueOf(value)); 
        }

        public void put(String key, long value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(String.valueOf(value)); 
        }

        public void put(String key, boolean value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(String.valueOf(value)); 
        }
        
        public void put(String key, float value)
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(String.valueOf(value)); 
        }
        
        public void put(String key, double value)
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(String.valueOf(value)); 
        }

        public void put(String key, Collection<Object> value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            processCollection(this, value);
        }

        public void put(String key, Map<String, Object> value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            processMap(this, value);
        }    
        
        public void put(String key, FormatConverter.Bean value) 
        {
            _buffer.append(',');
            addKey(_buffer, key);
            processConverterBean(this, value);
        }
        
        public void putRaw(String key, CharSequence value)
        {
            _buffer.append(',');
            addKey(_buffer, key);
            _buffer.append(value==null ? NULL : value.toString());
        }
        
    }
    
}
