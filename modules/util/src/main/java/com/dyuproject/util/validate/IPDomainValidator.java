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

package com.dyuproject.util.validate;

/**
 * A string input validator for IPs and domains.
 * 
 * @author David Yu
 * @created Jan 10, 2009
 */

public final class IPDomainValidator
{
    
    public static final int INVALID = 0;
    public static final int PLAIN = 1;
    public static final int ALPHANUMERIC = 2;
    public static final int HYPHENATED = 3;
    public static final int MIXED = 4;
    public static final int IP = 5;
    
    /**
     * Returns 0 if the char array {@code part} is invalid.
     */
    public static int tokenValidate(char[] part, int start, int len)
    {         
        int digitCount = 0, lastHyphen = -1;
        for(int i=0; i<len; i++)
        {
            char c = part[start++];            
            if(Character.isDigit(c))
                digitCount++;
            else if(!Character.isLetter(c))
            {
                if(c=='-')
                {
                    if(i<len-1)
                    {
                        if(lastHyphen+1==i || lastHyphen-1==i)
                        {
                            // invalid placement of hyphens (succeeding, prefix)                            
                            return INVALID;
                        }
                        lastHyphen = i;
                    }
                    else
                    {
                        // invalid placement of hyphens (suffix)  
                        return INVALID;
                    }
                }
                else
                    return INVALID;
            }
        }
        if(digitCount==len)
            return IP;
        
        if(digitCount==0)
            return lastHyphen==-1 ? PLAIN : HYPHENATED;
        
        return lastHyphen==-1 ? ALPHANUMERIC : MIXED;
    }
    
    /**
     * Returns the index of the char {@code c} from the char array {@code ch}.
     */
    public static int indexOf(char[] ch, char c, int start)
    {
        for(int i=start; i<ch.length; i++)
        {
            if(ch[i]==c)
                return i;
        }
        return -1;
    }
    
    /**
     * Returns the index (starting from the last) of the char {@code c} from the 
     * array of characters {@code ch}.
     */
    public static int lastIndexOf(char[] ch, char c, int start)
    {
        for(int i=start; i-->0;)
        {
            if(ch[i]==c)
                return i;
        }
        return -1;
    }
    
    /**
     * Returns 0 if the string {@code domain} is invalid.
     */
    public static int validate(String domain, int start, int end)
    {
        char[] ch = new char[end-start];
        domain.getChars(start, end, ch, 0);
        return validate(ch);
    }
    
    /**
     * Returns 0 if the string {@code domain} is invalid.
     */
    public static int validate(String domain)
    {
        return validate(domain.toCharArray());
    }
    
    /**
     * Returns 0 if the char array {@code domain} is invalid.
     */
    public static int validate(char[] domain)
    {
        return validate(domain, 0, domain.length);
    }
    
    /**
     * Returns 0 if the char array {@code domain} is invalid.
     */
    public static int validate(char[] domain, int start, int end)
    {        
        boolean mixed = false, hyphenated = false, alphanumeric = false;
        int tokens = 0, digitTokens = 0, extLen = 0;
        for(int i=end; i>0;)
        {
            int idx = lastIndexOf(domain, '.', i-1);
            int l = i-idx-1;
            int check = tokenValidate(domain, idx+1, l);
            i = idx;      
            switch(check)
            {
                case INVALID:
                    return INVALID;
                case PLAIN:
                    if(tokens==0)
                    {
                        if(l==1)
                            return INVALID;
                        
                        extLen = l;
                    }
                    break;
                case ALPHANUMERIC:                    
                    if(tokens==0)
                    {
                        // invalid domain extension (alphanumeric)
                        return INVALID;
                    }
                    alphanumeric = true;                    
                    break;
                case HYPHENATED:
                    if(tokens==0)
                    {
                        // invalid domain extension (alphanumeric)
                        return INVALID;
                    }
                    hyphenated = true;                    
                    break;
                case MIXED:
                    if(tokens==0)
                    {
                        // invalid domain extension (alphanumeric)
                        return INVALID;
                    }
                    mixed = true;                    
                    break;
                case IP:
                    if(tokens==0 && l>3)
                    {
                        // invalid domain extension (max of 3)
                        return INVALID;                    
                    }
                    alphanumeric = true;
                    digitTokens++;                    
                    break;            
            }
            // if exceeds maximum chars for a domain
            if(l>63)
                return INVALID;
            tokens++;
        }
        if(tokens==digitTokens)
            return tokens>4 ? INVALID : IP;
            
        if(extLen==0 || tokens==1)
            return INVALID;
        
        if(mixed)
            return MIXED;
        
        if(hyphenated)
            return HYPHENATED;
        
        return alphanumeric ? ALPHANUMERIC : PLAIN;
    }
    
    /**
     * Checks whether the string {@code domain} is valid.
     */
    public static boolean isValid(String domain)
    {
        return validate(domain)!=INVALID;
    }
    
    /**
     * Checks whether the char array {@code domain} is valid.
     */
    public static boolean isValid(char[] domain)
    {
        return validate(domain)!=INVALID;
    }
    
    /**
     * Checks whether the string {@code domain} is valid.
     */
    public static boolean isValid(String domain, int start, int end)
    {
        return validate(domain, start, end)!=INVALID;
    }
    
    /**
     * Checks whether the char array {@code domain} is valid.
     */
    public static boolean isValid(char[] domain, int start, int end)
    {
        return validate(domain, start, end)!=INVALID;
    }

}
