# OAuthConsumer #

### Signing requests manually with an already-authorized token ###

```

    /** 
     * The returned value is the value of the authorization header.
     * Normally you would use this for POST/PUT requests with no request parameters.
     * If you have request parameters, use the other method and fill the parameter map with it.
     * E.g
     * UrlEncodedParameterMap params = new UrlEncodedParameterMap(serviceUrl)
     *     .add("param_key", "param_value")
     *     .add("another_key", "another_value");
     * String sig = sign(params, httpMethod, context, ep, authorizedToken);
     */
    public static String sign(String serviceUrl, String httpMethod, ConsumerContext context, 
            Endpoint ep, Token authorizedToken) throws IOException
    {
        return sign(new UrlEncodedParameterMap(serviceUrl), httpMethod, context, ep, authorizedToken);
    }
    
    public static String sign(UrlEncodedParameterMap params, String httpMethod, ConsumerContext context, 
            Endpoint ep, Token authorizedToken) throws IOException
    {
        context.getNonceAndTimestamp().put(params, authorizedToken.getCk());
        params.put(Constants.OAUTH_CONSUMER_KEY, ep.getConsumerKey());
        params.put(Constants.OAUTH_TOKEN, authorizedToken.getKey());
        params.put(Constants.OAUTH_SIGNATURE_METHOD, ep.getSignature().getMethod());
        return ep.getSignature().sign(ep.getConsumerSecret(), authorizedToken.getSecret(), 
                Signature.getBase(params, httpMethod));
    }

```

### Desktop/Embedded environment ###

`consumer_google.properties`
```
domain = www.google.com
consumer_key = your_consumer_key
consumer_secret = your_consumer_secret
secure = true
request_token_url = /accounts/OAuthGetRequestToken
authorization_url = /accounts/OAuthAuthorizeToken
access_token_url = /accounts/OAuthGetAccessToken
signature_method = HMAC-SHA1
#Authorization, POST or GET
transport_name = Authorization
```

`DesktopClient.java`
```
public class DesktopClient
{
    
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
    
    public static void main(String[] args) throws Exception
    {
        
        Endpoint googleEndpoint = Endpoint.load("consumer_google.properties");
        
        Token token = new Token(googleEndpoint.getConsumerKey());
        
        UrlEncodedParameterMap requestTokenParams = new UrlEncodedParameterMap()            
            .add(Constants.OAUTH_CALLBACK, "http://localhost:8080/callbackUrl")
            .add("scope", "http://www.google.com/m8/feeds/");
        
        // fetch request token
        Response requestTokenResponse = fetchToken(TokenExchange.REQUEST_TOKEN, requestTokenParams, 
                googleEndpoint, token);
        
        assert(requestTokenResponse.getStatus()==200
                && token.getState()==Token.UNAUTHORIZED
                && "true".equals(token.getAttribute(Constants.OAUTH_CALLBACK_CONFIRMED)));
        
        
        /* ===================================================================================== */
        // Redirect to service provider url
        
        String authUrl = Transport.getAuthUrl(googleEndpoint.getAuthorizationUrl(), token);
        
        /* ===================================================================================== */
        // After receiving callback of authToken, oauthVerifier
        
        String oauthToken = "oauth_token from callback";
        String oauthVerifier = "oauth_verifier from callback";
        
        // check that its authorized
        assert(token.authorize(oauthToken, oauthVerifier));
        
        UrlEncodedParameterMap accessTokenParams = new UrlEncodedParameterMap();
        
        // fetch access token
        Response accessTokenResponse = fetchToken(TokenExchange.ACCESS_TOKEN, accessTokenParams, 
                googleEndpoint, token);
        
        assert(accessTokenResponse.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN);
        
        /* ===================================================================================== */
        // You are now ready to make an authorized web service request
        
        UrlEncodedParameterMap serviceParams = new UrlEncodedParameterMap("http://www.google.com/m8/feeds/contacts/default/full");
        
        Response serviceResponse = doGET(serviceParams, googleEndpoint, token);
        
        // get header
        String someHeader = serviceResponse.getHeader("someHeader");
        
        // parse data
        InputStream in = serviceResponse.getInputStream();

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
    
    static Response doPOST(UrlEncodedParameterMap params, Endpoint endpoint, Token token) 
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
        
        if(params.isEmpty())
            return connector.doPOST(params.getUrl(), authorizationHeader, (Map<?,?>)null, null);
        
        byte[] data = params.getUrlFormEncodedBytesRFC3986(Constants.ENCODING);
        
        return connector.doPOST(params.getUrl(), authorizationHeader, Constants.ENCODING, data);
    }

}

```


---


### Web/Servlet Environment ###

