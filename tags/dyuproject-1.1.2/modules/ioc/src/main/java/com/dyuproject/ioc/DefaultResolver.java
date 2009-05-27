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
import java.util.Map;

import com.dyuproject.ioc.Resource.Resolver;

/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class DefaultResolver extends ResolverCollection
{
    
    public static final String TYPE = AbstractResolver.generateTypeFromClass(DefaultResolver.class);
    
    private static DefaultResolver __instance;
    
    public static DefaultResolver getInstance()
    {
        if(__instance==null)
        {
            synchronized(DefaultResolver.class)
            {
                if(__instance==null)
                    __instance = new DefaultResolver();
            }
        }
        return __instance;
    }
    
    public static void putDefaultResolvers(Map<String,Resolver> resolvers)
    {
        resolvers.put(FileResolver.getDefault().getType(), FileResolver.getDefault());
        resolvers.put(URLResolver.getDefault().getType(), URLResolver.getDefault());
        resolvers.put(ClasspathResolver.getDefault().getType(), ClasspathResolver.getDefault());
    }
    
    private DefaultResolver()
    {
        super(5);
        putDefaultResolvers(getResolvers());
    }
    
    public String getType()
    {
        return TYPE;
    }
    
    /*public void resolve(Resource resource, Context context) throws IOException
    {
        if(resource.getType()==null)
            resolveDefault(resource, context);
        else
            super.resolve(resource, context);
    }*/

    protected void resolveDefault(Resource resource, Context context) throws IOException
    {
        String path = resource.getPath().trim();
        int idx = path.indexOf(':');
        switch(idx)
        {
            case -1:
                FileResolver.getDefault().resolve(resource, context);
                return;
            //case 0:
            //    throw new IOException("invalid resource: " + path);
            case 1:
                // windows drive letter
                FileResolver.getDefault().resolve(resource, context);
                return;
            case 4:
            case 5:
                if(path.charAt(0)=='h')
                {
                    // http(s) url
                    URLResolver.getDefault().resolve(resource, context);
                    return;
                }
        }
        String type = path.substring(0, idx).trim();
        Resolver resolver = getResolver(type);
        if(resolver==null)
            FileResolver.getDefault().resolve(resource, context);
        else
        {
            path = path.substring(idx+1).trim();
            if(path.length()==0)
                throw new IOException("invalid resource: " + path);
            
            resource.setPath(path);
            resolver.resolve(resource, context);
        }
    }

    public Resource createResource(String path) throws IOException
    {
        Resource resource = new Resource(path);
        resolveDefault(resource, null);
        return resource;
    }

}