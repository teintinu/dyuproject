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

package com.dyuproject.ext.jpa;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.dyuproject.web.rest.AbstractLifeCycle;
import com.dyuproject.web.rest.Interceptor;
import com.dyuproject.web.rest.RequestContext;

/**
 * @author David Yu
 * @created Aug 30, 2008
 */

public final class EntityManagerManager extends AbstractLifeCycle implements Interceptor, Filter
{
    
    private static final ContextLocal __context = new ContextLocal();
    
    public static EntityManager getCurrentEntityManager()
    {
        return __context.get().getEntityManager();
    }
    
    public static Object getAttribute(String key)
    {
        return __context.get().getAttribute(key);
    }
    
    public static void setAttribute(String key, Object value)
    {
        __context.get().setAttribute(key, value);
    }
    
    private String _persistenceUnitName;
    private EntityManagerFactory _entityManagerFactory;  
    
    public EntityManagerManager()
    {
        
    }
    
    public EntityManagerManager(String persistenceUnitName)
    {
        this(persistenceUnitName, true);
    }
    
    public EntityManagerManager(String persistenceUnitName, boolean init) 
    {
        setPersistenceUnitName(persistenceUnitName);
        if(init)
            init();
    }
    
    public void setPersistenceUnitName(String persistenceUnitName)
    {
        _persistenceUnitName = persistenceUnitName;
    }
    
    public String getPersistenceUnitName()
    {
        return _persistenceUnitName;
    }    
    
    public EntityManager getEntityManager()
    {
        EntityManager manager = getCurrentEntityManager();
        if(manager==null)
        {
            manager = _entityManagerFactory.createEntityManager();
            __context.get().setEntityManager(manager);
        }
        return manager;
    }

    protected void init()
    {        
        if(_entityManagerFactory==null)
        {
            if(getPersistenceUnitName()==null)
                throw new IllegalStateException("*persistenceUnitName* not set.");
            _entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName());
        }
    }
    
    public void destroy()
    {
        if(_entityManagerFactory!=null)
            _entityManagerFactory.close();
    }

    public boolean preHandle(RequestContext requestContext) 
    throws ServletException, IOException
    {        
        return true;
    }

    public void postHandle(boolean handled, RequestContext requestContext)
    {        
        __context.get().clear();
    }

    public void doFilter(ServletRequest sreq, ServletResponse sresp,
            FilterChain chain) throws IOException, ServletException
    {
        try
        {
            chain.doFilter(sreq, sresp);
        }
        finally
        {            
            __context.get().clear();
        }        
    }

    public void init(FilterConfig config) throws ServletException
    {
        if(_persistenceUnitName==null)
        {
            _persistenceUnitName = config.getInitParameter("persistenceUnitName");
            if(_persistenceUnitName==null)
                throw new IllegalStateException("*persistenceUnitName* init-param not set.");
        }
        
        init();        
    }
    
    static class Context
    {
        
        private EntityManager _entityManager;
        private Map<String,Object> _attributes = new HashMap<String,Object>(3);
        
        public EntityManager getEntityManager()
        {
            return _entityManager;
        }
        
        void setEntityManager(EntityManager entityManager)
        {
            _entityManager = entityManager;
        }        
        
        void clear()
        {
            _attributes.clear();
            if(_entityManager!=null)
                _entityManager.close();
            _entityManager = null;
        }
        
        public void setAttribute(String key, Object value)
        {
            _attributes.put(key, value);
        }
        
        public Object getAttribute(String key)
        {
            return _attributes.get(key);
        }
        
    }
    
    private static class ContextLocal extends ThreadLocal<Context>
    {
        protected Context initialValue()
        {
            return new Context();
        }
    }

}
