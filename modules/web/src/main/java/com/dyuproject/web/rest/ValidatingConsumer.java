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


/**
 * @author David Yu
 * @created Jan 14, 2009
 */

public interface ValidatingConsumer extends LifeCycle
{
    
    public static final String REQUEST_CONTENT_TYPE = "request_content_type";
    
    public void preConfigure(String httpMethod, String contentType, Class<?> pojoClass, 
            Map<?,?> fieldParams);
    
    public String getContentType();
    
    public String getHttpMethod();
    
    public Object consume(RequestContext requestContext) throws IOException, ValidationException;
    
    public boolean merge(Object pojo, RequestContext rc) throws IOException, ValidationException;
    
    public interface FieldValidator
    {
        
        public String validate(Object value);
        
    }

}
