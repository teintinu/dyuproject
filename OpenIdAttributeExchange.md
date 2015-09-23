**OpenIdAttributeExchange**

`AxSchema Extension` for http://www.axschema.org/types/

See also http://openid.net/specs/openid-attribute-exchange-1_0-05.html

```

    static
    {
        RelyingParty.getInstance()
        .addListener(new AxSchemaExtension()
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
        Map<String,String> axschema = AxSchemaExtension.get(user);
        String email = axschema.get("email");
        String country = axschema.get("country");
        String language = axschema.get("language");
        
        // do something with your user's data
    }


```