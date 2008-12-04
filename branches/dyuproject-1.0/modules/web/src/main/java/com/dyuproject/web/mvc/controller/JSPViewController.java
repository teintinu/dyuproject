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

package com.dyuproject.web.mvc.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.mvc.AbstractController;

/**
 * Takes a string argument to a jsp view.
 * Dispatches all requests to the specific jsp view.
 * 
 * @author David Yu
 * @created Jun 3, 2008
 */

public class JSPViewController extends AbstractController
{
    
    private String _view;
    
    public JSPViewController()
    {
        
    }
    
    public void setView(String view)
    {
        _view = view;
    }
    
    public String getView()
    {
        return _view;
    }
    
    protected void init()
    {
        if(getView()==null)
            setView("/WEB-INF/jsp/" + getIdentifier() + "/index.jsp");        
    }

    public void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        getWebContext().getJSPDispatcher().dispatch(getView(), request, response);        
    }    

}
