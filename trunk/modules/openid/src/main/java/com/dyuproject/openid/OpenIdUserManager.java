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

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Manages associated/authenticated users; 
 * This can get/read, save/update and invalidate/delete an OpenIdUser.
 * 
 * @author David Yu
 * @created Sep 20, 2008
 */

public interface OpenIdUserManager
{
    
    /**
     * Initialize this object via user-configured properties.
     */
    public void init(Properties properties);
    
    /**
     * Gets/reads the user associated with the given {@code request}.
     */
    public OpenIdUser getUser(HttpServletRequest request)
    throws IOException;
    
    /**
     * Saves/persists the user associated with the given {@code request}.
     */
    public boolean saveUser(OpenIdUser user, HttpServletRequest request, 
            HttpServletResponse response) throws IOException;
    
    /**
     * Invalidates/removes/deletes the user associated with the given {@code request}.
     */
    public boolean invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException;
    

}
