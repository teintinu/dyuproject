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

package com.dyuproject.web.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;


/**
 * @author David Yu
 * @created Jan 14, 2009
 */

public interface ValidatingConsumer extends LifeCycle
{
    
    public static final String CONSUMED_OBJECT = "consumed_object";
    public static final String CONSUME_TYPE = "consume_type";
    public static final String MSG = "msg";
    public static final String DISPATCHER_NAME = "dispatcher_name";
    public static final String DISPATCH_URI = "dispatch_uri";
    public static final String RESPONSE_CONTENT_TYPE = "response_content_type";
    public static final String REQUEST_CONTENT_TYPE = "request_content_type";
    public static final String REQUEST_ATTRIBUTES = "request_attributes";
    
    public void preConfigure(String httpMethod, Class<?> pojoClass, Map<?,?> initParams);
    public String getRequestContentType();
    
    public String getHttpMethod();
    
    public boolean consume(RequestContext requestContext) throws ServletException, IOException;
    
    
    public interface FieldValidator
    {
        
        public String validate(Object value);
        
    }

}
