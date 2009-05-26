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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.mortbay.util.UrlEncoded;

/**
 * Simple http connector using the built-in HttpURLConnection
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public class SimpleHttpConnector implements HttpConnector
{
    
    private static int __bufferSize = Integer.getInteger("shc.buffer_size", 4096).intValue();
    private static int __connectTimeout = Integer.getInteger("shc.connect_timeout", 10000).intValue();
    private static boolean __followRedirects = !"false".equals(System.getProperty("shc.follow_redirects"));
    
    public static void setBufferSize(int bufferSize)
    {
        __bufferSize = bufferSize;
    }
    
    public static int getBufferSize()
    {
        return __bufferSize;
    }
    
    public static void setConnectTimeout(int connectTimeout)
    {
        if(connectTimeout>0)
            __connectTimeout = connectTimeout;
    }
    
    public static int getConnectTimeout()
    {
        return __connectTimeout;
    }
    
    public static void setFollowRedirects(boolean followRedirects)
    {
        __followRedirects = followRedirects;
    }
    
    public static boolean isFollowRedirects()
    {
        return __followRedirects;
    }
    
    public SimpleHttpConnector()
    {
        
    }    
    
    public Response doHEAD(String url, Map<?,?> headers) throws IOException
    {
        return send(HEAD, headers, (HttpURLConnection)new URL(url).openConnection());
    }

    public Response doGET(String url, Map<?,?> headers)
    throws IOException
    {        
        return send(GET, headers, (HttpURLConnection)new URL(url).openConnection());
    }    

    public Response doGET(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException
    {
        StringBuilder buffer = new StringBuilder().append(url);
        char separator = url.lastIndexOf('?')==-1 ? '?' : '&';
        for(Map.Entry<?,?> entry : parameters.entrySet())
        {
            buffer.append(separator).append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
            separator = '&';
        }
        return doGET(buffer.toString(), headers);
    }
    
    public Response doDELETE(String url, Map<?,?> headers)
    throws IOException
    {
        return send(DELETE, headers, (HttpURLConnection)new URL(url).openConnection());
    }
    
    public Response doDELETE(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException
    {
        StringBuilder buffer = new StringBuilder().append(url);
        char separator = url.lastIndexOf('?')==-1 ? '?' : '&';
        for(Map.Entry<?,?> entry : parameters.entrySet())
        {
            buffer.append(separator).append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
            separator = '&';
        }
        return doDELETE(buffer.toString(), headers);
    }
    
    static Response send(String method, Map<?,?> headers, HttpURLConnection connection)
    throws IOException
    {
        connection.setRequestMethod(method);
        if(headers!=null)
        {
            for(Map.Entry<?,?> entry : headers.entrySet())
            {
                connection.setRequestProperty(entry.getKey().toString(), 
                        entry.getValue().toString());
            }
        }
        connection.setConnectTimeout(__connectTimeout);
        connection.setInstanceFollowRedirects(__followRedirects);
        connection.setDoInput(true);        
        connection.connect();
        return new HttpURLConnectionWrapper(connection);
    }

    public Response doPOST(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {       
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<?,?> entry : parameters.entrySet())
        {
            buffer.append('&').append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
        }
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = buffer.substring(1).getBytes();
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = buffer.substring(1).getBytes(charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return doPOST(url, headers, contentType, data);
    }
    
    public Response doPOST(String url, Map<?,?> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(POST, headers, (HttpURLConnection)new URL(url).openConnection(), 
                contentType, data);
    }    

    public Response doPOST(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(POST, headers, (HttpURLConnection)new URL(url).openConnection(), 
                contentType, reader);
    }
    
    public Response doPUT(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {       
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<?,?> entry : parameters.entrySet())
        {
            buffer.append('&').append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
        }
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = buffer.substring(1).getBytes();
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = buffer.substring(1).getBytes(charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return doPUT(url, headers, contentType, data);
    }
    
    public Response doPUT(String url, Map<?,?> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(PUT, headers, (HttpURLConnection)new URL(url).openConnection(), 
                contentType, data);
    }    

    public Response doPUT(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(PUT, headers, (HttpURLConnection)new URL(url).openConnection(), 
                contentType, reader);
    }
    
    static Response sendContent(String method, Map<?,?> headers, HttpURLConnection connection, 
            String contentType, byte[] data) throws IOException
    {
        connection.setRequestMethod(method);
        if(headers!=null)
        {
            for(Map.Entry<?,?> entry : headers.entrySet())
            {
                connection.setRequestProperty(entry.getKey().toString(), 
                        entry.getValue().toString());
            }
        }
        connection.setRequestProperty(CONTENT_TYPE_HEADER, contentType);
        connection.setRequestProperty(CONTENT_LENGTH_HEADER, String.valueOf(data.length));
        connection.setConnectTimeout(__connectTimeout);
        connection.setInstanceFollowRedirects(__followRedirects);
        connection.setDoInput(true);        
        connection.setDoOutput(true); 
        OutputStream out = null;
        try
        {
            out = connection.getOutputStream();
            out.write(data);
        }
        finally
        {
            if(out!=null)
                out.close();
        }        
        return new HttpURLConnectionWrapper(connection);
    }
    
    static Response sendContent(String method, Map<?,?> headers, HttpURLConnection connection, String contentType, 
            InputStreamReader reader) throws IOException
    {
        connection.setRequestMethod(method);
        if(headers!=null)
        {
            for(Map.Entry<?,?> entry : headers.entrySet())
            {
                connection.setRequestProperty(entry.getKey().toString(), 
                        entry.getValue().toString());
            }
        }
        connection.setRequestProperty(CONTENT_TYPE_HEADER, contentType);        
        connection.setConnectTimeout(__connectTimeout);
        connection.setInstanceFollowRedirects(__followRedirects);
        connection.setDoInput(true);        
        connection.setDoOutput(true); 
        OutputStreamWriter out = null;
        try
        {
            out = new OutputStreamWriter(connection.getOutputStream(), reader.getEncoding());            
            char[] buf = new char[__bufferSize];
            for(int len=0; (len=reader.read(buf))!=-1;)
                out.write(buf, 0, len);
        }
        finally
        {
            if(out!=null)
                out.close();
        }        
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
        
        public int getStatus()
        {
            try
            {
                return _connection.getResponseCode();
            }
            catch(IOException e)
            {
                // TODO throw exception?
                e.printStackTrace();
                return 404;
            }
        }
    }

}
