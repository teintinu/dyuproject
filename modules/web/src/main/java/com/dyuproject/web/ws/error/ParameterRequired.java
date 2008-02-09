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

package com.dyuproject.web.ws.error;

import com.dyuproject.web.ws.AbstractWebServiceError;

/**
 * @author David Yu
 */

public class ParameterRequired extends AbstractWebServiceError
{
    
    public static final String MESSAGE = "the parameter *method* is required.";
    private static final ParameterRequired __instance = new ParameterRequired();
    
    
    public static ParameterRequired getInstance()
    {
        return __instance;
    }
    
    private ParameterRequired()
    {
        
    }
    
    public String getMessage()
    {
        return MESSAGE;
    }
    
}
