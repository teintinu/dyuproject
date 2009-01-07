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

package com.dyuproject.openid;

import java.io.InputStream;

import junit.framework.TestCase;

import com.dyuproject.openid.HttpConnector.Response;

/**
 * @author David Yu
 * @created Jan 7, 2009
 */

public class SimpleHttpConnectorTest extends TestCase
{
    
    public void test1() throws Exception
    {
        HttpConnector connector = new SimpleHttpConnector();
        Response response = connector.doGET("http://dyuproject.googlecode.com/svn/trunk/README.txt", 
                null);
        assertTrue(response.getStatus()==200);
        InputStream is = response.getInputStream();
        try
        {
            byte[] buf = new byte[SimpleHttpConnector.getBufferSize()];
            for(int len=0; (len=is.read(buf))!=-1;)
                System.err.print(new String(buf, 0, len));
        }        
        finally
        {
            try
            {
                is.close();
            }
            finally    
            {
                response.close();
            }            
        }
    }

}
