<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Home</title>
<style type="text/css">
    body {
      font: 1.2em Verdana;      
    }
    #container {
      padding: 1em;
      
    }    
</style>
</head>
<body>
<div id="container">
  Welcome <span style="color:green">${openid_user.claimedId}</span>
  <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/logout/">logout</a></span>
</div>
</body>
</html>