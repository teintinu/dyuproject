openid relying party configuration

See the [Quick Start Guide](QuickStartOpenid.md), [OpenIdServletFilter](OpenIdServletFilter.md) and [SampleOpenIdServlet](SampleOpenIdServlet.md).

For extensions, see [OpenIdAttributeExchange](OpenIdAttributeExchange.md) and [OpenIdSimpleRegistration](OpenIdSimpleRegistration.md)


---


### _openid.properties_ ###
```
#defaults (No extra configuration)
openid.identifier.parameter = openid_identifier
openid.discovery = com.dyuproject.openid.DefaultDiscovery
openid.assocation = com.dyuproject.openid.DiffieHellmanAssociation
openid.httpconnector = com.dyuproject.openid.SimpleHttpConnector
openid.authredirection = com.dyuproject.openid.SimpleRedirection
openid.user.manager = com.dyuproject.openid.manager.HttpSessionUserManager

# when the user is redirected to his provider and he somehow navigates away from his
# provider and returns to your site ... the relying party will do an automatic redirect
# back to his provider for authentication
openid.automatic_redirect = true]

# when the discovery fails, use the openid_identifer as the openid server/provider url.
openid.identifier_as_server = false

# if identifier_select.properties is found in classpath, it will be loaded to the cache
# this is especially usefull for google, yahoo where there is a generic id.
# this cache bypasses the discovery w/c will make the openid process faster.
openid.user.cache = com.dyuproject.openid.IdentifierSelectUserCache
```

# If you are working directly with the `RelyingParty` api #
You would write your servlet/filter to something like this:

```
    final RelyingParty _relyingParty = RelyingParty.getInstance();
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
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
                    // set error msg if the openid_identifier is not resolved.
                    if(request.getParameter(_relyingParty.getIdentifierParameter())!=null)
                        request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
                    
                    // new user
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            if(user.isAuthenticated())
            {
                // user already authenticated
                request.getRequestDispatcher("/home.jsp").forward(request, response);
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
                    response.sendRedirect(request.getContextPath() + "/home/");
                }
                else
                {
                    // failed verification
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                return;
            }
            
            // associate and authenticate user
            StringBuffer url = request.getRequestURL();
            String trustRoot = url.substring(0, url.indexOf("/", 9));
            String realm = url.substring(0, url.lastIndexOf("/"));
            String returnTo = url.toString();            
            if(_relyingParty.associateAndAuthenticate(user, request, response, trustRoot, realm, 
                    returnTo))
            {
                // successful association
                return;
            }   
    }

```