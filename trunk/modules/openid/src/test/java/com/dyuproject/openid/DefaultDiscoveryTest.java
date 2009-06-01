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

import junit.framework.TestCase;

import com.dyuproject.util.http.SimpleHttpConnector;

/**
 * @author David Yu
 * @created Sep 24, 2008
 */

public class DefaultDiscoveryTest extends TestCase
{
    
    public void testDiscovery() throws Exception
    {        
        OpenIdContext context = new OpenIdContext();
        context.setHttpConnector(new SimpleHttpConnector());
        context.setDiscovery(new DefaultDiscovery());
        Identifier identifier = Identifier.getIdentifier("http://davidyuftw.blogspot.com", null, context);
        OpenIdUser user = context.getDiscovery().discover(identifier, context);
        assertTrue(user!=null && user.getOpenIdServer()!=null);
        System.err.println(user.getOpenIdServer());
        System.err.println(user.getClaimedId());
    }

}
