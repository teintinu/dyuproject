<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Login</title>
<style type="text/css">
    #container {
      padding: 1em;      
    }
    
    #container p {
      font-size: 1.2em;
      font-family: Tahoma,Verdana;
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
  <div style="color:red;font-size:1.4em">&nbsp;${openid_servlet_filter_msg}</div>
  <p>Login with your <span style="color:orange">openid</span></p>
  <form method="POST">
    <input id="openid_identifier" name="openid_identifier" type="text" size=80/>
    <input class="btn" type="submit" value="send"/>
  </form>
  <p><span style="color:green;font-size:1em">https://www.google.com/accounts/o8/id</span><span> for google accounts</span></p>
  <p>or enter <span style="color:green;font-size:1em">your_username@gmail.com</span></p>
  <p><a href="/home/">HomeServlet</a></p>
  <p><a href="/home.jsp">home.jsp</a> <span style="font-size:.8em">(filtered by OpenIdServletFilter)</span></p>
</div>

</body>
</html>