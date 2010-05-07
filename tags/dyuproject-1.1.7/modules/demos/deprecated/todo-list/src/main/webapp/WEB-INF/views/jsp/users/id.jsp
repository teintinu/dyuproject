<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">User</span>
	<p>
	<c:choose>
	  <c:when test="${empty action}">	  
	  <span><a href="<c:url value="/users/edit?id=${user.id}"/>">Edit</a></span>
	  <span><a href="<c:url value="/users/${user.id}/todos/create"/>">New Todo</a></span>	  
	  </c:when>
	  <c:otherwise>
	  <span><a href="<c:url value="/"/>">Back to Home</a></span>
	  </c:otherwise>
	</c:choose>
	</p>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>	
  </div>
  <form method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">    
    <table height="100%" class="bean_table" cellspacing="4" cellpadding="0">	  
	  <tr>
	    <td class="left_col">First Name</td>
		<td name="First Name">${user.firstName}</td>
	  </tr>
	  <tr>
	    <td class="left_col">Last Name</td>
		<td name="Last Name">${user.lastName}</td>
	  </tr>
	  <tr>
	    <td class="left_col">Email</td>
		<td name="Email">${user.email}</td>
	  </tr>	  
    </table>
  </form>  
  <div>
    <span class="big_label">Todos</span>    
	<c:choose>
	  <c:when test="${empty user.todos}"><tr><td><span style="padding: 0 5px;">none</span></td></tr></c:when>      
	  <c:otherwise>
	  <table cellspacing="5">
	    <tr class="header">
		  <td>Title</td>
		  <td>Content</td>
		  <td>Completed</td>
		  <td>&nbsp;</td>
		  <td>&nbsp;</td>
		</tr>
	  <c:forEach var="t" items="${user.todos}">	
	    <tr>	    
	      <td><a href="<c:url value="/todos/${t.id}"/>">${t.title}</a></td>
	      <td>${t.content}</td>
		  <td>${t.completed}</td>
		  <td><a href="<c:url value="/todos/edit?id=${t.id}"/>">edit</a></td>
		  <td><a href="<c:url value="/todos/delete?id=${t.id}"/>">delete</a></td>
	    </tr>
	  </c:forEach>
	  </table>
	  </c:otherwise>
	</c:choose>
  </div>  
</div>
</tl:page>