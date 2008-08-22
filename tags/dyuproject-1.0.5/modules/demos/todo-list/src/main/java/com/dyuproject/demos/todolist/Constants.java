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

/**
 * @author David Yu
 * @created May 22, 2008
 */

public class Constants
{
    
    public static final String XML = "xml";
    public static final String JSON = "json";
    
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_XML = "text/xml";
    
    public static final String ID = "id";
    public static final String CALLBACK = "callback";
    public static final String MSG = "msg";
    
    public static final String ACTION = "action";
    public static final String ACTION_EDIT = "Edit";
    public static final String ACTION_CREATE = "Create";
    public static final String ACTION_DELETE = "Delete";
    
    public static final String DISABLED = "disabled";
    
    public static final String NO_CHANGES_MADE = "No changes made.";

    // todos constants
    
    public static final String TODO = "todo";
    public static final String TODOS = "todos";  
    
    public static final String TODO_CREATED = "Todo created.";
    public static final String TODO_UPDATED = "Todo updated.";
    public static final String TODO_DELETED = "Todo deleted.";
    
    public static final String TODO_NOT_FOUND = "Todo not found.";    
    
    public static final String COULD_NOT_CREATE_TODO = "Could not create todo.";
    public static final String COULD_NOT_UPDATE_TODO = "Could not update todo.";
    public static final String COULD_NOT_DELETE_TODO = "Could not delete todo.";
    
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String COMPLETED = "completed";
    
    // users constants
    
    public static final String USER = "user";
    public static final String USERS = "users";
    
    public static final String USER_CREATED = "User created.";
    public static final String USER_UPDATED = "User updated.";
    public static final String USER_DELETED = "User deleted.";
    
    public static final String USER_NOT_FOUND = "User not found."; 
    
    public static final String COULD_NOT_CREATE_USER = "Could not create user.";
    public static final String COULD_NOT_UPDATE_USER = "Could not update user.";
    public static final String COULD_NOT_DELETE_USER = "Could not delete user.";  
    
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String OLD_PASSWORD = "oldPassword";
    public static final String NEW_PASSWORD = "newPassword";
    
    public static final String PASSWORD_CHANGED = "Password changed.";
    
    public static final String REQUIRED_PARAMS_USER_CREATE = "The ff are required: First Name, Last Name, Email, Username, Password, Confirm Password";
    public static final String REQUIRED_PARAMS_CHANGE_PASSWORD = "Old and new password are required.";
    public static final String PASSWORD_DID_NOT_MATCH = "Password did not match.";
    

}
