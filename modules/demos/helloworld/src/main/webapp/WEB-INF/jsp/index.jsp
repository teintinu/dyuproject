<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Welcome</title>
</head>
<body>
<div>
  <p><span style="color:green;font-size:1.4em">Welcome</span></p><br/>
  <p>    
    html | <a href="<c:url value="/helloworld"/>">helloworld</a>.
  </p>
  <p>    
    xml | <a href="<c:url value="/helloworld.xml"/>">helloworld</a>.
  </p>
  <p>    
    json | <a href="<c:url value="/helloworld.json"/>">helloworld</a>.
  </p>  
</div>
</body>