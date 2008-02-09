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

package com.dyuproject.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author David Yu
 */

public class XMLConverter extends FormatConverter
{
    
    private static final XMLConverter __instance = new XMLConverter();
    
    protected static final String ATTR_SUFFIX = "\"/>";
    
    protected static final String DATA_NULL = "<data>null</data>";
    
    protected static final String HEADER = "<?xml version=\"1.0\"?>";
    
    protected static final String ROOT_P = "<data>", ROOT_S = "</data>";
    
    protected static final String LIST_P = "<list>", LIST_S = "</list>";
    
    protected static final String VALUE_P = "<v>", VALUE_S = "</v>";    
    
    protected static final String OBJECT_P = "<object>", OBJECT_S = "</object>", OBJECT_WITH_CLASS = "<object class=\"";
    
    protected static final String PROP_P = "<k name=\"", PROP_S = "</k>";
    
    
    public static XMLConverter getInstance()
    {
        return __instance;
    }
    
    private XMLConverter()
    {
        
    }    
    
    public String getContentType()
    {        
        return TEXT_XML;
    }
    
    public String getFormat()
    {        
        return XML;
    }
    
    public String toString(Object obj, String callback)
    {
        if(obj==null)
            return DATA_NULL;
        return getBuffer(obj, callback).toString();
    }
    
    public String toString(Object[] objs, String callback)
    {
        if(objs==null)
            return DATA_NULL;
        return getBuffer(objs, callback).toString();
    }
    
    public String toString(Collection<Object> obj, String callback)
    {
        if(obj==null)
            return DATA_NULL;
        return getBuffer(obj, callback).toString();
    }
    
    public String toString(Map<String, Object> m, String callback)
    {
        if(m==null)
            return DATA_NULL;
        return getBuffer(m, callback).toString();
    }
    
    public String toString(Bean bean, String callback)
    {
        if(bean==null)
            return DATA_NULL;
        return getBuffer(bean, callback).toString();
    }
    
    public String toString(Map<String, Object>[] m, String callback)
    {
        if(m==null)
            return DATA_NULL;
        return getBuffer(m, callback).toString();
    }
    
