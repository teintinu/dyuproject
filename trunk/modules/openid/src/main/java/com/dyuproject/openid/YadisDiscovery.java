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

import com.dyuproject.openid.HttpConnector.Response;
import com.dyuproject.util.xml.LazyHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * Yadis Discovery
 * 
 * @author David Yu
 * @created Sep 25, 2008
 */

public class YadisDiscovery implements Discovery
{
    
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

    public OpenIdUser discover(String claimedId, OpenIdContext context)
            throws Exception
    {        
        return discover(claimedId, context.getHttpConnector().doHEAD(claimedId, context), context);
    }
    
    static OpenIdUser discover(String claimedId, Response response, OpenIdContext context) 
    throws Exception
    {
        String location = response.getHeader(X_XRDS_LOCATION);
        if(location==null)
        {            
            try{response.close();}catch(IOException e){}
            return null;
        }
        response = context.getHttpConnector().doGET(location, context);
        InputStreamReader reader = null;
        OpenIdUser user = null;
        try
        {            
            reader = new InputStreamReader(response.getInputStream(), Constants.DEFAULT_ENCODING);            
            user = discover(claimedId, reader);
        }
        catch(Exception e)
        {            
            user = null;
        }
        
        if(reader!=null)
        {
            try{reader.close();}catch(IOException ioe){}
        }
        try{response.close();}catch(IOException ioe){}
        
        return user;
    }
    
    static OpenIdUser discover(String claimedId, InputStreamReader reader) throws Exception
    {
        XmlHandler handler = new XmlHandler();
        XMLParser.parse(reader, handler, true);
        return handler._openIdServer==null ? null : new OpenIdUser(claimedId, handler._openIdServer, 
                handler._openIdDelegate);
    }    
    
    static class XmlHandler implements LazyHandler
    {
        
        private int _stack = 0;
        private boolean _service = false;
        private String _lastName;
        private String _openIdServer;
        private String _openIdDelegate;
        
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
            return 1<_stack;
        }

        public void attribute(String name, String value)
        {            
            
        }

        public void characters(char[] data, int start, int length)
        {
            if(_stack==4)
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
                    if(str.endsWith(SIGNON) || str.endsWith(SERVER))
                    {
                        _service = true;
                        return;
                    }
                }
            }
        }        
    }

}
