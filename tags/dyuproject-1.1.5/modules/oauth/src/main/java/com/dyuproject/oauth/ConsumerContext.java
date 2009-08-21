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

import java.util.HashMap;
import java.util.Map;

import com.dyuproject.util.http.HttpConnector;

/**
 * ConsumerContext - contains all necessary objects to operate on oauth procedures.
 * 
 * @author David Yu
 * @created May 29, 2009
 */

public class ConsumerContext
{
    
    private HttpConnector _httpConnector;
    private NonceAndTimestamp _nonceAndTimestamp;
    private Map<String,Endpoint> _endpoints = new HashMap<String,Endpoint>();
    
    public ConsumerContext()
    {
        
    }
    
    public ConsumerContext(HttpConnector httpConnector, NonceAndTimestamp nonceAndTimestamp)
    {
        _httpConnector = httpConnector;
        _nonceAndTimestamp = nonceAndTimestamp;
    }
    
    public HttpConnector getHttpConnector()
    {
        return _httpConnector;
    }

    public void setHttpConnector(HttpConnector httpConnector)
    {
        _httpConnector = httpConnector;
    }
    
    public NonceAndTimestamp getNonceAndTimestamp()
    {
        return _nonceAndTimestamp;
    }
    
    public void setNonceAndTimestamp(NonceAndTimestamp nonceAndTimestamp)
    {
        _nonceAndTimestamp = nonceAndTimestamp;
    }
    
    public ConsumerContext addEndpoint(Endpoint ep)
    {
        _endpoints.put(ep.getDomain(), ep);
        return this;
    }
    
    public Endpoint getEndpoint(String domain)
    {
        return _endpoints.get(domain);
    }
    
    public Map<String,Endpoint> getEndpoints()
    {
        return _endpoints;
    }
    
    public void setEndpoints(Map<String,Endpoint> endpoints)
    {
        _endpoints.putAll(endpoints);
    }
    
    public void addEndpoints(Endpoint[] endpoints)
    {
        for(Endpoint ep : endpoints)
            _endpoints.put(ep.getDomain(), ep);
    }
    
    public void addEndpoints(Iterable<Endpoint> endpoints)
    {
        for(Endpoint ep : endpoints)
            _endpoints.put(ep.getDomain(), ep);
    }


}
