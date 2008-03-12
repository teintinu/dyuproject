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

import com.dyuproject.web.HttpMethod;
import com.dyuproject.web.ws.WebServiceException;
import com.dyuproject.web.ws.WebServiceHandler;
import com.dyuproject.web.ws.error.IdentifierMissing;
import com.dyuproject.web.ws.error.NotSupported;
import com.dyuproject.web.ws.error.ResourceUnavailable;

/**
 * @author David Yu
 */

public class RESTResource implements WebServiceHandler
{
    
    public static RESTResource create(Class clazz)
    {
        return create(clazz, true);
    }
    
    public static RESTResource create(Class clazz, boolean plural)
    {
        return new RESTResource(plural ? clazz.getSimpleName().toLowerCase().concat("s") : 
            clazz.getSimpleName().toLowerCase());
    }
    
    private int _depth = 0;
    private boolean _initialized = false;
    private Handler _handler;
    private String _name;
    private Map<String,RESTResource> _resources = new HashMap<String,RESTResource>();
    private Map<String,AbstractRESTVerbHandler> _verbs = new HashMap<String,AbstractRESTVerbHandler>();
    
    public RESTResource(String name)
    {
        this(name, null);
    }
    
    public void init()
    {
        if(_name==null)
            throw new IllegalStateException("REST Resource *name* must not be null");
        if(_handler==null)
            throw new IllegalStateException("REST Resource /" + _name + " handler must not be null");
        for(RESTResource r : _resources.values())
            r.init();
        for(AbstractRESTVerbHandler vh : _verbs.values())
            vh.init();
        _initialized = true;
    }
    
    public RESTResource(String name, Handler handler)
    {
        _name = name;
        setHandler(handler);
    }
    
    public RESTResource(String name, Handler handler, AbstractRESTVerbHandler[] verbHandlers)
    {
        this(name, handler);
        for(AbstractRESTVerbHandler vh : verbHandlers)
            addVerbHandler(vh);
    }
    
    public RESTResource(String name, Handler handler, RESTResource[] children)
    {
        this(name, handler);
        for(RESTResource r : children)
            addResource(r);
    }
    
    public RESTResource(String name, Handler handler, RESTResource parent)
    {
        this(name, handler);
        parent.addResource(this);
    }
    
    public String getName()
    {
        return _name;
    }
    
    private void setDepth(int depth)
    {
        _depth = depth;
        for(RESTResource r : _resources.values())
            r.setDepth(_depth+1);
        for(AbstractRESTVerbHandler vh : _verbs.values())
            vh.setDepth(_depth+1);
    }
    
    public int getDepth()
    {
        return _depth;
    }
    
    public int hashCode()
    {
        return _name.hashCode();
    }
    
    public boolean equals(Object obj)
    {
        return obj!=null && obj.hashCode() == hashCode();
    }
    
    public String toString()
    {
        return _name;
    }
    
    public void setHandler(Handler handler)
    {
        _handler = handler;
    }
    
    public Handler getHandler()
    {
        return _handler;
    }
    
    public void addResource(RESTResource resource)
    {
        if(_initialized)
            return;
        _resources.put(resource.getName(), resource);
        resource.setDepth(_depth+1);
    }
    
    public void addVerbHandler(AbstractRESTVerbHandler handler)
    {
        if(_initialized)
            return;
        _verbs.put(handler.getName(), handler);
        if(handler instanceof RESTVerbResource)
            handler.setDepth(_depth+1);
    }
    
