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

package com.dyuproject.web.cometd.servlet;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dyuproject.util.FormatConverter;
import com.dyuproject.web.cometd.BasicListener;
import com.dyuproject.web.cometd.DefaultBayeux;
import dojox.cometd.Bayeux;
import dojox.cometd.Listener;

/**
 * @author David Yu
 */

public class DefaultCometdServlet extends AbstractCometdServlet
{
    
    protected void doUpdate(HttpServletResponse response, BasicListener listener, 
            FormatConverter converter, String callback)throws IOException 
    {
        response.setContentType(converter.getContentType());
        //response.getOutputStream().write(converter.toString(listener.takeMessagesAsArray()).getBytes());
        ServletOutputStream out = response.getOutputStream();
        out.write(converter.toString(listener.takeMessages(), callback).getBytes());
        //out.flush();
        //out.close();                
    }
    
    protected Bayeux initBayeux(ServletContext sc) throws ServletException 
    {
        Bayeux bayeux = (Bayeux)sc.getAttribute(Bayeux.class.getName());        
        return bayeux!=null ? bayeux: new DefaultBayeux(sc.getContextPath());
    }

    
    protected Listener newListener(HttpServletRequest request, String ids) 
    {        
        return null;
    }

}
