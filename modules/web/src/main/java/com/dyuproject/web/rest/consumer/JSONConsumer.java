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

package com.dyuproject.web.rest.consumer;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dyuproject.web.rest.RequestContext;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public class JSONConsumer extends AbstractConsumer
{
    
    public static final String CONTENT_TYPE = "text/json";
    
    protected void init()
    {
                
    }
    
    protected void configure()
    {
                
    }

    public boolean consume(RequestContext requestContext) throws ServletException, IOException
    {

        
        return false;
    }

    public String getRequestContentType()
    {        
        return CONTENT_TYPE;
    }

    



}
