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
 * Discovery
 * 
 * @author David Yu
 * @created Sep 10, 2008
 */

public interface Discovery
{
    
    public OpenIdUser discover(Identifier identifier, OpenIdContext context) 
    throws Exception;
    
    
    /**
     * The cached user objects used by relyingParty will be filled with data.
     * The relying party always gets from the cache with clone set to true.
     *
     */
    public interface UserCache
    {
        public OpenIdUser get(String key, boolean clone);
        public void put(String key, OpenIdUser user);
    }

}
