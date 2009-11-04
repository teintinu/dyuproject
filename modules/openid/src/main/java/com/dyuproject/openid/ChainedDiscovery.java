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

package com.dyuproject.openid;

import java.util.List;

/**
 * ChainedDiscovery - chains and delegates the discovery to its wrapped discoveries until an 
 * {@link OpenIdUser} is successfully discovered.
 * 
 * @author David Yu
 * @created May 26, 2009
 */

public class ChainedDiscovery implements Discovery
{
    
    private final Discovery[] _chained;
    
    public ChainedDiscovery(Discovery[] discoveries)
    {
        _chained = discoveries;
    }
    
    public ChainedDiscovery(List<Discovery> discoveries)
    {        
        this(discoveries.toArray(new Discovery[discoveries.size()]));
    }

    public final OpenIdUser discover(Identifier identifier, OpenIdContext context)
            throws Exception
    {
        // cache to local copy
        Discovery[] chained = _chained;
        for(int i=0,len=chained.length; i<len; i++)
        {
            OpenIdUser user = chained[i].discover(identifier, context);
            if(user!=null)
                return user;
        }
        return null;
    }

}
