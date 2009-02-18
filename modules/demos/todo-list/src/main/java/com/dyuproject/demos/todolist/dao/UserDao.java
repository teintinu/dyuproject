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

public class UserDao extends AbstractDao
{
    
    private static final String GET = "select u from " + User.class.getSimpleName() + " u";
    private static final String GET_BY_USERNAME = GET + " where u.username=?";
    private static final String GET_BY_USER_AND_PASS = GET + " where u.username=? and u.password=?";
    
    private static final Feedback USERNAME_ALREADY_EXISTS = new Feedback("Username already exists.", false);
    
    public User get(String username, String password)
    {
        List<?> result = createQuery(GET_BY_USER_AND_PASS, new Object[]{username, password});
        return result.isEmpty() ? null : (User)result.get(0);
    }
    
    public User get(String username)
    {
        List<?> result = createQuery(GET_BY_USERNAME, new Object[]{username});
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
    
    public boolean create(User user)
    {
        try
        {
            return persist(user);
        } 
        catch(EntityExistsException e)
        {            
            setCurrentFeedback(USERNAME_ALREADY_EXISTS);
            return false;
        }
        catch (Exception e)
        {
            if(e.getCause()!=null && 
                    e.getCause().getClass().getSimpleName().equals(CONSTRAINT_VIOLATION))
            {                
                setCurrentFeedback(USERNAME_ALREADY_EXISTS);
            }
            else
                e.printStackTrace();
            return false;
        }
    }

}
