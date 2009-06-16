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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ConcurrentHashMap - in-memory consumer keys
 * @author David Yu
 * @created Jun 8, 2009
 */

public class ConcurrentMapHashStore extends HashStore
{
    
    private ConcurrentMap<String,String> _consumers = new ConcurrentHashMap<String,String>();
    
    public ConcurrentMapHashStore(String secretKey, String macSecretKey)
    {
        setSecretKey(secretKey);
        setMacSecretKey(macSecretKey);
    }

    protected String getConsumerSecret(String consumerKey)
    {
        return _consumers.get(consumerKey);
    }
    
    public ConcurrentMapHashStore addConsumer(String key, String secret)
    {
        _consumers.putIfAbsent(key, secret);
        return this;
    }

}
