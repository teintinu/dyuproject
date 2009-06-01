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
import java.util.Iterator;
import java.util.Map;

/**
 * HttpConnector - reads/writes bytes from/to http endpoints.
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public interface HttpConnector
{
    
    /*public enum Method
    {
        HEAD,
        GET,
        POST,
        PUT,
        DELETE;
    }*/
    
    public static final String HEAD = "HEAD";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String DEFAULT_ENCODING = System.getProperty("httpconnector.default_encoding", "UTF-8");
    
    // HEAD
    
    public Response doHEAD(String url, Map<?,?> headers) throws IOException;
    
    public Response doHEAD(String url, Iterable<Parameter> headers) throws IOException;
    
    // GET
    
    public Response doGET(String url, Map<?,?> headers) throws IOException;
    
    public Response doGET(String url, Iterable<Parameter> headers) throws IOException;
    
    public Response doGET(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException;
    
    public Response doGET(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException;
    
    public Response doGET(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters) 
    throws IOException;
    
    // DELETE
    
    public Response doDELETE(String url, Map<?,?> headers) throws IOException;
    
    public Response doDELETE(String url, Iterable<Parameter> headers) throws IOException;
    
    public Response doDELETE(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException;
    
    public Response doDELETE(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException;
    
    public Response doDELETE(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters) 
    throws IOException;
    
    // POST
    
    public Response doPOST(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    public Response doPOST(String url, Map<?,?> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException;
    
    public Response doPOST(String url, Iterable<Parameter> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    public Response doPOST(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException;
    
    public Response doPOST(String url, Map<?,?> headers, String contentType, byte[] data) 
    throws IOException;
    
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, byte[] data) 
    throws IOException;
    
    public Response doPOST(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    // PUT
    
    public Response doPUT(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    public Response doPUT(String url, Map<?,?> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException;
    
    public Response doPUT(String url, Iterable<Parameter> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    public Response doPUT(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, String charset) 
    throws IOException;
    
    public Response doPUT(String url, Map<?,?> headers, String contentType, byte[] data) 
    throws IOException;
    
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, byte[] data) 
    throws IOException;
    
    public Response doPUT(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    
    public interface Response
    {
        
        public InputStream getInputStream() throws IOException;
        
        public String getHeader(String name);
        
        public int getStatus();
        
        public void close() throws IOException;

    }
    
    public static class Parameter implements Iterable<Parameter> // Lazy
    {
        private String _key, _value;
        
        public Parameter(String key, String value)
        {
            _key = key;
            _value = value;
        }
        
        public String getKey()
        {
            return _key;
        }
        
        public String getValue()
        {
            return _value;
        }

        public Iterator<Parameter> iterator()
        {
            return new Itr();
        }
        
        class Itr implements Iterator<Parameter>
        {
            private boolean _hasNext = true;

            public boolean hasNext()
            {
                return _hasNext;
            }

            public Parameter next()
            {
                _hasNext = false;
                return Parameter.this;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();                
            }            
        }      
    }

}
