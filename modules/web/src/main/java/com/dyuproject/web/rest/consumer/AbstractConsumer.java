//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.web.rest.consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.dyuproject.util.Delim;
import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.ValidatingConsumer;
import com.dyuproject.web.rest.ViewDispatcher;

/**
 * @author David Yu
 * @created Jan 18, 2009
 */

public abstract class AbstractConsumer extends AbstractLifeCycle implements ValidatingConsumer
{
    
    protected String _httpMethod;
    protected Class<?> _pojoClass;
    protected Map<?,?> _initParams;
    protected String _consumeType;
    protected String _dispatcherName;
    protected String _dispatchUri;
    protected String _responseContentType;
    protected String _requestContentType;
    protected Map<String,String> _requestAttrs;
    protected ViewDispatcher _dispatcher;
    
    public Class<?> getPojoClass()
    {
        return _pojoClass;
    }
    
    public Map<?,?> getInitParams()
    {
        return _initParams;
    }
    
    public String getHttpMethod()
    {
        return _httpMethod;
    }
    
    public String getResponseContentType()
    {
        return _responseContentType;
    }
    
    public String getRequestContentType()
    {
        return _requestContentType;
    }
    
    public Map<String,String> getRequestAttrs()
    {
        return _requestAttrs;
    }
    
    public String getConsumeType()
    {
        return _consumeType;
    }
    
    public void preConfigure(String httpMethod, Class<?> pojoClass, Map<?,?> initParams)
    {
        if(_pojoClass!=null)
            throw new IllegalStateException("pojoClass already set.");
        if(httpMethod==null)
            throw new IllegalStateException("httpMethod is required.");
        if(pojoClass==null)
            throw new IllegalStateException("pojoClass must be provided.");
        if(initParams==null)
            throw new IllegalStateException("initParams must be provided.");
        
        _httpMethod = httpMethod;
        _pojoClass = pojoClass;
        _initParams = initParams;
        _consumeType = (String)_initParams.get(CONSUME_TYPE);
    }
    
    protected void initDefaults()
    {
        _dispatcherName = (String)_initParams.get(DISPATCHER_NAME);
        if(_dispatcherName==null)
            _dispatcherName = getDefaultDispatcherName();
        
        _dispatcher = getWebContext().getViewDispatcher(_dispatcherName);
        if(_dispatcher==null)
            throw new IllegalStateException("dispatcher *" + _dispatcherName + "* not found.");
        
        _dispatchUri = (String)_initParams.get(DISPATCH_URI);
        
        _responseContentType = (String)_initParams.get(RESPONSE_CONTENT_TYPE);
        if(_responseContentType==null)
            _responseContentType = getDefaultResponseContentType();
        
        _requestContentType = (String)_initParams.get(REQUEST_CONTENT_TYPE);
        if(_requestContentType==null)
            _requestContentType = getDefaultRequestContentType();
        
        String requestAttrsParam = (String)_initParams.get(REQUEST_ATTRIBUTES);
        if(requestAttrsParam!=null)
        {
            String[] msgs = Delim.SEMI_COLON.split(requestAttrsParam);
            if(msgs.length!=0)
            {
                for(String m : msgs)
                {
                    int colon = m.indexOf(':');
                    if(colon>0 && colon<m.length()-1)
                    {
                        String key = m.substring(0, colon).trim();
                        String val = m.substring(colon+1).trim();
                        if(_requestAttrs==null)
                        {
                            int size = (int)(1+(msgs.length/.75));
                            _requestAttrs = new HashMap<String,String>(size);
                        }
                        _requestAttrs.put(key, val);
                    }
                }
            }
        }
    }
    
    protected abstract String getDefaultDispatcherName();
    protected abstract String getDefaultResponseContentType();
    protected abstract String getDefaultRequestContentType();
    
    public static String getDefaultErrorMsg(String field)
    {
        return getDisplayField(field).append(" must be correctly provided.").toString();
    }
    
    public static StringBuilder getDisplayField(String field)
    {
        StringBuilder buffer = new StringBuilder();
        char[] ch = field.toCharArray();
        char firstLetter = ch[0];
        buffer.append((char)(firstLetter<91 ? firstLetter : firstLetter-32));
        for(int i=1; i<ch.length; i++)
        {
            char c = ch[i];
            if(c<91)
                buffer.append(' ');
            
            buffer.append(c);
        }
        return buffer;
    }
    
    protected void dispatch(String errorMsg, RequestContext rc, String uri) 
    throws ServletException, IOException
    {
        if(_requestAttrs!=null)
        {
            for(Map.Entry<String, String> entry : _requestAttrs.entrySet())
                rc.getRequest().setAttribute(entry.getKey(), entry.getKey());
        }
        rc.getRequest().setAttribute(ERROR_MSG, errorMsg);
        rc.getResponse().setContentType(_responseContentType);
        _dispatcher.dispatch(uri, rc.getRequest(), rc.getResponse());
    }
    
    protected void dispatch(String errorMsg, RequestContext rc) 
    throws ServletException, IOException
    {
        dispatch(errorMsg, rc, _dispatchUri);
    }

    
}
