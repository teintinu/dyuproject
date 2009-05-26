//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.openid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Same as HtmlBasedDiscvoery but uses regex to search for the openid.server 
 * and openid.delegate
 * 
 * @author David Yu
 * @created May 26, 2009
 */

public class RegexHtmlBasedDiscovery implements Discovery
{
    
    static final Pattern __pattern = Pattern.compile("rel=['\"]openid2?\\.(\\w+)['\"]");
    
    static final HashMap<String,Boolean> __suffixMap = new HashMap<String,Boolean>();
    
    private static final String __lookup = "href=";
    
    private static int __bufferSize = Integer.getInteger("rhbd.buffer_size", 2048).intValue();
    
    public static void setBufferSize(int bufferSize)
    {
        __bufferSize = bufferSize;
    }
    
    public static int getBufferSize()
    {
        return __bufferSize;
    }
    
    static
    {
        __suffixMap.put("server", Boolean.TRUE);
        __suffixMap.put("provider", Boolean.TRUE);
        __suffixMap.put("delegate", Boolean.FALSE);
        __suffixMap.put("local_id", Boolean.FALSE);
    }
    
    public OpenIdUser discover(Identifier identifier, OpenIdContext context)
    throws Exception
    {
        return tryDiscover(identifier, context);
    }    

    static OpenIdUser tryDiscover(Identifier identifier, OpenIdContext context)
    throws Exception
    {
        return parse(identifier, new BufferedReader(new InputStreamReader(
                context.getHttpConnector().doGET(identifier.getUrl(), null).getInputStream(), 
                Constants.DEFAULT_ENCODING), __bufferSize));
    }

    static OpenIdUser parse(Identifier identifier, BufferedReader br) throws Exception
    {
        String line = null;
        String openIdServer = null, openIdDelegate = null;
        while((line=br.readLine())!=null)
        {
            Matcher matcher = __pattern.matcher(line);
            if(matcher.find())
            {
                System.err.println(line);
                Boolean isServer = __suffixMap.get(matcher.group(1).trim());
                if(isServer!=null)
                {
                    int idx = line.indexOf(__lookup);
                    if(idx!=-1)
                    {
                        char c = line.charAt(idx+__lookup.length());
                        String value = line.substring(idx+__lookup.length()+1, 
                                line.indexOf(c, idx+__lookup.length()+1)).trim();
                        if(isServer.booleanValue())
                        {
                            openIdServer = value;
                            if(openIdDelegate!=null)
                            {
                                new OpenIdUser(identifier.getId(), identifier.getId(), 
                                        openIdServer, openIdDelegate);
                            }
                        }
                        else
                        {
                            openIdDelegate = value;
                            if(openIdServer!=null)
                            {
                                new OpenIdUser(identifier.getId(), identifier.getId(), 
                                        openIdServer, openIdDelegate);
                            }
                        }                        
                    }
                }
            }
        }
        return openIdServer==null ? null : new OpenIdUser(identifier.getId(), identifier.getId(), 
                openIdServer, openIdDelegate);
    }

}
