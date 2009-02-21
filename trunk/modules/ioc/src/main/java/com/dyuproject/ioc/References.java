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

import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Convertible;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.ioc.Parser.Context;

/**
 * @author David Yu
 * @created Feb 20, 2009
 */

public class References implements Convertible
{

    References _refs;
    Map<String,Object> _map;
    
    public References()
    {
        Context context = Parser.getCurrentContext();
        if(context!=null)
            context.getAppContext().addRefs(this);
    }
    
    public References(Map<String,Object> map)
    {
        this();
        _map = map;
    }
    
    public void addRefs(References refs)
    {
        ApplicationContext.wrapRefs(refs, this);
    }
    
    public Object put(String key, Object value)
    {
        if(_map==null)
            _map = new HashMap<String,Object>();
        
        return _map.put(key, value);
    }
    
    public Object get(String key)
    {
        if(key==null)
            return null;
        return ApplicationContext.getRef(key, this);
    }
    
    public void putAll(Map<String,Object> map)
    {
        if(_map==null)
            _map = map;
        else
            _map.putAll(map);
    }
    
    public void fromJSON(Map map)
    {
        if(_map==null)
            _map = (Map<String,Object>)map;
        else
            _map.putAll((Map<String,Object>)map);
    }

    public void toJSON(Output out)
    {
        if(_map==null || _map.isEmpty())
            return;
        
        for(Map.Entry<String, Object> entry: _map.entrySet())
            out.add(entry.getKey(), entry.getValue());
    }

}
