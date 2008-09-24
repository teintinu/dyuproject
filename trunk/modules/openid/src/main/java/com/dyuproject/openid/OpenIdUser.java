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
public class OpenIdUser implements Serializable, JSON.Convertible
{
    
    private String _claimedId;
    private String _openIdServer;
    private String _openIdDelegate;
    private String _identity;
    private String _assocHandle;
    
    private Map<String,Object> _associationData;
    
    public OpenIdUser()
    {
        
    }
    
    OpenIdUser(String claimedId, String openIdServer, String openIdDelegate)
    {
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
            _assocHandle = null;
            _associationData = null;
            if(_claimedId==null)
                _claimedId = identity;
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
    
    public int hashCode()
    {
        return _assocHandle!=null ? _assocHandle.hashCode() : _claimedId.hashCode();
    }

    public void fromJSON(Map map)
    {
        _claimedId = (String)map.get("ci");
        _assocHandle = (String)map.get("ah");
        _identity = (String)map.get("id");
        _openIdServer = (String)map.get("os");
        _associationData = (Map<String,Object>)map.get("ad");    
    }

    public void toJSON(Output out)
    {
        out.addClass(getClass());
        out.add("ci", _claimedId);
        out.add("ah", _assocHandle);
        out.add("id", _identity);
        out.add("os" ,_openIdServer);
        out.add("ad", _associationData);
    }
}
