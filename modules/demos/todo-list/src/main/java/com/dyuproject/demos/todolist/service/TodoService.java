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

package com.dyuproject.demos.todolist.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jra.Delete;
import org.codehaus.jra.Get;
import org.codehaus.jra.HttpResource;
import org.codehaus.jra.Post;
import org.codehaus.jra.Put;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.dao.TodoDao;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.model.Todo;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.web.rest.RequestContext;
import com.dyuproject.web.rest.service.AbstractService;

/**
 * @author David Yu
 * @created Dec 6, 2008
 */

public class TodoService extends AbstractService
{
    
    private TodoDao _todoDao;
    private UserDao _userDao;

    @Override
    protected void init()
    {
        _todoDao = (TodoDao)getWebContext().getAttribute("todoDao");
        _userDao = (UserDao)getWebContext().getAttribute("userDao");
    }
    
    @HttpResource(location="/todos")
    @Get
    public void get(RequestContext rc) throws IOException, ServletException
    {        
        dispatchToView(_todoDao.get(), rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/todos/$")
    @Get
    public void getById(RequestContext rc) throws IOException, ServletException
    {
        
        String id = rc.getPathElement(1);
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo==null)
        {
            rc.getResponse().sendError(404);
            return;
        }
        dispatchToView(todo, rc.getRequest(), rc.getResponse());
    }    
    
    @HttpResource(location="/users/$/todos")
    @Get
    public void getByUserId(RequestContext rc) throws IOException, ServletException
    {
        String id = rc.getPathElement(1);
        dispatchToView(_todoDao.getByUser(Long.valueOf(id)), rc.getRequest(), rc.getResponse());
    }    
    
    @HttpResource(location="/todos/$")
    @Delete
    public void deleteById(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo==null)
        {
            rc.getResponse().sendError(404);
            return;
        }
        
        boolean deleted = _todoDao.delete(todo);
        
        request.setAttribute(Constants.MSG, deleted ? Feedback.TODO_DELETED.getMsg() : 
            Feedback.COULD_NOT_DELETE_TODO.getMsg());
        
        dispatchToView(todo, request, response);
    }
    
    @HttpResource(location="/todos/$")
    @Put
    public void updateById(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);        
        boolean completed = false;
        
        String title = request.getParameter(Constants.TITLE);
        String content = request.getParameter(Constants.CONTENT);
        String completedParam = request.getParameter(Constants.COMPLETED);
        
        if(completedParam!=null)
            completed = Boolean.parseBoolean(completedParam);
        
