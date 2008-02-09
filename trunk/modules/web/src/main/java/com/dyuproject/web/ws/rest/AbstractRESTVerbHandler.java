//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
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

package com.dyuproject.web.ws.rest;

import java.util.Map;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.ResourceUnavailable;

/**
 * @author David Yu
 */

public abstract class AbstractRESTVerbHandler implements WebServiceHandler
{
    
    int _depth = 0;    
    boolean _initialized = false;
    private String _name;
    
    public AbstractRESTVerbHandler(String name)
    {
        _name = name;
    }
    
    public void init()
    {
        if(_name==null)
            throw new IllegalStateException("Verb *name* cannot be null");
        _initialized = true;
    }
    
    void setDepth(int depth)
    {
        _depth = depth;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public Object handle(String[] pathInfo, Map<String, String> params, long parentId) throws Exception
    {
        return ResourceUnavailable.getInstance();
    }
    
    public int hashCode()
    {
        return _name.hashCode();
    }
    
    public boolean equals(Object obj)
    {
        return obj!=null && obj.hashCode() == hashCode();
    }
    
    public String toString()
    {
        return _name;
    }

}
