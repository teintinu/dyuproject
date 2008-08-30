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

import com.dyuproject.demos.todolist.model.Todo;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class TodoDao extends AbstractDao
{
    
    static final String GET = "select t from " + Todo.class.getSimpleName() + " t";
    static final String GET_BY_USER = GET + " where t.user.id=?";
    static final String GET_BY_STATUS = GET + " where t.completed=?";
    static final String GET_BY_USER_AND_STATUS = GET_BY_USER + " and t.completed=?";
    
    public List<?> get()
    {
        return createQuery(GET);
    }
    
    public List<?> getByStatus(boolean completed)
    {
        return createQuery(GET_BY_STATUS, new Object[]{completed});
    }
    
    public List<?> getByUser(Long userId)
    {
        return createQuery(GET_BY_USER, new Object[]{userId});
    }
    
    public List<?> getByUserAndStatus(Long userId, boolean completed)
    {
        return createQuery(GET_BY_USER_AND_STATUS, new Object[]{userId, completed});
    }
    
    public Todo get(Long id)
    {
        return find(Todo.class, id);
    }
    
    public Todo find(Long id)
    {
        return find(Todo.class, id);
    }
    
    public boolean create(Todo todo)
    {
        boolean created = false;
        try
        {
            created = persist(todo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            created = false;
        }
        return created;
    }
    
    public boolean update(Todo todo)
    {
        boolean updated = false;
        try
        {
            updated = merge(todo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            updated = false;
        }
        return updated;
    }
    
    public boolean delete(Todo todo)
    {
        boolean deleted = false;
        try
        {
            deleted = remove(todo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            deleted = false;
        }
        return deleted;
    }

}
