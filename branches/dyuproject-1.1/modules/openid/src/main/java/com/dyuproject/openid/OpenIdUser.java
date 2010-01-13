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

public final class OpenIdUser implements Serializable, JSON.Convertible
{
    
    private static final long serialVersionUID = 2009100658L;
    
    public static final String ATTR_NAME = "openid_user";
    
    /**
     * Populate/clone the provided {@code user}.
     */
    public static OpenIdUser populate(OpenIdUser user)
    {
        return new OpenIdUser(user.getIdentifier(), user.getClaimedId(), user.getOpenIdServer(), 
                user.getOpenIdDelegate());
    }
    
    /**
     * Creates a new OpenIdUser pre-populated with discovery data (to skip discovery).
     */
    public static OpenIdUser populate(String identifier, String claimedId, String openIdServer)
    {
        return new OpenIdUser(identifier, claimedId, openIdServer, null);
    }
    
    /**
     * Creates a new OpenIdUser pre-populated with discovery data (to skip discovery).
     */
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
    
    /**
     * Checks whether this user is already associated with his openid provider.
     */
    public boolean isAssociated()
    {
        return _identity!=null || (_assocHandle!=null && _associationData!=null);
    }
    
    /**
     * Checks whether this user is already authenticated.
     */
    public boolean isAuthenticated()
    {
        return _identity!=null;
    }
    
    /**
     * Gets the raw identifier (openid_identifier) supplied on the login form.
     */
    public String getIdentifier()
    {
        return _identifier;
    }
    
    /**
     * Gets the claimed id of the user.  If this is a generic id (E.g the user does not 
     * own this id), this will either be the user's local id or identity.
     */
    public String getClaimedId()
    {
        return _claimedId;
    }
    
    /**
     * Gets the user's openid server.
     */
    public String getOpenIdServer()
    {
        return _openIdServer;
    }
    
    /**
     * Gets the user's local id
     */
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
    
    /**
     * Gets the user's openid identity.
     */
    public String getIdentity()
    {
        return _identity;
    }
    
    void setAssocHandle(String assocHandle)
    {
        _assocHandle = assocHandle;
    }
    
    /**
     * Gets the handle/key for the openid associated.
     */
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
    
    /**
     * Sets a custom attribute that will be peristent across different http requests.
     */
    public void setAttribute(String key, Object value)
    {
        if(_attributes==null)
            _attributes = new HashMap<String,Object>();
        
        _attributes.put(key, value);
    }
    
    /**
     * Gets the custom attributes that was peristed across different http requests.
     */
    public Map<String,Object> getAttributes()
    {
        return _attributes;
    }
    
    /**
     * Shorthand for {@link #getAttributes()}, which is convenient for views/templates.
     */
    public Map<String,Object> getA()
    {
        return _attributes;
    }
    
    /**
     * Gets the custom, persistent attribute mapped to the given {@code key}.
     */
    public Object getAttribute(String name)
    {
        return _attributes==null ? null : _attributes.get(name);
    }
    
    /**
     * Removes the custom, persistent attribute mapped to the given {@code key}.
     */
    public Object removeAttribute(String name)
    {
        return _attributes==null ? null : _attributes.remove(name);
    }
    
    /**
     * Gets the extensions added to this user's openid provider.
     */
    public Map<String,String> getExtensions()
    {
        return _extensions;
    }
    
    /**
     * Adds an extension mapping for this user's openid provider if this extension truly is 
     * available on the user's openid provider. 
     * <blockquote>
     * <pre>
     * NOTE: 
     *   This is invoked only on discovery and association.
     *   The mappings are not persistent across different http requests (transient).
     * </pre>
     * </blockquote>
     */
    public void addExtension(String namespace, String alias)
    {
        if(_extensions==null)
            _extensions = new HashMap<String,String>(3);
        
        _extensions.put(namespace, alias);
    }
    
    /**
     * Gets the alias of the extension mapped with the given {@code namespace}.
     */
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
