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
    
    /**
     * Gets the consumerSecret.
     */
    public String getConsumerSecret();
    /**
     * Gets the request or access token.
     */
    public String getKey();
    /**
     * Gets the token secret.
     */
    public String getSecret();
    /**
     * Gets the application assigned id when upon authorization of a user.
     * This basically could be the username tied to the user during the 
     * user's authentication on the service provider.
     */
    public String getId();
    
    /**
     * The token store.  Internally the implementation would generate new request 
     * tokens after validation and verification of the oauth parameters.
     */
    public interface Store
    {
        
        /**
         * Generates a new request token to be used by the caller to write a response.
         * The token should generally be bound or associated with the {@code consumerKey}  
         * and {@code callback}.
         */
        public ServiceToken newRequestToken(String consumerKey, String callback);
        
        /**
         * Gets the request token with secret to be verified by the caller; 
         * Returns null if the request token is invalid.
         */
        public ServiceToken getRequestToken(String consumerKey, String requestToken);        
         
        /**
         * Particularly useful for hybrid openid+oauth;  The underlying implementation will 
         * loosen the validation/verification since openid authentication is being used. 
         */
        public ServiceToken newHybridRequestToken(String consumerKey, String id);

        /**
         * Gets the auth callback or verifier; This could either be the url 
         * with oauth_token and oauth_verifier params, or the verifier "oob".
         */
        public String getAuthCallbackOrVerifier(String requestToken, String id);
        
        /**
         * Generates a new access token that is basically exchanged from the given 
         * {@code requestToken};  Returns null if the {@code requestToken} is invalid. 
         */
        public ServiceToken newAccessToken(String consumerKey, String verifier, String requestToken);

        /**
         * Generates a new access token that is basically exchanged from the given 
         * {@code requestToken};  Returns null if the {@code requestToken} is invalid.
         * The param {@code verifiedRequestToken} is the service token recently obtained 
         * from {@link #getRequestToken(String, String)}.  That is to use the consumerSecret 
         * linked to the same consumerKey to avoid another lookup.
         * This method is added for efficiency..
         */
        public ServiceToken newAccessToken(String consumerKey, String verifier, String requestToken, 
                ServiceToken verifiedRequestToken);
        
        /**
         * Gets the access token to be verified by the caller;
         * Returns null if the access token is invalid.
         * This is the method that you will use for every subsequent oauth request from 
         * consumers who have already been authenticated.
         * Even if the access token is valid, it could still return null due to an access 
         * token timeout.
         */
        public ServiceToken getAccessToken(String consumerKey, String accessToken);
    }

}
