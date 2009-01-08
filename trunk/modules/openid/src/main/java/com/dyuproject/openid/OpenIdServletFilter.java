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
    static final String DEFAULT_ERROR_MSG = "The provided openid claimed id could not be resolved.";
    static final String CLAIMEDID_NOT_FOUND = "The provided openid claimed id does not exist.";

    private boolean _sregEnabled = false;
    private String _forwardUri;    
    private RelyingParty _relyingParty;    
    
    public void init(FilterConfig config) throws ServletException
    {
        _forwardUri = config.getInitParameter("forwardUri");
        if(_forwardUri==null)
            throw new ServletException("forwardUri must not be null.");
        
        String sregEnabled = config.getInitParameter("sregEnabled");
        if(sregEnabled!=null)
            _sregEnabled = Boolean.parseBoolean(sregEnabled);
        
        // resolve from ServletContext
        _relyingParty = (RelyingParty)config.getServletContext().getAttribute(
                RelyingParty.class.getName());
        
        //default config if null
        if(_relyingParty==null)
            _relyingParty = RelyingParty.getInstance();
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
    throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
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
                return;
            }
            
            if(user.isAuthenticated())
            {
                // user already authenticated
                request.setAttribute(OpenIdUser.ATTR_NAME, user);
                chain.doFilter(request, response);                
                return;
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
                    // failed verification ... re-authenticate
                    request.getRequestDispatcher(_forwardUri).forward(request, response);
                }
                return;
            }
            
            // associate user
            if(_relyingParty.associate(user, request, response))
            {
                // authenticate user to his/her openid provider
                StringBuffer url = request.getRequestURL();
                String trustRoot = url.substring(0, url.indexOf("/", 9));
                String realm = url.substring(0, url.lastIndexOf("/"));
                String returnTo = url.toString();
                
                if(_sregEnabled)
                {
                    UrlEncodedParameterMap params = RelyingParty.getAuthUrlMap(user, trustRoot, 
                            realm, returnTo);
                    params.put(Constants.OPENID_NS_SREG, Constants.Sreg.VERSION);                    
                    params.put(Constants.OPENID_SREG_OPTIONAL, Constants.Sreg.OPTIONAL);
                    response.sendRedirect(params.toString());
                }
                else
                {
                    response.sendRedirect(RelyingParty.getAuthUrlString(user, trustRoot, realm, 
                            returnTo)); 
                }                
                return;
            }        
        }
        catch(UnknownHostException uhe)
        {
            errorMsg = CLAIMEDID_NOT_FOUND;
        }
        catch(FileNotFoundException fnfe)
        {
            errorMsg = CLAIMEDID_NOT_FOUND;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            errorMsg = e.getMessage();
        }
        // failed association
        request.setAttribute(ERROR_MSG_ATTR, errorMsg);
        request.getRequestDispatcher(_forwardUri).forward(request, response);
    }

    public void destroy()
    {        
        
    }

}
