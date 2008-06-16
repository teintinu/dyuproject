<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">${action} Todo</span>
	<p>
	<c:choose>
	  <c:when test="${empty action}">
	  <span><a href="<c:url value="/todos/edit?id=${todo.id}"/>">Edit</a></span>
	  </c:when>
	  <c:otherwise>
	  <span><a href="<c:url value="/"/>">Back to Home</a></span>
	  </c:otherwise>
	</c:choose>
	</p>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>
	<p>Assigned to: <a href="<c:url value="/users/${todo.user.id}"/>">${todo.user.firstName} ${todo.user.lastName}</a></p>
  </div>
  <form method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">    
    <table height="100%" class="bean_table" cellspacing="4" cellpadding="0">	  
	  <tr>
	    <td class="left_col">Title</td>
		<td name="Title">${todo.title}</td>
	  </tr>
	  <tr>
	    <td class="left_col">Content</td>
		<td name="Content">${todo.content}</td>
	  </tr>  
    </table>
  </form>  
</div>
</tl:page>