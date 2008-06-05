<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>index</title>  
  <style type="text/css">
    body, p, ul, table {
	  margin: 0;
	  padding: 0;
	  border: 0;
	}
	button {
	  padding: 0;
	}
	div#wrapper {
	  margin: 10px auto;
	  width: 900px;
	  font-family: Tahoma, Verdana, Arial;
	  font-size: 1em;
	  background-color: #ccc;
	}
	div#inner {
	  background-color: white;
	  text-align: left;
	  margin: 5px;
	}
    div#top {
	  background-color: #ccc;
	}
	div#top p {
	  padding: 5px;
	  font-size: 1.4em;
	}
	div#content {
	  background-color: #fff;
	}
	div#bottom {
	  background-color: #ccc;
	}
	div#left {
	  float: left;
	  width: 20%;
	  padding: 5px;
	}
	div#mid {
	  float: left;
	  width: 55%;      
	  background-color: #ccc;
	}
	div#right {
	  float: left;
	  width: 25%
      padding: 5px;	  
	}	
	ul.simple {
	  list-style: none;	  
	}
	p.msg {
	  color: red;
	  font-style: italic;
	  font-family: Arial;
	}
	table.bean_table {
	  
	}
	table.bean_table tr {
	  
	}
	td.left_col {
	  font-size: 0.8em;
	  color: green;
	}
	span.big_label {
	  font-size: 1.1em;  
	  color: green;
	}
	input.disabled {
	  display: none;
	}
	tr.header {
	  font-family: Georgia, Arial;	  
	  font-weight: bold;
	}
	a.btn {
      text-align: center;
      color: #7f93bc;
      margin: 0 1px;
      padding: 0 1em; 
      cursor: pointer;
      background-color: white;
      font-weight: bold;
      font-size: 0.8em;
      text-decoration: none;
      border: 1px solid #ccc; 
	}
	a.btn:hover {
	  color: green;
	}
  </style>
</head>
<body>
<div id="wrapper">
  <div id="inner">
    <div id="top">	
	  <p>To-do List <span style="color:#fff">Demo</span></p>
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
		  <div style="margin:0 5px;padding:0 5px;background-color:#fff">		    
		    <table cellspacing="0" cellpadding="0">
			  <tr><td><jsp:doBody/></td></tr>
			</table>
			<br/><br/><br/><br/>
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