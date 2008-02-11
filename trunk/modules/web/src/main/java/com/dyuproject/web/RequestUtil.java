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

package com.dyuproject.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.dyuproject.util.Delim;

import org.mortbay.util.MultiMap;
import org.mortbay.util.UrlEncoded;

/**
 * @author David Yu
 */

public abstract class RequestUtil
{
    
    public static final String URL_FORM_ENCODED = "application/x-www-form-urlencoded";

    // Parses only POST application/x-www-form-urlencoded parameters
    /*public static Map<String, String> getParams(HttpServletRequest request)
    {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> map = request.getParameterMap();
        for(Map.Entry<String, String[]> entry : map.entrySet())
        {
            String[] value = (String[])entry.getValue();
            params.put(entry.getKey(), value!=null && value.length>0 ? value[0] : null);
        }
        return params;
    }*/
    
    /*
     * Parses POST AND PUT application/x-www-form-urlencoded parameters
     */
    public static Map<String, String> getParams(HttpServletRequest request) throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();        
        
        String queryString = request.getQueryString();
        if(queryString!=null && queryString.length()>0)
        {
            String[] pairs = Delim.AMPER.split(queryString);
            for(int i=0; i<pairs.length; i++)
            {
                String p = pairs[i];
                int idx = p.indexOf('=');
                params.put(p.substring(0, idx), p.substring(idx+1));
            }
        }
        String contentType = request.getContentType();
        if(contentType!=null && contentType.length()>0)
        {
            int idx = contentType.indexOf(';');
            contentType = idx>0 ? contentType.substring(0, idx).trim() : contentType.trim();
            int method = request.getMethod().hashCode();
            if(contentType.equalsIgnoreCase(URL_FORM_ENCODED) && 
                    (method==HttpMethod.Hash.POST || method==HttpMethod.Hash.PUT))
            {
                int length = request.getContentLength();
                if(length>0)
                {
                    MultiMap map = new MultiMap();
                    UrlEncoded.decodeTo(request.getInputStream(), map, request.getCharacterEncoding(), -1);
                    for(Iterator<Map.Entry<String, String[]>> iter = map.toStringArrayMap().entrySet().iterator(); iter.hasNext();)
                    {
                        Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)iter.next();
                        String[] value = (String[])entry.getValue();
                        params.put(entry.getKey().toString(), value!=null && value.length>0 ? value[0] : null);
                    }
                }                
            }
        }        
        return params;
    }
    
}
