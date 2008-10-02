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

package com.dyuproject.web;

import java.util.Map;
import java.util.Set;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

/**
 * Session attribues being stored on the client cookie.
 * 
 * @author David Yu
 * @created May 19, 2008
 */

public class CookieSession implements JSON.Convertible
{
    
    private Map<String,Object> _attributes;
    private long _timeCreated = 0;
    private long _timeUpdated = 0;
    private boolean _persisted = false;
    
    public CookieSession()
    {
        
    }

    CookieSession(Map<String,Object> attributes)
    {
        _attributes = attributes;
        _timeCreated = System.currentTimeMillis();
    }
    
    /**
     * 
     * @param name
     * @param value The value should be a Map, Collection, String, Number(Long or Double) or 
     * JSON.Convertible. You will get a ClassCastException from getAttribute(name) if the value was 
     * not any of the specified objects.  
     * The value could also be an array of the specified objects.
     */
    public void setAttribute(String name, Object value)
    {
        if(_persisted)
            throw new IllegalStateException("session has already been persisted during this request.");
        
        _attributes.put(name, value);
    }
    
    public Object getAttribute(String name)
    {
        return _attributes.get(name);
    }
    
    public Set<String> getAttributeNames()
    {
        return _attributes.keySet();
    }
    
    public Map<String,Object> getAttributes()
    {
        return _attributes;
    }
    
    public boolean removeAttribute(String name)
    {
        if(_persisted)
            throw new IllegalStateException("session has already been persisted during this request.");
        
        return _attributes.remove(name)!=null;
    }
    
    public Map<String,Object> getAttrs()
    {
        return _attributes;
    }
    
    public long getTimeCreated()
    {
        return _timeCreated;
    }
    
    public long getTimeUpdated()
    {
        return _timeUpdated;
    }
    
    void markPersisted()
    {
        _persisted = true;
        _timeUpdated = System.currentTimeMillis();
    }
    
    public boolean isPersisted()
    {
        return _persisted;
    }

    public void fromJSON(Map map)
    {
        _attributes = (Map<String,Object>)map.get("a");
        _timeCreated = ((Number)map.get("c")).longValue();
        _timeUpdated = ((Number)map.get("u")).longValue();
    }

    public void toJSON(Output out)
    {
        out.addClass(getClass());
        out.add("a", _attributes);
        out.add("c", _timeCreated);
        out.add("u", _timeUpdated);
    }
    
    public String toString()
    {
        return JSON.toString(this);
    }    

}
