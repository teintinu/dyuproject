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

package com.dyuproject.web.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Pre-handling and post-handling of REST requests allowing the request to continue or not.
 * Security handling is decoupled/contained.
 * Also works as an interceptor.
 * 
 * @author David Yu
 * @created May 18, 2008
 */

public interface Filter
{
    
    public void init(WebContext webContext);
    public void destroy(WebContext webContext);
    
    public boolean preHandle(String mime, HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException; 
    
    public void postHandle(boolean handled, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException;

}
