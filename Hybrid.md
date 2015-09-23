# Hybrid openid + oauth #

The ultimate goal of the hybrid protocol is to enhance the user experience and to speed up your application's authentication process by combining both protocols.

See http://step2.googlecode.com/svn/spec/openid_oauth_extension/latest/openid_oauth_extension.html

#### A mechanism to combine an OpenID authentication request with the approval of an OAuth request token ####

To test it locally, see the instructions found [here](http://code.google.com/p/dyuproject/issues/detail?id=15#c10)

The source code is directly from the example on http://dyuproject.appspot.com

`HybridGoogleService.java`
```

public final class HybridGoogleService extends AbstractService
{
    
    static final String GOOGLE_IDENTIFIER = "https://www.google.com/accounts/o8/id";
    static final String GOOGLE_OPENID_SERVER = "https://www.google.com/accounts/o8/ud";
    static final Endpoint __google = Consumer.getInstance().getEndpoint("www.google.com");
    
    static
    {
        RelyingParty.getInstance().addListener(new RelyingParty.Listener()
        {
            public void onDiscovery(OpenIdUser user, HttpServletRequest request)
            {
            }
            public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request, UrlEncodedParameterMap params)
            {
                String scope = (String)user.getAttribute("google_scope");
                if(scope!=null)
                {
                    params.add("openid.ns.oauth", "http://specs.openid.net/extensions/oauth/1.0");
                    params.put("openid.oauth.consumer", __google.getConsumerKey());
                    params.put("openid.oauth.scope", scope);
                }
            }
            public void onAuthenticate(OpenIdUser user, HttpServletRequest request)
            {
                if(user.getAttribute("google_scope")!=null)
                {
                    String alias = user.getExtension("http://specs.openid.net/extensions/oauth/1.0");
                    if(alias!=null)
                    {
                        String requestToken = request.getParameter("openid." + alias + ".request_token");
                        Token token = new Token(__google.getConsumerKey(), requestToken, null, Token.AUTHORIZED);
                        UrlEncodedParameterMap accessTokenParams = new UrlEncodedParameterMap();
                        try
                        {
                            Response accessTokenResponse = fetchToken(TokenExchange.ACCESS_TOKEN, 
                                    accessTokenParams, __google, token);
                            if(accessTokenResponse.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN)
                            {
                                user.setAttribute("token_k", token.getKey());
                                user.setAttribute("token_s", token.getSecret());
                            }
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            public void onAccess(OpenIdUser user, HttpServletRequest request)
            {
                
            }
        });
    }
    
    static void invalidate(HttpServletRequest request, HttpServletResponse response) 
    throws IOException
    {
        RelyingParty.getInstance().getOpenIdUserManager().invalidate(request, response);
    }
    
    public static Response fetchToken(TokenExchange exchange, UrlEncodedParameterMap params,
            Endpoint endpoint, Token token) 
    throws IOException
    {
        // via GET, POST or Authorization
        Transport transport = endpoint.getTransport();
        
        // via HMAC-SHA1 or PLAINTEXT
        Signature sig = endpoint.getSignature();
        
        // nonce and timestamp generator
        NonceAndTimestamp nts = SimpleNonceAndTimestamp.getDefault();
        
        // http connector
        HttpConnector connector = SimpleHttpConnector.getDefault();
        
        // returns the http response
        return transport.send(params, endpoint, token, exchange, nts, sig, connector);
    }
    
    public static Response doGET(UrlEncodedParameterMap params, Endpoint endpoint, Token token) 
    throws IOException
    {
        // via HMAC-SHA1 or PLAINTEXT
        Signature sig = endpoint.getSignature();
        
        // nonce and timestamp generator
        NonceAndTimestamp nts = SimpleNonceAndTimestamp.getDefault();
        
        // http connector
        HttpConnector connector = SimpleHttpConnector.getDefault();
        
        // Authorization Header with the access_token
        Parameter authorizationHeader = new Parameter("Authorization", 
                HttpAuthTransport.getAuthHeaderValue(params, endpoint, token, nts,  sig));
        
        return connector.doGET(params.toStringRFC3986(), authorizationHeader);
    }
    
    private OpenIdInterceptor _interceptor;
    
    @Override
    protected void init()
    {
        _interceptor = (OpenIdInterceptor)getWebContext().getAttribute("openIdInterceptor");
    }
    
    @HttpResource(location="/hybrid/google/$")
    @Get
    public void service(RequestContext rc) throws IOException, ServletException
    {
        String type = rc.getPathElement(2);
        GoogleModule module = GoogleModule.get(type);
        if(module==null)
        {
            rc.getResponse().sendRedirect("/hybrid/google");
            return;
        }
        
        OpenIdUser user = RelyingParty.getInstance().getOpenIdUserManager().getUser(rc.getRequest());
        if(user==null || !type.equals(user.getAttribute("google_type")))
        {
            // we expect it to be google so skip discovery to speed up the openid process
            user = OpenIdUser.populate(GOOGLE_IDENTIFIER, YadisDiscovery.IDENTIFIER_SELECT, 
                    GOOGLE_OPENID_SERVER);
            user.setAttribute("google_scope", module.getScope());
            user.setAttribute("google_type", type);
        }
        
        rc.getRequest().setAttribute(OpenIdUser.ATTR_NAME, user);
        
        if(!_interceptor.preHandle(rc))
            return;
        
        System.err.println("info: " + user.getAttribute("info"));
        
        
        String key = (String)user.getAttribute("token_k");
        String secret = (String)user.getAttribute("token_s");
        Token token = new Token(__google.getConsumerKey(), key, secret, Token.ACCESS_TOKEN);
        UrlEncodedParameterMap serviceParams = new UrlEncodedParameterMap(module.getUrl());
        
        Response serviceResponse = doGET(serviceParams, __google, token);
        BufferedReader br = new BufferedReader(new InputStreamReader(serviceResponse.getInputStream(), "UTF-8"));
        rc.getResponse().setContentType("text/xml");
        PrintWriter pw = rc.getResponse().getWriter();
        for(String line=null; (line=br.readLine())!=null;)
            pw.append(line);
    }    

}


```