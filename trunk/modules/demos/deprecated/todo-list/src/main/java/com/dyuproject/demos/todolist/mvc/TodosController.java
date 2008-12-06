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

package com.dyuproject.demos.todolist.mvc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dyuproject.demos.todolist.Constants;
import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.dao.TodoDao;
import com.dyuproject.demos.todolist.dao.UserDao;
import com.dyuproject.demos.todolist.model.Todo;
import com.dyuproject.demos.todolist.model.User;
import com.dyuproject.util.format.JSONConverter;
import com.dyuproject.util.format.XMLConverter;
import com.dyuproject.web.rest.mvc.controller.CRUDController;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class TodosController extends CRUDController
{
    
    public static final String IDENTIFIER = "todos";
    public static final String IDENTIFIER_ATTR = "todos.verbOrId";    
    
    private TodoDao _todoDao;
    private UserDao _userDao;
    
    public TodosController()
    {
        setIdentifier(IDENTIFIER);
        setIdentifierAttribute(IDENTIFIER_ATTR);
        setAllowMethodOverride(true);
    }   
    
    protected void init()
    {
        super.init();
        _todoDao = (TodoDao)getWebContext().getAttribute("todoDao");
        _userDao = (UserDao)getWebContext().getAttribute("userDao");
    }
    
    protected void create(HttpServletRequest request,
            HttpServletResponse response, String mime) throws IOException,
            ServletException
    {
        String userId = (String)request.getAttribute(UsersController.IDENTIFIER_ATTR);
        User user = userId!=null ? _userDao.get(Long.valueOf(userId)) : null;
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
        
        if(Constants.XML.equals(mime))
        {                     
            writeXML(created ? todo : Feedback.COULD_NOT_CREATE_TODO, request, response);
        }
        else if(Constants.JSON.equals(mime))
        {            
            writeJSON(created ? todo : Feedback.COULD_NOT_CREATE_TODO, request, response);
        }
        else
        {
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
        
    }
    
    protected void delete(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo==null)
        {
            response.sendError(404);
            return;
        }
        
        boolean deleted = _todoDao.delete(todo);
        
        if(Constants.XML.equals(mime))
        {
            writeXML(deleted ? todo : null, request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(deleted ? todo : null, request, response);
        }
        else
        {            
            request.setAttribute(Constants.MSG, deleted ? Feedback.TODO_DELETED.getMsg() : 
                Feedback.COULD_NOT_DELETE_TODO.getMsg());
            
            dispatchToView(todo, request, response);
        }        
    }
    
    protected void read(HttpServletRequest request,
            HttpServletResponse response, String mime)
            throws IOException, ServletException
    {
        String userId = (String)request.getAttribute(UsersController.IDENTIFIER_ATTR);        
        if(Constants.XML.equals(mime))
        {
            writeXML(userId==null ? _todoDao.get() : _todoDao.getByUser(Long.valueOf(userId)), 
                    request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(userId==null ? _todoDao.get() : _todoDao.getByUser(Long.valueOf(userId)), 
                    request, response);
        }
        else
        {
            dispatchToView(userId==null ? _todoDao.get() : _todoDao.getByUser(Long.valueOf(userId)), 
                    request, response);
        }
    }
    
    protected void read(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {        
        Todo todo = _todoDao.get(Long.valueOf(id));
        if(todo==null)
        {            
            response.sendError(404);
            return;
        }
        
        if(Constants.XML.equals(mime))    
            writeXML(todo, request, response);
        else if(Constants.JSON.equals(mime))
            writeJSON(todo, request, response);
        else            
            dispatchToView(todo, request, response);        
    }

    
    protected void update(HttpServletRequest request,
            HttpServletResponse response, String mime, String id)
            throws IOException, ServletException
    {        
        boolean completed = false;
        
        String title = request.getParameter(Constants.TITLE);
        String content = request.getParameter(Constants.CONTENT);
        String completedParam = request.getParameter(Constants.COMPLETED);
        
        // preparse the boolean (for errors) before querying the db
        if(completedParam!=null)
            completed = Boolean.parseBoolean(completedParam);
        
        Todo todo = _todoDao.get(Long.valueOf(id));
        
        boolean updated = false;
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
        
        if(Constants.XML.equals(mime))
        {
            writeXML(updated ? todo : Feedback.COULD_NOT_UPDATE_TODO, request, response);            
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(updated ? todo : Feedback.COULD_NOT_UPDATE_TODO, request, response);
        }
        else
        {            
            request.setAttribute(Constants.MSG, updated ? Feedback.TODO_UPDATED.getMsg() : 
                Feedback.COULD_NOT_UPDATE_TODO.getMsg());            
            dispatchToFormView(todo, request, response); 
        }        
       
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
    
    private void writeXML(Object data, HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        response.setContentType(Constants.TEXT_XML);
        ServletOutputStream out = response.getOutputStream();
        out.write(XMLConverter.getInstance().toString(data, null).getBytes());
        out.close();
    }
    
    private void writeJSON(Object data, HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        response.setContentType(Constants.TEXT_PLAIN);
        ServletOutputStream out = response.getOutputStream();
        out.write(JSONConverter.getInstance().toString(data, 
                request.getParameter(Constants.CALLBACK)).getBytes());
        out.close();
    }
    
    /* ============================================================================= */
    // VERBS
    /* ============================================================================= */
    
    protected void complete(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        if(!GET.equals(request.getMethod()) && !POST.equals(request.getMethod()))
        {
            response.sendError(404);
            return;
        }
        Long id = Long.valueOf(request.getParameter(Constants.ID));        
        boolean updated = false;
        
        Todo todo = _todoDao.get(id);
        if(todo!=null && !todo.isCompleted())
        {
            todo.setCompleted(true);
            updated = _todoDao.update(todo);
        }
        
        if(Constants.XML.equals(mime))
        {
                
            writeXML(updated ? todo : null, request, response);
        }
        else if(Constants.JSON.equals(mime))            
        {

            writeJSON(updated ? todo: null, request, response);
        }
        else        
        {
            if(!updated)
                request.setAttribute(Constants.MSG, Feedback.COULD_NOT_UPDATE_TODO.getMsg());            
            String referer = request.getHeader("Referer");
            if(referer==null)                
                dispatchToView(todo, request, response);
            else
                response.sendRedirect(referer);
        }
    }
    
    protected void create(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    { 
        String method = request.getMethod();
        if(method.equals(GET))            
        {
            Todo todo = null;
            request.setAttribute(Constants.ACTION, Constants.ACTION_CREATE);
            dispatchToFormView(todo, request, response);
        }
        else if(method.equals(POST))
            create(request, response, mime);
        else
            response.sendError(404);
    }

    protected void edit(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        String id = request.getParameter(Constants.ID);
        if(id==null)
        {            
            response.sendError(404);
            return;
        }
        
        String method = request.getMethod();
        if(method.equals(GET))
        {
            Todo todo = _todoDao.get(Long.valueOf(id));
            if(todo==null)
                request.setAttribute(Constants.MSG, Feedback.TODO_NOT_FOUND.getMsg());
            else
                request.setAttribute(Constants.TODO, todo);
            request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
            dispatchToFormView(todo, request, response);
            return;
        }
        if(method.equals(POST))
        {
            request.setAttribute(Constants.ACTION, Constants.ACTION_EDIT);
            update(request, response, mime, id);            
            return;
        }
        
        response.sendError(404);
    }
    
    protected void delete(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        String id = request.getParameter(Constants.ID);        
        if(id==null)
        {
            response.sendError(404);
            return;
        }
        request.setAttribute(Constants.ACTION, Constants.ACTION_DELETE);
        delete(request, response, mime, id);  
    }
    
    /* ============================================================================= */
    // FILTERED
    /* ============================================================================= */
    
    protected void completed(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        byStatus(true, mime, request, response);
    }
    
    protected void current(String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        byStatus(false, mime, request, response);
    }
    
    private void byStatus(boolean completed, String mime, HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        if(!GET.equals(request.getMethod()))
        {
            response.sendError(404);
            return;
        }
        
        String userId = (String)request.getAttribute(UsersController.IDENTIFIER_ATTR);        
        if(Constants.XML.equals(mime))
        {
            writeXML(userId==null ? _todoDao.getByStatus(completed) : 
                _todoDao.getByUserAndStatus(Long.valueOf(userId), completed), request, response);
        }
        else if(Constants.JSON.equals(mime))
        {
            writeJSON(userId==null ? _todoDao.getByStatus(completed) : 
                _todoDao.getByUserAndStatus(Long.valueOf(userId), completed), request, response);
        }
        else
        {
            dispatchToView(userId==null ? _todoDao.getByStatus(completed) : 
                _todoDao.getByUserAndStatus(Long.valueOf(userId), completed), request, response);
        }
    }

}
