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

package com.dyuproject.web;

import javax.servlet.http.HttpServletRequest;

/**
 * @author David Yu
 * @created Jun 8, 2008
 */

public final class RequiredParametersValidator
{
    
    private final String[] _requiredParams;
    
    public RequiredParametersValidator(String[] requiredParams)
    {
        _requiredParams = requiredParams;
    }
    
    public String[] getValidatedParams(HttpServletRequest request)
    {
        return (String[])request.getAttribute(RequiredParametersValidator.class.getName());
    }

    public boolean validate(HttpServletRequest request)
    {
        String[] validatedParams = new String[_requiredParams.length];
        for(int i=0; i<_requiredParams.length; i++)
        {
            String param = request.getParameter(_requiredParams[i]);
            if(param==null || param.length()==0)
                return false;
            validatedParams[i] = param;
        }
        request.setAttribute(RequiredParametersValidator.class.getName(), validatedParams);
        return true;
    }

}
