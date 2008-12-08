<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Home</title>
</head>
<body>
<div>
  Welcome <span style="color:green">${user.claimedId}</span>
  <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/logout/">logout</a></span>
</div>
</body>
</html>