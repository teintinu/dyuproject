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
 * @created Sep 12, 2008
 */

public class CryptographyTest extends TestCase
{
    
    public void testDES() throws Exception
    {
        Cryptography crypto = Cryptography.createDES("12345678");
        String text = "The quick brown fox jumps over the lazy dog.";
        String encrypted = crypto.encrypt(text);
        String decrypted = crypto.decrypt(encrypted);
        assertEquals(decrypted, text);
    }
    
    public void testDESede() throws Exception
    {
        Cryptography crypto = Cryptography.createDESede("123456789012345678901234");
        String text = "The quick brown fox jumps over the lazy dog.";
        String encrypted = crypto.encrypt(text);
        String decrypted = crypto.decrypt(encrypted);
        assertEquals(decrypted, text);
    }

}
