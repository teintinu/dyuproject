**REST Configuration**

For a sample REST service, see [HelloWorldService](HelloWorldService.md)

_**3 ways to setup the REST services, resources and interceptors:**_

**1.** Through the servlet's init-param:

```

<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app 
   xmlns="http://java.sun.com/xml/ns/javaee" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" 
   version="2.5" metadata-complete="true">
   
    <servlet>
        <servlet-name>rest</servlet-name>
        <servlet-class>com.dyuproject.web.rest.RESTServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
        <init-param>
          <param-name>webContext</param-name>
          <param-value>com.dyuproject.web.rest.service.RESTServiceContext</param-value>
        </init-param>          
        <!-- separated by comma or semi colon -->
        <init-param>
          <param-name>services</param-name>
          <param-value>
            com.dyuproject.demos.helloworld.HelloWorldService
          </param-value>
        </init-param>
        <init-param>
          <param-name>resources</param-name>
          <param-value>
            com.example.SomeResource@/someresource,
            com.example.AcmeResource@/acme
          </param-value>
        </init-param>
        <init-param>
          <param-name>interceptors</param-name>
          <param-value>
            com.dyuproject.demos.helloworld.DigestAuthInterceptor@/helloworld/protected,
            com.dyuproject.demos.helloworld.SimpleInterceptor@/foo/bar/baz,
            com.dyuproject.demos.helloworld.SimpleInterceptorWC1@/foo/bar/*,
            com.dyuproject.demos.helloworld.SimpleInterceptorWC2@/foo/**
          </param-value>
        </init-param>        
    </servlet>

    <servlet-mapping>
        <servlet-name>rest</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

    
</web-app>

```
**2.** Through JSON IOC (as of 1.1.3 and up)

WEB-INF/web.xml
```
  <listener>
    <listener-class>com.dyuproject.web.rest.ApplicationServletContextListener</listener-class>
  </listener>

```

WEB-INF/application.json
```
{
  "loginInterceptor":
  {
    "class": "com.dyuproject.demos.todolist.service.LoginInterceptor"
  },
  "webContext":
  {
    "class": "com.dyuproject.web.rest.service.RESTServiceContext",
    "interceptors":
    {
      "/overview": $loginInterceptor,
      "/account/**": $loginInterceptor
    },
    "services":
    [
      {
        "class": "com.dyuproject.demos.todolist.service.MainService"
      },
      {
        "class": "com.dyuproject.demos.todolist.service.UserService"
      },
      {
        "class": "com.dyuproject.demos.todolist.service.TodoService"
      }
    ]
  }
}

```

**3.** Through Spring IOC

```

    <dependency>
        <groupId>com.dyuproject.ext</groupId>
        <artifactId>dyuproject-spring-ioc</artifactId>
        <version>1.1.0</version>
    </dependency>  

```

WEB-INF/web.xml
```

    <listener>
      <listener-class>com.dyuproject.web.rest.SpringServletContextListener</listener-class>
    </listener>
```

WEB-INF/webContext.xml
```

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">


<beans>
  
  <bean id="loginInterceptor" class="com.dyuproject.demos.todolist.service.LoginInterceptor">
      </bean>
  
  <bean id="webContext" class="com.dyuproject.web.rest.service.RESTServiceContext">    
    <property name="interceptors">
      <map>
        <entry key="/overview" value-ref="loginInterceptor"/>
      </map>
    </property>    

    <property name="services">    
      <list>        
        <bean class="com.dyuproject.demos.todolist.service.MainService"/>
        <bean class="com.dyuproject.demos.todolist.service.TodoService"/>
        <bean class="com.dyuproject.demos.todolist.service.UserService"/>
      </list>
    </property>  
  </bean>

</beans>

```

**_session config_**

By default, session is turned off.

To enable, configure it on WEB-INF/**env.properties**:

```

session.enabled=true

session.cookie.name=your_app_name

session.cookie.secretKey=your_secret_key

#----------optional------------
#session.cookie.maxAge=3600
#default value: 3600 (1 hour for the session to expire)

#session.cookie.path=/

#session.cookie.domain=some_domain

#session.cookie.include.remoteAddress=true
#default value: false  (setting to true prevents session hijacking)

```

If the session is enabled, getWebContext().getSession(request); will return a session if it already exists.

Pass the second argument(boolean) getWebContext().getSession(request, true); to force it to create a session if ever none exists.

**_mime types_**

To allow a certain mime type to be handled, configure it on WEB-INF/**mime.properties**:

```

xml=text/xml
json=text/plain

#html=text/html
#csv=text/plain

```



## `MainService.java` ##
```
public class MainService
{
    @HttpResource(location="/")
    @Get
    public void root(RequestContext rc) throws IOException, ServletException
    {
        rc.getResponse().setContentType("text/html");
        getWebContext().getJSPDispatcher().dispatch("index.jsp", rc.getRequest(), rc.getResponse());
    }
    
}
```