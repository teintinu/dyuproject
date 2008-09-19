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
import java.net.HttpURLConnection;
import java.net.URL;

import com.dyuproject.util.xml.LazyHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * Discovery mechanism to retrieve the openid.server and openid.delegate from link tags.
 * 
 * @author David Yu
 * @created Sep 15, 2008
 */

public class LinkHrefDiscovery implements Discovery
{
    
    public OpenIdUser discover(String claimedId, OpenIdContext context)
    throws Exception
    {
        OpenIdXmlHandler handler = new OpenIdXmlHandler();
        InputStreamReader reader = new InputStreamReader(context.getHttpConnector().doGET(claimedId, 
                context), "UTF-8");
        XMLParser.parse(reader, handler, false);
        return handler._openIdServer==null ? null : new OpenIdUser(claimedId, 
                handler._openIdServer, handler._openIdDelegate);
    }
    
    static class OpenIdXmlHandler implements LazyHandler
    {
        
        private String _openIdServer;
        private String _openIdDelegate;
        private String _lastHref;
        private String _lastRel;
        private int _stack = 0;
        private boolean _headFound = false;
        private boolean _link = false;        
        private boolean _terminate = false;
        
        OpenIdXmlHandler()
        {
            
        }

        public boolean rootElement(String name)
        {            
            return "html".equalsIgnoreCase(name);            
        }

        public boolean startElement(String name)
        {            
            _stack++;
            if(_headFound)
            {                
                _link="link".equalsIgnoreCase(name);
                return true;
            }
            _headFound = "head".equalsIgnoreCase(name);
            return _headFound;
        }

        public boolean endElement()
        {
            _lastRel = null;
            _lastHref = null;
            _stack--;            
            return _terminate || _stack!=0;
        }
        
        public void attribute(String name, String value)
        {
            if(_link)
            {
                if(_lastRel==null && "rel".equalsIgnoreCase(name))
                {
                    _lastRel = value;
                    if(_lastHref!=null)
                    {
                        if(OPENID_SERVER.equals(value))
                        {
                            _openIdServer = _lastHref;
                            _terminate = _openIdDelegate!=null;                                
                        }
                        else if(OPENID_DELEGATE.equals(value))
                        {
                            _openIdDelegate = _lastHref;
                            _terminate = _openIdServer!=null;
                        }
                        _lastRel = null;
                        _lastHref = null;
                    }
                }
                else if(_lastHref==null && "href".equalsIgnoreCase(name))
                {
                    _lastHref = value;
                    if(_lastRel!=null)
                    {
                        if(OPENID_SERVER.equals(_lastRel))
                        {
                            _openIdServer = value;
                            _terminate = _openIdDelegate!=null;   
                        }
                        else if(OPENID_DELEGATE.equals(_lastRel))
                        {
                            _openIdDelegate = value;
                            _terminate = _openIdServer!=null;                            
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

    public static void main(String[] args) throws Exception
    {
        String url = "http://davidyuftw.blogspot.com";
        HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setDefaultUseCaches(false);
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.connect();        
        OpenIdXmlHandler handler = new OpenIdXmlHandler();
        InputStreamReader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
        XMLParser.parse(reader, handler, false);
        System.err.println(handler._openIdServer + " | " + handler._openIdDelegate);
        con.disconnect();
    }
}
