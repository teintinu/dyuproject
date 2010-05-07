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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Util for digesting and encoding bytes/strings.
 * 
 * @author David Yu
 * @created Sep 7, 2008
 */

public final class DigestUtil
{
    
    /**
     * "MD5"
     */
    public static final String MD5 = "MD5";
    /**
     * "SHA-1"
     */
    public static final String SHA1 = "SHA-1";
    /**
     * "SHA-256"
     */
    public static final String SHA256 = "SHA-256";
    
    /**
     * The hex chars as bytes.
     */
    public static final byte[] HEXADECIMAL = 
    {
        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
        0x61, 0x62, 0x63, 0x64, 0x65, 0x66
    };
    
    /**
     * Returns the bytes {@code data} in hex form.
     */
    public static byte[] getHexBytes(byte[] data)
    {

        byte[] out = new byte[data.length*2];        
        for (int i=0,y=0; i<data.length; i++) 
        {            
            int l = (data[i] & 0xf0) >> 4;
            int r = data[i] & 0x0f; 
            out[y++] = HEXADECIMAL[l];
            out[y++] = HEXADECIMAL[r];       
        }
        return out;
    }
    
    /**
     * Returns the bytes {@code data} in hex form as characters.
     */
    public static char[] getHexChars(byte[] data)
    {

        char[] out = new char[data.length*2];        
        for (int i=0,y=0; i<data.length; i++) 
        {            
            int l = (data[i] & 0xf0) >> 4;
            int r = data[i] & 0x0f; 
            out[y++] = (char)HEXADECIMAL[l];
            out[y++] = (char)HEXADECIMAL[r];       
        }
        return out;
    }
    
    static String getHexString(byte[] data)    
    {
        return new String(getHexBytes(data));
    }
    
    /**
     * Returns the bytes {@code data} in hex form as string.
     */
    public static String getHexString(byte[] data, String charset) 
    throws UnsupportedEncodingException
    {
        return new String(getHexBytes(data), charset);
    }
    
    /**
     * Digests the {@code data} with the specified {@code type} and {@code charset}.
     */
    public static String getDigestedValue(String type, String data, String charset)
    throws UnsupportedEncodingException
    {
        return getDigestedValue(type, data.getBytes(charset), charset);
    }
    
    /**
     * Digests the {@code data} with the specified {@code type}.
     */
    public static String getDigestedValue(String type, String data)
    {
        return getDigestedValue(type, data.getBytes());
    }
    
    static String getDigestedValue(String type, byte[] data, String charset)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(type);
            return getHexString(digest.digest(data), charset);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    static String getDigestedValue(String type, byte[] data)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(type);
            return getHexString(digest.digest(data));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Digests the {@code data} with the specified {@code type} and returns the 
     * raw unencoded bytes.
     */
    public static byte[] getPlainDigestedValue(String type, byte[] data)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(type);
            return digest.digest(data);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Digests the {@code data} with MD5 and returns the raw unencoded bytes.
     */
    public static String digestMD5(String data)
    {
        return getDigestedValue(MD5, data.getBytes());
    }
    
    /**
     * Digests the {@code data} with MD5 and the specified {@code charset} and 
     * returns the raw unencoded bytes.
     */
    public static String digestMD5(String data, String charset)
    {
        try
        {
            return getDigestedValue(MD5, data, charset);
        } 
        catch (Exception e)
        {            
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Digests the {@code data} with SHA-1 and returns the raw unencoded bytes.
     */
    public static String digestSHA1(String data)
    {
        return getDigestedValue(SHA1, data.getBytes());
    }
    
    /**
     * Digests the {@code data} with SHA-1 and the specified {@code charset} and 
     * returns the raw unencoded bytes.
     */
    public static String digestSHA1(String data, String charset)
    {
        try
        {
            return getDigestedValue(SHA1, data, charset);
        } 
        catch (Exception e)
        {            
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Digests the {@code data} with SHA-256 and returns the raw unencoded bytes.
     */
    public static String digestSHA256(String data)
    {
        return getDigestedValue(SHA256, data.getBytes());
    }
    
    /**
     * Digests the {@code data} with SHA-256 and the specified {@code charset} and 
     * returns the raw unencoded bytes.
     */
    public static String digestSHA256(String data, String charset)
    {
        try
        {
            return getDigestedValue(SHA256, data, charset);
        } 
        catch (Exception e)
        {            
            e.printStackTrace();
            return null;
        }
    }

}
