//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.util;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import com.dyuproject.util.xml.Node;
import com.dyuproject.util.xml.SimpleHandler;
import com.dyuproject.util.xml.XMLParser;

/**
 * @author David Yu
 * @created Jan 13, 2009
 */

public abstract class Manifest
{
    
    private static final String PREFIX = "META-INF/";
    
    static String getPath(String uri)
    {
        if(uri.startsWith(PREFIX))
           return uri; 
            
        return uri.charAt(0)=='/' ? PREFIX + uri.substring(1) : PREFIX + uri;
    }
    
    public static Properties getProperties(String uri)
    {
        try
        {
            URL resource = ClassLoaderUtil.getResource(getPath(uri), Manifest.class);
            if(resource==null)
                return null;            
                
            Properties props = new Properties();
            props.load(resource.openStream());
            return props;             
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Node getNode(String uri)
    {
        try
        {
            URL resource = ClassLoaderUtil.getResource(getPath(uri), Manifest.class);
            if(resource==null)
                return null;               
            
            SimpleHandler handler = new SimpleHandler();
            XMLParser.parse(new InputStreamReader(resource.openStream()), handler, true);
            return handler.getNode();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
