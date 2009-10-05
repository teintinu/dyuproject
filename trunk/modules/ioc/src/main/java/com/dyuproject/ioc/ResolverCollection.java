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
import java.util.HashMap;
import java.util.Map;

import com.dyuproject.ioc.Resource.Resolver;


/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class ResolverCollection implements Resolver
{
    
    public static final String TYPE = "collection";
    
    protected final String _type;
    protected final Map<String,Resolver> _resolvers;
    
    public ResolverCollection()
    {
        this(new HashMap<String,Resolver>(), TYPE);
    }
    
    public ResolverCollection(int size)
    {
        this(new HashMap<String,Resolver>(size), TYPE);
    }
    
    public ResolverCollection(int size, String type)
    {
        this(new HashMap<String,Resolver>(size), type);
    }
    
    public ResolverCollection(Map<String,Resolver> resolvers, String type)
    {
        _resolvers = resolvers;
        _type = type;
    }

    public final String getType()
    {        
        return _type;
    }
    
    public final Resolver getResolver(String type)
    {
        return _resolvers.get(type);
    }
    
    public final Resolver putResolver(Resolver resolver)
    {
        return _resolvers.put(resolver.getType(), resolver);
    }

    public final void resolve(Resource resource, Context context) throws IOException
    {
        Resolver resolver = getResolver(resource.getType());
        if(resolver==null)
            resolveDefault(resource, context);
        else
            resolver.resolve(resource, context);
    }
    
    protected void resolveDefault(Resource resource, Context context) throws IOException
    {
        throw new IOException("resource: " + resource.getPath() + " not resolved");
    }
    
    public Resource createResource(String path) throws IOException
    {
        throw new UnsupportedOperationException();
    }

}
