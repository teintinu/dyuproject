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

package com.dyuproject.web.rest;

/**
 * @author David Yu
 * @created Feb 25, 2009
 */

@SuppressWarnings("serial")
public class ValidationException extends IllegalArgumentException
{
    
    private String _field;
    private Object _pojo;
    
    public ValidationException(String message, String field)
    {
        super(message);
        _field = field;        
    }
    
    public ValidationException(String message, String field, Object pojo)
    {
        super(message);
        _field = field; 
        _pojo = pojo;
    }
    
    public String getField()
    {
        return _field;
    }
    
    public Object getPojo()
    {
        return _pojo;
    }

}
