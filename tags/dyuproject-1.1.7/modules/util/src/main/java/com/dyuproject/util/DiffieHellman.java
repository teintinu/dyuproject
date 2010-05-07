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

/**
 * 
 * Diffie-Hellman key exchange is a cryptographic protocol that allows two parties 
 * that have no prior knowledge of each other to jointly establish a shared secret 
 * key over an insecure communications channel.
 * 
 * @author David Yu
 * @created Sep 7, 2008
 */

public final class DiffieHellman
{
    
    public static final DiffieHellman BASE_2 = new DiffieHellman(BigInteger.valueOf(2));
    public static final DiffieHellman BASE_5 = new DiffieHellman(BigInteger.valueOf(5));
    
    private static final long __loadTime = System.currentTimeMillis();
    
    private final BigInteger _base;
    
    public DiffieHellman(BigInteger base)
    {
        _base = base;
    }
    
    /**
     * Generates a random private Key (element 0) and a random public key (element 1) 
     * from the given {@code modulus}.
     * 
     * @param modulus
     * @return BigInteger array.  Element 0 is privateKey.  Element 1 is publicKey.
     */
    public BigInteger[] generateRandomKeys(BigInteger modulus)
    {
        BigInteger privateKey = BigInteger.valueOf(System.currentTimeMillis() + __loadTime);
        return new BigInteger[]{privateKey, generatePublicKey(privateKey, modulus)};
    }
    
    /**
     * Generates a public key from the given {@code privateKey} and {@code modulus}.
     */
    public BigInteger generatePublicKey(BigInteger privateKey, BigInteger modulus)
    {
        return _base.modPow(privateKey, modulus);
    }
    
    /**
     * Gets/computes the shared secret key from the given {@code privateKey}, 
     * {@code modulus} and {@code responseKey} - which is a public key.
     */
    public static BigInteger getSharedSecretKey(BigInteger privateKey, BigInteger modulus, 
            BigInteger responseKey)
    {
        return responseKey.modPow(privateKey, modulus);
    }

}
