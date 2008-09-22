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

package com.dyuproject.util.xml;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Sep 18, 2008
 */

public class XMLParserTest extends TestCase
{
    
    public void testParseOpenId() throws Exception
    {
        String url = "http://davidyuftw.blogspot.com";
        HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setDefaultUseCaches(false);
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.connect();
        SimpleHandler handler = new SimpleHandler();
        InputStreamReader reader = new InputStreamReader(con.getInputStream());
        try
        {
            XMLParser.parse(reader, handler, true);
            Node html = handler.getNode();
            Node head = html.getNode("head");
            List<Node> links = head.getNodes("link");
            for(Node n : links)
            {
                String rel = n.getAttribute("rel");
                if(rel.equals("openid.server"))
                {
                    String openIdServer = n.getAttribute("href");                    
                    assertTrue(openIdServer!=null);
                    System.err.println(openIdServer);
                    break;
                }
            }
        }
        finally
        {
            reader.close();
            con.disconnect();
        }   
    }

}