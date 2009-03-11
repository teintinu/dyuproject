<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Hello World! from JSP</title>
</head>
<body>
<div>  
  <p><span style="color:green;font-size:1.4em">Hello World! from JSP</span></p>
  <span><a href="<c:url value="/"/>">back</a></span><br/>
  <p>    
    Message: <span style="color:red">${message}</span>
  </p>
  <p>
    Timestamp: <span style="color:red">${timestamp}</span>
  </p>
</div>
</body>