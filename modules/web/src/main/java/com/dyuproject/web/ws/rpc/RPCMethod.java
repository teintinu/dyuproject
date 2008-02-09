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
import com.dyuproject.util.Delim;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.WebServiceException;
import com.dyuproject.web.ws.error.ParametersIncomplete;

/**
 * @author David Yu
 */

public class RPCMethod implements WebServiceHandler
{
    private static final String[] NO_PARAMS = new String[]{};
    
    private String _name;    
    private String _sql;
    private Handler _handler;
    private String[] _params;
    private ParametersIncomplete _error;
    
    public RPCMethod(String name, String params)
    {
        this(name, params, null);
    }
    
    public void init()
    {
        if(_handler==null)
            throw new IllegalStateException("RPC Method *" + _name + "* handler must not be null");
    }
    
    public RPCMethod(String name, String params, Handler handler)
    {        
        _name = name;
        _handler = handler;        
        if(params!=null)
        {
            _params = Delim.COMMA.split(params);
            _error = new ParametersIncomplete(params);            
        }
        else
            _params = NO_PARAMS;        
    }
    
    public String getName()
    {
        return _name;
    }
    
    public int getHash()
    {
        return _name.hashCode();
    }
    
    public void setSql(String sql)
    {
        _sql = sql;
    }
    
    public String getSql()
    {
        return _sql;
    }
    
    public String toString()
    {
        return _name;
    }
    
    public int hashCode()
    {
        return _name.hashCode();
    }
    
    public RPCMethod setHandler(Handler handler)
    {
        _handler = handler;
        return this;
    }
    
    public Handler getHandler()
    {
        return _handler;
    }
    
    public Object handle(String[] pathInfo, Map<String, String>params) throws WebServiceException, Exception
    {
        if(_params.length==0)           
            return _handler.handle(null);
        String[] values = new String[_params.length];
        for(int i=0; i<_params.length; i++)
        {
            values[i] = params.get(_params[i]);
            if(values[i]==null)
                return _error;
        }
        return _handler.handle(values);
    }
    
    public interface Handler
    {
        public Object handle(String[] values) throws Exception;
    }
    
}
