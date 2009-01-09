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

import java.io.InputStreamReader;

import com.dyuproject.openid.HttpConnector.Response;
import com.dyuproject.util.xml.LazyHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * Discovery mechanism to retrieve the openid.server and openid.delegate from link tags.
 * 
 * @author David Yu
 * @created Sep 15, 2008
 */

public class HtmlBasedDiscovery implements Discovery
{    
    
    static final String HTML = "html";
    static final String HEAD = "head";
    static final String LINK = "link";
    static final String REL = "rel";
    static final String HREF = "href";    
    
    public OpenIdUser discover(String claimedId, String url, OpenIdContext context)
    throws Exception
    {
        return discover(claimedId, context.getHttpConnector().doGET(url, null));
    }
    
    static OpenIdUser discover(String claimedId, Response response) throws Exception
    {
        OpenIdUser user = null;
        InputStreamReader reader = null;
        try
        {
            reader = new InputStreamReader(response.getInputStream(), Constants.DEFAULT_ENCODING);
            user = discover(claimedId, reader);
        }
        finally
        {
            if(reader!=null)
                reader.close();
            response.close();
        }
        return user;
    }
    
    static OpenIdUser discover(String claimedId, InputStreamReader reader) throws Exception
    {
        XmlHandler handler = new XmlHandler();
        XMLParser.parse(reader, handler, false);
        return handler._openIdServer==null ? null : new OpenIdUser(claimedId, 
                handler._openIdServer, handler._openIdDelegate);        
    }
    
    static class XmlHandler implements LazyHandler
    {
        
        private String _openIdServer;
        private String _openIdDelegate;
        private String _lastHref;
        private String _lastRel;
        private int _stack = 0;
        private boolean _headFound = false;
        private boolean _link = false;        
        private boolean _searching = true;
        
        XmlHandler()
        {
            
        }

        public boolean rootElement(String name, String namespace)
        {            
            /*_headFound = false;
            _link = false;
            _searching = true;
            _openIdServer = null;
            _openIdDelegate = null;
            _lastHref = null;
            _lastRel = null;*/
            _stack = 1;
            return HTML.equalsIgnoreCase(name);            
        }

        public boolean startElement(String name, String namespace)
        {            
            _stack++;
            if(_headFound)
            {                
                _link = LINK.equalsIgnoreCase(name);
                return true;
            }
            _headFound = HEAD.equalsIgnoreCase(name);
            return _headFound;
        }

        public boolean endElement()
        {
            _lastRel = null;
            _lastHref = null;            
            return _searching && 1<--_stack;
        }
        
        public void attribute(String name, String value)
        {
            if(_link)
            {
                if(_lastRel==null && REL.equalsIgnoreCase(name))
                {
                    _lastRel = value;
                    if(_lastHref!=null)
                    {
                        if(OPENID_SERVER.equals(value))
                        {
                            _openIdServer = _lastHref;
                            _searching = _openIdDelegate==null;                                
                        }
                        else if(OPENID_DELEGATE.equals(value))
                        {
                            _openIdDelegate = _lastHref;
                            _searching = _openIdServer==null;
                        }
                        _lastRel = null;
                        _lastHref = null;
                    }
                }
                else if(_lastHref==null && HREF.equalsIgnoreCase(name))
                {
                    _lastHref = value;
                    if(_lastRel!=null)
                    {
                        if(OPENID_SERVER.equals(_lastRel))
                        {
                            _openIdServer = value;
                            _searching = _openIdDelegate==null;   
                        }
                        else if(OPENID_DELEGATE.equals(_lastRel))
                        {
                            _openIdDelegate = value;
                            _searching = _openIdServer==null;                            
                        }
                        _lastRel = null;
                        _lastHref = null;
                    }
                }
            }            
        }

        public void characters(char[] data, int start, int length)
        {
            // not needed as we're not parsing innerText            
        }
        
    }

}
