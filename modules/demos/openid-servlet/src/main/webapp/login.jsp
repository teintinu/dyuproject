<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Login</title>
<style type="text/css">
    #openid_identifier {      
      background-image: url(http://www.openid.net/login-bg.gif);
      background-position: 3px 2px;
      background-repeat: no-repeat;
      margin: 0;
      padding: 2px 2px 2px 20px;
      vertical-align: middle;
      width: 322px;
    }
</style>
</head>
<body>
<div>
  <p>Login with your openid</p>
  <form method="POST">
    <input id="openid_identifier" name="openid_identifier" type="text" size="40"/>
    <input type="submit" value="send"/>
  </form>
</div>
</body>
</html>