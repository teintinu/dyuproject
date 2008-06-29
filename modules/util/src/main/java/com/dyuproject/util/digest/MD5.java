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

package com.dyuproject.util.digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author David Yu
 * @created Jun 23, 2008
 */

public class MD5
{
    
    public static String digest(String toDigest, String charsetName)
    {
        try
        {
            return digest(toDigest.getBytes(charsetName));
        } 
        catch (UnsupportedEncodingException e)
        {            
            e.printStackTrace();
            return null;
        }
    }
    
    public static String digest(String toDigest)
    {
        return digest(toDigest.getBytes());
    }
    
    public static String digest(byte[] toDigest)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance(MD5.class.getSimpleName());
            byte[] data = md5.digest(toDigest);
            StringBuilder buffer = new StringBuilder();
            for (int i=0; i<data.length; i++) 
            {
                buffer.append(Integer.toHexString((data[i] & 0xf0) >>> 4));
                buffer.append(Integer.toHexString(data[i] & 0x0f));
            }
            return buffer.toString();
        } 
        catch (NoSuchAlgorithmException e)
        {            
            e.printStackTrace();
            return null;
        }
    }

}
