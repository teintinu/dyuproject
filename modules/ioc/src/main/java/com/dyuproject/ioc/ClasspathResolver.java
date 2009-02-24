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

import java.io.IOException;
import java.io.Reader;
import java.net.URL;


/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class ClasspathResolver extends AbstractResolver
{
    
    public static final String TYPE = generateTypeFromClass(ClasspathResolver.class);
    
    private static boolean __checkParents = Boolean.getBoolean("classpathresolver.check_parents");
    
    private static ClasspathResolver __default;
    
    public static ClasspathResolver getDefault()
    {
        if(__default==null)
        {
            synchronized(ClasspathResolver.class)
            {
                if(__default==null)
                    __default = new ClasspathResolver();  
            }
        }
        return __default;
    }    
    
    public static boolean isCheckParents()
    {
        return __checkParents;
    }
    
    public static void setCheckParents(boolean checkParents)
    {
        __checkParents = checkParents;
    }
    
    public static Class<?> loadClass(String className, Class<?> context)
    {
        return Parser.loadClass(className, context, __checkParents);
    }
    
    public static Class<?> loadClass(String className)
    {
        return Parser.loadClass(className, ClasspathResolver.class, __checkParents);
    }
    
    public static URL getResource(String path, Class<?> context)
    {
        return Parser.getResource(path, context, __checkParents);
    }
    
    public static URL getResource(String path)
    {
        return Parser.getResource(path, ClasspathResolver.class, __checkParents);
    }    
    
    public ClasspathResolver()
    {
        
    }
    
    public String getType()
    {
        return TYPE;
    }    

    public void resolve(Resource resource, Context context) throws IOException
    {
        URL url = getResource(resource.getPath());
        if(url==null)
            throw new IOException(resource.getPath() + " not found in classpath.");

        resource.resolve(newReader(url.openStream()));
    }

    public Resource createResource(String path) throws IOException
    {
        URL url = getResource(path);
        if(url==null)
            throw new IOException(path + " not found in classpath.");
        
        Reader reader = newReader(url.openStream());
        Resource resource = new Resource(path, getType());
        resource.resolve(reader);
        return resource;
    }

}
