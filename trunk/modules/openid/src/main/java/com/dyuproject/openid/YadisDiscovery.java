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
import java.io.InputStreamReader;
import java.util.Map;

import com.dyuproject.util.http.HttpConnector;
import com.dyuproject.util.http.HttpConnector.Response;
import com.dyuproject.util.xml.LazyHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * YadisDiscovery - parses xrds documents either specified by the header {@link #X_XRDS_LOCATION} 
 * or the identifier itself if the content type is {@link #XRDS_CONTENT_TYPE}. 
 * 
 * @author David Yu
 * @created Sep 25, 2008
 */

public final class YadisDiscovery implements Discovery
{
    public static final String IDENTIFIER_SELECT = "http://specs.openid.net/auth/2.0/identifier_select";
    static final String XRDS_CONTENT_TYPE = "application/xrds+xml";
    static final String X_XRDS_LOCATION = "X-XRDS-Location";
    static final String NS_PREFIX = "http://specs.openid.net/auth/2.0/";
    static final String NS_SERVER = "http://specs.openid.net/auth/2.0/server";
    static final String NS_SIGNON = "http://specs.openid.net/auth/2.0/signon";
    static final String SERVER = "server";
    static final String SIGNON = "signon";
    static final String XRDS = "XRDS";    
    static final String XRD = "XRD";    
    static final String SERVICE = "Service";
    static final String TYPE = "Type";
    static final String URI = "URI";
    static final String LOCAL_ID = "LocalID";

    public OpenIdUser discover(Identifier identifier, OpenIdContext context) throws Exception
    {        
        return tryDiscover(identifier, context);
    }
    
    static OpenIdUser tryDiscover(Identifier identifier, OpenIdContext context) 
    throws Exception
    {
        if(identifier.isUrlContentTypeXrds())
        {
            // configured by the resolver
            return discoverXRDS(identifier, identifier.getUrl(), context);
        }
        
        Response response = context.getHttpConnector().doHEAD(identifier.getUrl(), (Map<?,?>)null);
        String location = response.getHeader(X_XRDS_LOCATION);
        if(location==null)
        {
            String contentType = response.getHeader(HttpConnector.CONTENT_TYPE_HEADER);            
            if(contentType==null || !contentType.startsWith(XRDS_CONTENT_TYPE))
            {
                try{response.close();}catch(IOException e){}
                return null;
            }
            
            location = identifier.getUrl();
        }
        try{response.close();}catch(IOException e){}
        
        return discoverXRDS(identifier, location, context);
    }
    
    static OpenIdUser discoverXRDS(Identifier identifier, String location, OpenIdContext context) 
    throws Exception
    {
        Response response = context.getHttpConnector().doGET(location, (Map<?,?>)null);
        InputStreamReader reader = null;
        OpenIdUser user = null;
        try
        {            
            reader = new InputStreamReader(response.getInputStream(), 
                    Constants.DEFAULT_ENCODING);            
            user = parse(identifier, reader);
        }
        catch(Exception e)
        {            
            user = null;
        }
        finally
        {
            if(reader!=null)
            {
                try{reader.close();}catch(IOException ioe){}
            }
            try{response.close();}catch(IOException ioe){} 
        }
       
        return user;
    }
    
    static OpenIdUser parse(Identifier identifier, InputStreamReader reader) throws Exception
    {        
        XmlHandler handler = new XmlHandler();
        XMLParser.parse(reader, handler, true);
        if(handler._openIdServer==null)
            return null;
        if(handler._signon && handler._openIdDelegate!=null)
        {
            return new OpenIdUser(identifier.getId(), identifier.getId(), handler._openIdServer, 
                    handler._openIdDelegate);   
        }
        return new OpenIdUser(identifier.getId(), YadisDiscovery.IDENTIFIER_SELECT, 
                handler._openIdServer, null);
    }    
    
    /**
     * Lazily parses the xrds document.
     */
    static final class XmlHandler implements LazyHandler
    {
        
        private int _stack = 0;
        private boolean _service = false, xrd = false;
        private String _lastName;
        private String _openIdServer;
        private String _openIdDelegate;
        private boolean _signon = false;
        
        XmlHandler()
        {
            
        }

        public boolean rootElement(String name, String namespace)
        {            
            _stack = 1;
            return XRDS.equals(name);
        }

        public boolean startElement(String name, String namespace)
        {
            if(_stack==1)
                xrd = XRD.equals(name);

            _stack++;
            _lastName = name;
            return true;
        }        

        public boolean endElement()
        {
            if(--_stack==2)
            {
                if(_openIdServer!=null)
                    return false;
                _service = false;
            }
            return !xrd || 1<_stack;
        }

        public void attribute(String name, String value)
        {            
            
        }

        public void characters(char[] data, int start, int length)
        {
            if(xrd && _stack==4)
            {                
                if(_service)
                {
                    if(URI.equals(_lastName))                    
                        _openIdServer = new String(data, start, length).trim();                    
                    else if(LOCAL_ID.equals(_lastName))                    
                        _openIdDelegate = new String(data, start, length).trim();                    
                    return;
                }
                
                if(!TYPE.equals(_lastName))
                    return;
                
                String str = new String(data, start, length).trim();
                if(str.startsWith(NS_PREFIX))
                {
                    _service = true;
                    _signon = str.endsWith(SIGNON);
                }
            }
        }        
    }

}
