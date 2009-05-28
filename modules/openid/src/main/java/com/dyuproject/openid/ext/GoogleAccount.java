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

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.UrlEncodedParameterMap;

/**
 * Deprecated.  Use AxSchemaExtension instead.
  *
 * @author David Yu
 * @created Feb 16, 2009
 */

@SuppressWarnings("serial")
@Deprecated
public class GoogleAccount implements Serializable, JSON.Convertible
{
    
    public static final String ATTR_NAME = "google_account";
    
    public static final String NS_KEY = "openid.ns.ext1";
    public static final String NS_VALUE = "http://openid.net/srv/ax/1.0";
    
    public static final String MODE_KEY = "openid.ext1.mode";
    public static final String MODE_VALUE = "fetch_request";
    
    public static final String TYPE_EMAIL_KEY = "openid.ext1.type.email";
    public static final String TYPE_EMAIL_VALUE = "http://axschema.org/contact/email";
    
    public static final String REQUIRED_KEY = "openid.ext1.required";
    public static final String REQUIRED_VALUE = "email";
    
    public static final String EXPECTED_MODE_VALUE = "fetch_response";
    public static final String EXPECTED_EMAIL_KEY = "openid.ext1.value.email";    
    
    static void put(UrlEncodedParameterMap params)
    {
        params.put(NS_KEY, NS_VALUE);
        params.put(MODE_KEY, MODE_VALUE);
        params.put(TYPE_EMAIL_KEY, TYPE_EMAIL_VALUE);
        params.put(REQUIRED_KEY, REQUIRED_VALUE);
    }
    
    public static GoogleAccount get(OpenIdUser user)
    {
        return (GoogleAccount)user.getAttribute(ATTR_NAME);
    }
    
    static void set(OpenIdUser user, GoogleAccount ga)
    {
        user.setAttribute(ATTR_NAME, ga);
    }
    
    static GoogleAccount parse(HttpServletRequest request)
    {
        if(NS_VALUE.equals(request.getParameter(NS_KEY))
                && EXPECTED_MODE_VALUE.equals(request.getParameter(MODE_KEY)))
        {
            String email = request.getParameter(EXPECTED_EMAIL_KEY);
            if(email!=null)
            {
                GoogleAccount ga = new GoogleAccount(email);
                request.setAttribute(ATTR_NAME, ga);
                return ga;
            }
        }        
        return null;
    }
    
    private String _email;
    
    public GoogleAccount()
    {
        
    }
    
    GoogleAccount(String email)
    {
        setEmail(email);
    }
    
    public String getEmail()
    {
        return _email;
    }
    
    void setEmail(String email)
    {
        _email = email;
    }

    public void fromJSON(Map map)
    {
        _email = (String)map.get("e");        
    }

    public void toJSON(Output out)
    {
        out.addClass(getClass());
        out.add("e", _email);
    }

}
