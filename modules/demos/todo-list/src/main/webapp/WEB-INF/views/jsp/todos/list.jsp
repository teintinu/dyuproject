<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div class="clearfix bar">
    <div class="rightcol">
      <button class="link" onclick="window.location='<c:url value="/todos"/>'"><span>All</span></button>
      <button class="link" onclick="loadTodos(false);"><span>Current</span></button>
      <button class="link" onclick="loadTodos(true);"><span>Completed</span></button>
    </div>
    <div class="leftcol">
      <span class="highlight large">Todos</span> 
    </div>
  </div>
  <div>
    <p class="msg"><span id="feedback">${msg}</span></p>
  </div>
  <div>
    <table id="table_todo" class="todos stretch" cellspacing="5">
      <thead>
        <tr>
          <td><span>Title</span></td>
          <td><span>Content</span></td>
          <td><span>Assignee</span></td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </thead>
      <tbody class="items">
        <c:forEach var="t" items="${todos}">
        <tr class="item">
          <td><a class="title" href="<c:url value="/todos/${t.id}"/>">${t.title}</a></td>
          <td>${t.content}</td>
          <td><a href="<c:url value="/users/${t.user.id}"/>">${t.user.firstName} ${t.user.lastName}</a></td>
          <td>
            <c:if test="${t.completed==false}"><a class="btn" href="<c:url value="/todos/${t.id}/complete"/>">complete</a></c:if>
          </td>
          <td><a class="btn" href="<c:url value="/todos/${t.id}/edit"/>">edit</a> </td>
          <td><a class="btn" href="<c:url value="/todos/${t.id}/delete"/>">delete</a> </td>
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
<script type="text/javascript">
//<!--
function loadTodos(completed) {
  var path = window.location.toString();
  var last = path.charAt(path.length-1);
  if(completed) {
    if(path.indexOf('completed')==-1)
      path+='/completed';
    path=path.replace(/\/current/, '');
  }
  else {
    if(path.indexOf('current')==-1)
      path+='/current';
    path=path.replace(/\/completed/, '');
  }
  window.location = last=='/' ? path.substring(0, path.length-1) : path;
}
//-->
</script>
</tl:page>