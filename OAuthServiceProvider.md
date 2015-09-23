# OAuthServiceProvider #

**For reference, you can refer to this example -  http://dyuproject.googlecode.com/svn/trunk/modules/demos/oauth-serviceprovider-servlet**

The first thing about being a `ServiceProvider` is that you must store/persist
the tokens of your consumers.

There is a built-in `PropertiesHashStore` which would contain the consumer\_key/consumer\_secret key-value pairs

`consumers.properties`
```
consumer_key1 = consumer_secret1
consumer_key2 = consumer_secret2
consumer_key2 = consumer_secret3
```

Normally you would store these tokens on a database or a key-value store or perhaps memcache.

You just have to extend `HashStore` and implement:
```
    protected String getConsumerSecret(String consumerKey)
    {
        String consumerSecret = fetchConsumerSecretFromDatabase(consumerKey);
        // generally its recommended that you use memcache or key-value stores
        return consumerSecret;
    }
```

On a servlet environment, you could setup the `ServiceProvider` in a way that allows it to be shared by all servlets.

`ContextListener.java`
```
public class ContextListener implements ServletContextListener
{

    public void contextInitialized(ServletContextEvent event)
    {
        Properties consumers = new Properties();
        URL resource = ClassLoaderUtil.getResource("consumers.properties", 
                AuthorizeTokenServlet.class);
        if(resource==null)
            throw new IllegalStateException("consumers.properties not found in classpath.");
        try
        {
            consumers.load(resource.openStream());
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
        
        PropertiesHashStore store = new PropertiesHashStore("secret", "macSecret", consumers);
        ServiceProvider serviceProvider = new ServiceProvider(store);
        event.getServletContext().setAttribute(ServiceProvider.class.getName(), serviceProvider);
        System.err.println("ServiceProvider initialized.");
    }
    
    public void contextDestroyed(ServletContextEvent event)
    {
        
    }
}
```

### Request Token URL ###
`GetRequestTokenServlet.java`
```
public class GetRequestTokenServlet extends HttpServlet
{
    
    protected ServiceProvider _serviceProvider;
    
    public void init() throws ServletException
    {
        _serviceProvider = (ServiceProvider)getServletContext().getAttribute(ServiceProvider.class.getName());
    }
    
    public ServiceProvider getServiceProvider()
    {
        return _serviceProvider;
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        getServiceProvider().handleTokenRequest(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        getServiceProvider().handleTokenRequest(request, response);
    }

}
```

### Access Token URL ###
`GetAccessTokenServlet.java`
```
public class GetAccessTokenServlet extends HttpServlet
{
    
    protected ServiceProvider _serviceProvider;
    
    public void init() throws ServletException
    {
        _serviceProvider = (ServiceProvider)getServletContext().getAttribute(ServiceProvider.class.getName());
    }
    
    public ServiceProvider getServiceProvider()
    {
        return _serviceProvider;
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        getServiceProvider().handleTokenExchange(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        getServiceProvider().handleTokenExchange(request, response);
    }

}
```

### User Authorization URL ###

After authenticating the user, you need to redirect back to the consumer's callback url.

If the callback specified is "oob", you instruct your user to pass the oauth\_verifier back to the consumer.
```
        String callbackOrVerifier = getServiceProvider().getAuthCallbackOrVerifier(requestToken, 
                username);

        if(callbackOrVerifier.startsWith("http"))
            response.sendRedirect(callbackOrVerifier);
```

Note that this also links the user with the request\_token.


### Webservice URL ###
`WebserviceServlet.java`
```
public class WebserviceServletextends HttpServlet
{
    
    protected ServiceProvider _serviceProvider;
    
    public void init() throws ServletException
    {
        _serviceProvider = (ServiceProvider)getServletContext().getAttribute(ServiceProvider.class.getName());
    }
    
    public ServiceProvider getServiceProvider()
    {
        return _serviceProvider;
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        ServiceToken accessToken = getServiceProvider().getAccessToken(request);
        if(accessToken==null)
        {            
            response.setStatus(401);
            return;
        }
        
        // get the associated user identifier that was linked on login/authentication
        String username = accessToken.getId();
        // generate your response
        // normally you would query your db with the username and return the results.
        
    }

}
```