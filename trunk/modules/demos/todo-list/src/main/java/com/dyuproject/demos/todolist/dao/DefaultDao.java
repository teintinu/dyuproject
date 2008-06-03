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

package com.dyuproject.demos.todolist.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.ServiceManager;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class DefaultDao
{
    
    protected ServiceManager _serviceManager;
    
    public void setServiceManager(ServiceManager serviceManager)
    {
        _serviceManager = serviceManager;
    }
    
    public ServiceManager getServiceManager()
    {
        return _serviceManager;
    }
    
    public static void setCurrentErrorMessage(String errorMessage)
    {
        ServiceManager.getCurrentEntityManagerWrapper().setErrorMessage(errorMessage);
    }
    
    public static void setCurrentFeedback(Feedback feedback)
    {
        ServiceManager.getCurrentEntityManagerWrapper().setFeedback(feedback);
    }
    
    public static String getCurrentErrorMessage()
    {
        return ServiceManager.getCurrentEntityManagerWrapper().getErrorMessage();
    }
    
    public static Feedback getCurrentFeedback()
    {
        return ServiceManager.getCurrentEntityManagerWrapper().getFeedback();
    }
    
    protected boolean persist(Object obj) throws Exception
    {
        EntityManager em = _serviceManager.getEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected boolean merge(Object obj) throws Exception
    {
        EntityManager em = _serviceManager.getEntityManager();
        em.getTransaction().begin();
        em.merge(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected boolean remove(Object obj) throws Exception
    {
        EntityManager em = _serviceManager.getEntityManager();        
        em.getTransaction().begin();
        em.remove(obj);
        em.getTransaction().commit();
        return true;
    }
    
    protected List<?> createQuery(String query)
    {
        long start = System.currentTimeMillis();        
        List<?> result = _serviceManager.getEntityManager().createQuery(query).getResultList();
        System.err.println("Elapsed: " + (System.currentTimeMillis()-start) + " ms");
        return result;
    }
    
    protected List<?> createQuery(String query, Object[] params)
    {
        long start = System.currentTimeMillis();        
        Query q = _serviceManager.getEntityManager().createQuery(query);
        for(int i=0; i<params.length; i++)
            q.setParameter(i+1, params[i]);
        List<?> result = q.getResultList();
        System.err.println("Elapsed: " + (System.currentTimeMillis()-start) + " ms");
        return result;
    }
    
    protected List<?> createNamedQuery(String name)
    {
        return _serviceManager.getEntityManager().createNamedQuery(name).getResultList();
    }
    
    protected <T> T find(Class<T> clazz, Object id)
    {
        return _serviceManager.getEntityManager().find(clazz, id);
    }

}
