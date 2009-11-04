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

/**
 * Discovery - the mechanism to obtain information about a user's openid provider and 
 * its available services.
 * 
 * @author David Yu
 * @created Sep 10, 2008
 */

public interface Discovery
{
    
    /**
     * Discovers the user's openid server endpoint and local id (optional).
     */
    public OpenIdUser discover(Identifier identifier, OpenIdContext context) 
    throws Exception;
    
    
    /**
     * The cached user objects used by relyingParty will be filled with data.
     * The relying party always gets from the cache with clone set to true.
     *
     */
    public interface UserCache
    {
        /**
         * Gets the user assoicated with the given {@code key} from the cache; 
         * The flag {@code clone} is whether to create a different instance containting 
         * the same properties (useful when the OpenIdUser is not deserialized but instead 
         * held in memory).
         */
        public OpenIdUser get(String key, boolean clone);
        /**
         * Puts the {@code user} associated with the {@code key} in the cache.
         */
        public void put(String key, OpenIdUser user);
    }

}
