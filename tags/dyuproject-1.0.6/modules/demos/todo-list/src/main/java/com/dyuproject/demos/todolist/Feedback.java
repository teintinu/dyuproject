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

import com.dyuproject.util.format.FormatConverter;
import com.dyuproject.util.format.FormatConverter.Builder;

/**
 * @author David Yu
 * @created May 22, 2008
 */

public class Feedback implements FormatConverter.Bean
{
    
    public static final Feedback USER_NOT_FOUND = new Feedback("User not found.", false);  
    
    public static final Feedback COULD_NOT_CREATE_USER = new Feedback(
            "Could not create user.", false);
    
    public static final Feedback COULD_NOT_UPDATE_USER = new Feedback(
            "Could not update user.", false);
    
    public static final Feedback COULD_NOT_DELETE_USER = new Feedback(
            "Could not delete user.", false);
    
    public static final Feedback TODO_NOT_FOUND = new Feedback(
            "Todo not found.", false);
    
    public static final Feedback COULD_NOT_CREATE_TODO = new Feedback(
            "Could not create todo.", false);
    
    public static final Feedback COULD_NOT_UPDATE_TODO = new Feedback(
            "Could not update todo.", false);
    
    public static final Feedback COULD_NOT_DELETE_TODO = new Feedback(
            "Could not delete todo.", false);
    
    public static final Feedback NO_CHANGES_MADE = new Feedback(
            "No changes made.", false);
    
    public static final Feedback REQUIRED_PARAMS_USER_CREATE = new Feedback(
            "The ff are required: First Name, Last Name, Email, Username, Password, Confirm Password", false);
    
    public static final Feedback REQUIRED_PARAMS_CHANGE_PASSWORD = new Feedback(
            "Old and new password are required.", false);
    
    public static final Feedback PASSWORD_DID_NOT_MATCH = new Feedback(
            "Password did not match.", false);
    
    /* ---------------------------------------------------------------------------- */
    
    public static final Feedback PASSWORD_CHANGED = new Feedback("Password changed.", true);
    
    public static final Feedback TODO_CREATED = new Feedback("Todo created.", true);
    
    public static final Feedback TODO_UPDATED = new Feedback("Todo updated.", true);
    
    public static final Feedback TODO_DELETED = new Feedback("Todo deleted.", true);
    
    public static final Feedback USER_CREATED = new Feedback("User created.", true);
    
    public static final Feedback USER_UPDATED = new Feedback("User updated.", true);
    
    public static final Feedback USER_DELETED = new Feedback("User deleted.", true);    
    
    /* ---------------------------------------------------------------------------- */
    
    
    private String _msg;
    private boolean _positive;
    
    public Feedback(String msg, boolean positive)
    {
        _msg = msg;
        _positive = positive;
    }

    public void convert(Builder builder, String format)
    {
        builder.put("msg", _msg);
        builder.put("positive", _positive);
        builder.put("error", !_positive);
    }
    
    public String getMsg()
    {
        return _msg;
    }
    
    public boolean isPositive()
    {
        return _positive;
    }
    
    public boolean isError()
    {
        return !_positive;
    }
    
    public String toString()
    {
        return _msg;
    }

}
