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

package com.dyuproject.util.http;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.StringUtil;
import org.mortbay.util.UrlEncoded;

/**
 * A parameter map where the values of the parameter names are url encoded on toString()
 * 
 * @author David Yu
 * @created Sep 11, 2008
 */

public final class UrlEncodedParameterMap extends HashMap<String,String>
{
    
    private static final long serialVersionUID = 2009100616L;
    
    private String _url;
    
    public UrlEncodedParameterMap()
    {
        
    }
    
    public UrlEncodedParameterMap(String url)
    {
        _url = url;
    }
    
    public String getUrl()
    {
        return _url;
    }
    
    public UrlEncodedParameterMap setUrl(String url)
    {
        _url = url;
        return this;
    }
    
    public UrlEncodedParameterMap add(String key, String value)
    {
        put(key, value);
        return this;
    }
    
    public String toString()
    {
        StringBuilder buffer = new StringBuilder().append(getUrl());
        char separator = getUrl().lastIndexOf('?')==-1 ? '?' : '&';
        for(Map.Entry<String,String> entry : entrySet())
        {
            buffer.append(separator)
                .append(entry.getKey())
                .append('=')
                .append(UrlEncoded.encodeString(entry.getValue()));            
            separator = '&';
        }       
        return buffer.toString();
    }
    
    public String toStringRFC3986()
    {
        StringBuilder buffer = new StringBuilder().append(getUrl());
        char separator = getUrl().lastIndexOf('?')==-1 ? '?' : '&';
        for(Map.Entry<String,String> entry : entrySet())
        {
            buffer.append(separator)
                .append(entry.getKey())
                .append('=')
                .append(encodeRFC3986(entry.getValue()));            
            separator = '&';
        }       
        return buffer.toString();
    }
    
    public byte[] getUrlFormEncodedBytes(String charset) throws UnsupportedEncodingException
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<String,String> entry : entrySet())
        {
            buffer.append('&')
                .append(entry.getKey())
                .append('=')
                .append(UrlEncoded.encodeString(entry.getValue(), charset));
        }       
        return buffer.substring(1).getBytes(charset);
    }
    
    public byte[] getUrlFormEncodedBytesRFC3986(String charset) throws UnsupportedEncodingException
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<String,String> entry : entrySet())
        {
            buffer.append('&')
                .append(entry.getKey())
                .append('=')
                .append(encodeRFC3986(entry.getValue(), charset));            
        }
        return buffer.substring(1).getBytes(charset);
    }
    
    public void prettyPrint(PrintStream out)
    {
        for(Map.Entry<String,String> entry : entrySet())
        {
            out.print(entry.getKey());
            out.print(" = ");
            out.println(entry.getValue());
        }
    }
    
    /**
     * Gets the url encoded value from the given {@code key}.
     */
    public String getEncoded(String key)
    {
        String value = get(key);
        return value==null ? null : encode(value);
    }
    
    /**
     * Gets the url encoded value from the given {@code key}.
     */
    public String encodedGet(String key)
    {
        return getEncoded(key);
    }
    
    /**
     * Encodes (url encoding) the {@code value} with the default charset UTF-8.
     */
    public static String encode(String value)
    {
        return encode(value, StringUtil.__UTF8);
    }
    
    /**
     * Encodes (url encoding) the {@code value} with the specified {@code charset}.
     */
    public static String encode(String value, String charset)
    {
        return UrlEncoded.encodeString(value, charset);
    }
    
    /**
     * Decodes (url encoding) the {@code value} with the default charset UTF-8.
     */
    public static String decode(String value)
    {
        return UrlEncoded.decodeString(value, 0, value.length(), StringUtil.__UTF8);
    }
    
    /**
     * Decodes (url encoding) the {@code value} with the specified {@code charset}.
     */
    public static String decode(String value, String charset)
    {
        return UrlEncoded.decodeString(value, 0, value.length(), charset);
    }
    
    /**
     * Decodes (url encoding) the {@code value} with the specified {@code charset}, 
     * starting at {@code start} with the length {@code len}.
     */
    public static String decode(String value, int start, int len, String charset)
    {
        return UrlEncoded.decodeString(value, start, len, charset);
    }
    
    /**
     * Encodes the value using RFC 3986 url encoding - which basically 
     * skips {'-', '.', '_', '~'}.
     */
    public static String encodeRFC3986(String value)
    {
        return encodeRFC3986(value, StringUtil.__UTF8);
    }
    
    /* From UrlEncoded snippet customized to skip {'-', '.', '_', '~'} */
    /**
     * Encodes the value with the given {@code charset} using RFC 3986 
     * url encoding - which basically skips {'-', '.', '_', '~'}.
     * 
     * @param value string to encode 
     * @return encoded string.
     */
    public static String encodeRFC3986(String value, String charset)
    {
        byte[] bytes=null;
        try
        {
            bytes=value.getBytes(charset);
        }
        catch(UnsupportedEncodingException e)
        {
            // Log.warn(LogSupport.EXCEPTION,e);
            bytes=value.getBytes();
        }
        
        int len=bytes.length;
        byte[] encoded= new byte[bytes.length*3];
        int n=0;
        boolean noEncode=true;
        
        for (int i=0;i<len;i++)
        {
            byte b = bytes[i];
            
            if (b==' ')
            {
                noEncode=false;
                //encoded[n++]=(byte)'+';
                encoded[n++]=(byte)'%';
                encoded[n++]=(byte)'2';
                encoded[n++]=(byte)'0';
            }
            else if (b>='a' && b<='z' ||
                     b>='A' && b<='Z' ||
                     b>='0' && b<='9')
            {
                encoded[n++]=b;
            }
            else
            {
                switch(b)
                {
                    case '-':
                    case '.':
                    case '_':
                    case '~':
                        encoded[n++] = b;
                        continue;                        
                }
                noEncode=false;
                encoded[n++]=(byte)'%';
                byte nibble= (byte) ((b&0xf0)>>4);
                if (nibble>=10)
                    encoded[n++]=(byte)('A'+nibble-10);
                else
                    encoded[n++]=(byte)('0'+nibble);
                nibble= (byte) (b&0xf);
                if (nibble>=10)
                    encoded[n++]=(byte)('A'+nibble-10);
                else
                    encoded[n++]=(byte)('0'+nibble);
            }
        }

        if (noEncode)
            return value;
        
        try
        {    
            return new String(encoded,0,n,charset);
        }
        catch(UnsupportedEncodingException e)
        {
            // Log.warn(LogSupport.EXCEPTION,e);
            return new String(encoded,0,n);
        }
    }

}
