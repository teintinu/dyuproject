<%@ page session="false" %>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Home</title>
</head>
<body>
<div>
  <p><span style="color:green;font-size:1.4em"><a href="<c:url value="/"/>">Home</a></span></p>
  <p><span style="color:red;font-size:1.5em;font-style:italic">${message}</span><br/>
  <div>
    <p>
      html | <a href="<c:url value="/helloworld"/>">helloworld</a>
    </p>
    <p>
      html | <a href="<c:url value="/velocity/helloworld"/>">helloworld from velocity</a>
    </p>

  </div>
  <hr/>
  <div>
    <p style="font-weight:bold">/helloworld/{id}</p>
    <p>
      html | <a href="<c:url value="/helloworld/1"/>">helloworld/1</a>
    </p>
    <p>
      html | <a href="<c:url value="/velocity/helloworld/2"/>">helloworld/2 from velocity</a>
    </p>

  </div>
  <hr/>
  <div>
    <p style="font-weight:bold">form verbs</p>
    <p>    
      html | <a href="<c:url value="/bean/new"/>">bean/new</a>
    </p>
    <p>    
      html | <a href="<c:url value="/bean/1/edit"/>">bean/1/edit</a>
    </p>
    <p>    
      html | <a href="<c:url value="/bean/2/delete"/>">bean/2/delete</a>
    </p>
  </div>
  <hr/>
  <div>
    <p style="font-weight:bold">intereptors</p>
    <p>
      plain text | <a href="<c:url value="/foo"/>">/foo</a>
    </p>
    <p>
      plain text | <a href="<c:url value="/foo/bar"/>">/foo/bar</a>
    </p>
    <p>
      plain text | <a href="<c:url value="/foo/bar/baz"/>">/foo/bar/baz</a>
    </p>
  </div>
  <hr/>
  <div>
    <p style="font-weight:bold">page with authentication</p>
    <p><a href="<c:url value="/helloworld/protected"/>">protected</a></p>
    <p><b>credentials:</b> hello/world, foo/bar</p>
  </div>  
</div>
</body>