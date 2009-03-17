//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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
import com.dyuproject.demos.todolist.model.Credential;

/**
 * @author David Yu
 * @created Feb 25, 2009
 */

public class CredentialDao extends AbstractDao
{
    
    public static final String GET = "select c from " + Credential.class.getSimpleName() + " c";
    public static final String GET_BY_USER_AND_PASS = GET + " where c.username=? and c.password=?";
        
    private static final Feedback USERNAME_ALREADY_EXISTS = new Feedback("Username already exists.", false);
    
    public Credential get(Long id)
    {
        return find(Credential.class, id);
    }
    
    public Credential get(String username, String password)
    {
        List<?> result = createQuery(GET_BY_USER_AND_PASS, new Object[]{username, password});
        return result.isEmpty() ? null : (Credential)result.get(0);
    }
    
    public boolean create(Credential cred)
    {
        try
        {
            return persist(cred);
        } 
        catch(EntityExistsException e)
        {            
            setCurrentFeedback(USERNAME_ALREADY_EXISTS);
            return false;
        }
        catch (Exception e)
        {
            System.err.println("ERROR: " + e);
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
