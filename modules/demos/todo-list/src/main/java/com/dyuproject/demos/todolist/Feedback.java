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

import com.dyuproject.web.rest.JSONDispatcher.SimpleResponse;

/**
 * @author David Yu
 * @created May 22, 2008
 */

public class Feedback extends SimpleResponse
{
    
    public static final Feedback USER_NOT_FOUND = new Feedback("User not found.", false);  
    
    public static final Feedback COULD_NOT_CREATE_ACCOUNT= new Feedback(
            "Could not create account.", false);
    
    public static final Feedback AUTH_REQUIRED = new Feedback(
            "The ff are required: Username, Password", false);  
    
    public static final Feedback ACCOUNT_CREATE_REQUIRED = new Feedback(
            "The ff are required: Username, Password, Confirm Password", false);
    
    public static final Feedback COULD_NOT_UPDATE_ACCOUNT = new Feedback(
            "Could not update account.", false);
    
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
    
    public static final Feedback CHANGE_PASSWORD_REQUIRED = new Feedback(
            "The ff are required: Old Password, New Password, Confirm Password", false);
    
    public static final Feedback PASSWORD_DID_NOT_MATCH = new Feedback(
            "Password did not match.", false);
    
    public static final Feedback PASSWORD_INCORRECT = new Feedback(
            "Password is incorrect.", false);
    
    /* ---------------------------------------------------------------------------- */
    
    public static final Feedback PASSWORD_CHANGED = new Feedback("Password changed.", true);
    
    public static final Feedback TODO_CREATED = new Feedback("Todo created.", true);
    
    public static final Feedback TODO_UPDATED = new Feedback("Todo updated.", true);
    
    public static final Feedback TODO_DELETED = new Feedback("Todo deleted.", true);
    
    public static final Feedback USER_CREATED = new Feedback("User created.", true);
    
    public static final Feedback USER_UPDATED = new Feedback("User updated.", true);
    
    public static final Feedback USER_DELETED = new Feedback("User deleted.", true);    
    
    /* ---------------------------------------------------------------------------- */

    
    public Feedback(String msg, boolean positive)
    {
        super(msg, !positive);
    }
    
    public void addJSON(StringBuffer buffer)
    {            
        buffer.append('{').append(MSG).append(':').append(_msg);
        buffer.append(',').append(ERROR).append(':').append(_error);
        buffer.append(',').append("positive").append(':').append(!_error);
        buffer.append('}');
    }
    
    public boolean isPositive()
    {
        return !isError();
    }

}
