<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>OAuth Service Provider Demo</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<c:url value="/css/style.css"/>" type="text/css" media="screen">
</head>
<body>
<div id="wrap">

<div id="header">
  <div id="head">
    <h1>OAuth Service Provider Demo</h1>
  </div>
</div>

<div id="main" class="clearfix">
<div class="box">  
  <div id="login_box">
    <div><p class="msg"><span id="feedback">${msg}</span></p></div>
    <div class="clearfix bar">
      <div>
        <span class="highlight large">Login</span>
      </div>
    </div>
    <form id="form_login" name="login" class="box" method="POST" action="<c:url value="/oauth/AuthorizeToken/"/>" >
      <ul>
        <li>
          <div><label>Username</label></div>
          <div name="Username"><input name="username" value="${param.username}" required="true"/></div>
        </li>
        <li>
          <div><label>Password</label></div>
          <div name="Password"><input name="password" type="password" value="${param.password}" required="true"/></div>
        </li>
        <li>
          <div><input type="hidden" name="oauth_token" value="${requestToken}"/></div>
          <div><button type="submit" class="submit"><div>Submit</div></button></div>
        </li>
      </ul>
    </form>
  </div>
</div>
</div>

</div>

<div id="footer">
  <div id="foot">    
    <p>&copy; Copyright 2009 <a href="http://code.google.com/p/dyuproject/">dyuproject</a>  All rights reserved.</p>
  </div>
</div>
</body>
</html>