# `Openid RelyingParty QuickStart Guide` #

Let us learn by example ... this will help you create/deploy asap.
For extensions, see [OpenIdAttributeExchange](OpenIdAttributeExchange.md) and [OpenIdSimpleRegistration](OpenIdSimpleRegistration.md)

### `/WEB-INF/web.xml` ###
```
<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app 
   xmlns="http://java.sun.com/xml/ns/javaee" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" 
   version="2.5" metadata-complete="true">
  
  <filter>
    <filter-name>openid-filter</filter-name>
    <filter-class>com.dyuproject.openid.OpenIdServletFilter</filter-class>
    <load-on-startup>1</load-on-startup>
      <init-param>
        <param-name>forwardUri</param-name>
        <param-value>/WEB-INF/views/jsp/login.jsp</param-value> <!-- login page when user is not authenticated-->
      </init-param>
  </filter> 

  <filter-mapping>
    <filter-name>openid-filter</filter-name>
    <url-pattern>/user/*</url-pattern>
  </filter-mapping>

  <servlet>
    <servlet-name>home-servlet</servlet-name>
    <servlet-class>com.example.openid.HomeServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>home-servlet</servlet-name>
    <url-pattern>/user/home/</url-pattern>
  </servlet-mapping>
    
</web-app>
```

### `/WEB-INF/views/jsp/login.jsp` ###
```
<html>
<body>
  <div style="color:red;font-size:1.4em">${openid_servlet_filter_msg}</div>
  <p>Login with your <span style="color:orange">openid</span></p>
  <form method="POST">
    <input id="openid_identifier" name="openid_identifier" type="text" size=80/>
    <input class="btn" type="submit" value="send"/>
  </form>
</body>
</html>
```

### `HomeServlet.java` ###

```
public class HomeServlet extends HttpServlet
{
    static
    {
        RelyingParty.getInstance()
        .addListener(new AxSchemaExtension()
            .addExchange("email")
            .addExchange("country")
            .addExchange("language")
        );
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
        Map<String,String> axschema = AxSchemaExtension.get(user);
        String email = axschema.get("email");
        String country = axschema.get("country");
        String language = axschema.get("language");
        
        // do something with your user's data
    }
}
```

## Thats it! ##