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

package com.dyuproject.openid.ext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.util.ClassLoaderUtil;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Simple Registration Extension
 * See http://openid.net/specs/openid-simple-registration-extension-1_1-01.html
 * 
 * @author David Yu
 * @created May 27, 2009
 */

public final class SRegExtension extends AbstractExtension
{
    
    /**
     * "openid.ns.sreg"
     */
    public static final String NS_KEY = "openid.ns.sreg";
    /**
     * "openid.sreg.optional"
     */
    public static final String OPTIONAL_KEY = "openid.sreg.optional";
    /**
     * "http://openid.net/extensions/sreg/1.1"
     */
    public static final String NAMESPACE = "http://openid.net/extensions/sreg/1.1";
    /**
     * The attribute name set on the {@link OpenIdUser}. ("sreg")
     */
    public static final String ATTR_NAME = "sreg";
    
    /**
     * Gets the sreg value set on the {@link OpenIdUser user}.
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> get(OpenIdUser user)
    {
        return (Map<String,String>)user.getAttribute(ATTR_NAME);
    }
    
    /**
     * Removes the sreg value set on the {@link OpenIdUser user}.
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> remove(OpenIdUser user)
    {
        return (Map<String,String>)user.removeAttribute(ATTR_NAME);
    }
    
    private static final Properties __sregConfig = new Properties();
    private static final String __optional;
    
    static
    {
        try
        {
            __sregConfig.load(ClassLoaderUtil.getResource("com/dyuproject/openid/ext/sreg.properties", 
                    AxSchemaExtension.class).openStream());
            __optional = (String)__sregConfig.remove("sreg.optional");
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private final Map<String,Exchange> _exchanges = new HashMap<String,Exchange>();
    private String _checkKey;
    
    public SRegExtension()
    {
        super("sreg", NAMESPACE);
    }
    
    /**
     * Adds an exchange (field) to be included in the extension parameters.
     */
    public SRegExtension addExchange(String alias)
    {
        if(alias==null)
            throw new IllegalArgumentException("alias must be specified.");
        if(!__sregConfig.containsKey(alias))
            throw new IllegalArgumentException("'" + alias + "' not found. The ff are supported:\n" + __optional);
        
        return addExchange(new SimpleExchange(alias));
    }
    
    protected SRegExtension addExchange(Exchange exchange)
    {
        // 1 single test key (email recommended)
        if(_exchanges.size()==0)
            _checkKey = "openid.sreg." + exchange.getAlias();
        _exchanges.put(exchange.getAlias(), exchange);
        return this;
    }

    public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
            UrlEncodedParameterMap params)
    {
        params.put(NS_KEY, NAMESPACE);
        StringBuilder optional = new StringBuilder();
        for(String alias : _exchanges.keySet())
            optional.append(alias).append(',');
        
        params.put(OPTIONAL_KEY, optional.substring(0, optional.length()-1));
    }

    public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
    {
        String alias = user.getExtension(getNamespace());
        if(alias!=null || NAMESPACE.equals(request.getParameter(NS_KEY)) 
                || request.getParameter(_checkKey)!=null)
        {
            Map<String,String> attributes = new HashMap<String,String>(new Double(_exchanges.size()/.75).intValue()+1);
            user.setAttribute(ATTR_NAME, attributes);
            for(Exchange e : _exchanges.values())
                e.parseAndPut(user, request, attributes, getAlias());
        }
    }
    
    /**
     * SimpleExchange - parses the value denoted by "openid.sreg.$alias = value".
     */
    public static final class SimpleExchange implements Exchange
    {
        
        private final String _alias, _key;
        
        public SimpleExchange(String alias)
        {
            _alias = alias;
            _key = "openid.sreg." + _alias;
        }

        public String getAlias()
        {
            return _alias;
        }

        public void put(OpenIdUser user, HttpServletRequest request,
                UrlEncodedParameterMap params, String extensionAlias)
        {
            
        }        

        public void parseAndPut(OpenIdUser user, HttpServletRequest request,
                Map<String, String> attributes, String extensionAlias)
        {
            String value = request.getParameter(_key);
            if(value!=null)
                attributes.put(_alias, value);
        }
        
    }

}
