<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">Your Current Todos</span>
	<p>
	  <span><a href="<c:url value="/users/${user.id}/todos/create"/>">Create Todo</a></span>
	</p>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>
  </div>
  <table cellspacing="5">
    <c:set var="open" value="0" scope="page"/>
	<c:set var="total" value="0" scope="page"/>
    <c:forEach var="t" items="${user.todos}" varStatus="status">
	  <c:set var="total" value="${status.count}" scope="page"/>
	  <c:if test="${!t.completed}">
	    <c:set var="open" value="${open+1}" scope="page"/>
        <tr>
		  <td><a href="<c:url value="/todos/${t.id}"/>">${t.title}</a></td>
		  <td><a class="btn" href="<c:url value="/todos/${t.id}/complete"/>">complete</a></td>
		</tr>
	  </c:if>
	</c:forEach>
  </table>
  <div>
    <p>
	  You have <a class="link1_2" href="<c:url value="/users/${user.id}/todos/current"/>">${open}</a> current todos. 
      <a class="link1_2" href="<c:url value="/users/${user.id}/todos/completed"/>">${total-open}</a> completed. 
      <a class="link1_2" href="<c:url value="/users/${user.id}/todos"/>">${total}</a> total.
    </p>
  </div>
</div>
</tl:page>