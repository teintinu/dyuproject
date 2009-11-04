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
 * Initially delegates discovery to {@link YadisDiscovery} and then 
 * to {@link HtmlBasedDiscovery} if the former is not successful.
 * 
 * @author David Yu
 * @created Sep 23, 2008
 */

public final class DefaultDiscovery extends ChainedDiscovery
{
    
    public DefaultDiscovery()
    {
        super(new Discovery[]{new YadisDiscovery(), new HtmlBasedDiscovery()});
    }

}
