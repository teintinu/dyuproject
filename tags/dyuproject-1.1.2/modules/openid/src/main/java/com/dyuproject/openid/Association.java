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

package com.dyuproject.openid;

import java.util.Map;

/**
 * Association
 * 
 * @author David Yu
 * @created Sep 10, 2008
 */

public interface Association
{
    
    public static final String ASSOC_HMAC_SHA1 = "HMAC-SHA1";
    
    public static final String ASSOC_HMAC_SHA256 = "HMAC-SHA256";
    
    
    public static String SESSION_NO_ENCRYPTION = "no-encryption";
    
    public static final String SESSION_DH_SHA1 = "DH-SHA1";
    
    public static final String SESSION_DH_SHA256 = "DH-SHA256";
    
    
    public boolean associate(OpenIdUser user, OpenIdContext context) 
    throws Exception;
    
    public boolean verifyAuth(OpenIdUser user, Map<String,String> authRedirect, 
            OpenIdContext context) throws Exception;

}
