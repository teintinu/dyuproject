<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div class="clearfix bar">
    <div class="rightcol">
      <a href="<c:url value="/account/new"/>">Register</a>
    </div>
    <div class="leftcol">
      <span class="highlight large">Users</span> 
    </div>
  </div>
  <div>
    <p class="msg"><span id="feedback">${msg}</span></p>
  </div>
  <div>
    <table id="table_user" class="stretch" cellspacing="5">
      <thead>
        <tr>
          <td><span>First Name</span></td>
          <td><span>Last Name</span></td>
          <td><span>Email</span></td>
          <td>&nbsp;</td>
          <!--<td>&nbsp;</td>-->
        </tr>
      </thead>
      <tbody class="items">
        <c:forEach var="u" items="${users}">
        <tr>
          <td><a href="<c:url value="/users/${u.id}"/>">${u.firstName}</a></td>
          <td>${u.lastName}</td>
          <td>${u.email}</td>
          <td><a class="btn" href="<c:url value="/users/${u.id}/edit"/>">edit</a> </td>
          <!--<td><a class="btn" href="<c:url value="/users/${u.id}/delete"/>">delete</a> </td>-->
        </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</div>
</tl:page>