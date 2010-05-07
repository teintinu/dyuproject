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

package com.dyuproject.web.rest;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * Pre-handling and post-handling of REST requests allowing the request to continue or not.
 * Security handling is decoupled/contained. 
 * 
 * @author David Yu
 * @created May 18, 2008
 */

public interface Interceptor extends LifeCycle
{
    
    public boolean preHandle(RequestContext requestContext)
    throws ServletException, IOException; 
    
    public void postHandle(boolean handled, RequestContext requestContext);

}
