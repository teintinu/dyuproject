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

import com.dyuproject.util.validate.IPDomainValidator;

/**
 * @author David Yu
 * @created Jan 10, 2009
 */

public class Identifier
{
    
    public static final String CHECKED_PREFIX = "http";
    public static final String ASSIGNED_PREFIX = "http://";
    
    private String _url;
    private String _id;
    private int _reason = 0;
    
    public Identifier(String id)
    {
        _id = id;
    }
    
    public void setUrl(String url)
    {
        if(url!=null)
            _url = url;
    }
    
    public String getUrl()
    {
        return _url;
    }
    
    public void setId(String id)
    {
        if(_url!=null)
            _id = id;
    }
    
    public String getId()
    {
        return _id;
    }
    
    public boolean isUrlResolved()
    {
        return _url!=null;
    }
    
    public int getReason()
    {
        return _reason;
    }
    
    public static Identifier getIdentifier(String id, UrlResolver resolver)
    {
        Identifier identifier = new Identifier(id);
        normalize(identifier);
        if(!identifier.isUrlResolved() && resolver!=null)
            resolver.resolveUrl(identifier);
        
        return identifier;
    }
    
    public static void normalize(Identifier identifier)
    {
        String url = identifier.getId();
        int start = 0, end = 0;
        int len = url.length();
        boolean addPrefix = true;
        if(url.startsWith(CHECKED_PREFIX))
        {
            if(len<11)
                return;
            
            char c = url.charAt(4);
            if(c=='s')
                start = 8;
            else if(c==':')
                start = 7;
            else
                return;
            
            addPrefix = false;
        }        
        
        boolean appendSlash = false;
        int lastSlash = url.indexOf('/', start);        
        if(lastSlash==-1)
        {            
            appendSlash = true;
            end = len;
        }
        else
            end = lastSlash;
        
        int colon = url.indexOf(':', start);
        if(colon!=-1)
        {
            // port validation
            int portsLen = end-colon+1;
            if(portsLen<1 && portsLen>5)
                return;
            
            char[] ch = new char[portsLen];
            url.getChars(colon+1, end, ch, 0);
            if(!isDigit(ch, 0, portsLen))
                return;
            
            end = colon;
        }
        
        // must be at least 4 characters long (e.g. 'a.ph')
        int domainLen = end-start;
        if(domainLen<4)
            return;

        char[] domain = new char[domainLen];
        url.getChars(start, end, domain, 0);        
        if(!IPDomainValidator.isValid(domain))
            return;
        
        if(addPrefix)
            identifier._url = appendSlash ? ASSIGNED_PREFIX + url + '/' : ASSIGNED_PREFIX + url;
        else
            identifier._url = appendSlash ? url + '/' : url;
        
        // normalized
        identifier._id = identifier.getUrl();
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
    
    public interface UrlResolver
    {        
        public void resolveUrl(Identifier identifier);
    }
    
    public static class UrlResolverCollection implements UrlResolver
    {
        private UrlResolver[] _urlResolvers = new UrlResolver[]{};
        
        public UrlResolverCollection addUrlResolver(UrlResolver urlResolver)
        {
            if(urlResolver==null)
                return this;
            
            UrlResolver[] oldUrlResolvers = _urlResolvers;
            UrlResolver[] urlResolvers = new UrlResolver[oldUrlResolvers.length];
            System.arraycopy(oldUrlResolvers, 0, urlResolvers, 0, oldUrlResolvers.length);
            urlResolvers[oldUrlResolvers.length] = urlResolver;
            _urlResolvers = urlResolvers;
            
            return this;
        }
        
        public UrlResolver[] getUrlResolvers()
        {
            return _urlResolvers;
        }
        
        public void resolveUrl(Identifier identifier)
        {            
            for(UrlResolver r : getUrlResolvers())
            {
                r.resolveUrl(identifier);
                if(identifier.isUrlResolved())
                    break;
            }
        }        
    }    

}
