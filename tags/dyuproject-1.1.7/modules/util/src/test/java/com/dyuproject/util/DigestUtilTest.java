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

package com.dyuproject.util;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Sep 8, 2008
 */

public class DigestUtilTest extends TestCase
{
    
    public void testHexString()
    {
        String input = "0123456789";
        String expected = "30313233343536373839";
        assertEquals(expected, DigestUtil.getHexString(input.getBytes()));
    }
    
    public void testMD5()
    {
        // from http://en.wikipedia.org/wiki/MD5
        String input = "The quick brown fox jumps over the lazy dog";        
        String expected = "9e107d9d372bb6826bd81d3542a419d6";
        assertEquals(expected, DigestUtil.digestMD5(input));
    }

}