    public StringBuilder getBuffer(Object obj, String callback)
    {
        XMLBuilder builder = new XMLBuilder();        
        builder._buffer.append(HEADER).append(ROOT_P);
        processObject(builder, obj);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(Object[] objs, String callback)
    {
        XMLBuilder builder = new XMLBuilder();
        builder._buffer.append(HEADER).append(ROOT_P);
        for(int i=0; i<objs.length; i++)
            processObject(builder, objs[i]);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(Collection<Object> c, String callback)
    {
        XMLBuilder builder = new XMLBuilder();
        builder._buffer.append(HEADER).append(ROOT_P);
        processCollection(builder, c);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }

    
    public StringBuilder getBuffer(Map<String, Object> m, String callback)
    {
        XMLBuilder builder = new XMLBuilder();
        builder._buffer.append(HEADER).append(ROOT_P);
        processMap(builder, m);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(FormatConverter.Bean bean, String callback)
    {
        XMLBuilder builder = new XMLBuilder();
        builder._buffer.append(HEADER).append(ROOT_P);
        processConverterBean(builder, bean);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }
    
    public StringBuilder getBuffer(Map<String, Object>[] m, String callback)
    {
        XMLBuilder builder = new XMLBuilder();
        builder._buffer.append(HEADER).append(ROOT_P);
        for(int i=0; i<m.length; i++)
            processMap(builder, m[i]);
        builder._buffer.append(ROOT_S);
        return builder._buffer;
    }
    
    static void processObject(XMLBuilder builder, Object obj)
    {
        if(obj==null)
        {
            builder._buffer.append(VALUE_P).append(NULL).append(VALUE_S);
            return;
        }
        if(obj instanceof FormatConverter.Bean)
            processConverterBean(builder, (FormatConverter.Bean)obj);
        else if(obj instanceof String)
            builder._buffer.append(VALUE_P).append(obj.toString()).append(VALUE_S);
        else if(obj instanceof Number || obj instanceof Boolean)                
            builder._buffer.append(VALUE_P).append(obj.toString()).append(VALUE_S);
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
    
    static void addKey(StringBuilder buffer, String key)
    {
        buffer.append(PROP_P).append(key).append('"').append('>');
    }
    
    static void processBean(XMLBuilder builder, Object obj)
    {        
        Map<String, Method> methods = getMethods(obj.getClass());
        String[] props = Delim.COMMA.split(getOrder(methods.keySet()));
        builder._buffer.append(VALUE_P).append(OBJECT_WITH_CLASS).append(obj.getClass().getSimpleName()).append('"').append('>');
        for(int i=0; i<props.length; i++)
        {
            Method method = methods.get(props[i]);
            try
            {
                Object value = method.invoke(obj, EMPTY_ARRAY);                
                addKey(builder._buffer, props[0]);                            
                processObject(builder, value);  
                builder._buffer.append(PROP_S);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        builder._buffer.append(OBJECT_S).append(VALUE_S);        
    }
    
    static void processConverterBean(XMLBuilder builder, FormatConverter.Bean bean)
    {
        builder._buffer.append(VALUE_P).append(OBJECT_WITH_CLASS).append(bean.getClass().getSimpleName()).append('"').append('>');
        bean.convert(builder, XML);
        builder._buffer.append(OBJECT_S).append(VALUE_S);
    }
    
    static void processMap(XMLBuilder builder, Map<String,Object> m)
    {
        builder._buffer.append(VALUE_P).append(OBJECT_P);
        Iterator<Map.Entry<String, Object>> iter = m.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<String, Object> entry = iter.next();           
            addKey(builder._buffer, entry.getKey());            
            processObject(builder, entry.getValue());
            builder._buffer.append(PROP_S);
        }
        builder._buffer.append(OBJECT_S).append(VALUE_S);
    }
    
    static void processCollection(XMLBuilder builder, Collection<Object> c)
    {
        builder._buffer.append(VALUE_P).append(LIST_P);
        Object[] array = c.toArray();
        int size = array.length;
        for(int i=0; i<size; i++)                    
            processObject(builder, array[i]);        
        builder._buffer.append(LIST_S).append(VALUE_S);
    }
    
    static void processArray(XMLBuilder builder, Object array, Class componentType)
    {
        builder._buffer.append(VALUE_P).append(LIST_P);
        int size = Array.getLength(array);
        for(int i=0; i<size; i++)            
            processObject(builder, Array.get(array, i));        
        builder._buffer.append(LIST_S).append(VALUE_S);
    }
    
    private static class XMLBuilder implements FormatConverter.Builder
    {        
        StringBuilder _buffer = new StringBuilder();

        public String getContentType()
        {            
            return TEXT_XML;
        }

        public String getFormat()
        {            
            return XML;
        }

        public void put(String key, Object value)
        {            
            addKey(_buffer, key);         
            processObject(this, value);
            _buffer.append(PROP_S);
        }

        public void put(String key, String value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(value).append(VALUE_S).append(PROP_S);            
        }

        public void put(String key, Number value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(value.toString()).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, Boolean value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(value.toString()).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, int value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(String.valueOf(value)).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, long value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(String.valueOf(value)).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, boolean value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(String.valueOf(value)).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, float value)
        {            
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(String.valueOf(value)).append(VALUE_S).append(PROP_S);
        }

        public void put(String key, double value)
        {
            addKey(_buffer, key);
            _buffer.append(VALUE_P).append(String.valueOf(value)).append(VALUE_S).append(PROP_S);            
        }

        public void put(String key, Collection<Object> value)
        {
            addKey(_buffer, key);            
            processCollection(this, value);
            _buffer.append(PROP_S);            
        }

        public void put(String key, Map<String, Object> value)
        {
            addKey(_buffer, key);            
            processMap(this, value);
            _buffer.append(PROP_S);     
        }

        public void put(String key, FormatConverter.Bean value)
        {
            addKey(_buffer, key);            
            processConverterBean(this, value);
            _buffer.append(PROP_S);       
        }
        
    }

}
