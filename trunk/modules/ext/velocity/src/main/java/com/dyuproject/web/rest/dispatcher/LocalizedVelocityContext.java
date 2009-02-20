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

package com.dyuproject.web.rest.dispatcher;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.context.Context;

/**
 * @author David Yu
 * @created Jun 15, 2008
 */

public class LocalizedVelocityContext implements Context
{
    
    private static final Local __current = new Local();
    
    public static LocalizedVelocityContext getCurrent()
    {
        return __current.get();
    }
    
    private HttpServletRequest _request;
    
    private LocalizedVelocityContext()
    {
        
    }
    
    LocalizedVelocityContext setRequest(HttpServletRequest request)
    {
        _request = request;
        return this;
    }

    public boolean containsKey(Object key)
    {        
        return key!=null && _request.getAttribute(key.toString())!=null;
    }

    public Object get(String key)
    {        
        return _request.getAttribute(key);
    }

    public Object[] getKeys()
    {
        ArrayList<Object> list = new ArrayList<Object>();
        for(Enumeration<?> en = _request.getAttributeNames(); en.hasMoreElements();)
            list.add(en.nextElement());        
        return list.toArray();
    }

    public Object put(String key, Object value)
    {
        Object old = _request.getAttribute(key);
        _request.setAttribute(key, value);
        return old;
    }

    public Object remove(Object key)
    {
        if(key==null)
            return null;
        Object value = _request.getAttribute(key.toString());
        if(value!=null)
            _request.removeAttribute(key.toString());
        return value;
    }
    
    static class Local extends ThreadLocal<LocalizedVelocityContext>
    {        
        protected LocalizedVelocityContext initialValue()
        {
            return new LocalizedVelocityContext();
        }        
    }

}
