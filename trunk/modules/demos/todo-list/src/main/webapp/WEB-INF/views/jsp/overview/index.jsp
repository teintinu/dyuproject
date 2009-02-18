<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix bar">
    <div class="rightcol">
      <a href="<c:url value="/users/${user.id}/todos/new"/>">New Todo</a>
      <span class="separator">&nbsp;|&nbsp;</span>
      <a href="<c:url value="/logout"/>">Logout</a>
    </div>
    <div><span class="highlight large">Your Current Todos</span></div>
  </div>
  <table id="table_overview" class="stretch" cellspacing="5">
    <tbody>
    <c:set var="open" value="0" scope="page"/>
    <c:set var="total" value="0" scope="page"/>
    <c:forEach var="t" items="${user.todos}" varStatus="status">
    <c:set var="total" value="${status.count}" scope="page"/>
    <c:if test="${!t.completed}">
      <c:set var="open" value="${open+1}" scope="page"/>
      <tr class="item">
        <td>
          <div class="clearfix item">
            <div class="rightcol">
              <a class="btn" href="<c:url value="/todos/${t.id}/complete"/>">complete</a>
            </div>
            <div class="leftcol">
              <div><a class="title" href="<c:url value="/todos/${t.id}"/>">${t.title}</a></div>
              <div><span class="copntent">${t.content}</span></div>
            </div>
          </div>
          <hr size="1"/>
        </td>
      </tr>
    </c:if>
    </c:forEach>
    </tbody>
  </table>
  <div>
    <p>
      You have <a class="highlight" href="<c:url value="/users/${user.id}/todos/current"/>">${open}</a> current todos. 
      <a class="highlight" href="<c:url value="/users/${user.id}/todos/completed"/>">${total-open}</a> completed. 
      <a class="highlight" href="<c:url value="/users/${user.id}/todos"/>">${total}</a> total.
    </p>
  </div>
</div>
</tl:page>