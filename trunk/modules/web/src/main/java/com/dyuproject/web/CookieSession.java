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

import java.io.Serializable;
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

@SuppressWarnings("serial")
public final class CookieSession implements Serializable, JSON.Convertible
{
    
    public static final String ATTR_NAME = "cs";
    
    private Map<String,Object> _attributes;
    private long _timeUpdated = 0;
    private String _ip;
    private transient boolean _persisted = false;
    
    public CookieSession()
    {
        
    }

    CookieSession(Map<String,Object> attributes)
    {
        _attributes = attributes;
    }
    
    /**
     * 
     * @param name
     * @param value can be any object/pojo.
     */
    public void setAttribute(String name, Object value)
    {
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
        return _attributes.remove(name)!=null;
    }
    
    public Map<String,Object> getAttrs()
    {
        return _attributes;
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
    
    public String getIP()
    {
        return _ip;
    }
    
    void setIP(String ip)
    {
        _ip = ip;
    }

    @SuppressWarnings("unchecked")
    public void fromJSON(Map map)
    {
        _attributes = (Map<String,Object>)map.get("a");
        _timeUpdated = ((Number)map.get("u")).longValue();
        _ip = (String)map.get("i");
    }

    public void toJSON(Output out)
    {
        //out.addClass(getClass());
        if(_attributes!=null)
            out.add("a", _attributes);
        
        out.add("u", _timeUpdated);
        
        if(_ip!=null)
            out.add("i", _ip);
    }

}
