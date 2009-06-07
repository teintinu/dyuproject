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

package com.dyuproject.oauth.sp;

import java.io.Serializable;


/**
 * The token persisted by the service provider
 * 
 * @author David Yu
 * @created Jun 8, 2009
 */

public interface ServiceToken extends Serializable
{
    
    public String getConsumerSecret();
    public String getKey();
    public String getSecret();
    
    public interface Store
    {
        public ServiceToken getRequestToken(String consumerKey);
        public ServiceToken getAccessToken(String consumerKey, String requestToken);
        // url with oauth_token and oauth_verifier param
        public StringBuilder getAuthorizedCallbackUrl(String requestToken);
    }

}
