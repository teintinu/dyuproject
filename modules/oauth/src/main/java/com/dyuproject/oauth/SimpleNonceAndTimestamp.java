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
 * SimpleNonceAndTimestamp
 * 
 * @author David Yu
 * @created Jun 1, 2009
 */

public final class SimpleNonceAndTimestamp implements NonceAndTimestamp
{
    
    public static final SimpleNonceAndTimestamp DEFAULT = new SimpleNonceAndTimestamp();

    public void put(UrlEncodedParameterMap params, String consumerKey)
    {
        long ts = System.currentTimeMillis();
        params.put(Constants.OAUTH_TIMESTAMP, String.valueOf(ts/1000));
        params.put(Constants.OAUTH_NONCE, String.valueOf(ts));
    }

}
