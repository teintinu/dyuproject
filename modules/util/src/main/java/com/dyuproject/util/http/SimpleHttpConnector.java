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

package com.dyuproject.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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

public final class SimpleHttpConnector implements HttpConnector
{
    
    private static int __bufferSize = Integer.getInteger("shc.buffer_size", 4096).intValue();
    private static int __connectTimeout = Integer.getInteger("shc.connect_timeout", 10000).intValue();
    private static boolean __followRedirects = !"false".equals(System.getProperty("shc.follow_redirects"));
    
    private static final SimpleHttpConnector __default = new SimpleHttpConnector();
    
    public static SimpleHttpConnector getDefault()
    {
        return __default;
    }
    
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
    
    private int _instanceBufferSize = __bufferSize;
    private int _instanceConnectTimeout = __connectTimeout;
    private boolean _instanceFollowRedirects = __followRedirects;
    
    public SimpleHttpConnector()
    {
        
    }
    
    public SimpleHttpConnector(int bufferSize, int connectTimeout, boolean followRedirects)
    {
        _instanceBufferSize = bufferSize;
        _instanceConnectTimeout = connectTimeout;
        _instanceFollowRedirects = followRedirects;
    }
    
    public int getInstanceBufferSize()
    {
        return _instanceBufferSize;
    }
    
    public void setInstanceBufferSize(int bufferSize)
    {
        _instanceBufferSize = bufferSize;
    }
    
    public int getInstanceConnectTimeout()
    {
        return _instanceConnectTimeout;
    }
    
    public void setInstanceConnectTimeout(int instanceConnectTimeout)
    {
        _instanceConnectTimeout = instanceConnectTimeout;
    }
    
    public boolean isInstanceFollowRedirects()
    {
        return _instanceFollowRedirects;
    }
    
    public void setInstanceFollowRedirects(boolean instanceFollowRedirects)
    {
        _instanceFollowRedirects = instanceFollowRedirects;
    }
    
