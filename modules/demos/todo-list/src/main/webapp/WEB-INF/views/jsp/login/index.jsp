<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">  
  <div id="login_box">
    <div><p class="msg"><span id="feedback">${msg}</span></p></div>
    <div class="clearfix bar">
      <div class="rightcol">
        <a href="<c:url value="/users/new"/>">Register</a>
      </div>
      <div>
        <span class="highlight large">Login</span>
      </div>
    </div>
    <form method="POST" action="<c:url value="/auth"/>" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
      <table id="table_login" cellspacing="5">
        <tr>
          <td>Username</td>
          <td name="Username"><input name="username" value="${param.username}" required="true"/></td>
        </tr>
        <tr>
          <td>Password</td>
          <td name="Password"><input name="password" type="password" value="${param.password}" required="true"/></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><input type="submit" value="Submit"/></td>
        </tr>
      </table>
    </form>
  </div>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>