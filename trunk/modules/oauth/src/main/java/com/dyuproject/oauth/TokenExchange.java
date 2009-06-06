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

import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * TokenExchange
 * 
 * @author David Yu
 * @created Jun 1, 2009
 */

public abstract class TokenExchange
{
    
    public static TokenExchange getExchange(Token token)
    {
        return token.getKey()==null ? REQUEST_TOKEN : ACCESS_TOKEN; 
    }
    
    public static final TokenExchange REQUEST_TOKEN = new TokenExchange()
    {
        public void put(UrlEncodedParameterMap params, Endpoint ep, Token token)
        {
            if(params.getUrl()==null)
                params.setUrl(ep.getRequestTokenUrl());
            
            if(!params.containsKey(Constants.OAUTH_VERSION))
                params.put(Constants.OAUTH_VERSION, Constants.CURRENT_VERSION);
            
            params.put(Constants.OAUTH_CONSUMER_KEY, ep.getConsumerKey());
        }   
    };
    
    public static final TokenExchange ACCESS_TOKEN = new TokenExchange()
    {
        public void put(UrlEncodedParameterMap params, Endpoint ep, Token token)
        {
            if(params.getUrl()==null)
                params.setUrl(ep.getAccessTokenUrl());
            
            if(!params.containsKey(Constants.OAUTH_VERSION))
                params.put(Constants.OAUTH_VERSION, Constants.CURRENT_VERSION);
            
            params.add(Constants.OAUTH_CONSUMER_KEY, ep.getConsumerKey())
                .add(Constants.OAUTH_TOKEN, token.getKey())
                .add(Constants.OAUTH_VERIFIER, token.getAttribute(Constants.OAUTH_VERIFIER).toString());
        }  
    };

    
    public abstract void put(UrlEncodedParameterMap params, Endpoint ep, Token token);

}
