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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mortbay.util.ajax.JSON.Convertor;
import org.mortbay.util.ajax.JSON.Output;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class StandardConvertorCache implements ConvertorCache
{
    
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
    
    private ConcurrentMap<String,Convertor> _convertors = new ConcurrentHashMap<String,Convertor>();
    
    public StandardConvertorCache()
    {
        
    }
    
    protected Convertor getConvertor(String className)
    {
        Convertor convertor = (Convertor)_convertors.get(className);
        if(convertor==null)
        {
            Class<?> clazz = ClasspathResolver.loadClass(className);
            convertor = clazz==null ? UNRESOLVED_CONVERTOR : createConvertor(clazz);
            _convertors.putIfAbsent(className, convertor);
        }
        return convertor;
    }    
    
    public Convertor createConvertor(Class<?> clazz)
    {
        return new StandardPojoConvertor(clazz);
    }

    public Convertor getConvertor(Class<?> clazz, boolean create)
    {
        Convertor convertor = (Convertor)_convertors.get(clazz.getName());
        if(convertor==null && create)
        {
            convertor = createConvertor(clazz);
            _convertors.putIfAbsent(clazz.getName(), convertor);
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