        boolean updated = false;
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo!=null)
        {
            if(title!=null)
            {
                updated = true;
                todo.setTitle(title);
            }
            if(content!=null)
            {
                updated = true;
                todo.setContent(content);
            }
            if(completed!=todo.isCompleted() && completedParam!=null)
            {
                updated = true;
                todo.setCompleted(completed);
            }
            updated =  updated ? _todoDao.update(todo) : false;
        }
        
        request.setAttribute(Constants.MSG, updated ? Feedback.TODO_UPDATED.getMsg() : 
            Feedback.COULD_NOT_UPDATE_TODO.getMsg());            
        dispatchToFormView(todo, request, response); 
    }
    
    @HttpResource(location="/users/$/todos")
    @Post
    public void createByUserId(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String userId = rc.getPathElement(1);        
        User user = _userDao.get(Long.valueOf(userId));        
        if(user==null)
        {
            response.sendError(404);
            return;
        }
        
        String title = request.getParameter(Constants.TITLE);        
        String content = request.getParameter(Constants.CONTENT);
        
        boolean created = false;        
        Todo todo = null;
        
        if(title!=null && title.length()>0)
        {
            todo = new Todo();
            todo.setTitle(title);
            todo.setContent(content);
            todo.setUser(user);            
            created = _todoDao.create(todo);
        }
        
        if(created)
        {
            /*request.setAttribute(Constants.MSG, Feedback.TODO_CREATED.getMsg());
            request.setAttribute(Constants.USER, user);
            response.setContentType(Constants.TEXT_HTML);
            getWebContext().getJSPDispatcher().dispatch("users/id.jsp", 
                    request, response);*/
            response.sendRedirect(request.getContextPath() + "/users/" + user.getId() + "/todos");
        }
        else
        {
            request.setAttribute(Constants.MSG, Feedback.COULD_NOT_CREATE_TODO.getMsg());
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            dispatchToFormView(todo, request, response);
        }
    }
    
    /* ====================== VIEW VERBS(GET) and FORMS(POST) ====================== */
        
    @HttpResource(location="/todos/$/delete")
    @Get
    public void verb_delete(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_DELETE);
        deleteById(rc);
    }
    
    @HttpResource(location="/todos/$/edit")
    @Get
    public void verb_edit(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo==null)
            request.setAttribute(Constants.MSG, Feedback.TODO_NOT_FOUND.getMsg());
        else
            request.setAttribute(Constants.TODO, todo);
        request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
        dispatchToFormView(todo, request, response);
    }
    
    @HttpResource(location="/todos/$/edit")
    @Post
    public void form_edit(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
        updateById(rc);
    }

    @HttpResource(location="/users/$/todos/new")
    @Get
    public void verb_new(RequestContext rc) throws IOException, ServletException
    {
        rc.getRequest().setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
        dispatchToFormView((Todo)null, rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/users/$/todos/new")
    @Post
    public void form_new(RequestContext rc) throws IOException, ServletException
    {
        createByUserId(rc);
    }
    
    @HttpResource(location="/todos/$/complete")
    @Get
    public void verb_complete(RequestContext rc) throws IOException, ServletException
    {
        HttpServletRequest request = rc.getRequest();
        HttpServletResponse response = rc.getResponse();
        
        String id = rc.getPathElement(1);
        
        boolean updated = false;
        
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo!=null && !todo.isCompleted())
        {
            todo.setCompleted(true);
            updated = _todoDao.update(todo);
        }
        
        if(!updated)
            request.setAttribute(Constants.MSG, Feedback.COULD_NOT_UPDATE_TODO.getMsg());            
        String referer = request.getHeader("Referer");
        if(referer==null)                
            dispatchToView(todo, request, response);
        else
            response.sendRedirect(referer);
    }
    
    /* ================================== FILTERS ================================== */
    
    @HttpResource(location="/users/$/todos/completed")
    @Get
    public void filter_user_completed(RequestContext rc) throws IOException, ServletException
    {
        String userId = rc.getPathElement(1);
        dispatchToView(_todoDao.getByUserAndStatus(Long.valueOf(userId), true), rc.getRequest(), 
                rc.getResponse());
    }
    
    @HttpResource(location="/users/$/todos/current")
    @Get
    public void filter_user_current(RequestContext rc) throws IOException, ServletException
    {
        String userId = rc.getPathElement(1);
        dispatchToView(_todoDao.getByUserAndStatus(Long.valueOf(userId), false), rc.getRequest(), 
                rc.getResponse());
    }
    
    @HttpResource(location="/todos/current")
    @Get
    public void filter_completed(RequestContext rc) throws IOException, ServletException
    {
        dispatchToView(_todoDao.getByStatus(true), rc.getRequest(), rc.getResponse());
    }
    
    @HttpResource(location="/todos/current")
    @Get
    public void filter_current(RequestContext rc) throws IOException, ServletException
    {
        dispatchToView(_todoDao.getByStatus(false), rc.getRequest(), rc.getResponse());
    }
    
    /* ============================================================================= */
    
    private void dispatchToFormView(Todo todo, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.TODO, todo);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("todos/form.jsp", request, response);
    }
    
    private void dispatchToView(Todo todo, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.TODO, todo);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("todos/id.jsp", request, response);
    }

    private void dispatchToView(List<?> todos, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        request.setAttribute(Constants.TODOS, todos);
        response.setContentType(Constants.TEXT_HTML);
        getWebContext().getJSPDispatcher().dispatch("todos/list.jsp", request, 
                response);
    }

}