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

/**
 * @author David Yu
 * @created Sep 20, 2008
 */

public class HtmlBasedDiscoveryTest extends TestCase
{
    
    public void testDiscovery1() throws Exception
    {
        doDiscovery(newContext(new HtmlBasedDiscovery()), "http://davidyu.myopenid.com");
    }
    
    public void $testDiscovery2() throws Exception
    {
        doDiscovery(newContext(new HtmlBasedDiscovery()), "http://davidyuftw.blogspot.com");
    }
    
    public void testDiscovery3() throws Exception
    {
        doDiscovery(newContext(new HtmlBasedDiscovery()), "http://ct15.wordpress.com");
    }
    
    public void testRegexDiscovery1() throws Exception
    {
        doDiscovery(newContext(new RegexHtmlBasedDiscovery()), "http://davidyu.myopenid.com");
    }
    
    public void testRegexDiscovery2() throws Exception
    {
        doDiscovery(newContext(new RegexHtmlBasedDiscovery()), "http://davidyuftw.blogspot.com");
    }
    
    public void testRegexDiscovery3() throws Exception
    {
        doDiscovery(newContext(new RegexHtmlBasedDiscovery()), "http://ct15.wordpress.com");
    }
    
    static OpenIdContext newContext(Discovery discovery)
    {
        OpenIdContext context = new OpenIdContext();
        context.setHttpConnector(new SimpleHttpConnector());
        context.setDiscovery(discovery);
        return context;
    }
    
    static void doDiscovery(OpenIdContext context, String url) throws Exception
    {
        Identifier identifier = Identifier.getIdentifier(url, null, context);
        OpenIdUser user = context.getDiscovery().discover(identifier, context);
        assertTrue(user!=null && user.getOpenIdServer()!=null);
        System.err.println(user.getOpenIdServer());
    }

}
