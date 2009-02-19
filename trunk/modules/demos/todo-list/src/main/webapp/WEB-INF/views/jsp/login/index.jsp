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
    <form id="form_login" name="login" class="box" method="POST" action="<c:url value="/auth"/>" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
      <ul>
        <li>
          <div><label>Username</label></div>
          <div name="Username"><input name="username" value="${param.username}" required="true"/></div>
        </li>
        <li>
          <div><label>Password</label></div>
          <div name="Password"><input name="password" type="password" value="${param.password}" required="true"/></div>
        </li>
        <li>
          <div><button type="submit" class="submit"><div>Submit</div></button></div>
        </li>
      </ul>
    </form>
  </div>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>