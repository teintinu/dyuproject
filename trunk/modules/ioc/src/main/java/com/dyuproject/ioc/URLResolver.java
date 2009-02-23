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
import java.net.URL;


/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class URLResolver extends AbstractResolver
{
    
    public static final String TYPE = generateTypeFromClass(URLResolver.class);
    
    private static URLResolver __default;
    
    public static URLResolver getDefault()
    {
        if(__default==null)
        {
            synchronized(URLResolver.class)
            {
                if(__default==null)
                    __default = new URLResolver();  
            }
        }
        return __default;
    }
    
    public URLResolver()
    {
        
    }
    
    public String getType()
    {
        return TYPE;
    }    

    public void resolve(Resource resource, Context context) throws IOException
    {
        resource.resolve(newReader(new URL(resource.getPath()).openStream()));
    }

    public Resource createResource(String path) throws IOException
    {
        return new Resource(path, newReader(new URL(path).openStream()));
    }
    
    public Resource createResource(URL url) throws IOException
    {
        return new Resource(newReader(url.openStream()));
    }
    
    
}
