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

package com.dyuproject.web.rest.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.WebContext;

/**
 * Serves REST requests
 * 
 * @author David Yu
 * @created May 9, 2008
 */

public interface Controller
{
    
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    
    public void init(WebContext webContext);
    public void destroy(WebContext webContext);
    
    public void handle(String mime, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException;
    
    public String getIdentifier();
    public String getIdentifierAttribute();
    
    public Interceptor getInterceptor();    

}
