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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Converts a bean to a string in another format.
 * 
 * @author David Yu
 */

public abstract class FormatConverter 
{
    public static final String TEXT_JAVASCRIPT = "text/javascript";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_PLAIN = "text/plain";

    public static final String XML = "xml";
    public static final String JSON = "json";
    
    public static final String CLASS = "_class";
    public static final String NULL = "null";    
    
    protected static final Object[] EMPTY_ARRAY = new Object[]{};
    
    private static final int ROOT = "java.lang.Object".hashCode();
    private static final int GET = "get".hashCode();
    private static final int IS = "is".hashCode();
    
    private static final Map<String, FormatConverter> __converters = new HashMap<String, FormatConverter>();
    
    static
    {
        __converters.put(JSON, JSONConverter.getInstance());
        __converters.put(XML, XMLConverter.getInstance());
    }
    
    public static FormatConverter getConverter(String format)
    {        
        return __converters.get(format);
    }
    
    public static FormatConverter getDefault()
    {
        return JSONConverter.getInstance();
    }
    
    protected static Map<String, Method> getMethods(Class beanClass) 
    {
        return getMethods(beanClass, new HashMap<String, Method>());
    }
    
    private static Map<String, Method> getMethods(Class beanClass, Map<String, Method> baseMap) 
    {    
        Class parentClass = beanClass.getSuperclass();
        if(ROOT!=parentClass.getName().hashCode())
            getMethods(parentClass, baseMap);
        
        Method[] methods = beanClass.getDeclaredMethods();
        for(int i=0; i<methods.length; i++)
        {          
            if(0==methods[i].getParameterTypes().length)
            {                
                int hash = methods[i].getName().hashCode();
                if(GET==hash || IS==hash)
                {
                    char[] prop = methods[i].getName().toCharArray();
                    int fourth = prop[3]; 
                    prop[3] = (char)(fourth<91 ? fourth + 32 : fourth);
                    baseMap.put(new String(prop, 3, prop.length-3), methods[i]);
                }
            }
        }
        return baseMap; 
    }
    
    protected static String getOrder(Set<String> keySet) 
    {
        String order = keySet.toString();
        return order.substring(1, order.length()-1);
    }

    public abstract String getContentType();
    public abstract String getFormat();
    public abstract String toString(Object obj, String callback);
    public abstract String toString(Object[] objs, String callback);
    public abstract String toString(Collection<Object> obj, String callback);
    public abstract String toString(Map<String, Object> m, String callback);
    public abstract String toString(Bean bean, String callback);
    public abstract String toString(Map<String, Object>[] m, String callback);
    public abstract StringBuilder getBuffer(Object obj, String callback);
    public abstract StringBuilder getBuffer(Object[] objs, String callback);
    public abstract StringBuilder getBuffer(Collection<Object> c, String callback);
    public abstract StringBuilder getBuffer(Map<String, Object> m, String callback);
    public abstract StringBuilder getBuffer(Bean bean, String callback);
    public abstract StringBuilder getBuffer(Map<String, Object>[] m, String callback);
    
    public interface Bean
    {
        public void convert(Builder builder, String format);         
    }
    
    public interface Builder
    {
        public String getContentType();
        public String getFormat();
        
        public void put(String key, Object value);
        public void put(String key, String value);
        public void put(String key, Number value);    
        public void put(String key, Boolean value);
        public void put(String key, int value);
        public void put(String key, long value);
        public void put(String key, boolean value);
        public void put(String key, float value);
        public void put(String key, double value);
        public void put(String key, Collection<Object> value);
        public void put(String key, Map<String, Object> value);
        public void put(String key, Bean value);
        public void putRaw(String key, CharSequence value);
    }
    
}
