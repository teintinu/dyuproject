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
  <form id="form_user" name="user" class="box" method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <ul>
      <li>
        <div><label>First Name</label></div>
        <div name="First Name"><input name="firstName" type="text" value="${user.firstName}" required="true"/></div>
      </li>
      <li>
        <div><label>Last Name</label></div>
        <div name="Last Name"><input name="lastName" type="text" value="${user.lastName}" required="true"/></div>
      </li>
      <li>
        <div><label>Email</label></div>
        <div name="Email"><input name="email" type="text" value="${user.email}" required="true"/></div>
      </li>
      <li>
        <div>
          <input type="hidden" name="sub" value="1"/>
          <button type="submit" class="submit"><div>Submit</div></button>
        </div>
      </li>
    </ul>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>