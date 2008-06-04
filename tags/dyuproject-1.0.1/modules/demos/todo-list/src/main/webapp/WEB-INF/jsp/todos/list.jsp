<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">Todos</span>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>	
  </div>
  <div>
    <table cellspacing="5">
	  <tr class="header">
	    <td>Title</td>
		<td>Content</td>
		<td>Assignee</td>
		<td>Action</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	  </tr>
      <c:forEach var="t" items="${todos}">
	  <tr>	    
	    <td><a href="<c:url value="/todos/${t.id}"/>">${t.title}</a></td>
	    <td>${t.content}</td>
		<td><a href="<c:url value="/users/${t.user.id}"/>">${t.user.firstName} ${t.user.lastName}</a></td>
	    <td>
		  <c:if test="${t.completed==false}"><a class="btn" href="<c:url value="/todos/complete?id=${t.id}"/>">complete</a></c:if>
		</td>
		<td><a href="<c:url value="/todos/edit?id=${t.id}"/>">edit</a></td>
		<td><a href="<c:url value="/todos/delete?id=${t.id}"/>">delete</a></td>

	  </tr>
	  </c:forEach>
	</table>
  </div>  
</div>
</tl:page>