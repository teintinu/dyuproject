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

package com.dyuproject.web.rest.validator;

import com.dyuproject.web.rest.ValidatingConsumer.FieldValidator;

/**
 * Base class for validators that have generic error messages.
 * 
 * @author David Yu
 * @created Jan 20, 2009
 */

public abstract class AbstractValidator implements FieldValidator
{
    
    protected String _errorMsg;
    
    public void setErrorMsg(String errorMsg)
    {
        _errorMsg = errorMsg;
    }
    
    public final String getErrorMsg()
    {
        return _errorMsg;
    }

}
