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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mortbay.util.ajax.JSON.Convertor;
import org.mortbay.util.ajax.JSON.Output;

/**
 * The standard convertor cache.
 * 
 * @author David Yu
 * @created Feb 21, 2009
 */

@SuppressWarnings("unchecked")
public class StandardConvertorCache implements ConvertorCache
{
    
    /**
     * The convertor for classes that cannot be loaded/resolved.
     */
    public static final Convertor UNRESOLVED_CONVERTOR = new Convertor()
    {
        public Object fromJSON(Map map)
        {
            return map;
        }
        public void toJSON(Object obj, Output out)
        {
            
        }        
    };
    
    private final ConcurrentMap<String,Convertor> _convertors = new ConcurrentHashMap<String,Convertor>();
    
    public StandardConvertorCache()
    {
        
    }
    
    public Convertor newConvertor(Class<?> clazz, boolean addClass)
    {
        return new StandardPojoConvertor(clazz, addClass);
    }
    
    public Convertor newConvertor(Class<?> clazz)
    {
        return newConvertor(clazz, true);
    }
    
    public Convertor getConvertor(Class<?> clazz, boolean create)
    {
        return getConvertor(clazz, create, true);
    }

    public Convertor getConvertor(Class<?> clazz, boolean create, boolean addClass)
    {
        Convertor convertor = (Convertor)_convertors.get(clazz.getName());
        if(convertor==null && create)
        {
            convertor = newConvertor(clazz, addClass);
            Convertor existing = _convertors.putIfAbsent(clazz.getName(), convertor);
            if(existing != null)
                convertor = existing;
        }        
        return convertor;
    }
    
    public Convertor getConvertor(Class<?> clazz)
    {
        return (Convertor)_convertors.get(clazz.getName());
    }
    
    public boolean addConvertor(Class<?> clazz, Convertor convertor)
    {
        return convertor!=null && _convertors.putIfAbsent(clazz.getName(), convertor)==null;
    }
    
    public boolean hasConvertor(Class<?> clazz)
    {
        return getConvertor(clazz)!=null;
    }
    
    protected Convertor getConvertor(String className)
    {
        Convertor convertor = (Convertor)_convertors.get(className);
        if(convertor==null)
        {
            Class<?> clazz = StandardJSON.loadClass(className);
            convertor = clazz==null ? UNRESOLVED_CONVERTOR : newConvertor(clazz);
            Convertor existing = _convertors.putIfAbsent(className, convertor);
            if(existing != null)
                convertor = existing;
        }
        return convertor;
    } 
    
    public Object fromJSON(Map map)
    {
        String className = (String)map.get("class");
        return className==null ? map : getConvertor(className).fromJSON(map);
    }

    public void toJSON(Object obj, Output out)
    {
        getConvertor(obj.getClass(), true).toJSON(obj, out);
    }

}
