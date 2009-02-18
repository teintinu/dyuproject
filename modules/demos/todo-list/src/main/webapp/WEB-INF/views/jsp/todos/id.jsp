<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix item todo">
    <div class="clearfix bar">
      <div class="rightcol">
        <c:if test="${todo.completed==false}">
          <a href="<c:url value="/todos/${todo.id}/complete"/>">Complete</a>
          <span class="separator">&nbsp;|&nbsp;</span>
        </c:if>
        <a href="<c:url value="/todos/${todo.id}/edit"/>">Edit</a>
        <span class="separator">&nbsp;|&nbsp;</span>
        <a href="<c:url value="/todos/${todo.id}/delete"/>">Delete</a>
      </div>
      <div class="leftcol">
        <div><span class="large highlight">${todo.title}</span></div>
      </div>
    </div>
    <div>
      <span class="content">${todo.content}</span>
    </div>
  </div>
</div>
</tl:page>