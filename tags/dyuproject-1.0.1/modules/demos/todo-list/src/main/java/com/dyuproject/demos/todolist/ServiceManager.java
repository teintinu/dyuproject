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

package com.dyuproject.demos.todolist;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.demos.todolist.dao.TodoDao;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.mvc.AuthController;
import com.dyuproject.demos.todolist.mvc.DefaultController;
import com.dyuproject.demos.todolist.mvc.OverviewController;
import com.dyuproject.demos.todolist.mvc.LoginController;
import com.dyuproject.demos.todolist.mvc.LogoutController;
import com.dyuproject.demos.todolist.mvc.TodosController;
import com.dyuproject.demos.todolist.mvc.UserFilter;
import com.dyuproject.demos.todolist.mvc.UsersController;
import com.dyuproject.web.mvc.AbstractFilter;
import com.dyuproject.web.mvc.Controller;
import com.dyuproject.web.mvc.FilterCollection;
import com.dyuproject.web.mvc.WebContext;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class ServiceManager extends AbstractFilter implements ServletContextListener
{
    
    private static final ThreadLocal<EntityManagerWrapper> __wrapper = new ThreadLocal<EntityManagerWrapper>();
    
    private EntityManagerFactory _factory;
    private WebContext _webContext;
    
    public static EntityManagerWrapper getCurrentEntityManagerWrapper()
    {
        return __wrapper.get();
    }

    
    protected void init()
    {        
        
    }
    
    public void contextDestroyed(ServletContextEvent event)
    {
        if(_factory!=null)
            _factory.close();        
    }

    public void contextInitialized(ServletContextEvent event)
    {
        _webContext = new WebContext();
        _factory = Persistence.createEntityManagerFactory("todo-list-persistence");
        System.err.println("FACTORY: " + _factory);
        
        TodoDao todoDao = new TodoDao();  
        UserDao userDao = new UserDao();
        todoDao.setServiceManager(this);
        userDao.setServiceManager(this);
        _webContext.addAttribute("todoDao", todoDao);
        _webContext.addAttribute("userDao", userDao);
        
        UserFilter userFilter = new UserFilter();
        
        FilterCollection userFilterChain = new FilterCollection();
        userFilterChain.addFilter(this);
        userFilterChain.addFilter(userFilter);
        
        TodosController todos = new TodosController();
        
        UsersController users = new UsersController();
        
        LoginController login = new LoginController();        
        
        OverviewController home = new OverviewController();
        
        AuthController auth = new AuthController();
        
        LogoutController logout = new LogoutController();
        
        todos.setFilter(this);
        users.setFilter(this);        
        home.setFilter(userFilterChain);        
        
        _webContext.setDefaultController(new DefaultController());
        _webContext.setControllers(new Controller[]{
                todos,
                users,
                login,
                logout,
                home,
                auth
        });        
        
        event.getServletContext().setAttribute(WebContext.class.getName(), _webContext);       
    }

    public void postHandle(boolean handled, String mime,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        EntityManagerWrapper wrapper = getCurrentEntityManagerWrapper();
        __wrapper.set(null);
        if(wrapper!=null)
            wrapper.close();
    }

    public boolean preHandle(String mime, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {        
        return true;
    }
    
    public EntityManager getEntityManager()
    {
        EntityManagerWrapper wrapper = getCurrentEntityManagerWrapper();
        if(wrapper==null)
        {
            wrapper = new EntityManagerWrapper(_factory.createEntityManager());
            __wrapper.set(wrapper);
        }
        return wrapper.getEntityManager();
    }
    
    public static class EntityManagerWrapper
    {
        private String _errorMessage;
        private EntityManager _entityManager;
        private Feedback _feedback;
        
        private EntityManagerWrapper(EntityManager entityManager)
        {
            _entityManager = entityManager;
        }
        
        public EntityManager getEntityManager()
        {
            return _entityManager;
        }
        
        public void setErrorMessage(String errorMessage)
        {
            _errorMessage = errorMessage;
        }
        
        public String getErrorMessage()
        {
            return _errorMessage;
        }
        
        public void setFeedback(Feedback feedback)
        {
            _feedback = feedback;
        }
        
        public Feedback getFeedback()
        {
            return _feedback;
        }
        
        private void close()
        {
            _errorMessage = null;
            _feedback = null;
            _entityManager.close();
            _entityManager = null;
        }
        
    }

}
