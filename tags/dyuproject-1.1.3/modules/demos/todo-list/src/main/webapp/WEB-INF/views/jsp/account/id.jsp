<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix item user">
    <div class="clearfix bar">
      <div class="rightcol">
        <a href="<c:url value="/account/change_password"/>">Change Password</a>
      </div>
      <div class="leftcol"><span class="large highlight">${account.username}</span></div>
    </div>
  </div>
</div>
</tl:page>