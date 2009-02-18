<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix bar">
    <div class="rightcol"></div>
    <div class="leftcol"><span class="highlight">${action} User</span></div>
  </div>
  <form id="form_user" name="user" method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <table cellspacing="5">
      <tbody>
        <tr>
          <td><span>First Name</span></td>
          <td name="First Name"><input name="firstName" type="text" value="${user.firstName}" required="true"/></td>
        </tr>
        <tr>
          <td><span>Last Name</span></td>
          <td name="Last Name"><input name="lastName" type="text" value="${user.lastName}" required="true"/></td>
        </tr>
        <tr>
          <td><span>Email</span></td>
          <td name="Email"><input name="email" type="text" value="${user.email}" required="true"/></td>
        </tr>
        <c:if test="${action=='New'}">
        <tr>
          <td><span>Username</span></td>
          <td name="Username"><input name="username" type="text" value="${user.username}" required="true"/></td>
        </tr>
        <tr>
          <td><span>Password</span></td>
          <td name="Password"><input name="password" type="password" value="${user.password}" required="true"/></td>
        </tr>
        <tr>
          <td><span>Confirm Password</span></td>
          <td name="Confirm Password"><input name="confirmPassword" type="password" required="true"/></td>
        </tr>
        </c:if>
        <tr>
          <td>&nbsp;</td>
          <td><input type="submit" value="Submit"/></td>
        </tr>
      </tbody>
    </table>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>