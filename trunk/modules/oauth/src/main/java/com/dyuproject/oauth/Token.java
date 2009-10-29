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

public final class Token implements Serializable, JSON.Convertible
{
    
    private static final long serialVersionUID = 2009100604L;
    
    /**
     * Uninitialized state.
     */
    public static final int UNITIALIZED = 0;
    /**
     * Unauthorized state.
     */
    public static final int UNAUTHORIZED = 1;
    /**
     * Authorized state.
     */
    public static final int AUTHORIZED = 2;
    /**
     * Access token state.  The token can already be used to do query data 
     * from the service provider.
     */
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
        this(ck, key, secret, UNITIALIZED);
    }
    
    public Token(String ck, String key, String secret, int state)
    {
        _ck = ck;
        _key = key;
        _secret = secret;
        _state = state;
    }
    
    /**
     * Gets the state of the token.
     */
    public int getState()
    {
        return _state;
    }
    
    void setState(int state)
    {
        _state = state;
    }
    
    /**
     * Gets the consumer secret.
     */
    public String getCk()
    {
        return _ck;
    }    
    
    /**
     * Gets the consumer key.
     */
    public String getKey()
    {
        return _key;
    }
    
    void setKey(String key)
    {
        _key = key;
    }
    
    /**
     * Gets the secret key; This is obtained from the exchanges (OAuth dance).
     */
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
    
    /**
     * Checks whether the key and the verifier is authorized or not.
     */
    public boolean authorize(String key, String verifier)
    {
        if(_state<AUTHORIZED)
        {            
            if(_state>UNITIALIZED && _key!=null && _key.equals(key))
            {
                _state = AUTHORIZED;
                // allow for oauth+openid hybrid
                if(verifier!=null)
                    setAttribute(Constants.OAUTH_VERIFIER, verifier);
                return true;
            }
            return false;
        }
        return true;
    }
    
    /**
     * Checks whether the token is authentic (aka useable to make auth requests against 
     * the service provider.
     */
    public boolean isAuthentic()
    {
        return _state == ACCESS_TOKEN;
    }
    
    /**
     * Gets the attributes of this token.
     */
    public Map<String,Object> getAttributes()
    {
        return _attributes;
    }
    
    /**
     * Gets an attribute based from the given key.
     */
    public Object getAttribute(String key)
    {
        return _attributes==null ? null : _attributes.get(key);
    }
    
    /**
     * Sets an attribute based from the given key and value.
     */
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
        out.addClass(getClass());
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
