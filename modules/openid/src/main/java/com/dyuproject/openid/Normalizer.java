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

/**
 * @author David Yu
 * @created Jan 10, 2009
 */

public abstract class Normalizer
{
    
    public static final String CHECKED_PREFIX = "http";
    public static final String ASSIGNED_PREFIX = "http://";
    
    public static String normalize(String url)
    {
        return normalize(url, false);
    }
    
    public static String normalize(String url, boolean allowIP)
    {
        int start = 0;
        int len = url.length();
        boolean addPrefix = true;
        if(url.startsWith(CHECKED_PREFIX))
        {
            if(len<11)
                return null;
            char c = url.charAt(4);
            if(c=='s')
                start = 8;
            else if(c==':')
                start = 7;
            else
                return null;
            
            addPrefix = false;
        }
        
        boolean appendSlash = false;
        int lastSlash = url.indexOf('/', start);        
        if(lastSlash==-1)
        {
            lastSlash = len++;
            appendSlash = true;          
        }
        
        if(lastSlash-start<4 || url.lastIndexOf('.', lastSlash)==-1)
            return null;
        
        // domain extension validation
        int dot = url.lastIndexOf('.', lastSlash);        
        if(dot==-1)
            return null;
        
        int domainExtLen = lastSlash - dot -1;
        if(domainExtLen<1 || domainExtLen>6)
            return null;                
        
        char[] ch = new char[domainExtLen];
        url.getChars(dot+1, lastSlash, ch, 0);
        int digitCount = 0;
        for(int i=0; i<domainExtLen; i++)
        {
            char c = ch[i];
            if(Character.isDigit(c))
                digitCount++;
            else if(!Character.isLetter(c) && c!='-')
                return null;
        }        
        
        if(digitCount==domainExtLen && domainExtLen<4)
            return allowIP ? normalizeIP(url, start, addPrefix, appendSlash) : null;
        // invalid domain extension
        if(digitCount>0)
            return null;
        
        /*if(lastSlash+1==len)
        {
            // root uri
            char[] ch = new char[domainExtLen];
            url.getChars(dot+1, lastSlash, ch, 0);
            int count = getDigitCount(ch, 0, domainExtLen);
            if(count!=0)
            {                
                if(allowIP && count==domainExtLen && domainExtLen<4)
                    return normalizeIP(url, start, addPrefix, appendSlash);
                
                //invalid
                return null;
            }
        }*/    

        
        if(domainExtLen<2)
            return null;
        
        if(addPrefix)
            return appendSlash ? ASSIGNED_PREFIX + url + '/' : ASSIGNED_PREFIX + url;
        
        return appendSlash ? url + '/' : url;
    }
    
    static int getDigitCount(char[] ch, int start, int len)
    {
        int count = 0;
        for(int i=start; i<len; i++)
        {
            if(Character.isDigit(ch[i]))
                count++;
        }
        return count;
    }
    
    static boolean isDigit(char[] ch, int start, int len)
    {
        for(int i=start; i<len; i++)
        {
            if(!Character.isDigit(ch[i]))
                return false;
        }
        return true;
    }
    
    static String normalizeIP(String ip, int start, boolean addPrefix, boolean appendSlash)
    {        
        int end = 0, tokens = 0;
        char[] ch = new char[3];
        boolean loop = true;
        while(loop)
        {            
            int idx = ip.indexOf('.', start);
            if(idx==-1)
            {
                //start = end + 1;
                //end = appendSlash ? ip.length() : ip.length()-1;                
                //loop = false;
                
                // last token already checked prior to this call
                if(++tokens>4)
                    return null;                
                break;
            }
            else
                end = idx;
            
            int len = end-start;
            if(len>3)
                return null;
            
            ip.getChars(start, end, ch, 0);
            if(!isDigit(ch, 0, len))
                return null;
                            
            if(++tokens>4)
                return null;
            
            start = end+1;
        }
        if(tokens!=4)
            return null;
        
        if(addPrefix)
            return appendSlash ? ASSIGNED_PREFIX + ip + '/' : ASSIGNED_PREFIX + ip;
        
        return appendSlash ? ip + '/' : ip;
    }
    
    
    public static void main(String[] args)
    {

        String[] ss = {
                "a.com",
                "b.cd",
                "b.c",
                "aaaa.c",
                "ab.museum",
                "ab.museum2",
                "http",
                "ab",
                "https",
                "https:///....../",
                "https://da.com1",
                "https://a.cs",
                "http://a.cd",
                "192.168.111.111",
                "192.168.111.2/",
                "192.168.111.3/b/c"
        };
        for(String s : ss)
            System.err.println(normalize(s, true)/* + " " + s*/);
    }

}
