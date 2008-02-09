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

package com.dyuproject.web.ws.rest;

import java.util.HashMap;
import java.util.Map;
import com.dyuproject.web.ws.WebServiceException;
import com.dyuproject.web.ws.error.ResourceUnavailable;

/**
 * @author David Yu
 */

public class RESTVerbResource extends AbstractRESTVerbHandler
{
    
    public static RESTVerbResource create(Class clazz)
    {
        return create(clazz, true);
    }
    
    public static RESTVerbResource create(Class clazz, boolean plural)
    {
        return new RESTVerbResource(plural ? clazz.getSimpleName().toLowerCase().concat("s") : 
            clazz.getSimpleName().toLowerCase());
    }
    
    private Map<String,AbstractRESTVerbHandler> _verbs = new HashMap<String,AbstractRESTVerbHandler>();
    
    public RESTVerbResource(String name)
    {
        super(name);
    }
    
    public void init()
    {        
        for(AbstractRESTVerbHandler h : _verbs.values())
            h.init();
        super.init();
    }
    
    void setDepth(int depth)
    {
        super.setDepth(depth);
        for(AbstractRESTVerbHandler vh : _verbs.values())
            vh.setDepth(_depth+1);
    }
    
    public void addVerbHandler(AbstractRESTVerbHandler handler)
    {
        if(_initialized)
            return;
        _verbs.put(handler.getName(), handler);
        if(handler instanceof RESTVerbResource)
            handler.setDepth(_depth+1);
    }

    public Object handle(String[] tokens, Map<String, String> params) 
    throws WebServiceException, Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub)
            return ResourceUnavailable.getInstance();        
        if(tokens.length==sub+1)
        {
            AbstractRESTVerbHandler vh = _verbs.get(tokens[sub]);
            return vh!=null && vh._depth==0 ? vh.handle(tokens, params) : 
                ResourceUnavailable.getInstance();
        }
        AbstractRESTVerbHandler vh = _verbs.get(tokens[sub+1]);
        return vh!=null && vh._depth>0 ? vh.handle(tokens, params) :
                ResourceUnavailable.getInstance();
    }

    public Object handle(String[] tokens, Map<String, String> params, long parentId) 
    throws Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub)
            return ResourceUnavailable.getInstance(); 
        if(tokens.length==sub+1)
        {
            AbstractRESTVerbHandler vh = _verbs.get(tokens[sub]);
            return vh!=null && vh._depth==0 ? vh.handle(tokens, params, parentId) : 
                ResourceUnavailable.getInstance();
        }
        AbstractRESTVerbHandler vh = _verbs.get(tokens[sub+1]);
        return vh!=null && vh._depth>0 ? vh.handle(tokens, params, parentId) :
                ResourceUnavailable.getInstance();
    }

}
