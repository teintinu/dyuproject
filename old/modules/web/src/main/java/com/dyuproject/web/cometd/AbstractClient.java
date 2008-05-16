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

import com.dyuproject.util.format.FormatConverter;
import com.dyuproject.util.format.FormatConverter.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import dojox.cometd.Channel;
import dojox.cometd.Client;
import dojox.cometd.Listener;

/**
 * @author David Yu
 */

public abstract class AbstractClient implements Client, FormatConverter.Bean 
{
	
	private static Log log = LogFactory.getLog(AbstractClient.class);
	private String _id;
	private Listener _listener;
	private DefaultBayeux _bayeux;
	private ConcurrentMap<String, Channel> _channels = new ConcurrentHashMap<String, Channel>();	
	private List<Map<String, Object>> _messages = new ArrayList<Map<String, Object>>();
	private boolean _local = true;
	
	protected AbstractClient(DefaultBayeux bayeux, String clientId, Listener listener) 
	{
		_bayeux = bayeux;
		_id = clientId;		
		setListener(newListener(listener));	
	}
	
	void setId(String id)
	{
	    _id = id;
	}

	public final String getId() 
	{		
		return _id;
	}

	public final void publish(String toChannel, Object data, String msgId) 
	{
		if(toChannel==null || data==null)
			return;		
		if(msgId==null)
			msgId = "msgId@" + System.currentTimeMillis();
		if(toChannel.indexOf('*')>-1) 
		{
			publishAll(data, msgId);
			return;
		}	
		Channel channel = _channels.get(toChannel);
		if(channel!=null)		
			channel.publish(this, data, msgId);
		else
		{
		    channel = _bayeux.getChannel(toChannel, false);
		    if(channel!=null)
		        channel.publish(this, data, msgId);
		}
	}
	
	private void publishAll(Object data, String msgId) 
	{
		for(Channel c : _channels.values())
			c.publish(this, data, msgId);		
	}
	
	public final void subscribe(String toChannel) 
	{
		if(toChannel==null || _channels.containsKey(toChannel))
			return;		
		Channel channel = _bayeux.getChannel(toChannel, true);
		if(channel!=null) 
		{
			log.warn(getId() + "has subscribed to " + channel.getId());
			channel.subscribe(this);
			_channels.put(channel.getId(), channel);
		}
	}

	public final void unsubscribe(String toChannel) 
	{
		if(toChannel==null)
			return;
		Channel channel = _channels.remove(toChannel);
		if(channel!=null)
			channel.unsubscribe(this);
	}

	public final void remove(boolean timeout) 
	{
        if(_listener!=null)
            _listener.removed(getId(), timeout);
	    for(Channel c : _channels.values()) 			
			c.unsubscribe(this);		
		_bayeux.removeClient(this);
		reset();
	}

	public final boolean hasMessages() 
	{
		return !_messages.isEmpty();
	}

	public final List<Map<String, Object>> takeMessages() 
	{		
		return _messages;
	}

	public final void deliver(Client from, Map<String, Object> message) 
	{
		if(!_local && from.getId().equals(getId())) 			
			return;
		synchronized(_messages)
		{
		    _messages.add(message);
		}
		if(_listener!=null)
			_listener.deliver(from, this, message);	
	}

	public final void setListener(Listener listener) 
	{
		_listener = listener;		
	}

	public final Listener getListener() 
	{
		return _listener;
	}
	
	public final void setLocal(boolean local) 
	{
		_local = local;
	}	
	
	public final boolean isLocal() 
	{
		return _local;
	}
	
	public final Set<String> getChannels() 
	{
		return _channels.keySet();
	}
	
	void reset() 
	{
		_id = null;
		_channels.clear();
		_messages.clear();
		if(_listener!=null && _listener instanceof DefaultListener)
		    ((DefaultListener)_listener).reset();
		_bayeux.recycle(this);
	}
	
	public void convert(Builder builder, String format)
	{
	    builder.put("id", _id);
	    builder.put("channels", _channels.keySet());
	}

	/* ------------------------------------------------------------ */
	
	public final void startBatch() 
	{
		// TODO Auto-generated method stub		
	}

	public final void endBatch() 
	{
		// TODO Auto-generated method stub		
	}
	
	protected abstract Listener newListener(Listener listener);

}
