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

import java.util.ArrayList;
import java.util.List;

/**
 * Chained Discovery
 * 
 * @author David Yu
 * @created May 26, 2009
 */

public class ChainedDiscovery implements Discovery
{
    
    private List<Discovery> _chained = new ArrayList<Discovery>();
    
    public ChainedDiscovery add(Discovery discovery)
    {
        _chained.add(discovery);
        return this;
    }
    
    public ChainedDiscovery set(List<Discovery> chained)
    {
        _chained.addAll(chained);
        return this;
    }

    public OpenIdUser discover(Identifier identifier, OpenIdContext context)
            throws Exception
    {
        OpenIdUser user = null;
        for(Discovery d : _chained)
        {
            if((user=d.discover(identifier, context))!=null)
                break;
        }
        return user;
    }

}
