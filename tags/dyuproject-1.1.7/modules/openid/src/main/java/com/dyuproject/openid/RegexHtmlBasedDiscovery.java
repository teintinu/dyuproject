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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Same as HtmlBasedDiscvoery but uses regex to search for the openid2.provider, 
 * openid.server, openid2.local_id and openid.delegate.
 * 
 * @author David Yu
 * @created May 26, 2009
 */

public final class RegexHtmlBasedDiscovery implements Discovery
{
    
    static final Pattern __pattern = Pattern.compile("rel=['\"]openid2?\\.(\\w+)['\"]");
    
    private static final HashMap<String,Integer> __suffixMap = new HashMap<String,Integer>(6);
    
    private static final String __lookup = "href=";
    
    private static int __bufferSize = Integer.getInteger("rhbd.buffer_size", 2048).intValue();
    
    /**
     * Sets the buffer size used by the {@link BufferedReader} for parsing.
     */
    public static void setBufferSize(int bufferSize)
    {
        __bufferSize = bufferSize;
    }
    
    /**
     * Gets the buffer size used by the {@link BufferedReader} for parsing.
     */
    public static int getBufferSize()
    {
        return __bufferSize;
    }
    
    static
    {
        __suffixMap.put("provider", new Integer(1));
        __suffixMap.put("server", new Integer(2));
        __suffixMap.put("local_id", new Integer(3));
        __suffixMap.put("delegate", new Integer(4));
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
                context.getHttpConnector().doGET(identifier.getUrl(), (Map<?,?>)null).getInputStream(), 
                Constants.DEFAULT_ENCODING), __bufferSize));
    }

    static OpenIdUser parse(Identifier identifier, BufferedReader br) throws Exception
    {
        boolean twoDotX = false;
        String line = null, openIdServer = null, openIdDelegate = null;
        boolean parsedServer = false;
        while((line=br.readLine())!=null)
        {
            Matcher matcher = __pattern.matcher(line);
            if(matcher.find())
            {
                Integer type = __suffixMap.get(matcher.group(1).trim());
                if(type!=null)
                {
                    int idx = line.indexOf(__lookup);
                    if(idx!=-1)
                    {
                        char c = line.charAt(idx+__lookup.length());
                        int start = idx+__lookup.length()+1;
                        String value = line.substring(start, line.indexOf(c, start)).trim();
                        if(value.length()!=0)
                        {
                            switch(type.intValue())
                            {
                                case 1:
                                    if(openIdDelegate!=null)
                                    {
                                        return new OpenIdUser(identifier.getId(), identifier.getId(), 
                                                value, openIdDelegate);
                                    }
                                    openIdServer = value;
                                    parsedServer = true;
                                    twoDotX = true;
                                    break;
                                case 2:
                                    // prioritize 2.0 if previously parsed
                                    if(openIdDelegate!=null && !twoDotX)
                                    {
                                        return new OpenIdUser(identifier.getId(), 
                                                identifier.getId(), value, openIdDelegate);
                                    }
                                    else if(parsedServer)
                                        break;
                                    
                                    openIdServer = value;
                                    parsedServer = true;
                                    break;
                                case 3:
                                    if(parsedServer)
                                    {
                                        return new OpenIdUser(identifier.getId(), 
                                                identifier.getId(), openIdServer, value);
                                    }
                                    openIdDelegate = value;
                                    twoDotX = true;
                                    break;
                                case 4:
                                    // prioritize 2.0 if previously parsed
                                    if(parsedServer && !twoDotX)
                                    {
                                        return new OpenIdUser(identifier.getId(), 
                                                identifier.getId(), openIdServer, value);
                                    }
                                    else if(openIdDelegate==null)
                                        openIdDelegate = value;
                                    break;                                    
                            }
                        }                        
                    }
                }
            }
            else if(parsedServer)
            {
                // the <link rel='openid.suffix'> tags are expected to be next to each other.
                if(twoDotX && openIdDelegate==null)
                {
                   return new OpenIdUser(identifier.getId(), YadisDiscovery.IDENTIFIER_SELECT, 
                           openIdServer, null);
                }
                return new OpenIdUser(identifier.getId(), identifier.getId(), openIdServer, 
                        openIdDelegate);
            }
                
        }
        if(!parsedServer)
            return null;
        
        if(twoDotX && openIdDelegate==null)
        {
           return new OpenIdUser(identifier.getId(), YadisDiscovery.IDENTIFIER_SELECT, 
                   openIdServer, null);
        }
        
        return new OpenIdUser(identifier.getId(), identifier.getId(), openIdServer, openIdDelegate);
    }

}
