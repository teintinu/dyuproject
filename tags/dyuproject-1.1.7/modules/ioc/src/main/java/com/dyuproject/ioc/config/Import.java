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

package com.dyuproject.ioc.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;

import org.mortbay.log.Log;
import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.ioc.ApplicationContext;
import com.dyuproject.ioc.Context;
import com.dyuproject.ioc.Resource;

/**
 * A configuration component from a json context file w/c imports and loads another 
 * {@link ApplicationContext} into the current parsing context {@link Context}.
 * 
 * @author David Yu
 * @created Feb 21, 2009
 */

public final class Import implements Convertible
{
    
    static void importResources(Map<?,?> resources) throws IOException
    {
        for(Map.Entry<?, ?> entry : resources.entrySet())
            importResource((String)entry.getKey(), entry.getValue().toString());
    }
    
    static void importResources(Object array) throws IOException
    {
        int len = Array.getLength(array);
        for(int i=0; i<len; i++)
        {
            Object resource = Array.get(array, i);
            if(resource instanceof String)
                importResource((String)resource, null);
        }
    }
    
    static void importResource(String path, String type) throws IOException
    {
        Context context = Context.getCurrent();
        if(context==null)
        {
            Log.warn("Not in context.");
            return;
        }
        Resource resource = new Resource(path, type);
        context.getParser().getResolver().resolve(resource, context);
        if(!resource.isResolved())
            throw new IllegalStateException("resource not resolved.");
        try
        {
            context.getAppContext().addImport(ApplicationContext.load(resource, 
                    context.getParser()));
        }
        finally
        {
            Context.setCurrent(context);
        }
    }    

    @SuppressWarnings("unchecked")
    public void fromJSON(Map map)
    {
        map.remove("class");
        Object resources = map.get("resources");
        if(resources==null)
        {
            if(map.size()>0)
            {
                try
                {
                    // the resources are the key/value pairs
                    // e.g "some/relative/path": "file"
                    importResources(map);
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            return;
        }
        
        if(resources.getClass().isArray())
        {
            try
            {
                importResources(resources);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        
        if(map.size()>1)
        {
            try
            {
                // the resources are the key/value pairs
                // e.g "some/relative/path": "file"
                importResources(map);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }  
        }      
    }

    public void toJSON(Output out)
    {
        
    }

}
