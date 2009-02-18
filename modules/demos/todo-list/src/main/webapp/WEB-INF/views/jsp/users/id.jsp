<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix item user">
    <div class="clearfix bar">
      <div class="rightcol">
        <a href="<c:url value="/users/${user.id}/todos/new"/>">New Todo</a>
        <span class="separator">&nbsp;|&nbsp;</span>
        <a href="<c:url value="/users/${user.id}/edit"/>">Edit</a>
        <span class="separator">&nbsp;|&nbsp;</span>
        <a href="<c:url value="/users/${user.id}/delete"/>">Delete</a>
      </div>
      <div class="leftcol"><span class="large highlight">${user.firstName} ${user.lastName}</span></div>
    </div>
    <div><span class="large highlight">Todos</div>
    <div>
      <c:choose>
      <c:when test="${empty user.todos}"><tr><td><span style="padding: 0 5px;">none</span></td></tr></c:when>
      <c:otherwise>
      <table id="table_todo" class="stretch" cellspacing="5">
        <thead>
          <tr>
          <td><span>Title</span></td>
          <td><span>Content</span></td>
          <td></td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        </thead>
        <tbody class="items">
          <c:forEach var="t" items="${user.todos}">
          <tr>
            <td><a class="title" href="<c:url value="/todos/${t.id}"/>">${t.title}</a></td>
            <td><span class="content">${t.content}</span></td>
            <td>
              <c:if test="${t.completed==false}"><a class="btn" href="<c:url value="/todos/${t.id}/complete"/>">complete</a></c:if>
            </td>
            <td><a class="btn" href="<c:url value="/todos/${t.id}/edit"/>">edit</a> </td>
            <td><a class="btn" href="<c:url value="/todos/${t.id}/delete"/>">delete</a> </td>
          </tr>
          </c:forEach>
      </table>
      </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
</tl:page>