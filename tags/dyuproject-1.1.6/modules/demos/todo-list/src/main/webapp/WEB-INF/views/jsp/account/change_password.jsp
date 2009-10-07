<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix bar">
    <div class="rightcol"></div>
    <div class="leftcol"><span class="highlight">Change Password</span></div>
  </div>
  <form id="form_change_password" name="change_password" class="box" method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <ul>
      <li>
        <div><label>Old Password</label></div>
        <div name="Old Password<"><input name="oldPassword" type="text" required="true"/></div>
      </li>
      <li>
        <div><label>New Password</label></div>
        <div name="New Password"><input name="newPassword" type="password" required="true"/></div>
      </li>
      <li>
        <div><label>Confirm Password</label></div>
        <div name="Confirm Password"><input name="confirmPassword" type="password" required="true"/></div>
      </li>
      <li>
        <div>
          <button type="submit" class="submit"><div>Submit</div></button>
        </div>
      </li>
    </ul>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>