`oauth_consumer.properties`
```
oauth.consumer.endpoint.domains = www.google.com, api.login.yahoo.com
#oauth.consumer.token.manager = com.dyuproject.oauth.manager.CookieBasedTokenManager
#oauth.token.manager.cookie.security.secret_key = secret

#google
www.google.com.consumer_key = your_consumer_key
www.google.com.consumer_secret = your_consumer_secret
www.google.com.secure = true
www.google.com.request_token_url = /accounts/OAuthGetRequestToken
www.google.com.authorization_url = /accounts/OAuthAuthorizeToken
www.google.com.access_token_url = /accounts/OAuthGetAccessToken
www.google.com.signature_method = HMAC-SHA1
www.google.com.transport_name = POST

#yahoo 
# MyBlogLog, Yahoo! Contacts, Yahoo! Profiles, Yahoo! Status, Yahoo! Updates, Wretch
api.login.yahoo.com.consumer_key = your_consumer_key
api.login.yahoo.com.consumer_secret = your_consumer_secret
api.login.yahoo.com.secure = true
api.login.yahoo.com.request_token_url = /oauth/v2/get_request_token
api.login.yahoo.com.authorization_url = /oauth/v2/request_auth
api.login.yahoo.com.access_token_url = /oauth/v2/get_token
api.login.yahoo.com.signature_method = PLAINTEXT
api.login.yahoo.com.transport_name = GET
```

`GoogleContactsServlet.java`
```
public class GoogleContactsServlet extends HttpServlet
{

    static final String CONTACTS_SERVICE_URL = "http://www.google.com/m8/feeds/contacts/default/full";
    
    Consumer _consumer = Consumer.getInstance();
    Endpoint _googleEndpoint = _consumer.getEndpoint("www.google.com");
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        Token token = _consumer.getToken(_googleEndpoint.getConsumerKey(), request);
        switch(token.getState())
        {
            case Token.UNITIALIZED:
                UrlEncodedParameterMap params = new UrlEncodedParameterMap()
                    .add("scope", "http://www.google.com/m8/feeds/")
                    .add(Constants.OAUTH_CALLBACK, request.getRequestURL().toString());
                    
                Response r = _consumer.fetchToken(_googleEndpoint, params, TokenExchange.REQUEST_TOKEN, 
                        token);
                if(r.getStatus()==200 && token.getState()==Token.UNAUTHORIZED)
                {
                    // unauthorized request token
                    _consumer.saveToken(token, request, response);
                    StringBuilder urlBuffer = Transport.buildAuthUrl(
                            _googleEndpoint.getAuthorizationUrl(), token);
                    Transport.appendToUrl("hd", "default", urlBuffer);
                    response.sendRedirect(urlBuffer.toString());
                }
                break;
                
            case Token.UNAUTHORIZED:
                if(token.authorize(request.getParameter(Constants.OAUTH_TOKEN), 
                        request.getParameter(Constants.OAUTH_VERIFIER)))
                {
                    if(fetchAccessToken(token, request, response))
                        queryGoogleContacts(token, request, response);
                    else
                        _consumer.saveToken(token, request, response);
                }
                break;
                
            case Token.AUTHORIZED:
                if(fetchAccessToken(token, request, response))
                    queryGoogleContacts(token, request, response);
                break;
                
            case Token.ACCESS_TOKEN:
                queryGoogleContacts(token, request, response);
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }
    
    public boolean fetchAccessToken(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        // authorized request token
        UrlEncodedParameterMap params = new UrlEncodedParameterMap();
        
        Response r = _consumer.fetchToken(_googleEndpoint, params, TokenExchange.ACCESS_TOKEN, token);
        if(r.getStatus()==200 && token.getState()==Token.ACCESS_TOKEN)
        {
            // access token
            _consumer.saveToken(token, request, response);
            return true;
        }
        return false;
    }
    
    protected void queryGoogleContacts(Token token, HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        Response r = serviceGET(CONTACTS_SERVICE_URL, _consumer.getConsumerContext(), _googleEndpoint, 
                token, request, response);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), "UTF-8"));
        response.setContentType("text/xml");
        PrintWriter pw = response.getWriter();
        for(String line=null; (line=br.readLine())!=null;)
            pw.append(line);
    }
    
    public static Response serviceGET(String serviceUrl, ConsumerContext context, Endpoint ep, 
            Token token, HttpServletRequest request, HttpServletResponse response) 
            throws IOException
    {
        HttpConnector connector = context.getHttpConnector();
        UrlEncodedParameterMap params = new UrlEncodedParameterMap(serviceUrl);
        context.getNonceAndTimestamp().put(params, token.getCk());
        Parameter authorization = new Parameter("Authorization", 
                HttpAuthTransport.getAuthHeaderValue(params, ep, token, 
                context.getNonceAndTimestamp(),  ep.getSignature()));
        return connector.doGET(params.getUrl(), authorization);
        
    }
    
}
```