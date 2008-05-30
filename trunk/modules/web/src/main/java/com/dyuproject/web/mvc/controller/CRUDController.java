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

/**
 * @author David Yu
 * @created May 22, 2008
 */

public abstract class CRUDController extends VerbMappedController
{    
    
    public static final String DEFAULT_METHOD_PARAM = "_method";
    
    private boolean _allowMethodOverride = false;
    
    private String _methodParam;
    
    protected void init()
    {
        super.init();
        if(_methodParam==null)
            _methodParam = DEFAULT_METHOD_PARAM;
        if(getIdentifierAttribute()==null)
            setIdentifierAttribute(getIdentifier() + ".verbOrId");
    }
    
    public void setAllowMethodOverride(boolean allowMethodOverride)
    {
        _allowMethodOverride = allowMethodOverride;
    }
    
    public boolean isAllowMethodOverride()
    {
        return _allowMethodOverride;
    }
    
    public void setMethodParam(String methodParam)
    {
        if(_methodParam==null && methodParam!=null)
            _methodParam = methodParam;
    }

    @Override
    protected void handleDefault(String id, String mime,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        String method = request.getMethod();
        if(isAllowMethodOverride())
        {
            String methodOverride = request.getParameter(_methodParam);
            if(methodOverride!=null)
                method = methodOverride;
        }        

        if(method.equals(GET))
        {
            if(id==null)
                read(request, response, mime);
            else
                read(request, response, mime, id);
            return;
        }
        if(method.equals(POST) || method.equals(PUT))
        {
            if(id==null)
                create(request, response, mime);
            else
                update(request, response, mime, id);
            return;
        }
        if(method.equals(DELETE))
        {
            delete(request, response, mime, id);
            return;
        }
        response.sendError(404);
    }
    
    protected abstract void create(HttpServletRequest request, HttpServletResponse response, 
            String mime) throws IOException, ServletException;
    
    protected abstract void read(HttpServletRequest request, HttpServletResponse response, 
            String mime) throws IOException, ServletException;
    
    protected abstract void read(HttpServletRequest request, HttpServletResponse response, 
            String mime, String id) throws IOException, ServletException;
    
    protected abstract void update(HttpServletRequest request, HttpServletResponse response, 
            String mime, String id) throws IOException, ServletException;
    
    protected abstract void delete(HttpServletRequest request, HttpServletResponse response, 
            String mime, String id) throws IOException, ServletException;

}
