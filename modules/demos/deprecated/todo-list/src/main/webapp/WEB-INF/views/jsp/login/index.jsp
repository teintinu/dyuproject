<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">Login</span>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>	
  </div>
  <form method="POST" action="<c:url value="/auth"/>" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <table cellspacing="5" cellpadding="0">
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
	    <td><input type="submit" value="Login"/></td>
	  </tr>
    </table>
  </form>
  <div><a href="<c:url value="/users/create"/>">New user?</a></div>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>