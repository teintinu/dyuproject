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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import dojox.cometd.Bayeux;
import dojox.cometd.Channel;
import dojox.cometd.Client;
import dojox.cometd.DataFilter;
import dojox.cometd.Listener;
import dojox.cometd.SecurityPolicy;

/**
 * @author David Yu
 */

public class DefaultBayeux implements Bayeux 
{
	
	private static Log log = LogFactory.getLog(DefaultBayeux.class);
	public static final Class[] clientArgs = new Class[]{DefaultBayeux.class, 
		String.class, Listener.class};
	private DefaultChannel _root = new DefaultChannel();
	private ConcurrentMap<String, Client> _clients = new ConcurrentHashMap<String, Client>();
	private List<AbstractClient> _recycled = new ArrayList<AbstractClient>();
	private Constructor _clientConstructor;
	private boolean _customClient = false;
	private String _name;
	private String _sig;
	
	public DefaultBayeux() 
	{
		this(null);
	}
	
	public DefaultBayeux(String n) 
	{		
		_name = n!=null ? n : "default";
		_sig = _name + String.valueOf(System.currentTimeMillis());
	}
	
	public DefaultBayeux(String name, Class clientClazz) 
	{
		this(name);
		setClientClass(clientClazz);
	}
	
	public String getName() 
	{
		return _name;
	}
	
	private void setClientClass(Class clientClazz) 
	{
		if(clientClazz!=null && AbstractClient.class.isAssignableFrom(clientClazz)) 
		{			
			_customClient = true;
			try 
			{
				_clientConstructor = clientClazz.getDeclaredConstructor(clientArgs);				
			} 
			catch (Exception e) 
			{
				_customClient = false;
				e.printStackTrace();
			} 
		}	
	}	
	
	final boolean removeClient(Client client) 
	{
		return _clients.remove(client.getId())!=null;
	}
	
	public final Client newClient(String id) 
	{
		return newClient(id, null);
	}

	public final Client newClient(String idprefix, Listener listener) 
	{
		return newClient(idprefix, listener, false);
	}
	
	public final Client newClient(String idprefix, Listener listener, boolean custom) 
	{
		if(idprefix==null || _clients.containsKey(idprefix))
			return null;		
		if(custom && _customClient) 
		{			
			Client client = null;
			try 
			{
				client = (Client)_clientConstructor.newInstance(new Object[]{this, 
						idprefix, listener});								
			} 
			catch(Exception e) 
			{
				client = null;
				e.printStackTrace();
				return defaultClient(idprefix, listener);			
			}			
			_clients.put(client.getId(), client);
			log.warn("new custom client.");
			return client;
		}
		return defaultClient(idprefix, listener);
	}
	
	public final Client newClient(String idprefix, Listener listener, Class clientClazz) 
	{
		if(idprefix==null || _clients.containsKey(idprefix))
			return null;		
		if(clientClazz!=null && AbstractClient.class.isAssignableFrom(clientClazz)) 
		{			
			Client client = null;
			Constructor constructor = null;
			try 
			{
				constructor = clientClazz.getDeclaredConstructor(clientArgs);
				client = (Client)constructor.newInstance(new Object[]{this, 
						idprefix, listener});
			} 
			catch(Exception e) 
			{
				client = null;
				e.printStackTrace();
				return null;
			}
			_clients.put(client.getId(), client);
			log.warn("new custom client.");
			return client;
		}		
		return null;
	}
	
    public void deliver(Client fromClient, Client toClient, String channel, Map<String, Object> msg) 
    {
        msg.put("from", fromClient.getId());
        toClient.deliver(fromClient, msg);   
    }
	
	private Client defaultClient(String idprefix, Listener listener) 
	{
	    AbstractClient client = null;
	    if(_recycled.isEmpty())	    
	        client = new DefaultClient(this, idprefix, listener);
	    else
	    {
	        synchronized(_recycled)
	        {
	            client = _recycled.remove(0);
	        }
	        client.setId(idprefix);
	        Listener cl = client.getListener();
	        if(cl instanceof DefaultListener)
	            ((DefaultListener)cl).setListener(listener);
	        else
	            client.setListener(listener);
	        log.warn("recycled.");
	    }	    
        _clients.put(client.getId(), client);
        log.warn("new client.");
	    return client;
	}

	public final Client getClient(String clientId) 
	{		
		return _clients.get(clientId);
	}

	public final Channel getChannel(String channelId, boolean create) 
	{		
		return _root.getChannel(channelId, create);
	}

	public final void publish(Client fromClient, String toChannel, Object data, String msgId) 
	{
		_root.getChannel(toChannel).publish(fromClient, data, msgId);		
	}

	public final void subscribe(String toChannel, Client subscriber) 
	{
		_root.getChannel(toChannel).subscribe(subscriber);		
	}

	public final void unsubscribe(String toChannel, Client subscriber) 
	{
		_root.getChannel(toChannel).unsubscribe(subscriber);		
	}

	public final boolean hasChannel(String channel) 
	{	
		return _root.getChannel(channel)!=null;
	}
	
	/* --------------------------------------------------------------- */

	public final void addFilter(String channels, DataFilter filter) 
	{
		// TODO Auto-generated method stub
		
	}

	public final void removeFilter(String channels, DataFilter filter) 
	{
		// TODO Auto-generated method stub
		
	}

	public final SecurityPolicy getSecurityPolicy() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public final void setSecurityPolicy(SecurityPolicy securityPolicy) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public String toString() 
	{
		return _sig;
	}
	
	public void recycle(AbstractClient client)
	{
	    synchronized(_recycled)
	    {
	        _recycled.add(client);
	    }
	}

}
