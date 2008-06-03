<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">${action} User</span>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>	
  </div>
  <form method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">    
    <table height="100%" class="bean_table" cellspacing="4" cellpadding="0">	  
	  <tr>
	    <td class="left_col">First Name</td>
		<td name="First Name"><input name="firstName" type="text" value="${user.firstName}"/></td>
	  </tr>
	  <tr>
	    <td class="left_col">Last Name</td>
		<td name="Last Name"><input name="lastName" type="text" value="${user.lastName}"/></td>
	  </tr>
	  <tr>
	    <td class="left_col">Email</td>
		<td name="Email"><input name="email" type="text" value="${user.email}"/></td>
	  </tr>
	  <c:if test="${action=='Create'}">
	  <tr class="${action}">
	    <td class="left_col">Username</td>
		<td name="Username"><input name="username" type="text"/ value="${user.username}"></td>	  
	  </tr>	  
	  <tr class="${action}">
	    <td class="left_col">Password</td>
		<td name="Password"><input name="password" type="password" value="${user.password}"/></td>	  
	  </tr>
	  </c:if>
	  <tr>
	    <td>&nbsp;</td>
		<td><input type="submit" value="${action}"/></td>
	  </tr>	  
    </table>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>