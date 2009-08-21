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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Jun 6, 2009
 */

public class TransportTest extends TestCase
{
    
    public void testParseResponse() throws Exception
    {
        byte[] b = "oauth_token=hh5s93j4hdidpola&oauth_token_secret=hdhd0244k9j7ao03".getBytes(
                Constants.ENCODING);
        ByteArrayInputStream in = new ByteArrayInputStream(b);
        Token token = new Token();
        Transport.parse(in, token);
        assertEquals("hh5s93j4hdidpola", token.getKey());
        assertEquals("hdhd0244k9j7ao03", token.getSecret());
    }

}
