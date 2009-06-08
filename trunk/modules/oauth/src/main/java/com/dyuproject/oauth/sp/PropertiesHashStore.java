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

import java.util.Properties;

/**
 * PropertiesHashStore - in-memory consumer keys
 * 
 * @author David Yu
 * @created Jun 8, 2009
 */

public class PropertiesHashStore extends HashStore
{
    
    private static PropertiesHashStore __instance;
    
    public static PropertiesHashStore getInstance()
    {
        if(__instance==null)
        {
            synchronized(PropertiesHashStore.class)
            {
                if(__instance==null)
                {
                    
                }
            }
        }
        return __instance;
    }
    
    private Properties _consumers;
    
    public PropertiesHashStore(String secret, String macSecretKey, Properties consumers)
    {
        setSecretKey(secret);
        setMacSecretKey(macSecretKey);
        _consumers = consumers;
    }

    protected String getConsumerSecret(String consumerKey)
    {
        return _consumers.getProperty(consumerKey);
    }

}
