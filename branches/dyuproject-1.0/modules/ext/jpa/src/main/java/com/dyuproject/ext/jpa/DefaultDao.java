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
    
    protected EntityManagerManager _entityManagerManager;
    
    public void setEntityManagerManager(EntityManagerManager entityManagerManager)
    {
        _entityManagerManager = entityManagerManager;
    }
    
    public EntityManagerManager setEntityManagerManager()
    {
        return _entityManagerManager;
    }
    
    protected boolean persist(Object obj) throws Exception
    {
        EntityManager em = _entityManagerManager.getEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected boolean merge(Object obj) throws Exception
    {
        EntityManager em = _entityManagerManager.getEntityManager();
        em.getTransaction().begin();
        em.merge(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected boolean remove(Object obj) throws Exception
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
    
    protected <T> T find(Class<T> clazz, Object id)
    {
        return _entityManagerManager.getEntityManager().find(clazz, id);
    }
}
