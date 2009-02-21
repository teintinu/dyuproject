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
import java.lang.reflect.Array;
import java.util.Map;

import org.mortbay.log.Log;
import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;
import org.mortbay.util.ajax.JSON.Source;

import com.dyuproject.ioc.Parser.Context;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class Import implements Convertible
{
    
    static void importResources(Map<?,?> resources) throws IOException
    {
        for(Map.Entry<?, ?> entry : resources.entrySet())
        {
            if(entry.getKey() instanceof String)
            {
                String key = (String)entry.getKey();
                if(!"class".equalsIgnoreCase(key))
                    importResource((String)entry.getKey(), entry.getValue().toString());
            }
        }
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
    
    static void importResource(String resource, String type) throws IOException
    {
        Context context = Parser.getCurrentContext();
        if(context==null)
        {
            Log.warn("Not in context.");
            return;
        }
        Source source = context.getParser().getSourceFactory().getSource(resource, type);
        try
        {
            context.getAppContext().addImport(ApplicationContext.load(source, context.getParser()));
        }
        finally
        {
            Parser.setCurrentContext(context);
        }
    }    

    public void fromJSON(Map map)
    {        
        Object resources = map.get("resources");
        if(resources==null)
        {
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
        
        if(map.size()>2)
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