    private Object handleGet(long parentId, String[] tokens, Map<String, String> params) 
    throws WebServiceException, Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub)
            return _handler.handleGet(-1, parentId);
        String verbOrId = tokens[sub];
        if(tokens.length==sub+1)
        {            
            AbstractRESTVerbHandler vh = _verbs.get(verbOrId);
            if(vh==null)
                return _handler.handleGet(Long.parseLong(verbOrId));
            return vh._depth==0 ? vh.handle(tokens, params, parentId) : 
                ResourceUnavailable.getInstance();
        }
        String subToken = tokens[sub+1];
        AbstractRESTVerbHandler vh = _verbs.get(subToken);
        if(vh!=null)
            return vh.handle(tokens, params, parentId);
        RESTResource resource = _resources.get(subToken);
        return resource==null ? ResourceUnavailable.getInstance() : 
            resource.handleGet(Long.parseLong(verbOrId), tokens, params);
    }
    
    private Object handlePost(long parentId, String[] tokens, Map<String, String> params) 
    throws WebServiceException, Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub)
            return _handler.handlePost(params, parentId);
        String verbOrId = tokens[sub];
        if(tokens.length==sub+1)
        {            
            AbstractRESTVerbHandler vh = _verbs.get(verbOrId);                
            return vh!=null && vh._depth==0 ? vh.handle(tokens, params) :
                ResourceUnavailable.getInstance();
        }
        String subToken = tokens[sub+1];
        AbstractRESTVerbHandler vh = _verbs.get(subToken);
        if(vh!=null)
            return vh.handle(tokens, params, parentId);
        RESTResource resource = _resources.get(subToken);
        return resource==null ? ResourceUnavailable.getInstance() : 
            resource.handlePost(Long.parseLong(verbOrId), tokens, params);
    }
    
    private Object handlePut(long parentId, String[] tokens, Map<String, String> params)
    throws WebServiceException, Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub+1)
            return _handler.handlePut(Long.parseLong(tokens[sub+1]), params, parentId);        
        RESTResource resource = _resources.get(tokens[sub+2]);        
        return resource==null ? ResourceUnavailable.getInstance() : 
            resource.handlePut(Long.parseLong(tokens[sub+1]), tokens, params);
    }
    
    private Object handleDelete(long parentId, String[] tokens)
    throws WebServiceException, Exception
    {
        int sub = _depth * 2 + 1;
        if(tokens.length==sub+1)
            return _handler.handleDelete(Long.parseLong(tokens[sub+1]), parentId);        
        RESTResource resource = _resources.get(tokens[sub+2]);        
        return resource==null ? ResourceUnavailable.getInstance() : 
            resource.handleDelete(Long.parseLong(tokens[sub+1]), tokens);
    }

    public Object handle(String[] tokens, Map<String, String> params) 
    throws WebServiceException, Exception
    {        
        int method = params.get("method").hashCode();
        if(method==HttpMethod.Hash.GET)
        {
            if(tokens.length==1)
                return _handler.handleGet();             
            String verbOrId = tokens[1];
            if(tokens.length==2)
            {              
                AbstractRESTVerbHandler vh = _verbs.get(verbOrId);
                if(vh==null)
                    return _handler.handleGet(Long.parseLong(verbOrId));
                return vh._depth==0 ? vh.handle(tokens, params) : 
                    ResourceUnavailable.getInstance();
            }            
            String subToken = tokens[2];
            AbstractRESTVerbHandler vh = _verbs.get(subToken);
            if(vh!=null)
                return vh.handle(tokens, params, Long.parseLong(verbOrId));
            RESTResource resource = _resources.get(subToken);
            return resource==null ? ResourceUnavailable.getInstance() : 
                resource.handleGet(Long.parseLong(verbOrId), tokens, params);
        }
        if(method==HttpMethod.Hash.POST)
        {
            if(tokens.length==1)
                return _handler.handlePost(params);
            String verbOrId = tokens[1];
            if(tokens.length==2)
            {                
                AbstractRESTVerbHandler vh = _verbs.get(verbOrId);                
                return vh!=null && vh._depth==0 ? vh.handle(tokens, params) :
                    ResourceUnavailable.getInstance();
            }
            String subToken = tokens[2];
            AbstractRESTVerbHandler vh = _verbs.get(subToken);
            if(vh!=null)
                return vh.handle(tokens, params, Long.parseLong(verbOrId));
            RESTResource resource = _resources.get(subToken);
            return resource==null ? ResourceUnavailable.getInstance() : 
                resource.handlePost(Long.parseLong(verbOrId), tokens, params);
        }
        if(method==HttpMethod.Hash.PUT)
        {
            if(tokens.length%2!=0)
                return IdentifierMissing.getInstance();
            if(tokens.length==2)
                return _handler.handlePut(Long.parseLong(tokens[1]), params);            
            RESTResource resource = _resources.get(tokens[2]);
            return resource==null ? ResourceUnavailable.getInstance() : 
                resource.handlePut(Long.parseLong(tokens[3]), tokens, params);
        }
        if(method==HttpMethod.Hash.DELETE)
        {
            if(tokens.length%2!=0)
                return IdentifierMissing.getInstance();
            if(tokens.length==2)
                return _handler.handleDelete(Long.parseLong(tokens[1]));
            RESTResource resource = _resources.get(tokens[2]);
            return resource==null ? ResourceUnavailable.getInstance() : 
                resource.handleDelete(Long.parseLong(tokens[3]), tokens);
        }
        return NotSupported.getInstance();
    }    
    
    public interface Handler
    {
        public Object handlePost(Map<String, String> params) throws Exception;
        public Object handlePost(Map<String, String> params, long parentId) throws Exception;
        
        public Object handleGet() throws Exception;
        public Object handleGet(long id) throws Exception;
        public Object handleGet(long id, long parentId) throws Exception;
        
        public Object handlePut(long id, Map<String, String> params) throws Exception;
        public Object handlePut(long id, Map<String, String> params, long parentId) throws Exception;
        
        public Object handleDelete(long id) throws Exception;
        public Object handleDelete(long id, long parentId) throws Exception;
    }

}
