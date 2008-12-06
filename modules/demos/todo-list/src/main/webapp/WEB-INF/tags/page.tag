<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>todo-list</title>  
  <style type="text/css">
    @import url(<c:url value="/css/todo-list.css"/>);
  </style>
</head>
<body>
<div id="wrapper">
  <div id="inner">
    <div id="top">	
	  <div style="float:left;width:50%"><p>To-do List <a class="simple" href="<c:url value="/"/>">Demo</a></p></div>
	  <div style="float:right;width:50%;font-size:0.8em;text-align:right;">
	    <div>Powered by: <a class="simple" href="http://code.google.com/p/dyuproject/">dyuproject</a></div>
		<div>
		  <span>Project <a class="simple" href="http://dyuproject.googlecode.com/svn/trunk/">source</a></span>
		  <span>&nbsp;|&nbsp;</span>
		  <span>Demo <a class="simple" href="http://dyuproject.googlecode.com/svn/trunk/modules/demos/todo-list">source</a></span>
		</div>
	  </div>
	  <div style="clear:both"></div>
	</div>
	<div id="content">
	  <div align="justify">
	    <div id="left">
		  <div>
		    <ul class="simple">
			  <li><a href="<c:url value="/"/>">home</a></li>
			  <li><a href="<c:url value="/overview"/>">overview</a></li>
		      <li><a href="<c:url value="/users"/>">users</a></li>
			  <li><a href="<c:url value="/todos"/>">todos</a></li>
		    </ul>
		  </div>
		</div>
		<div id="mid">
		  <div style="margin:0 5px;padding:0 5px;padding-bottom:60px;background-color:#fff">
		    <table cellspacing="0" cellpadding="0">
			  <tr><td><jsp:doBody/></td></tr>
			</table>
		  </div>
		</div>
		<div id="right">
		  <div style="padding:5px">
		  <c:if test="${!empty cs}">
		    <a href="<c:url value="/logout"/>">logout</a>
		  </c:if>
		  </div>
		</div>
		<div style="clear:both"></div>
	  </div>
	</div>
	<div id="bottom">
	  <center><p style="padding:2px">&copy; Copyright 2008 <a href="http://code.google.com/p/dyuproject/">dyuproject</a>.  All rights reserved</p></center>
	</div>
  </div>
</div>  
</body>