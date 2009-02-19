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
        <a href="<c:url value="/users/${user.id}/todos"/>">Todos</a>
        <span class="separator">&nbsp;|&nbsp;</span>
        <a href="<c:url value="/users/${user.id}/edit"/>">Edit</a>
      </div>
      <div class="leftcol"><span class="large highlight">${user.username}</span></div>
    </div>
  </div>
  <div class="clearfix">
    <ul class="padded">
      <li>
        <div>First Name</div>
        <div><span class="highlight2">${user.firstName}</span></div>
        <hr size="1"/>
      </li>
      <li>
        <div>Last Name</div>
        <div><span class="highlight2">${user.lastName}</span></div>
        <hr size="1"/>
      </li>
      <li>
        <div>Email</div>
        <div><span class="highlight2">${user.email}</span></div>
        <hr size="1"/>
      </li>
    </ul>
  </div>
</div>
</tl:page>