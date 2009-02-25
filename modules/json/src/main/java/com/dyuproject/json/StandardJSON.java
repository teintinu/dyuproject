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

import java.net.URL;
import java.util.Map;

import org.mortbay.log.Log;
import org.mortbay.util.Loader;
import org.mortbay.util.ajax.JSON;

/**
 * Standard JSON serialization of pojos using cached convertors with configurable classloading.
 * Enhancement for jetty-util's org.mortbay.util.ajax.JSON
 * 
 * @author David Yu
 * @created Feb 23, 2009
 */

public class StandardJSON extends JSON
{
    
    private static boolean __checkParents = Boolean.getBoolean("standardjson.check_parents");
    
    public static boolean isCheckParents()
    {
        return __checkParents;
    }
    
    public static void setCheckParents(boolean checkParents)
    {
        __checkParents = checkParents;
    }
    
    public static Class<?> loadClass(String className, Class<?> context, boolean checkParents)
    {
        try
        {
            return Loader.loadClass(context, className, checkParents);
        }            
        catch(ClassNotFoundException e)
        {
            Log.warn("{} not loaded", className, e);
            return null;
        }
    }
    
    public static Class<?> loadClass(String className)
    {
        return loadClass(className, StandardConvertorCache.class, __checkParents);
    }
    
    public static URL getResource(String path, Class<?> context, boolean checkParents)
    {
        try
        {
            return Loader.getResource(context, path, checkParents);
        } 
        catch (ClassNotFoundException e)
        {
            Log.warn(e);
            return null;
        }  
    }
    
    public static URL getResource(String path)
    {
        return getResource(path, StandardConvertorCache.class, __checkParents);
    }
    
    protected ConvertorCache _convertorCache;
    protected boolean _addClass = true;
    
    public StandardJSON()
    {
        this(new StandardConvertorCache());
    }
    
    public StandardJSON(ConvertorCache convertorCache)
    {
        _convertorCache = convertorCache;
    }
    
    public ConvertorCache getConvertorCache()
    {
        return _convertorCache;
    }
    
    public boolean isAddClass()
    {
        return _addClass;
    }
    
    public void setAddClass(boolean addClass)
    {
        _addClass = addClass;
    }
    
    protected Convertor getConvertor(Class clazz)
    {
        return getConvertorCache().getConvertor(clazz, true, _addClass);
    }
    
    protected Object parseObject(Source source)
    {
        if (source.next()!='{')
            throw new IllegalStateException();
        Map map=newMap();

        char next=seekTo("\"}",source);

        while (source.hasNext())
        {
            if (next=='}')
            {
                source.next();
                break;
            }

            String name=parseString(source);
            seekTo(':',source);
            source.next();

            Object value=contextFor(name).parse(source);
            map.put(name,value);

            seekTo(",}",source);
            next=source.next();
            if (next=='}')
                break;
            else
                next=seekTo("\"}",source);
        }
        // this part modified to use configure class loading
        String className = (String)map.get("class");
        if (className!=null)
        {
            Class<?> c = loadClass(className);
            return c==null ? map : convertTo(c, map);
        }
        return map;
    }

}
