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

import com.dyuproject.util.http.HttpConnector;

/**
 * OpenIdContext - contains all necessary objects to operate on openid procedures.
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public final class OpenIdContext
{
    
    public static final String OPENID_NS = "http://specs.openid.net/auth/2.0";

    private final Association _association;
    private final Discovery _discovery;
    private final HttpConnector _httpConnector;

    public OpenIdContext(Discovery discovery, Association association, HttpConnector httpConnector)
    {
        _discovery = discovery;
        _association = association;
        _httpConnector = httpConnector;
    }
    
    /**
     * Gets the association.
     */
    public Association getAssociation()
    {
        return _association;
    }
    
    /**
     * Gets the discovery.
     */
    public Discovery getDiscovery()
    {
        return _discovery;
    }
    
    /**
     * Gets the http connector.
     */
    public HttpConnector getHttpConnector()
    {
        return _httpConnector;
    }    

}
