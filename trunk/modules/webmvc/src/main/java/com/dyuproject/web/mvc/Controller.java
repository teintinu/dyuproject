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
 * @author David Yu
 * @created May 9, 2008
 */

public interface Controller
{    
    
    public void init(WebContext context);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response, 
            ContentGenerator generator) throws IOException, ServletException;
    
    public void doPost(HttpServletRequest request, HttpServletResponse response, 
            ContentGenerator generator) throws IOException, ServletException;
    
    public void doPut(HttpServletRequest request, HttpServletResponse response, 
            ContentGenerator generator) throws IOException, ServletException;
    
    public void doDelete(HttpServletRequest request, HttpServletResponse response, 
            ContentGenerator generator) throws IOException, ServletException;
    
    public String getResourceName();
    public String getResourceIdAttribute();

}
