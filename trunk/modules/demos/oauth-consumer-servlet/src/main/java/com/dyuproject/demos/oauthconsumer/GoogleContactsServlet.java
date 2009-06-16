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

package com.dyuproject.demos.oauthconsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.oauth.Constants;
import com.dyuproject.oauth.Consumer;
import com.dyuproject.oauth.ConsumerContext;
import com.dyuproject.oauth.Endpoint;
import com.dyuproject.oauth.HttpAuthTransport;
import com.dyuproject.oauth.Token;
import com.dyuproject.oauth.TokenExchange;
import com.dyuproject.oauth.Transport;
import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.UrlEncodedParameterMap;
import com.dyuproject.util.http.HttpConnector.Parameter;
import com.dyuproject.util.http.HttpConnector.Response;

/**
 * @author David Yu
 * @created Jun 15, 2009
 */

@SuppressWarnings("serial")
public class GoogleContactsServlet extends HttpServlet
{

    static final String CONTACTS_SERVICE_URL = "http://www.google.com/m8/feeds/contacts/default/full";
    
    Consumer _consumer = Consumer.getInstance();
    Endpoint _googleEndpoint = _consumer.getEndpoint("www.google.com");
    
    public void init()
    {

    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        Token token = _consumer.getToken(_googleEndpoint.getConsumerKey(), request);
        switch(token.getState())
        {
            case Token.UNITIALIZED:
                UrlEncodedParameterMap params = new UrlEncodedParameterMap()
                    .add("scope", "http://www.google.com/m8/feeds/")
                    .add(Constants.OAUTH_CALLBACK, request.getRequestURL().toString());
                    
                Response r = _consumer.fetchToken(_googleEndpoint, params, TokenExchange.REQUEST_TOKEN, 
                        token);
                if(r.getStatus()==200 && token.getState()==Token.UNAUTHORIZED)
                {
                    // unauthorized request token
                    _consumer.saveToken(token, request, response);
                    StringBuilder urlBuffer = Transport.buildAuthUrl(_googleEndpoint.getAuthorizationUrl(), 
                            token, null);
                    Transport.appendToUrl("hd", "default", urlBuffer);
                    response.sendRedirect(urlBuffer.toString());
                }
                break;
                
            case Token.UNAUTHORIZED:
                if(token.authorize(request.getParameter(Constants.OAUTH_TOKEN), 
                        request.getParameter(Constants.OAUTH_VERIFIER)))
                {
                    if(fetchAccessToken(token, request, response))
                        queryGoogleContacts(token, request, response);
                    else
                        _consumer.saveToken(token, request, response);
                }
                break;
                
            case Token.AUTHORIZED:
                if(fetchAccessToken(token, request, response))
                    queryGoogleContacts(token, request, response);
                break;
                
            case Token.ACCESS_TOKEN:
                queryGoogleContacts(token, request, response);
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }
    
    public boolean fetchAccessToken(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        // authorized request token
        UrlEncodedParameterMap params = new UrlEncodedParameterMap();
        
        Response r = _consumer.fetchToken(_googleEndpoint, params, TokenExchange.ACCESS_TOKEN, token);
        if(r.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN)
        {
            // access token
            _consumer.saveToken(token, request, response);
            return true;
        }
        return false;
    }
    
    protected void queryGoogleContacts(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        Response r = serviceGET(CONTACTS_SERVICE_URL, _consumer.getConsumerContext(), _googleEndpoint, 
                token, request, response);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), "UTF-8"));
        response.setContentType("text/xml");
        PrintWriter pw = response.getWriter();
        for(String line=null; (line=br.readLine())!=null;)
            pw.append(line);
    }
    
    public static Response serviceGET(String serviceUrl, ConsumerContext context, Endpoint ep, 
            Token token, HttpServletRequest request, HttpServletResponse response) 
            throws IOException
    {
        HttpConnector connector = context.getHttpConnector();
        UrlEncodedParameterMap params = new UrlEncodedParameterMap(serviceUrl);
        context.getNonceAndTimestamp().put(params, token.getCk());
        Parameter authorization = new Parameter("Authorization", 
                HttpAuthTransport.getAuthHeaderValue(params, ep, token, 
                context.getNonceAndTimestamp(),  ep.getSignature()));
        return connector.doGET(params.getUrl(), authorization);
        
    }
    
}
