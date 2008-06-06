<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">Users</span>
	<span><a href="<c:url value="/users/create"/>">Create</a></span>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>	
  </div>
  <div>
    <table cellspacing="5">
	  <tr class="header">
	    <td>First Name</td>
		<td>Last Name</td>
		<td>Email</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	  </tr>
      <c:forEach var="u" items="${users}">
	  <tr>	    
	    <td><a href="<c:url value="/users/${u.id}"/>">${u.firstName}</a></td>
	    <td>${u.lastName}</td>
	    <td>${u.email}</td>
		<td><a href="<c:url value="/users/edit?id=${u.id}"/>">edit</a></td>
		<td><a href="<c:url value="/users/delete?id=${u.id}"/>">delete</a></td>
	  </tr>
	  </c:forEach>
	</table>
  </div>
</div>
</tl:page>