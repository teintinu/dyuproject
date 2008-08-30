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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.web.mvc.AbstractFilter;

/**
 * @author David Yu
 * @created Aug 30, 2008
 */

public class EntityManagerManager extends AbstractFilter
{
    
    private static final ThreadLocal<EntityManager> __entityManager = new ThreadLocal<EntityManager>();
    
    public static EntityManager getCurrentEntityManager()
    {
        return __entityManager.get();
    }  
    
    private String _persistenceUnitName;
    private EntityManagerFactory _entityManagerFactory;  
    
    public EntityManagerManager()
    {
        
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
            __entityManager.set(manager);
        }
        return manager;
    }

    protected void init()
    {
        if(getPersistenceUnitName()==null)
            throw new IllegalStateException("*persistenceUnitName* not set.");
        _entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName());       
    }
    
    public void destroy()
    {
        if(_entityManagerFactory!=null)
            _entityManagerFactory.close();
    }

    public boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {        
        return true;
    }

    public void postHandle(boolean handled, String mime,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        EntityManager manager = getCurrentEntityManager();
        __entityManager.set(null);
        if(manager!=null)
            manager.close();
    }

}
