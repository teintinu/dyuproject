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

package com.dyuproject.openid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An openid servlet filter that can work with hardly any configuration.
 * The only thing required is the init-parameter "forwardUri".
 * 
 * @author David Yu
 * @created Jan 8, 2009
 */

public class OpenIdServletFilter implements Filter
{
    
    public static final String ERROR_MSG_ATTR = "openid_servlet_filter_msg";    
    public static final String DEFAULT_ERROR_MSG = "Your openid could not be resolved.";
    public static final String ID_NOT_FOUND_MSG = "Your openid does not exist.";

    protected String _forwardUri;    
    protected RelyingParty _relyingParty;
    
    protected String _defaultErrorMsg = DEFAULT_ERROR_MSG;
    protected String _idNotFoundMsg = ID_NOT_FOUND_MSG;
    
    public void init(FilterConfig config) throws ServletException
    {
        _forwardUri = config.getInitParameter("forwardUri");
        if(_forwardUri==null)
            throw new ServletException("forwardUri must not be null.");
        
        String defaultErrorMsg = config.getInitParameter("defaultErrorMsg");
        if(defaultErrorMsg!=null)
            _defaultErrorMsg = defaultErrorMsg;
        
        String idNotFoundMsg = config.getInitParameter("idNotFoundMsg");
        if(idNotFoundMsg!=null)
            _idNotFoundMsg = idNotFoundMsg;        
        
        // resolve from ServletContext
        _relyingParty = (RelyingParty)config.getServletContext().getAttribute(
                RelyingParty.class.getName());
        
        //default config if null
        if(_relyingParty==null)
            _relyingParty = RelyingParty.getInstance();
    }
    
    public String getForwardUri()
    {
        return _forwardUri;
    }
    
    public RelyingParty getRelyingParty()
    {
        return _relyingParty;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
    throws IOException, ServletException
    {
        if(handle((HttpServletRequest)req, (HttpServletResponse)res))
            chain.doFilter(req, res);
    }

    public void destroy()
    {        
        
    }
    
    public boolean handle(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String errorMsg = DEFAULT_ERROR_MSG;
        try
        {
            OpenIdUser user = _relyingParty.discover(request);
            if(user==null)
            {                
                if(RelyingParty.isAuthResponse(request))
                {
                    // authentication timeout                    
                    response.sendRedirect(request.getRequestURI());
                }
                else
                {
                    // new user
                    request.getRequestDispatcher(_forwardUri).forward(request, response);
                }
                return false;
            }
            
            if(user.isAuthenticated())
            {
                // user already authenticated
                request.setAttribute(OpenIdUser.ATTR_NAME, user);                                
                return true;
            }
            
            if(user.isAssociated() && RelyingParty.isAuthResponse(request))
            {
                // verify authentication
                if(_relyingParty.verifyAuth(user, request, response))
                {
                    // authenticated                    
                    // redirect to home to remove the query params instead of doing:
                    // request.setAttribute("user", user); request.getRequestDispatcher("/home.jsp").forward(request, response);
                    response.sendRedirect(request.getRequestURI());
                }
                else
                {
                    // failed verification
                    request.getRequestDispatcher(_forwardUri).forward(request, response);
                }
                return false;
            }
            
            // associate and authenticate user
            StringBuffer url = request.getRequestURL();
            String trustRoot = url.substring(0, url.indexOf("/", 9));
            String realm = url.substring(0, url.lastIndexOf("/"));
            String returnTo = url.toString();            
            if(_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm, 
                    returnTo))
            {
                // user is associated and then redirected to his openid provider for authentication                
                return false;
            }        
        }
        catch(UnknownHostException uhe)
        {
            errorMsg = ID_NOT_FOUND_MSG;
        }
        catch(FileNotFoundException fnfe)
        {
            errorMsg = ID_NOT_FOUND_MSG;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            errorMsg = DEFAULT_ERROR_MSG;
        }
        request.setAttribute(ERROR_MSG_ATTR, errorMsg);
        request.getRequestDispatcher(_forwardUri).forward(request, response);
        return false;
    }

}
