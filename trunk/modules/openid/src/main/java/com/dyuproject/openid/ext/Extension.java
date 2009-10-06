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

package com.dyuproject.openid.ext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Extension for openid
 * 
 * @author David Yu
 * @created May 27, 2009
 */

public interface Extension extends RelyingParty.Listener
{
    
    public String getAlias();
    public String getNamespace();
    
    
    public static interface Exchange
    {
        
        public String getAlias();
        
        public void put(OpenIdUser user, HttpServletRequest request,
                UrlEncodedParameterMap params, String extensionAlias);
        
        public void parseAndPut(OpenIdUser user, HttpServletRequest request, 
                Map<String,String> attributes, String extensionAlias);
        
    }

}
