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
import com.dyuproject.util.xml.Node;
import com.dyuproject.util.xml.SimpleHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * Discovery through yadis.  If that fails, delegates to LinkHrefDiscovery
 * 
 * @author David Yu
 * @created Sep 23, 2008
 */

public class DefaultDiscovery implements Discovery
{
    
    static final String X_XRDS_LOCATION = "X-XRDS-Location";
    static final String NS_PREFIX = "http://specs.openid.net/auth/2.0/";
    static final String NS_SERVER = "http://specs.openid.net/auth/2.0/server";
    static final String NS_SIGNON = "http://specs.openid.net/auth/2.0/signon";
    static final String SERVER = "server";
    static final String SIGNON = "signon";
    static final String XRDS = "xrds";    
    static final String XRD = "XRD";    
    static final String SERVICE = "Service";
    static final String TYPE = "Type";
    static final String URI = "URI";
    static final String LOCAL_ID = "LocalID";

    public OpenIdUser discover(String claimedId, OpenIdContext context)
            throws Exception
    {
        Response response = context.getHttpConnector().doHEAD(claimedId, context);
        String location = response.getHeader(X_XRDS_LOCATION);
        if(location==null)
        {            
            try{response.close();}catch(IOException e){}
            return LinkHrefDiscovery.discover(claimedId, 
                    context.getHttpConnector().doGET(claimedId, context));
        }
        
        response = context.getHttpConnector().doGET(location, context);
        InputStreamReader reader = null;
        OpenIdUser user = null;
        try
        {            
            reader = new InputStreamReader(response.getInputStream(), Constants.DEFAULT_ENCODING);            
            user = discoverYadis(reader);
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
        
        return user!=null ? user : LinkHrefDiscovery.discover(claimedId, 
                context.getHttpConnector().doGET(claimedId, context));
    }
    
    static OpenIdUser discoverYadis(InputStreamReader reader) throws Exception
    {        
        SimpleHandler handler = new SimpleHandler();
        XMLParser.parse(reader, handler, true);        
        Node xrds = handler.getNode();       
        Node xrd = xrds.getFirstNode();        
        for(Node service : xrd.getNodes())
        {
            if(!SERVICE.equalsIgnoreCase(service.getName()))
                continue;
            for(Node n : service.getNodes())
            {                
                int idx = n.getText().indexOf(NS_PREFIX);
                if(idx!=-1)
                {                    
                    String type = n.getText().substring(idx + NS_PREFIX.length(), 
                            n.getText().length()).trim();
                    if(SERVER.equals(type) || SIGNON.equals(type))
                    {
                        String openIdServer = null;
                        String openIdDelegate = null;
                        Node uri = service.getNodeFromLast(URI);
                        if(uri!=null)
                            openIdServer = uri.getText().toString().trim();
                        Node localId = service.getNodeFromLast(LOCAL_ID);
                        if(localId!=null)
                            openIdDelegate = localId.getText().toString().trim();
                        if(openIdServer!=null)
                            return new OpenIdUser(openIdDelegate, openIdServer, openIdDelegate);
                    }
                }
            }            
        }
        return null;        
    }

}
