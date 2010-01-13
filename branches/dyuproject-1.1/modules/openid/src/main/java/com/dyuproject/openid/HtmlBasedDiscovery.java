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

import com.dyuproject.util.http.HttpConnector.Response;
import com.dyuproject.util.xml.LazyHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * Discovery mechanism to retrieve the openid.server and openid.delegate from link tags.
 * 
 * @author David Yu
 * @created Sep 15, 2008
 */

public final class HtmlBasedDiscovery implements Discovery
{
    
    static final String CHECKED_PREFIX = "openid";
    static final String SERVER = "server";
    static final String DELEGATE = "delegate";    
    static final String PROVIDER = ".provider";
    static final String LOCAL_ID = ".local_id";

    static final int TYPE_IGNORE = 0;
    static final int TYPE_PROVIDER = 1;
    static final int TYPE_SERVER = 2;
    static final int TYPE_LOCAL_ID = 3;
    static final int TYPE_DELEGATE = 4;
    
    static final String HTML = "html";
    static final String HEAD = "head";
    static final String LINK = "link";
    static final String REL = "rel";
    static final String HREF = "href";    
    
    public OpenIdUser discover(Identifier identifier, OpenIdContext context)
    throws Exception
    {
        return tryDiscover(identifier, context);
    }
    
    static OpenIdUser tryDiscover(Identifier identifier, OpenIdContext context)
    throws Exception
    {
        return discoverHtmlHead(identifier, context.getHttpConnector().doGET(identifier.getUrl(), 
                (Map<?,?>)null));
    }
    
    static OpenIdUser discoverHtmlHead(Identifier identifier, Response response) throws Exception
    {
        OpenIdUser user = null;
        InputStreamReader reader = null;
        try
        {
            reader = new InputStreamReader(response.getInputStream(), Constants.DEFAULT_ENCODING);
            user = parse(identifier, reader);
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
        XMLParser.parse(reader, handler, false);
        if(handler._openIdServer==null)
            return null;
        if(handler._twoDotX && handler._openIdDelegate==null)
        {
            return new OpenIdUser(identifier.getId(), YadisDiscovery.IDENTIFIER_SELECT, 
                    handler._openIdServer, null);
        }
        return new OpenIdUser(identifier.getId(), identifier.getId(), handler._openIdServer, 
                handler._openIdDelegate);        
    }
    
    static int check(String rel)
    {
        if(!rel.startsWith(CHECKED_PREFIX))
            return TYPE_IGNORE;
        
        char c = rel.charAt(6);
        if(c=='2')
        {
            if(rel.startsWith(PROVIDER, 7))
            {
                if(rel.length()==16)
                    return TYPE_PROVIDER;                
                return Character.isWhitespace(rel.charAt(16)) ? TYPE_PROVIDER : TYPE_IGNORE;
            }
            if(rel.startsWith(LOCAL_ID, 7))
            {
                if(rel.length()==16)
                    return TYPE_LOCAL_ID;
                return Character.isWhitespace(rel.charAt(16)) ? TYPE_LOCAL_ID : TYPE_IGNORE;
            }
            return TYPE_IGNORE;
        }
        if(c=='.')
        {
            if(rel.startsWith(SERVER, 7))
            {
                if(rel.length()==13)
                    return TYPE_SERVER;
                return Character.isWhitespace(rel.charAt(13)) ? TYPE_SERVER : TYPE_IGNORE;
            }
            if(rel.startsWith(DELEGATE, 7))
            {
                if(rel.length()==15)
                    return TYPE_DELEGATE;
                return Character.isWhitespace(rel.charAt(15)) ? TYPE_DELEGATE : TYPE_IGNORE;
            }
            return TYPE_IGNORE;
        }
        
        return TYPE_IGNORE;
    }
    
    static final class XmlHandler implements LazyHandler
    {
        
        String _openIdServer;
        String _openIdDelegate;
        String _lastHref;
        String _lastRel;
        int _stack = 0;
        boolean _headFound = false;
        boolean _link = false;        
        boolean _searching = true;
        boolean _twoDotX = false;
        
        XmlHandler()
        {
            
        }

        public boolean rootElement(String name, String namespace)
        {
            _stack = 1;
            return HTML.equalsIgnoreCase(name);            
        }

        public boolean startElement(String name, String namespace)
        {            
            _stack++;
            if(_headFound)
            {
                _link = LINK.equalsIgnoreCase(name);
                _searching = _openIdServer==null;
                return true;
            }
            return (_headFound = HEAD.equalsIgnoreCase(name));
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
                        switch(check(value))
                        {
                            case TYPE_IGNORE:
                                break;
                            case TYPE_PROVIDER:
                                _openIdServer = _lastHref;
                                _searching = _openIdDelegate==null;
                                _twoDotX = true;
                                break;
                            case TYPE_SERVER:
                                // prioritize 2.0 if previously parsed
                                if(_openIdServer==null)
                                    _openIdServer = _lastHref;
                                
                                _searching = _openIdDelegate==null;
                                break;
                            case TYPE_LOCAL_ID:
                                _openIdDelegate = _lastHref;
                                _searching = _openIdServer==null;
                                _twoDotX = true;
                                break;
                            case TYPE_DELEGATE:
                                // prioritize 2.0 if previously parsed
                                if(_openIdDelegate==null)
                                    _openIdDelegate = _lastHref;
                                
                                _searching = _openIdServer==null;
                                break;
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
                        switch(check(_lastRel))
                        {
                            case TYPE_IGNORE:
                                break;
                            case TYPE_PROVIDER:
                                _openIdServer = value;
                                _searching = _openIdDelegate==null;
                                _twoDotX = true;
                                break;
                            case TYPE_SERVER:
                                // prioritize 2.0 if previously parsed
                                if(_openIdServer==null)
                                    _openIdServer = value;
                                
                                _searching = _openIdDelegate==null;   
                                break;
                            case TYPE_LOCAL_ID:
                                _openIdDelegate = value;
                                _searching = _openIdServer==null;
                                _twoDotX = true;
                                break;
                            case TYPE_DELEGATE:
                                // prioritize 2.0 if previously parsed
                                if(_openIdDelegate==null)
                                    _openIdDelegate = value;
                                
                                _searching = _openIdServer==null;
                                break;
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
    
    /*public static void main(String[] args)
    {
        System.err.println(check("openid.server"));
        System.err.println(check("openid.server "));
        System.err.println(check("openid.served"));
        System.err.println(check("openid.server2"));
        System.err.println("");
        System.err.println(check("openid.delegate"));
        System.err.println(check("openid.delegate "));
        System.err.println(check("openid.delegatd"));
        System.err.println(check("openid.delegate2"));
        System.err.println("");
        System.err.println(check("openid2.provider"));
        System.err.println(check("openid2.provider "));
        System.err.println(check("openid2.provided"));
        System.err.println(check("openid2.provider2"));
        System.err.println("");
        System.err.println(check("openid2.local_id"));
        System.err.println(check("openid2.local_id "));
        System.err.println(check("openid2.local_ip"));
        System.err.println(check("openid2.local_id2"));
    }*/

}
