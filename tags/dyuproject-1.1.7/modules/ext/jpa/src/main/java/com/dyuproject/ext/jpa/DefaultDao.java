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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author David Yu
 * @created Aug 30, 2008
 */

public class DefaultDao
{
    
    public static EntityManager getCurrentEntityManager()
    {
        return EntityManagerManager.getCurrentEntityManager();
    }
    
    protected EntityManagerManager _entityManagerManager;
    
    public void setEntityManagerManager(EntityManagerManager entityManagerManager)
    {
        _entityManagerManager = entityManagerManager;
    }
    
    public EntityManagerManager setEntityManagerManager()
    {
        return _entityManagerManager;
    }
    
    public static void begin()
    {
        EntityManagerManager.getCurrentEntityManager().getTransaction().begin();
    }
    
    public static void commit()
    {
        EntityManagerManager.getCurrentEntityManager().getTransaction().commit();        
    }
    
    public static void close()
    {
        EntityManagerManager.getCurrentEntityManager().close();
    }
    
    public static void beginAndCommit()
    {
        EntityManager em = getCurrentEntityManager();
        if(!em.getTransaction().isActive())
            em.getTransaction().begin();

        em.getTransaction().commit();
    }
    
    public static void setAttribute(String key, Object value)
    {
        EntityManagerManager.setAttribute(key, value);
    }
    
    public static Object getAttribute(String key)
    {
        return EntityManagerManager.getAttribute(key);
    }
    
    public boolean persist(Object obj)
    {
        EntityManager em = _entityManagerManager.getEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
        return true;
    }
    
    public Object merge(Object obj)
    {
        Object managed = null;
        EntityManager em = _entityManagerManager.getEntityManager();
        em.getTransaction().begin();
        managed = em.merge(obj);
        em.getTransaction().commit();
        return managed;
    }
    
    public boolean remove(Object obj)
    {
        EntityManager em = _entityManagerManager.getEntityManager();        
        em.getTransaction().begin();
        em.remove(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected List<?> createQuery(String query)
    {        
        return _entityManagerManager.getEntityManager().createQuery(query).getResultList();
    }
    
    protected List<?> createQuery(String query, Object[] params)
    {     
        Query q = _entityManagerManager.getEntityManager().createQuery(query);
        for(int i=0; i<params.length; i++)
            q.setParameter(i+1, params[i]);
        return q.getResultList();
    }
    
    protected List<?> createNamedQuery(String name)
    {
        return _entityManagerManager.getEntityManager().createNamedQuery(name).getResultList();
    }
    
    protected List<?> createNamedQuery(String name, Object[] params)
    {     
        Query q = _entityManagerManager.getEntityManager().createNamedQuery(name);
        for(int i=0; i<params.length; i++)
            q.setParameter(i+1, params[i]);
        return q.getResultList();
    }    
    
    public <T> T find(Class<T> clazz, Object id)
    {
        return _entityManagerManager.getEntityManager().find(clazz, id);
    }
    
    public <T> T findAndBegin(Class<T> clazz, Object id)
    {
        EntityManager em = _entityManagerManager.getEntityManager();
        T t = _entityManagerManager.getEntityManager().find(clazz, id);
        if(t!=null)
            em.getTransaction().begin();
        return t;
    }
}
