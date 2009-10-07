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

package com.dyuproject.oauth.manager;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dyuproject.oauth.Token;
import com.dyuproject.oauth.TokenManager;

/**
 * HttpSessionTokenManager - stores the token in the HttpSession
 * 
 * @author David Yu
 * @created May 30, 2009
 */

public final class HttpSessionTokenManager implements TokenManager
{
    
    public void init(Properties properties)
    {
        
    }
    
    public Token getToken(String consumerKey, HttpServletRequest request) throws IOException
    {
        HttpSession session = request.getSession(false);
        return session==null ? null: (Token)session.getAttribute(consumerKey);
    }

    public boolean invalidate(String consumerKey, HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        HttpSession session = request.getSession(false);
        if(session!=null)
            session.removeAttribute(consumerKey);
        return true;
    }

    public boolean saveToken(Token token, HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        request.getSession().setAttribute(token.getCk(), token);
        return true;
    }

}
