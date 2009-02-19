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
import com.dyuproject.web.rest.annotation.Consume;
import com.dyuproject.web.rest.consumer.SimpleParameterConsumer;
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
        
        Todo todo = _todoDao.get(Long.valueOf(rc.getPathElement(1)));
        if(todo==null)
        {
            response.sendError(404);
            return;
        }
        boolean updated = rc.getConsumer().merge(todo, rc);
        if(updated)
            updated = TodoDao.executeUpdate();
        
        if(updated)
        {
            String sub = request.getParameter("sub");
            if(sub!=null)
            {
                String[] pi = rc.getPathInfo();
                int len = pi.length - Integer.parseInt(sub);
                StringBuilder buffer = new StringBuilder().append(request.getContextPath());
                for(int i=0; i<len; i++)
                    buffer.append('/').append(pi[i]);
                response.sendRedirect(buffer.toString());
                return;
            }
            request.setAttribute(Constants.MSG, Feedback.TODO_UPDATED.getMsg());
        }
        else
            request.setAttribute(Constants.MSG, Feedback.COULD_NOT_UPDATE_TODO.getMsg());
   
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
          
        Todo todo = (Todo)rc.getConsumer().getConsumedObject(rc);
        todo.setUser(user);
        boolean created = _todoDao.create(todo);
        
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
        if(deleted)
        {            
            String sub = request.getParameter("sub");
            if(sub!=null)
            {
                String[] pi = rc.getPathInfo();
                int len = pi.length - Integer.parseInt(sub);
                StringBuilder buffer = new StringBuilder().append(request.getContextPath());
                for(int i=0; i<len; i++)
                    buffer.append('/').append(pi[i]);
                response.sendRedirect(buffer.toString());
            }
            else
                response.sendRedirect(request.getHeader("Referer"));
        }
        else
        {
            request.setAttribute(Constants.MSG, Feedback.COULD_NOT_DELETE_TODO.getMsg());        
            dispatchToView(todo, request, response);
        }
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
    @Consume(
            consumers={SimpleParameterConsumer.class}, pojoClass=Todo.class,
            fieldParams="id.included=false&completed.included=false&user.included=false&content.required=false",
            initParams="spc.dispatch_uri=todos/form&consume_type=map&request_attributes=action:Edit"
    )
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
    @Consume(
            consumers={SimpleParameterConsumer.class}, pojoClass=Todo.class, 
            fieldParams="id.included=false&completed.included=false&user.included=false&content.required=false",
            initParams="spc.dispatch_uri=todos/form&request_attributes=action:New"
    )
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
            updated = TodoDao.executeUpdate();
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
    
    @HttpResource(location="/todos/completed")
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
