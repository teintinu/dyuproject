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
    
    private String _id;
    private String _url;
    
    public Identifier(String id)
    {
        _id = id;
    }
    
    public void resolve(String url)
    {
        if(url==null)
            return;
        
        _url = url;
    }
    
    public void resolve(String url, String newId)
    {
        if(url==null)
            return;
        
        _url = url;
        _id = newId;
    }
    
    public String getUrl()
    {
        return _url;
    }
    
    public String getId()
    {
        return _id;
    }
    
    public boolean isResolved()
    {
        return _url!=null;
    }
    
    public static Identifier getIdentifier(String id, Resolver resolver, OpenIdContext context)
    {
        Identifier identifier = new Identifier(id);
        normalize(identifier);
        if(!identifier.isResolved() && resolver!=null)
            resolver.resolve(identifier, context);
        
        return identifier;
    }
    
    static void normalize(Identifier identifier)
    {
        String url = identifier.getId();
        int start = 0, end = 0, len = url.length();
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
        identifier._id = identifier._url;
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
    
    public interface Resolver
    {        
        public void resolve(Identifier identifier, OpenIdContext context);
    }
    
    public static class ResolverCollection implements Resolver
    {
        private Resolver[] _resolvers = new Resolver[]{};
        
        public ResolverCollection addResolver(Resolver resolver)
        {
            if(resolver==null || indexOf(resolver)!=-1)
                return this;
            
            Resolver[] oldResolvers = _resolvers;
            Resolver[] resolvers = new Resolver[oldResolvers.length];
            System.arraycopy(oldResolvers, 0, resolvers, 0, oldResolvers.length);
            resolvers[oldResolvers.length] = resolver;
            _resolvers = resolvers;
            
            return this;
        }
        
        public int indexOf(Resolver resolver)
        {
            if(resolver!=null)
            {
                Resolver[] resolvers = _resolvers;
                for(int i=0; i<resolvers.length; i++)
                {
                    if(resolvers[i].equals(resolver))
                        return i;
                }
            }
            return -1;
        }
        
        public Resolver[] getResolvers()
        {
            return _resolvers;
        }
        
        public void resolve(Identifier identifier, OpenIdContext context)
        {            
            for(Resolver r : getResolvers())
            {
                r.resolve(identifier, context);
                if(identifier.isResolved())
                    break;
            }
        }        
    }    

}
