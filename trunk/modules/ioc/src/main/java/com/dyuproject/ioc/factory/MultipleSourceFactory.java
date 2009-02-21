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

package com.dyuproject.ioc.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Source;

import com.dyuproject.ioc.SourceFactory;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public class MultipleSourceFactory implements SourceFactory
{
    
    public static final String TYPE_FILE = "file";
    public static final String TYPE_URL = "url";
    public static final String TYPE_CLASSPATH = "classpath";
    
    private static MultipleSourceFactory __default;
    
    public static MultipleSourceFactory getDefault()
    {
        if(__default==null)
        {
            synchronized(MultipleSourceFactory.class)
            {
                if(__default==null)
                {                    
                    __default = new MultipleSourceFactory();
                    addDefaultSourceFactories(__default.getSourceFactories());
                }
            }
        }
        return __default;
    }
    
    
    public static void addDefaultSourceFactories(Map<String,SourceFactory> sourceFactories)
    {
        sourceFactories.put(TYPE_FILE, FileSourceFactory.getInstance());
        sourceFactories.put(TYPE_URL, URLSourceFactory.getInstance());
        sourceFactories.put(TYPE_CLASSPATH, ClasspathSourceFactory.getInstance());        
    }
    
    private Map<String,SourceFactory> _sourceFactories;
    
    public MultipleSourceFactory()
    {
        this(new HashMap<String,SourceFactory>());
    }
    
    public MultipleSourceFactory(Map<String,SourceFactory> sourceFactories)
    {
        _sourceFactories = sourceFactories;
    }
    
    public Map<String,SourceFactory> getSourceFactories()
    {
        return _sourceFactories;
    }
    
    public SourceFactory getSourceFactory(String type)
    {
        return _sourceFactories.get(type);
    }
    
    public void addSourceFactory(String type, SourceFactory sourceFactory)
    {
        _sourceFactories.put(type, sourceFactory);
    }
    
    public Object getResource(String resource) throws IOException
    {
        resource = resource.trim();
        int idx = resource.indexOf(':');
        if(idx==-1)
            return FileSourceFactory.getInstance().getSource(resource);
        
        switch(idx)
        {
            case 1:
                // windows drive letter
                return FileSourceFactory.getInstance().getSource(resource);
            case 4:
            case 5:
                if(resource.charAt(0)=='h')
                {
                    // http(s) url
                    return URLSourceFactory.getInstance().getSource(resource);
                }
        }
        SourceFactory factory = getSourceFactory(resource.substring(0, idx));
        return factory==null ? FileSourceFactory.getInstance().getResource(resource) : 
            factory.getResource(resource.substring(idx+1));
    }

    public Source getSource(String resource) throws IOException
    {
        resource = resource.trim();
        int idx = resource.indexOf(':');
        if(idx==-1)
            return FileSourceFactory.getInstance().getSource(resource);
        
        switch(idx)
        {
            case 1:
                // windows drive letter
                return FileSourceFactory.getInstance().getSource(resource);
            case 4:
            case 5:
                if(resource.charAt(0)=='h')
                {
                    // http(s) url
                    return URLSourceFactory.getInstance().getSource(resource);
                }
        }
        
        SourceFactory factory = getSourceFactory(resource.substring(0, idx));
        return factory==null ? FileSourceFactory.getInstance().getSource(resource) : 
            factory.getSource(resource.substring(idx+1));
    }
    
    public Source getSource(String resource, String type) throws IOException
    {
        if(type==null || type.length()==0)
            return getSource(resource);
        
        SourceFactory factory = getSourceFactory(type);
        return factory==null ? FileSourceFactory.getInstance().getSource(resource) : 
            factory.getSource(resource);
    }

}
