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

package com.dyuproject.web.cometd.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;
import com.dyuproject.web.RequestUtil;
import com.dyuproject.util.FormatConverter;
import com.dyuproject.util.JSONConverter;
import com.dyuproject.web.cometd.Advice;
import com.dyuproject.web.cometd.BasicListener;
import com.dyuproject.web.cometd.MessageListener;
import dojox.cometd.Bayeux;
import dojox.cometd.Client;
import dojox.cometd.Listener;

/**
 * @author David Yu
 */

public abstract class AbstractCometdServlet extends HttpServlet 
{
    
    private static Log log = LogFactory.getLog(AbstractCometdServlet.class);
    
    protected static final int GET = "GET".hashCode();
    protected static final int POST = "POST".hashCode();
    protected static final int SUBSCRIBE = "subscribe".hashCode();
    protected static final int UNSUBSCRIBE = "unsubscribe".hashCode();
    protected static final int PUBLISH = "publish".hashCode();
    protected static final int CONNECT = "connect".hashCode();
    protected static final int RECONNECT = "reconnect".hashCode();
    protected static final int DISCONNECT = "disconnect".hashCode();
    
    protected static long __count = 0;
    protected static final String LISTENER_ATTR = Listener.class.getName();
    protected static final String CALLBACK = "callback";
    protected static final String ACTION = "action";
    
    private List<MessageListener> _dcCandidate;
    
    protected static char[] __letters = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t','u','v','y','x','y','z'};
    
    private Random _random = new Random();
    private Timer _timer;
    private Bayeux _bayeux;    
    private long _timeout = 60000;
    private int _sessionTimeout = 0;
    protected String _cookieName;
    protected String _cookieUri;
    protected int _cookieHash = 0;    
    
