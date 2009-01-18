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

    public static final String ERROR_MSG_KEY = "errorMsg";
    public static final String OUTPUT_KEY = "output";
    
    public static final String CONSUMER_OUTPUT_TYPE = "consumer.output.type";
    public static final String CONSUMER_VALIDATION_DISPATCHER_NAME = "consumer.validation.dispatcher_name";
    public static final String CONSUMER_VALIDATION_DISPATCH_URI = "consumer.validation.dispatch_uri";
    
    public void init(Class<?> pojoClass, Map<?,?> initParams);
    public String getRequestContentType();
    public boolean consume(RequestContext requestContext) throws ServletException, IOException;
    
    public interface FieldValidator
    {
        
        public String getErrorMsg(Object value);
        
    }

}
