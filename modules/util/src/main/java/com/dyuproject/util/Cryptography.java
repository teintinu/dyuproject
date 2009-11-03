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

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryption and Decryption utility
 * 
 * @author David Yu
 * @created Sep 12, 2008
 */

public final class Cryptography
{
    
    /**
     * The algorithm used for 8-character secret keys.
     */
    public static final String DES = "DES";
    /**
     * The algorithm used for 24-character secret keys.
     */
    public static final String DESede = "DESede";
    
    /**
     * Returns a string padded with the given character {@code pad} if the length is not 
     * equal to 8 or 24.
     */
    public static String pad(String secretKey, char pad)
    {
        int len = secretKey.length();
        if(len>24)
            throw new IllegalArgumentException(secretKey + " is limited to a length of 24.");
        if(len==8 || len==24)
            return secretKey;
        
        if(len<8)
        {
            StringBuilder buffer = new StringBuilder(8).append(secretKey);
            while(buffer.length()<8)
                buffer.append(pad);
            return buffer.toString();
        }
        
        StringBuilder buffer = new StringBuilder(24).append(secretKey);
        while(buffer.length()<24)
            buffer.append(pad);
        return buffer.toString();
    }

    /**
     * Creates a new instance with the given {@code secretKey } and the character 
     * {@code pad} for padding if the key is not long enough.
     * 
     * @throws IllegalArgumentException if the {@code secretKey} is more than 24 characters long.
     */
    public static Cryptography create(String secretKey, char pad) throws Exception
    {
        int len = secretKey.length();
        if(len>24)
            throw new IllegalArgumentException(secretKey + " is limited to a length of 24.");
        if(len==24)
            return createDESede(secretKey);        
        if(len==8)
            return createDES(secretKey);
        
        if(len<8)
        {
            StringBuilder buffer = new StringBuilder(8).append(secretKey);
            while(buffer.length()<8)
                buffer.append(pad);
            return createDES(buffer.toString());
        }
        
        StringBuilder buffer = new StringBuilder(24).append(secretKey);
        while(buffer.length()<24)
            buffer.append(pad);
        return createDESede(buffer.toString());
    }
    
    /**
     * Creates a new instance with the given {@code secretKey}.
     * 
     * @throws IllegalArgumentException if the {@code secretKey} is not 8 characters long.
     */
    public static Cryptography createDES(String secretKey) throws Exception
    {
        byte[] secret = secretKey.getBytes(B64Code.__ISO_8859_1);
        if(secret.length!=8)
            throw new IllegalArgumentException("DES secretKey must be 8 characters long.");
        
        return create(secret, DES);
    }
    
    /**
     * Creates a new instance with the given {@code secretKey}.
     * 
     * @throws IllegalArgumentException if the {@code secretKey} is not 24 characters long.
     */
    public static Cryptography createDESede(String secretKey) throws Exception
    {
        byte[] secret = secretKey.getBytes(B64Code.__ISO_8859_1);
        if(secret.length!=24)
            throw new IllegalArgumentException("DESede secretKey must be 24 characters long.");
        
        return create(secretKey, B64Code.__ISO_8859_1, DESede);
    }
    
    /**
     * Creates a new instance with the given {@code secretKey} and {@code algorithm}.
     */
    public static Cryptography create(String secretKey, String algorithm) throws Exception
    {
        return create(secretKey, B64Code.__ISO_8859_1, algorithm);
    }
    
    /**
     * Creates a new instance with the given {@code secretKey}, {@code charset} 
     * and {@code algorithm}.
     */
    public static Cryptography create(String secretKey, String charset, 
            String algorithm) throws Exception
    {        
        return create(secretKey.getBytes(charset), algorithm);
    }
    
    /**
     * Creates a new instance with the given bytes {@code secretKey} and {@code algorithm}.
     */
    public static Cryptography create(byte[] secretKey, String algorithm) throws Exception
    {        
        return new Cryptography(new SecretKeySpec(secretKey, algorithm));
    }
    
    /**
     * Creates a new instance with a random secret that is 8 characters long.
     */
    public static Cryptography generateDESRandom() throws Exception
    {
        return generateRandom(DES);
    }
    
    /**
     * Creates a new instance with a random secret that is 24 characters long.
     */
    public static Cryptography generateDESedeRandom() throws Exception
    {
        return generateRandom(DESede);
    }
    
    /**
     * Creates a new instance with a random secret with the given {@code algorithm}.
     */
    public static Cryptography generateRandom(String algorithm) throws Exception
    {
        return new Cryptography(KeyGenerator.getInstance(algorithm).generateKey());
    }
    
    private final Cipher _encrypt, _decrypt;
    private final Key _key;
    
    private Cryptography(Key key) throws Exception
    {
        _key = key;        
        _encrypt = Cipher.getInstance(_key.getAlgorithm());
        _decrypt = Cipher.getInstance(_key.getAlgorithm());
        _encrypt.init(Cipher.ENCRYPT_MODE, _key);
        _decrypt.init(Cipher.DECRYPT_MODE, _key);        
    }
    
    /**
     * Encrypts the given bytes {@code input}.
     */
    public byte[] encrypt(byte[] input) throws Exception
    {
        return _encrypt.doFinal(input);
    }
    
    /**
     * Encrypts the given string {@code input} with ISO-8859-1 encoding.
     */
    public String encrypt(String input) throws Exception
    {
        return encrypt(input, B64Code.__ISO_8859_1);
    }
    
    /**
     * Encrypts the given string {@code input} encoded with the given {@code charset}.
     */
    public String encrypt(String input, String charset) throws Exception
    {
        return new String(encrypt(input.getBytes(charset)), charset);
    }
    
    /**
     * Encrypts the given string {@code input} encoded with ISO-8859-1; 
     * The encrypted bytes will then be b64 encoded. 
     */
    public String encryptEncode(String input) throws Exception
    {
        return encryptEncode(input, B64Code.__ISO_8859_1);
    }
    
    /**
     * Encrypts the given string {@code input} encoded with the given {@code charset}; 
     * The encrypted bytes will then be b64 encoded. 
     */
    public String encryptEncode(String input, String charset) throws Exception
    {
        return new String(B64Code.encode(encrypt(input.getBytes(charset))));
    }
    
    /**
     * Decrypts the given bytes {@code input}.
     */
    public byte[] decrypt(byte[] input) throws Exception
    {
        return _decrypt.doFinal(input);
    }
    
    /**
     * Decrypts the given string {@code input} with ISO-8859-1 encoding.
     */
    public String decrypt(String input) throws Exception
    {
        return decrypt(input, B64Code.__ISO_8859_1);
    }
    
    /**
     * Decrypts the given string {@code input} with ISO-8859-1 encoding.
     */
    public String decrypt(String input, String charset) throws Exception
    {
        return new String(decrypt(input.getBytes(charset)), charset);
    }
    
    /**
     * Decodes the given string {@code input} with base 64 using ISO-8859-1 and 
     * decrypts the decoded string.
     */
    public String decryptDecode(String input) throws Exception
    {
        return decryptDecode(input, B64Code.__ISO_8859_1);
    }
    
    /**
     * Decodes the given string {@code input} with base 64 using the given {@code charset} and 
     * decrypts the decoded string.
     */
    public String decryptDecode(String input, String charset) throws Exception
    {
        return new String(decrypt(B64Code.decode(input.toCharArray())), charset);
    }
    
    /**
     * Gets the secret key.
     */
    public Key getKey()
    {
        return _key;
    }

}