    public void init() throws ServletException
    {
        String timeout = getInitParameter("timeout");
        if(timeout!=null)
            _timeout = Long.parseLong(timeout);
        
        _sessionTimeout = 60 * (int)_timeout/1000;        
        _dcCandidate = new ArrayList<MessageListener>();
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask(){            
            public void run() 
            {                
                checkDisconnected();
            }            
        }, _timeout, _timeout*2);
        _bayeux = initBayeux(getServletContext());
        log.warn("started.");
    }
    
    private void checkDisconnected() 
    {
        long now = System.currentTimeMillis();
        long dcTimeout = _timeout*2;
        synchronized(_dcCandidate)
        {
            for(Iterator<MessageListener> iter = _dcCandidate.iterator(); iter.hasNext();)
            {
                MessageListener listener = iter.next();
                if(now-listener.getLastConnect()>dcTimeout)
                {
                    if(listener.getClient().getId()!=null)
                    {
                        listener.getClient().remove(true);                        
                        log.warn("client disconnected.");
                    }
                    iter.remove();                    
                }                
            }            
        }
    }
    
    private void addCandidate(MessageListener listener)
    {        
        synchronized(_dcCandidate)
        {
            _dcCandidate.add(listener);
        }
    }
    
    public void destroy()
    {
        _timer.cancel();
        synchronized(_dcCandidate)
        {
            _dcCandidate.clear();
        }        
    }
    
    public final void service(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        MessageListener previous = (MessageListener)request.getAttribute(LISTENER_ATTR);
        if(previous!=null)
        {
            String cb = request.getParameter(CALLBACK);
            if(previous.hasNew())            
                doUpdate(response, previous, JSONConverter.getInstance(), cb);            
            else            
                doResponse(response, Advice.reconnect(cb));                           
            return;
        }
        int hash = request.getMethod().hashCode();          
        if(hash!=POST && hash!=GET)
            return; 
        
        String id = getClientId(request);
        Client client = id!=null ? _bayeux.getClient(id) : null;
        if(client==null)
        {
            id = newClientId(request);
            client = _bayeux.newClient(id, newListener(request, id));
            updateClient(response, id);
            addCandidate((MessageListener)client.getListener());
            doResponse(response, JSONConverter.getInstance().toString(client, 
                    request.getParameter(CALLBACK)).getBytes());
            return;
        }
        Map<String, String> params = RequestUtil.getParams(request);
        String callback = params.remove(CALLBACK);
        String actionParam = params.remove(ACTION);
        if(actionParam==null)
        {
            doResponse(response, Advice.noReconnect(callback));
            return;
        }
        int action = actionParam.hashCode();
        MessageListener listener = (MessageListener)client.getListener();
        if(action==RECONNECT)
        {
            listener.updateConnect();
            if(listener.hasNew())            
                doUpdate(response, listener, JSONConverter.getInstance(), callback);                           
            else
            {
                Continuation continuation = ContinuationSupport.getContinuation(request, null);
                request.setAttribute(LISTENER_ATTR, listener);
                listener.setContinuation(continuation);
                continuation.suspend(_timeout);                  
            }
            return;
        }
        String channel = request.getPathInfo();
        if(action==PUBLISH)
        {
            client.publish(channel, params, null);
            doResponse(response, Advice.noReconnect(callback));
            return;
        }
        if(action==SUBSCRIBE)
        {
            client.subscribe(channel);
            doResponse(response, Advice.noReconnect(callback));
            return;
        }
        if(action==UNSUBSCRIBE)
        {
            client.unsubscribe(channel);
            doResponse(response, Advice.noReconnect(callback));
            return;
        }
        
        /* ----------------------------------------------------------------------- */
        
        // happens when page is refreshed or client exits then returns
        if(action==CONNECT)
        {            
            listener.resumeExisting(null);
            doResponse(response, JSONConverter.getInstance().toString(client, callback).getBytes());            
            return;
        }
        // call to client.disconnect on client side
        if(action==DISCONNECT)
        {
            synchronized(_dcCandidate)
            {
                if(_dcCandidate.remove(listener))
                    listener.getClient().remove(false);                
            }
            doResponse(response, Advice.noReconnect(callback));
        }
    }
    
    protected void doResponse(HttpServletResponse response, byte[] reply) throws IOException
    {
        response.setContentType(JSONConverter.getInstance().getContentType());
        //response.getOutputStream().write(reply);
        ServletOutputStream out = response.getOutputStream();
        out.write(reply);
        //out.flush();
        //out.close();
    }    
  
    public Bayeux getBayeux()
    {
        return _bayeux;
    }    

    public long getTimeout()
    {
        return _timeout;
    }
    
    protected String getClientId(HttpServletRequest request)
    {
        if(_cookieName==null)
        {
            String cp = getServletContext().getContextPath();
            _cookieUri = cp.length()>1 ? cp.concat(request.getServletPath()) : 
                request.getServletPath();
            _cookieName = "root".concat(_cookieUri.replace('/', '.'));
            _cookieHash = _cookieName.hashCode();
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
        {
            for(int i=0; i<cookies.length; i++)
            {
                if(_cookieHash==cookies[i].getName().hashCode())
                    return cookies[i].getValue();
            }
        }
        return null;
    }
    
    private void updateClient(HttpServletResponse response, String id)
    {
        Cookie cookie = new Cookie(_cookieName, id);
        cookie.setPath(_cookieUri);
        cookie.setMaxAge(_sessionTimeout);
        response.addCookie(cookie);
    }
    
    protected String newClientId(HttpServletRequest request)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append(__count++);
        buffer.append(__letters[_random.nextInt(__letters.length)]);
        buffer.append(__letters[_random.nextInt(__letters.length)]);
        buffer.append(__letters[_random.nextInt(__letters.length)]);
        buffer.append(System.currentTimeMillis());
        buffer.append(__letters[_random.nextInt(__letters.length)]);        
        return buffer.toString();
    }
    
    protected abstract Bayeux initBayeux(ServletContext sc) throws ServletException;
    
    protected abstract void doUpdate(HttpServletResponse response, BasicListener listener, 
            FormatConverter converter, String callback) throws IOException;    
    
    protected abstract Listener newListener(HttpServletRequest request, String ids);    
    
}
