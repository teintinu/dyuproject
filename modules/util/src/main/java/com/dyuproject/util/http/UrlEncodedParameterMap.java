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

import java.util.HashMap;
import java.util.Map;

import org.mortbay.util.UrlEncoded;

/**
 * A parameter map where the values of the parameter names are url encoded on toString()
 * 
 * @author David Yu
 * @created Sep 11, 2008
 */
@SuppressWarnings("serial")
public class UrlEncodedParameterMap extends HashMap<String,String>
{
    
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
            buffer.append(separator).append(entry.getKey()).append('=').append(UrlEncoded.encodeString(entry.getValue()));            
            separator = '&';
        }       
        return buffer.toString();
    }
    
    public String getEncoded(String key)
    {
        String value = get(key);
        return value==null ? null : encode(value);
    }
    
    public String encodedGet(String key)
    {
        return getEncoded(key);
    }
    
    public static String encode(String value)
    {
        return UrlEncoded.encodeString(value);
    }
    
    public static String encode(String value, String charset)
    {
        return UrlEncoded.encodeString(value, charset);
    }
    
    public static String decode(String value, String charset)
    {
        return UrlEncoded.decodeString(value, 0, value.length(), charset);
    }
    
    public static String decode(String value, int start, int len, String charset)
    {
        return UrlEncoded.decodeString(value, start, len, charset);
    }

}
