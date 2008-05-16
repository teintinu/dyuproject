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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dyuproject.util.Delim;
import dojox.cometd.Bayeux;
import dojox.cometd.Channel;
import dojox.cometd.Client;

/**
 * @author David Yu
 */

public class DefaultChannel implements Channel 
{
	
	protected static Log log = LogFactory.getLog(DefaultChannel.class);	
	private String _id;
	private DefaultChannel _parent;
	private ConcurrentMap<String, Channel> _subChannels = new ConcurrentHashMap<String, Channel>();
	private ConcurrentMap<String, Client> _clients = new ConcurrentHashMap<String, Client>();
	private boolean _removed = false;
	private int _depth = 0;
	
	DefaultChannel() 
	{
		_id = "/";
	}
	
	private DefaultChannel(String[] heirarchy, String channelId, DefaultChannel parent) 
	{		
		_parent = parent;
		_depth = parent.getDepth() + 1;		
		int idx = parent.getDepth()>0 ? parent.getId().length() + 1 : 1;
		_id = channelId.substring(0, idx) + heirarchy[parent.getDepth()];
		log.warn("new channel: " + _id + " depth: " + _depth);
	}

	public boolean remove() 
	{
		if(_removed)
			return _removed;
		_removed = true;		
		for(Channel ch : _subChannels.values()) 
			ch.remove();		
				
		boolean empty = _subChannels.isEmpty();
		if(!empty)
			log.warn("channels not entirely removed!");		
		
		for(Client cl : _clients.values()) 
			cl.unsubscribe(getId());			
		
		destroy();
		return _parent!=null ? _parent.removeSubChannel(getId()) && empty : empty;
	}
	
	public String getId() 
	{		
		return _id;
	}

	public void publish(Client fromClient, Object data, String msgId) 
	{
		Map<String, Object> message = new HashMap<String, Object>();
		message.put(Bayeux.DATA_FIELD, data);
		message.put(Bayeux.CHANNEL_FIELD, getId());
		message.put(CometdConstants.MSG_ID, msgId);
		message.put(CometdConstants.FROM_CLIENT, fromClient.getId());		
		for(Client cl : _clients.values())	
			cl.deliver(fromClient, message);	
	}

	public void subscribe(Client subscriber) 
	{
		_clients.put(subscriber.getId(), subscriber);					
	}

	public void unsubscribe(Client subscriber) 
	{
		_clients.remove(subscriber.getId());	
	}
	
	boolean removeSubChannel(String id) 
	{	
		return _subChannels.remove(id)!=null;
	}
	
	Channel getChannel(String channel) 
	{
		return getChannel(channel, true);
	}
	
	Channel getChannel(String channelId, boolean create) 
	{		
		String channel = channelId;
		if(channel.charAt(channel.length()-1)=='/') 
			throw new IllegalStateException("channel must not end with \"/\"");
		if(channel.charAt(0)=='/') 
			channel = channel.substring(1);
		if(channel.length()<1)
			return null;
		String[] heirarchy = Delim.SLASH.split(channel);
		return heirarchy.length > getDepth() ? resolveChannel(heirarchy, channelId, 
				this, create) : null;
	}
	
	private DefaultChannel resolveChannel(String[] heirarchy, String channelId, 
	        DefaultChannel parent, boolean create) 
	{		
		DefaultChannel subChannel = parent.getSubChannel(heirarchy[parent.getDepth()]);		
		if(subChannel!=null) 
		{
			return heirarchy.length>subChannel.getDepth() ? subChannel.resolveChannel(heirarchy, 
			        channelId, subChannel, create) : subChannel;
		}
		return create ? createChannel(heirarchy, channelId, parent) : null;
	}
	
	private DefaultChannel createChannel(String[] heirarchy, String channelId, DefaultChannel parent) 
	{		
		log.warn("creating subChannel: " + heirarchy[parent.getDepth()]);
		DefaultChannel channel = null;
		synchronized(parent) 
		{
			channel = parent.getSubChannel(heirarchy[parent.getDepth()]);
			if(channel==null) 
			{				
				channel = new DefaultChannel(heirarchy, channelId, parent);				
				parent.addSubChannel(heirarchy[parent.getDepth()], channel);
			}			
		}
		return heirarchy.length>channel.getDepth() ? channel.resolveChannel(heirarchy, channelId, 
				channel, true) : channel;
	}
	
	private void addSubChannel(String h, DefaultChannel channel) 
	{
		_subChannels.put(h, channel);
	}
	
	private DefaultChannel getSubChannel(String h) 
	{
		return (DefaultChannel)_subChannels.get(h);
	}
	
	Client getClient(String clientId) 
	{
		return _clients.get(clientId);
	}
	
	Collection<Client> getClients() 
	{
		return _clients.values();
	}
	
	public int getDepth() 
	{
		return _depth;
	}
	
	private void destroy() 
	{
		_clients.clear();
		_parent = null;
	}
	
	/* --------------------------------------------------------------- */
	
	public boolean isPersistent() 
	{		
		return false;
	}

	public void setPersistent(boolean persistent) 
	{
		
	}

    public int getSubscribers() 
    {        
        return _clients.size();
    }

}
