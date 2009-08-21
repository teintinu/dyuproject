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

package com.dyuproject.util;

import java.net.URL;

/**
 * ClassLoader util for loading classes and finding resources.
 * No checked exceptions.
 * 
 * @author David Yu
 * @created Jan 20, 2009
 */

public abstract class ClassLoaderUtil
{
    
    public static Class<?> loadClass(String className, Class<?> context)
    {
        return loadClass(className, context, false);
    }
    
    public static Class<?> loadClass(String className, Class<?> context, boolean checkParent)
    {
        Class<?> clazz = null;
        try
        {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);  
        }
        catch(ClassNotFoundException e)
        {
            if(context!=null)
            {
                ClassLoader loader = context.getClassLoader();
                while(loader!=null)
                {
                    try
                    {                    
                        clazz = loader.loadClass(className);
                        return clazz;
                    }
                    catch(ClassNotFoundException e1)
                    {
                        loader = checkParent ? loader.getParent() : null;
                    }
                }
            }
        }
        return clazz;
    }
    
    public static Object newInstance(String className, Class<?> context) throws Exception
    {
        return newInstance(className, context, false);
    }
    
    public static Object newInstance(String className, Class<?> context, boolean checkParent) 
    throws Exception
    {
        Class<?> clazz = loadClass(className, context, checkParent);
        if(clazz==null)
            throw new Exception(className + " not found in the classpath.");
        
        return clazz.newInstance();
    }
    
    public static URL getResource(String resource, Class<?> context)
    {
        return getResource(resource, context, false);
    }
    
    public static URL getResource(String resource, Class<?> context, boolean checkParent)
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if(url!=null)
            return url;
        
        if(context!=null)
        {
            ClassLoader loader = context.getClassLoader();
            while(loader!=null)
            {
                url = loader.getResource(resource);
                if(url!=null)
                    return url;
                loader = checkParent ? loader.getParent() : null;
            }
        }
        
        return ClassLoader.getSystemResource(resource);
    }


}
