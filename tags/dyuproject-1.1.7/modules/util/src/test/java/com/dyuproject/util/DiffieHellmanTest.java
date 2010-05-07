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

import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Sep 8, 2008
 */

public class DiffieHellmanTest extends TestCase
{
    
    public void testSharedSecret()
    {
        //from http://en.wikipedia.org/wiki/Diffie-Hellman
        BigInteger modulus = BigInteger.valueOf(23);
        
        BigInteger a_private = BigInteger.valueOf(6);
        BigInteger a_public = DiffieHellman.BASE_5.generatePublicKey(a_private, modulus);        
        
        BigInteger b_private = BigInteger.valueOf(15);
        BigInteger b_public = DiffieHellman.BASE_5.generatePublicKey(b_private, modulus);
        
        BigInteger a_shared_secret = DiffieHellman.getSharedSecretKey(a_private, modulus, b_public);
        
        BigInteger b_shared_secret = DiffieHellman.getSharedSecretKey(b_private, modulus, a_public);
        
        assertEquals(a_shared_secret, b_shared_secret);
        assertEquals(2, a_shared_secret.intValue());
    }

}
