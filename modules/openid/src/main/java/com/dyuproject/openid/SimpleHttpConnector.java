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

    public InputStream doGET(String url, OpenIdContext context)
    throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(GET);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        return new HttpInputStream(connection);
    }    

    public InputStream doGET(String url, Map<String, Object> parameters,
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

    public InputStream doPOST(String url, Map<String, Object> parameters,
            OpenIdContext context) throws IOException
    {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)target.openConnection();
        connection.setRequestMethod(POST);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, X_WWW_FORM_URLENCODED);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);
        return new HttpInputStream(connection);
    }
    
    public InputStream doPOST(String url, String contentType, byte[] data, OpenIdContext context)
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
        return new HttpInputStream(connection);
    }
    
    // Disconnects the http url connection when inputstream is closed.
    public static class HttpInputStream extends InputStream
    {
        
        private HttpURLConnection _connection;
        private InputStream _is;
        
        private HttpInputStream(HttpURLConnection connection) throws IOException
        {
            _connection = connection;            
            _is = _connection.getInputStream();
        }

        @Override
        public int read() throws IOException
        {            
            return _is.read();
        }
        
        public int read(byte[] b) throws IOException
        {
            return _is.read(b);
        }
        
        public int read(byte[] b, int off, int len) throws IOException
        {
            return _is.read(b, off, len);
        }
        
        public long skip(long n) throws IOException
        {
            return _is.skip(n);
        }
        
        public int available() throws IOException
        {
            return _is.available();
        }
        
        public void close() throws IOException
        {
            try
            {
                _is.close();
            }
            finally
            {
                _connection.disconnect();
            }            
        }
        
        public void mark(int readlimit)
        {
            _is.mark(readlimit);
        }
        
        public void reset() throws IOException
        {
            _is.reset();
        }
        
        public boolean markSupported()
        {
            return _is.markSupported();
        }
        
        public int hashCode()
        {
            return _connection.getContentLength();
        }
        
        public String toString()
        {
            return _connection.getContentType() + ';' + _connection.getContentEncoding();
        }
        
        
    }


}
