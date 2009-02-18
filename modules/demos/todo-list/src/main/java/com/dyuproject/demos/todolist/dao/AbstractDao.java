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

import com.dyuproject.demos.todolist.Feedback;
import com.dyuproject.ext.jpa.DefaultDao;

/**
 * @author David Yu
 * @created Aug 30, 2008
 */

public abstract class AbstractDao extends DefaultDao
{
    
    protected static final String CONSTRAINT_VIOLATION = "ConstraintViolationException";
    
    protected static final String FEEDBACK_ATTR = "feedback";
    
    public static void setCurrentFeedback(Feedback feedback)
    {
        setAttribute(FEEDBACK_ATTR, feedback);
    }
    
    public static Feedback getCurrentFeedback()
    {
        return (Feedback)getAttribute(FEEDBACK_ATTR);
    }
    
    public static boolean executeUpdate()
    {
        try
        {
            beginAndCommit();
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean create(Object pojo)
    {
        try
        {
            return persist(pojo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(Object pojo)
    {
        try
        {
            return remove(pojo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
