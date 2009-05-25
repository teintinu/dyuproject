<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Home</title>
<style type="text/css">
    #container {
      padding: 1em;
    }
    #container p {
      font-size: 1.2em;
      font-family: Tahoma,Verdana;
    }
</style>
</head>
<body>
<div id="container">
  <div style="float:right;width:50%;font-size:0.8em;text-align:right;">
    <div>Powered by: <a class="simple" href="http://code.google.com/p/dyuproject/">dyuproject</a></div>
    <div>
      <span>Project <a href="http://dyuproject.googlecode.com/svn/trunk/modules/openid/">source</a></span>
      <span>&nbsp;|&nbsp;</span>
      <span>Demo <a href="http://dyuproject.googlecode.com/svn/trunk/modules/demos/openid-servlet/">source</a></span>
    </div>
  </div>
  <div style="clear:both"></div>
  <p>
    Welcome <span style="color:green">${openid_user.claimedId}</span><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<c:url value="/logout/"/>">logout</a></span>
  </p>
  <p><a href="/popup_login.html">/popup_login.html</a> <span style="font-size:.8em">Signing in without refreshing or leaving the page</span></p>
</div>
</body>
</html>