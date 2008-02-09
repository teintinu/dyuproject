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

package com.dyuproject.web.cometd;

import java.util.List;
import java.util.Map;
import org.mortbay.util.ajax.Continuation;
import dojox.cometd.Client;
import dojox.cometd.Listener;

/**
 * @author David Yu
 */

public class DefaultListener implements Listener, MessageListener
{		
	private Continuation _continuation;
	private DefaultClient _client;
	private Listener _listener;
	private long _lastConnect = 0;
	private Object _continuationLock = new Object();
	
	DefaultListener(DefaultClient client) 
	{		
		_client = client;			
	}
	
	void setListener(Listener listener)
	{
	    _listener = listener;
	}
	
	public void removed(String clientId, boolean timeout) 
	{
		if(_listener!=null)
			_listener.removed(clientId, timeout);		
	}
	
    public void deliver(Client fromClient, Client toClient, Map<String, Object> message) 
    {
        if(_listener!=null)
            _listener.deliver(fromClient, toClient, message);
        resume();
    }
    
    public void resume()
    {
        synchronized(_continuationLock)
        {
            if(_continuation!=null)
                _continuation.resume();
        }
    }
    
    public boolean resumeExisting(Continuation replacement)
    {
        boolean resumed = false;
        synchronized(_continuationLock)
        {
            if(_continuation!=null)
            {
                resumed = true;
                _continuation.resume();
            }
            _continuation = replacement;
        }
        return resumed;
    }

	public boolean hasNew() 
	{	
		return _client.hasMessages();
	}

	public void setContinuation(Continuation continuation) 
	{
	    synchronized(_continuationLock)
	    {
	        _continuation = continuation;
	    }				
	}

	public Continuation getContinuation() 
	{		
		return _continuation;
	}
	
	public Object[] takeMessages() 
	{
	    List<Map<String, Object>> messages = _client.takeMessages();
	    Object[] msgs = null;
	    synchronized(messages)
	    {	        
	        msgs = messages.toArray();
	        messages.clear();
	    }
	    return msgs;
	}
	
	public Listener getListener() 
	{
		return _listener;
	}
	
	public Client getClient() 
	{
		return _client;
	}
	
	public void updateConnect()
	{
	    _lastConnect = System.currentTimeMillis();
	}
	
	public long getLastConnect()
	{
	    return _lastConnect;
	}
	
	void reset()
	{
	    _lastConnect = 0;
	    _continuation = null;
	    _listener = null;
	}
	
}
