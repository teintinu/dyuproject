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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.mvc.AbstractController;

/**
 * @author David Yu
 * @created May 16, 2008
 */

public abstract class VerbMappedController extends AbstractController
{
    
    public static final String VOID = "void";
    
    private Map<String,Method> _verbMap = new HashMap<String,Method>();
    
    protected void init()
    {        
        try
        {
            Method[] methods = getClass().getDeclaredMethods();
            for(Method m : methods)
            {
                Class[] pt = m.getParameterTypes();
                if(pt.length==3)
                {
                    // e.g public void list(string, request, response);
                    if(String.class.isAssignableFrom(pt[0]) &&
                            HttpServletRequest.class.isAssignableFrom(pt[1]) &&
                            HttpServletResponse.class.isAssignableFrom(pt[2]) &&
                            m.getReturnType().getName().equals(VOID))
                    {                        
                        m.setAccessible(true);
                        _verbMap.put(m.getName(), m);
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }        
    }

    public final void handle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String verbOrId = (String)request.getAttribute(getIdentifierAttribute());
        if(verbOrId==null)
            handleDefault(null, mime, request, response);
        else
        {
            Method method = _verbMap.get(verbOrId);
            if(method==null)
                handleDefault(verbOrId, mime, request, response);
            else
            {
                try
                {
                    method.invoke(this, new Object[]{mime, request, response});
                } 
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                    response.sendError(404);                
                } 
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                    response.sendError(404);                
                } 
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                    response.sendError(404);                
                }
            }    
        }
    
    }
    
    protected abstract void handleDefault(String id, String mime, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;


}
