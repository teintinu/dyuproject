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
    
    public static final Feedback USER_NOT_FOUND = new Feedback(Constants.USER_NOT_FOUND, false);  
    
    public static final Feedback COULD_NOT_CREATE_USER = new Feedback(Constants.COULD_NOT_CREATE_USER, false);
    public static final Feedback COULD_NOT_UPDATE_USER = new Feedback(Constants.COULD_NOT_UPDATE_USER, false);
    public static final Feedback COULD_NOT_DELETE_USER = new Feedback(Constants.COULD_NOT_DELETE_USER, false);
    
    public static final Feedback TODO_NOT_FOUND = new Feedback(Constants.TODO_NOT_FOUND, false);
    
    public static final Feedback COULD_NOT_CREATE_TODO = new Feedback(Constants.COULD_NOT_CREATE_TODO, false);
    public static final Feedback COULD_NOT_UPDATE_TODO = new Feedback(Constants.COULD_NOT_UPDATE_TODO, false);
    public static final Feedback COULD_NOT_DELETE_TODO = new Feedback(Constants.COULD_NOT_DELETE_TODO, false);
    
    public static final Feedback NO_CHANGES_MADE = new Feedback(Constants.NO_CHANGES_MADE, false);
    
    public static final Feedback REQUIRED_PARAMS_USER_CREATE = new Feedback(Constants.REQUIRED_PARAMS_USER_CREATE, false);
    public static final Feedback REQUIRED_PARAMS_CHANGE_PASSWORD = new Feedback(Constants.REQUIRED_PARAMS_CHANGE_PASSWORD, false);
    public static final Feedback PASSWORD_DID_NOT_MATCH = new Feedback(Constants.PASSWORD_DID_NOT_MATCH, false);
    
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
