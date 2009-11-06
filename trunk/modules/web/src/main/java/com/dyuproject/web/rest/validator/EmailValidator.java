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

import com.dyuproject.util.validate.IPDomainValidator;


/**
 * Validates input that resembles an email.
 * 
 * @author David Yu
 * @created Jan 20, 2009
 */

public final class EmailValidator extends AbstractValidator
{
    
    public EmailValidator()
    {
        this("Invalid email.");
    }
    
    public EmailValidator(String errorMsg)
    {
        setErrorMsg(errorMsg);
    }

    public String validate(Object value)
    {        
        String email = value.toString();
        int idx = email.indexOf('@');
        if(idx>0)
        {
            int start = idx+1;
            char[] domain = new char[email.length()-start];
            email.getChars(start, email.length(), domain, 0);
            if(IPDomainValidator.isValid(domain))
                return null;
        }
        return getErrorMsg();
    }

}
