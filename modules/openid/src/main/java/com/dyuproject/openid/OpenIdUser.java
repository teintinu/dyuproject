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

package com.dyuproject.openid;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

/**
 * The openid user where the identity, claimedId and association metadata are persisted. 
 * 
 * @author David Yu
 * @created Sep 10, 2008
 */
@SuppressWarnings("serial")
public final class OpenIdUser implements Serializable, JSON.Convertible
{
    
    public static final String ATTR_NAME = "openid_user";
    
    public static OpenIdUser populate(OpenIdUser user)
    {
        return new OpenIdUser(user.getIdentifier(), user.getClaimedId(), user.getOpenIdServer(), 
                user.getOpenIdDelegate());
    }
    
    public static OpenIdUser populate(String identifier, String claimedId, String openIdServer)
    {
        return new OpenIdUser(identifier, claimedId, openIdServer, null);
    }
    
    public static OpenIdUser populate(String identifier, String claimedId, String openIdServer, 
            String openIdDelegate)
    {
        return new OpenIdUser(identifier, claimedId, openIdServer, openIdDelegate);
    }
    
    private String _identifier;
    private String _claimedId;
    private String _identity;
    private String _openIdServer;
    private String _openIdDelegate;
    private String _assocHandle;    
    
    private Map<String,Object> _associationData;
    private Map<String,Object> _attributes;
    private transient Map<String,String> _extensions;
    
    public OpenIdUser()
    {
        
    }
    
    OpenIdUser(String identifier, String claimedId, String openIdServer, String openIdDelegate)
    {
        _identifier = identifier;
        _claimedId = claimedId;
        _openIdServer = openIdServer;
        _openIdDelegate = openIdDelegate;
    }
    
    public boolean isAssociated()
    {
        return _identity!=null || (_assocHandle!=null && _associationData!=null);
    }
    
    public boolean isAuthenticated()
    {
        return _identity!=null;
    }
    
    public String getIdentifier()
    {
        return _identifier;
    }
    
    public String getClaimedId()
    {
        return _claimedId;
    }
    
    public String getOpenIdServer()
    {
        return _openIdServer;
    }
    
    public String getOpenIdDelegate()
    {
        return _openIdDelegate;
    }
    
    void setIdentity(String identity)
    {
        if(identity!=null)
        {
            _identity = identity;
            if(_openIdDelegate==null)
                _claimedId = identity;            
            
            _assocHandle = null;
            _associationData = null;
            // TODO confirm that nobody needs this when user is already authenticated
            _openIdServer = null;
            _openIdDelegate = null;
        }
    }
    
    public String getIdentity()
    {
        return _identity;
    }
    
    void setAssocHandle(String assocHandle)
    {
        _assocHandle = assocHandle;
    }
    
    public String getAssocHandle()
    {
        return _assocHandle;
    }
    
    void setAssociationData(Map<String,Object> associationData)
    {
        _associationData = associationData;
    }
    
    Map<String,Object> getAssociationData()
    {
        return _associationData;
    }
    
    public void setAttribute(String key, Object value)
    {
        if(_attributes==null)
            _attributes = new HashMap<String,Object>();
        
        _attributes.put(key, value);
    }
    
    public Map<String,Object> getAttributes()
    {
        return _attributes;
    }
    
    // shorthand (good for use with views/templates)
    public Map<String,Object> getA()
    {
        return _attributes;
    }
    
    public Object getAttribute(String name)
    {
        return _attributes==null ? null : _attributes.get(name);
    }
    
    public Object removeAttribute(String name)
    {
        return _attributes==null ? null : _attributes.remove(name);
    }
    
    public Map<String,String> getExtensions()
    {
        return _extensions;
    }
    
    public void addExtension(String namespace, String alias)
    {
        if(_extensions==null)
            _extensions = new HashMap<String,String>(3);
        
        _extensions.put(namespace, alias);
    }
    
    public String getExtension(String namespace)
    {
        return _extensions==null ? null : _extensions.get(namespace);
    }

    @SuppressWarnings("unchecked")
    public void fromJSON(Map map)
    {
        _identifier = (String)map.get("h");
        _claimedId = (String)map.get("a");
        _identity = (String)map.get("b");
        _assocHandle = (String)map.get("c");
        _associationData = (Map<String,Object>)map.get("d");
        _openIdServer = (String)map.get("e");
        _openIdDelegate = (String)map.get("f");        
        _attributes = (Map<String,Object>)map.get("g");
    }

    public void toJSON(Output out)
    {        
        //out.addClass(getClass());
        out.add("h", _identifier);
        out.add("a", _claimedId);
        if(_identity!=null)
        {
            out.add("b", _identity);
            // TODO confirm
            //out.add("e" ,_openIdServer);
            //out.add("f" ,_openIdDelegate);
        }
        else
        {
            if(_assocHandle!=null)
                out.add("c", _assocHandle);
            if(_associationData!=null)
                out.add("d", _associationData);
            
            out.add("e" ,_openIdServer);
            
            if(_openIdDelegate!=null)
                out.add("f" ,_openIdDelegate);            
        }
        
        if(_attributes!=null)
            out.add("g", _attributes);
    }
}
