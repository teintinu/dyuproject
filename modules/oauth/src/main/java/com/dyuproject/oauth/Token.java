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

package com.dyuproject.oauth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

/**
 * Token - stores the keys and state whether it is already useable or not.
 * 
 * @author David Yu
 * @created May 30, 2009
 */

@SuppressWarnings("serial")
public final class Token implements Serializable, JSON.Convertible
{
    
    public static final int UNITIALIZED = 0;
    public static final int UNAUTHORIZED = 1;
    public static final int AUTHORIZED = 2;
    public static final int ACCESS_TOKEN = 3;
    
    private int _state;
    private String _ck, _key, _secret;
    private Map<String,Object> _attributes;
    
    public Token()
    {
        
    }
    
    public Token(String ck)
    {
        _ck = ck;
    }
    
    public Token(String ck, String key, String secret)
    {
        _ck = ck;
        _key = key;
        _secret = secret;
    }
    
    public int getState()
    {
        return _state;
    }
    
    void setState(int state)
    {
        _state = state;
    }
    
    public String getCk()
    {
        return _ck;
    }    
    
    public String getKey()
    {
        return _key;
    }
    
    void setKey(String key)
    {
        _key = key;
    }
    
    public String getSecret()
    {
        return _secret;
    }
    
    void setSecret(String secret)
    {
        _secret = secret;
    }
    
    void set(int state, String key, String secret)
    {
        _state = state;
        _key = key;
        _secret = secret;
    }
    
    public boolean authorize(String key, String verifier)
    {
        if(_state<AUTHORIZED)
        {            
            if(_state>UNITIALIZED && _key!=null && _key.equals(key) && verifier!=null)
            {
                _state = AUTHORIZED;
                setAttribute(Constants.OAUTH_VERIFIER, verifier);
                return true;
            }
            return false;
        }
        return true;
    }
    
    public boolean isAuthentic()
    {
        return _state == ACCESS_TOKEN;
    }
    
    public Token get(String id)
    {
        return this;
    }
    
    public Token add(Token token)
    {
        return this;
    }    
    
    public Map<String,Object> getAttributes()
    {
        return _attributes;
    }
    
    public Object getAttribute(String key)
    {
        return _attributes==null ? null : _attributes.get(key);
    }
    
    public void setAttribute(String key, Object value)
    {
        if(_attributes==null)
            _attributes = new HashMap<String,Object>();
        
        _attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public void fromJSON(Map map)
    {
        _state = ((Number)map.get("st")).intValue();
        _ck = (String)map.get("ck");
        _key = (String)map.get("k");
        _secret = (String)map.get("sk");
        _attributes = (Map<String,Object>)map.get("a");
    }

    public void toJSON(Output out)
    {
        //out.addClass(getClass());
        out.add("st", _state);
        out.add("ck", _ck);
        if(_key!=null)
            out.add("k", _key);
        if(_secret!=null)
            out.add("sk", _secret);
        if(_attributes!=null)
            out.add("a", _attributes);
    }

}
