Enabling openid simple registration extension

Simple Registration Extension

See http://openid.net/specs/openid-simple-registration-extension-1_1-01.html

```
    static
    {
        RelyingParty.getInstance()
        .addListener(new SRegExtension()
            .addExchange("email")
            .addExchange("country")
            .addExchange("language")
        )
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        // we assume that the request has been successfully handled by the OpenIdServletFilter
        OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
        Map<String,String> sreg= SRegExtension.get(user);
        String email = sreg.get("email");
        String country = sreg.get("country");
        String language = sreg.get("language");
        
        // do something with your user's data
    }
```