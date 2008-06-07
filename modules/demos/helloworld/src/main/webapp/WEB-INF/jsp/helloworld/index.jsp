<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Hello World!</title>
</head>
<body>
<div>
  <p><span style="color:green;font-size:1.4em">Hello World!</span></p><br/>
  <p>    
    Message: <span>${helloWorldBean.message}</span>	
  </p>
  <p>
    VerbOrId: <span>${helloWorldBean.verbOrId}</span>
  </p>
  <p>
    <span>timestamp: ${helloWorldBean.timestamp}</span>
  </p>
</div>
</body>