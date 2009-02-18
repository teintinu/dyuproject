<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>To-do List</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css" media="screen">
</head>
<body>
<div id="wrap">
<div id="header">
  <div id="head">
    <h1>To-do List</h1>
    <ul id="nav" class="clearfix">
      <li id="link_home"><a href="<c:url value="/"/>">Home</a></li>
      <li id="link_overview"><a href="<c:url value="/overview"/>">Overview</a></li>
      <li id="link_users"><a href="<c:url value="/users"/>">Users</a></li>
      <li id="link_todos"><a href="<c:url value="/todos"/>">Todos</a></li>
    </ul>
  </div>
</div>
<div id="main" class="clearfix">
<jsp:doBody/>
</div>
</div>

<div id="footer">
  <div id="foot">    
    <p>&copy; Copyright 2009 <a href="http://code.google.com/p/dyuproject/">dyuproject</a>  All rights reserved.</p>
  </div>
</div>

<script type="text/javascript">
//<!--
window.onload = function() {
  var path = window.location.toString();
  var firstSlash = path.indexOf("/", 9);
  if(path.length==firstSlash+1) 
    document.getElementById("link_home").className = "current";  
  else {
    var nextSlash = path.indexOf("/", firstSlash+1);
    document.getElementById("link_" + path.substring(firstSlash+1, nextSlash==-1 ? path.length : nextSlash)).className = "current"; 
  }
}
//-->
</script>
</body>
</html>