    static HttpURLConnection getConnection(String url, Map<?,?> headers) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        if(headers!=null)
        {
            for(Map.Entry<?,?> entry : headers.entrySet())
            {
                connection.setRequestProperty(entry.getKey().toString(), 
                        entry.getValue().toString());
            }
        }
        return connection;
    }
    
    static HttpURLConnection getConnection(String url, Iterable<Parameter> headers) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        if(headers!=null)
        {
            for(Parameter h : headers)
                connection.setRequestProperty(h.getKey(), h.getValue());
        }
        return connection;
    }
    
    static String appendUrl(String url, Map<?,?> queryParams)
    {
        StringBuilder buffer = new StringBuilder().append(url);
        char separator = url.lastIndexOf('?')==-1 ? '?' : '&';
        for(Map.Entry<?,?> entry : queryParams.entrySet())
        {
            buffer.append(separator).append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
            separator = '&';
        }
        return buffer.toString();
    }
    
    static String appendUrl(String url, Iterable<Parameter> queryParams)
    {
        StringBuilder buffer = new StringBuilder().append(url);
        char separator = url.lastIndexOf('?')==-1 ? '?' : '&';
        for(Parameter p : queryParams)
        {
            buffer.append(separator).append(p.getKey()).append('=').append(
                    UrlEncoded.encodeString(p.getValue()));
            separator = '&';
        }
        return buffer.toString();
    }
    
    static byte[] getEncodedData(Map<?,?> parameters, String charset)
    throws UnsupportedEncodingException
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<?,?> entry : parameters.entrySet())
        {
            buffer.append('&').append(entry.getKey()).append('=').append(
                    UrlEncoded.encodeString(entry.getValue().toString()));
        }
        return buffer.substring(1).getBytes(charset);
    }
    
    static byte[] getEncodedData(Iterable<Parameter> parameters, String charset) 
    throws UnsupportedEncodingException
    {
        StringBuilder buffer = new StringBuilder();
        for(Parameter p : parameters)
        {
            buffer.append('&').append(p.getKey()).append('=').append(
                    UrlEncoded.encodeString(p.getValue().toString()));
        }
        return buffer.substring(1).getBytes(charset);
    }
    
    public Response doHEAD(String url, Map<?,?> headers) throws IOException
    {
        return send(HEAD, getConnection(url, headers));
    }
    
    public Response doHEAD(String url, Iterable<Parameter> headers) throws IOException
    {
        return send(HEAD, getConnection(url, headers));
    }

    public Response doGET(String url, Map<?,?> headers)
    throws IOException
    {        
        return send(GET, getConnection(url, headers));
    }
    
    public Response doGET(String url, Iterable<Parameter> headers)
    throws IOException
    {        
        return send(GET, getConnection(url, headers));
    }

    public Response doGET(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException
    {
        return send(GET, getConnection(appendUrl(url, parameters), headers));
    }
    
    public Response doGET(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException
    {
        return send(GET, getConnection(appendUrl(url, parameters), headers));
    }
    
    public Response doGET(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters) 
    throws IOException
    {
        return send(GET, getConnection(appendUrl(url, parameters), headers));
    }
    
    public Response doDELETE(String url, Map<?,?> headers)
    throws IOException
    {
        return send(DELETE, getConnection(url, headers));
    }
    
    public Response doDELETE(String url, Iterable<Parameter> headers)
    throws IOException
    {
        return send(DELETE, getConnection(url, headers));
    }
    
    public Response doDELETE(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException
    {
        return send(DELETE, getConnection(appendUrl(url, parameters), headers));
    }
    
    public Response doDELETE(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException
    {
        return send(DELETE, getConnection(appendUrl(url, parameters), headers));
    }
    
    public Response doDELETE(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters) 
    throws IOException
    {
        return send(DELETE, getConnection(appendUrl(url, parameters), headers));
    }
    
    Response send(String method, HttpURLConnection connection)
    throws IOException
    {
        connection.setRequestMethod(method);
        connection.setConnectTimeout(_instanceConnectTimeout);
        connection.setInstanceFollowRedirects(_instanceFollowRedirects);
        connection.setDoInput(true);        
        connection.connect();
        return new HttpURLConnectionWrapper(connection);
    }

    public Response doPOST(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(POST, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }
    
    public Response doPOST(String url, Map<?,?> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(POST, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }
    
    public Response doPOST(String url, Iterable<Parameter> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(POST, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }
    
    public Response doPOST(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(POST, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }
    
    public Response doPOST(String url, Map<?,?> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }    
    
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(POST, getConnection(url, headers), contentType, data);
    }

    public Response doPOST(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(POST, getConnection(url, headers), contentType, reader);
    }
    
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(POST, getConnection(url, headers), contentType, reader);
    }
    
    // PUT
    
    public Response doPUT(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(PUT, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }
    
    public Response doPUT(String url, Map<?,?> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(PUT, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }
    
    public Response doPUT(String url, Iterable<Parameter> headers, Map<?,?> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(PUT, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }
    
    public Response doPUT(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException
    {
        if(parameters==null)
            return send(PUT, getConnection(url, headers));
        
        byte[] data = null;
        String contentType = null;
        if(charset==null)
        {
            data = getEncodedData(parameters, DEFAULT_ENCODING);
            contentType = X_WWW_FORM_URLENCODED;
        }
        else
        {
            data = getEncodedData(parameters, charset);
            contentType = X_WWW_FORM_URLENCODED + "; charset=" + charset;
        }
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }
    
    public Response doPUT(String url, Map<?,?> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }    
    
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, byte[] data)
    throws IOException
    {
        return sendContent(PUT, getConnection(url, headers), contentType, data);
    }

    public Response doPUT(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(PUT, getConnection(url, headers), contentType, reader);
    }
    
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException
    {
        return sendContent(PUT, getConnection(url, headers), contentType, reader);
    }
    
    Response sendContent(String method, HttpURLConnection connection, 
            String contentType, byte[] data) throws IOException
    {
        connection.setRequestMethod(method);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, contentType);
        connection.setRequestProperty(CONTENT_LENGTH_HEADER, String.valueOf(data.length));
        connection.setConnectTimeout(_instanceConnectTimeout);
        connection.setInstanceFollowRedirects(_instanceFollowRedirects);
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
    
    Response sendContent(String method, HttpURLConnection connection, String contentType, 
            InputStreamReader reader) throws IOException
    {
        connection.setRequestMethod(method);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, contentType);        
        connection.setConnectTimeout(_instanceConnectTimeout);
        connection.setInstanceFollowRedirects(_instanceFollowRedirects);
        connection.setDoInput(true);        
        connection.setDoOutput(true); 
        OutputStreamWriter out = null;
        try
        {
            out = new OutputStreamWriter(connection.getOutputStream(), reader.getEncoding());            
            char[] buf = new char[_instanceBufferSize];
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
