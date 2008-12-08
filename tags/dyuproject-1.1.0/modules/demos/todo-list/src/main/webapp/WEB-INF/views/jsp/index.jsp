<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <p><span class="big_label">Welcome</span></p><br/>
  <p>    
    Create a sample user <a href="<c:url value="/users/new"/>">here</a>.<br/>
	<a href="<c:url value="/overview"/>">overview</a> requires login.<br/>
	You will be able to see your current todos when you're <a href="<c:url value="/login"/>">logged</a> in.<br/>
	<a href="<c:url value="/users"/>">users</a> and <a href="<c:url value="/todos"/>">todos</a> do not require login.
  </p>
</div>
</tl:page>