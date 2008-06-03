<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div>
  <div>
    <span class="big_label">${action} Todo</span>
  </div>
  <div>    
	<p class="msg"><span id="feedback">${msg}</span></p>
  </div>
  <form method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">    
    <table height="100%" class="bean_table" cellspacing="4" cellpadding="0">	  
	  <tr>
	    <td class="left_col">Title</td>
		<td name="Title"><input name="title" type="text" value="${todo.title}"/></td>
	  </tr>
	  <tr>
	    <td class="left_col">Content</td>
		<td name="Content"><textarea name="content">${todo.content}</textarea></td>
	  </tr>  
	  <tr>
	    <td>&nbsp;</td>
		<td><input type="submit" value="${action}"/></td>
	  </tr>	  
    </table>
  </form>  
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>