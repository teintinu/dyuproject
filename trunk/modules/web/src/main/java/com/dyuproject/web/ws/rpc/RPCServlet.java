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

package com.dyuproject.web.ws.rpc;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.dyuproject.web.ws.WebServiceServlet;

/**
 * @author David Yu
 */

public class RPCServlet extends WebServiceServlet
{

    protected Object handle(HttpServletRequest request, Map<String,String> params) throws Exception
    {
        return RPCService.getInstance().handle(_context, request, params);
    }
    
}
