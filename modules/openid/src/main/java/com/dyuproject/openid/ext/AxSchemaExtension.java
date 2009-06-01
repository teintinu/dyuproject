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
 * AxSchema Extension for http://www.axschema.org/types/
 * See also http://openid.net/specs/openid-attribute-exchange-1_0-05.html
 * 
 * @author David Yu
 * @created May 27, 2009
 */

public class AxSchemaExtension extends AbstractExtension
{
    
    public static final String NAMESPACE = "http://openid.net/srv/ax/1.0";
    public static final String MODE_REQUEST = "fetch_request";
    public static final String MODE_RESPONSE = "fetch_response";
    public static final String ATTR_NAME = "axschema";
    
    public static Map<String,String> get(OpenIdUser user)
    {
        return (Map<String,String>)user.getAttribute(ATTR_NAME);
    }
    
    public static Map<String,String> remove(OpenIdUser user)
    {
        return (Map<String,String>)user.removeAttribute(ATTR_NAME);
    }
    
    private static final Properties __axschemaConfig = new Properties();
    
    static
    {
        try
        {
            __axschemaConfig.load(ClassLoaderUtil.getResource("com/dyuproject/openid/ext/axschema.properties", 
                    AxSchemaExtension.class).openStream());
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private Map<String,Exchange> _exchanges = new HashMap<String,Exchange>();    
    
    public AxSchemaExtension()
    {
        this("ax");
    }
    
    public AxSchemaExtension(String alias)
    {
        setAlias(alias);
        setNamespace(NAMESPACE);
    }
    
    public AxSchemaExtension addExchange(String alias)
    {
        return addExchange(alias, __axschemaConfig.getProperty(alias));
    }
    
    public AxSchemaExtension addExchange(String alias, String namespace)
    {
        if(alias==null)
            throw new IllegalArgumentException("alias must be specified.");
        if(namespace==null)
            throw new IllegalArgumentException("namespace for '" + alias + "' not found.");
        
        return addExchange(new SimpleExchange(alias, namespace));
    }
    
    protected AxSchemaExtension addExchange(Exchange exchange)
    {
        _exchanges.put(exchange.getAlias(), exchange);
        return this;
    }
    
    public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
            UrlEncodedParameterMap params)
    {
        params.put("openid.ns." + getAlias(), getNamespace());
        params.put("openid." + getAlias() + ".mode", MODE_REQUEST);
        
        StringBuilder required = new StringBuilder();
        for(Exchange e : _exchanges.values())
        {
            required.append(e.getAlias()).append(',');
            e.put(user, request, params, getAlias());
        }
        params.put("openid." + getAlias() + ".required", required.substring(0, required.length()-1));
    }

    public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
    {
        String alias = user.getExtension(getNamespace());        
        if(alias!=null && MODE_RESPONSE.equals(request.getParameter("openid." + alias + ".mode")))
        {
            Map<String,String> attributes = new HashMap<String,String>(new Double(_exchanges.size()/.75).intValue()+1);
            user.setAttribute(ATTR_NAME, attributes);
            for(Exchange e : _exchanges.values())
                e.parseAndPut(user, request, attributes, alias);
        }
    }
    
    public static abstract class AbstractExchange implements Exchange
    {
        
        private String _alias;
        
        public String getAlias()
        {
            return _alias;
        }
        
        public void setAlias(String alias)
        {
            _alias = alias;
        }
        
        public void put(OpenIdUser user, HttpServletRequest request,
                UrlEncodedParameterMap params, String alias)
        {
            params.put("openid." + alias + ".type." + getAlias(), getNamespace());
        }
        
        public void parseAndPut(OpenIdUser user, HttpServletRequest request, 
                Map<String,String> attributes, String alias)
        {
            String value = request.getParameter("openid." + alias + ".value." + getAlias());
            if(value!=null)
                attributes.put(getAlias(), value);
        }
        
        public abstract String getNamespace();
        
        
    }
    
    public static class SimpleExchange extends AbstractExchange
    {
        
        private String _namespace;
        
        public SimpleExchange(String alias, String namespace)
        {
            setAlias(alias);
            _namespace = namespace;
        }

        public String getNamespace()
        {
            return _namespace;
        }
        
    }

}
