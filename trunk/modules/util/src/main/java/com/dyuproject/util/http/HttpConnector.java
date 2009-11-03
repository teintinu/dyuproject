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
    public static final String DEFAULT_ENCODING = 
        System.getProperty("httpconnector.default_encoding", "UTF-8");
    
    // HEAD
    
    /**
     * Makes a HEAD request with the given http {@code headers}.
     */
    public Response doHEAD(String url, Map<?,?> headers) throws IOException;
    
    /**
     * Makes a HEAD request with the given http {@code headers}.
     */
    public Response doHEAD(String url, Iterable<Parameter> headers) throws IOException;
    
    // GET
    
    /**
     * Makes a GET request with the given http {@code headers}.
     */
    public Response doGET(String url, Map<?,?> headers) throws IOException;
    
    /**
     * Makes a GET request with the given http {@code headers}.
     */
    public Response doGET(String url, Iterable<Parameter> headers) throws IOException;
    
    /**
     * Makes a GET request with the given http {@code headers}, {@code parameters}.
     */
    public Response doGET(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException;
    
    /**
     * Makes a GET request with the given http {@code headers}, {@code parameters}.
     */
    public Response doGET(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException;
    
    /**
     * Makes a GET request with the given http {@code headers}, {@code parameters}.
     */
    public Response doGET(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters) 
    throws IOException;
    
    // DELETE
    
    /**
     * Makes a DELETE request with the given http {@code headers}.
     */
    public Response doDELETE(String url, Map<?,?> headers) throws IOException;
    
    /**
     * Makes a DELETE request with the given http {@code headers}.
     */
    public Response doDELETE(String url, Iterable<Parameter> headers) throws IOException;
    
    /**
     * Makes a DELETE request with the given http {@code headers}, {@code parameters}.
     */
    public Response doDELETE(String url, Map<?,?> headers, Map<?,?> parameters) 
    throws IOException;
    
    /**
     * Makes a DELETE request with the given http {@code headers}, {@code parameters}.
     */
    public Response doDELETE(String url, Iterable<Parameter> headers, Map<?,?> parameters) 
    throws IOException;
    
    /**
     * Makes a DELETE request with the given http {@code headers}, {@code parameters}.
     */
    public Response doDELETE(String url, Iterable<Parameter> headers, 
            Iterable<Parameter> parameters) throws IOException;
    
    // POST
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPOST(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPOST(String url, Map<?,?> headers, Iterable<Parameter> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPOST(String url, Iterable<Parameter> headers, Map<?,?> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPOST(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters} and 
     * the content {@code data}.
     */
    public Response doPOST(String url, Map<?,?> headers, String contentType, byte[] data) 
    throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters}, 
     * {@code contentType} and the content {@code data}.
     */
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, 
            byte[] data) throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters}, 
     * {@code contentType} and the content {@code reader} as InputStreamReader.
     */
    public Response doPOST(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    /**
     * Makes a POST request with the given http {@code headers}, {@code parameters}, 
     * {@code contentType} and the content {@code reader} as InputStreamReader.
     */
    public Response doPOST(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    // PUT
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPUT(String url, Map<?,?> headers, Map<?,?> parameters, String charset) 
    throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPUT(String url, Map<?,?> headers, Iterable<Parameter> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPUT(String url, Iterable<Parameter> headers, Map<?,?> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and the
     * {@code charset} - which will be appended in the Content-Type header.
     */
    public Response doPUT(String url, Iterable<Parameter> headers, Iterable<Parameter> parameters, 
            String charset) throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and 
     * the content {@code data}.
     */
    public Response doPUT(String url, Map<?,?> headers, String contentType, byte[] data) 
    throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters} and 
     * the content {@code data}.
     */
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, byte[] data) 
    throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters}, 
     * {@code contentType} and the content {@code reader} as InputStreamReader.
     */
    public Response doPUT(String url, Map<?,?> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    /**
     * Makes a PUT request with the given http {@code headers}, {@code parameters}, 
     * {@code contentType} and the content {@code reader} as InputStreamReader.
     */
    public Response doPUT(String url, Iterable<Parameter> headers, String contentType, 
            InputStreamReader reader) throws IOException;
    
    
    /**
     * The http response which can be used to obtain the http status, headers and  
     * the content via input stream. 
     *
     */
    public interface Response
    {
        
        /**
         * Gets the response content via InputStream.
         */
        public InputStream getInputStream() throws IOException;
        
        /**
         * Gets the response header value from the given header {@code name}.
         */
        public String getHeader(String name);
        
        /**
         * Gets the response http status.
         */
        public int getStatus();
        
        /**
         * Closes/destroys this object after reading data.
         */
        public void close() throws IOException;

    }
    
    /**
     * A key/value pair
     */
    public static class Parameter implements Iterable<Parameter> // Lazy
    {
        private String _key, _value;
        
        public Parameter(String key, String value)
        {
            _key = key;
            _value = value;
        }
        
        /**
         * Gets the key.
         */
        public String getKey()
        {
            return _key;
        }
        
        /**
         * Gets the value.
         */
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
