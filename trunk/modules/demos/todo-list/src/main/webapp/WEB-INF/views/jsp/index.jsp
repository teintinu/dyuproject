<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <p>
    Create an account <a class="highlight" href="<c:url value="/users/new"/>">here</a>.<br/>
    You will be able to see your current todos when you're <a class="highlight" href="<c:url value="/login"/>">logged</a> in.<br/>
  </p>
</div>
</tl:page>