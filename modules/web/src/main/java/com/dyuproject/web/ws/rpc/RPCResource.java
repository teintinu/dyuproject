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

import java.util.HashMap;
import java.util.Map;
import com.dyuproject.web.ws.WebServiceException;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.NoSuchMethod;

/**
 * @author David Yu
 */

public class RPCResource implements WebServiceHandler
{
 
    private String _name;
    private Map<String,RPCMethod > _methods = new HashMap<String,RPCMethod>();
    
    public static RPCResource create(Class clazz)
    {
        return new RPCResource(clazz.getSimpleName());
    }
    
    public RPCResource(String name)
    {
        _name = name;
    }
    
    public void init()
    {
        if(_methods.size()==0)
            throw new IllegalStateException("RPC Resource *" + _name + "* has no methods");
    }
    
    public String getName()
    {
        return _name;
    }
    
    public RPCResource addMethod(RPCMethod method)
    {
        _methods.put(method.getName(), method);
        return this;
    }

    public Object handle(String[] pathInfo, Map<String, String> params) 
    throws WebServiceException, Exception
    {        
        RPCMethod method = _methods.get(pathInfo[1]);
        return method==null ? NoSuchMethod.getInstance() : method.handle(pathInfo, params);
    }

}
