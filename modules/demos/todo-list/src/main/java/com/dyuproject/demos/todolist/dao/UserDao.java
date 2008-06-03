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

import javax.persistence.EntityExistsException;

import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.demos.todolist.model.User;

/**
 * @author David Yu
 * @created May 21, 2008
 */

public class UserDao extends DefaultDao
{
    
    private static final String GET = "select u from " + User.class.getSimpleName() + " u";
    private static final String GET_BY_USER_AND_PASS = GET + " where u.username=? and u.password=?";
    
    private static final String USERNAME_ALREADY_EXISTS_STR = "Username already exists.";
    private static final Feedback USERNAME_ALREADY_EXISTS = new Feedback(USERNAME_ALREADY_EXISTS_STR, false);
    
    public User get(String username, String password)
    {
        List<?> result = createQuery(GET_BY_USER_AND_PASS, new Object[]{username, password});
        return result.isEmpty() ? null : (User)result.get(0);
    }
    
    public List<?> get()
    {
        return createQuery(GET);
    }
    
    public User get(Long id)
    {
        return find(User.class, id);
    }
    
    public User find(Long id)
    {
        return find(User.class, id);
    }
    
    public boolean create(User user)
    {
        boolean created = false;
        try
        {
            created = persist(user);
        } 
        catch(EntityExistsException e)
        {
            setCurrentErrorMessage(USERNAME_ALREADY_EXISTS_STR);
            setCurrentFeedback(USERNAME_ALREADY_EXISTS);
            created = false;
        }
        catch (Exception e)
        {            
            e.printStackTrace();
            created = false;
        }
        return created;
    }
    
    public boolean update(User user)
    {
        boolean updated = false;
        try
        {
            updated = persist(user);
        } 
        catch(Exception e)
        {
            setCurrentErrorMessage(USERNAME_ALREADY_EXISTS_STR);
            setCurrentFeedback(USERNAME_ALREADY_EXISTS);
            updated = false;
        }
        return updated;
    }
    
    public boolean delete(User user)
    {
        boolean deleted = false;
        try
        {
            deleted = remove(user);
        } 
        catch (Exception e)
        {            
            e.printStackTrace();
            deleted = false;
        }
        return deleted;
    }
    
    public boolean saveOrUpdate(User user)
    {
        return user.getId()==null ? create(user) : update(user);
    }

}
