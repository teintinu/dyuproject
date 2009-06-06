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

package com.dyuproject.oauth;

import java.io.IOException;
import java.util.Map;

import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.util.http.HttpConnector.Response;

/**
 * Sends the request parameters using HTTP GET and appended with queryString
 * 
 * @author David Yu
 * @created Jun 1, 2009
 */

public class HttpGetTransport extends Transport
{

    static final HttpGetTransport __default = new HttpGetTransport();
    
    public static HttpGetTransport getDefault()
    {
        return __default;
    }
    
    public String getName()
    {
        return HttpConnector.GET;
    }
    
    public String getMethod()
    {
        return HttpConnector.GET;
    }
    
    protected void handleOAuthParameter(String key, String value, StringBuilder buffer)
    {
        handleRequestParameter(key, value, buffer);
    }
    
    public Response send(UrlEncodedParameterMap params, Endpoint ep, Token token,
            TokenExchange exchange, NonceAndTimestamp nts, Signature signature, 
            HttpConnector connector) throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        
        putDefaults(params, ep, token, exchange, nts, signature, buffer, buffer);
        
        buffer.setCharAt(0, '?');
        buffer.insert(0, params.getUrl());
        
        return parse(connector.doGET(buffer.toString(), (Map<?,?>)null), token);
    }       

}
