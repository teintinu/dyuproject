//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.mortbay.util.UrlEncoded;

/**
 * Simple http connection using the built in HttpURLConnection
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public class SimpleHttpConnector implements HttpConnector
{
    
    public Response doHEAD(String url, OpenIdContext context) throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(HEAD);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        return new HttpURLConnectionWrapper(connection);
    }

    public Response doGET(String url, OpenIdContext context)
    throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(GET);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        return new HttpURLConnectionWrapper(connection);
    }    

    public Response doGET(String url, Map<String, Object> parameters,
            OpenIdContext context) throws IOException
    {
        StringBuilder buffer = new StringBuilder().append(url);
        char separator = '?';
        for(Map.Entry<String, Object> entry : parameters.entrySet())
        {
            buffer.append(separator).append(entry.getKey()).append('=').append(UrlEncoded.encodeString(entry.getValue().toString()));
            separator = '&';
        }
        return doGET(buffer.toString(), context);
    }

    public Response doPOST(String url, Map<String, Object> parameters,
            OpenIdContext context) throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(POST);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, X_WWW_FORM_URLENCODED);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);
        return new HttpURLConnectionWrapper(connection);
    }
    
    public Response doPOST(String url, String contentType, byte[] data, OpenIdContext context)
    throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(POST);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, contentType);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();        
        return new HttpURLConnectionWrapper(connection);
    }
    
    static class HttpURLConnectionWrapper implements Response
    {
        private HttpURLConnection _connection;
        
        HttpURLConnectionWrapper(HttpURLConnection connection)
        {
            _connection = connection;
        }
        public void close() throws IOException
        {
            _connection.disconnect();            
        }
        public String getHeader(String name)
        {            
            return _connection.getHeaderField(name);
        }
        public InputStream getInputStream() throws IOException
        {            
            return _connection.getInputStream();
        }
    }
    



}
