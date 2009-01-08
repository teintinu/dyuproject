<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Login</title>
<style type="text/css">
    body {
      font: 1.2em Verdana;
      background: background:url(http://davidyu.googlecode.com/svn/images/bg.jpg) repeat;
    }
    input {
      font-size: 1em;
      padding: 0;
      margin: 0;
    }
    
    #container {
      padding: 1em;      
    }
    
    #openid_identifier {
      background-image: url(http://www.openid.net/login-bg.gif);
      background-position: 3px 2px;
      background-repeat: no-repeat;
      margin: 0;
      padding: 0.2em 0.2em 0.2em 20px;
      vertical-align: middle;
      width: 322px;
    }
</style>
</head>
<body>
<div style="float:right; padding: 1em;">
  <span style="font-size:0.8em;font-family=Tahoma,Verdana">Powered by <a href="http://code.google.com/p/dyuproject/">dyuproject</a></span>
</div>
<div id="container">
  <div style="color:red;font-size:1.4em">&nbsp;${openid_servlet_filter_msg}</div>
  <p style="color:orange">Login with your openid</p>
  <form method="POST">
    <input id="openid_identifier" name="openid_identifier" type="text" size=80/>
    <input class="btn" type="submit" value="send"/>
  </form>
  <p><span style="color:green;font-size:1em">https://www.google.com/accounts/o8/id</span><span> for google accounts</span></p>
  <p><a href="/home/">HomeServlet</a></p>
  <p><a href="/home.jsp">home.jsp</a> <span style="font-size:.8em">(filtered by OpenIdServletFilter)</span></p>
</div>

</body>
</html>