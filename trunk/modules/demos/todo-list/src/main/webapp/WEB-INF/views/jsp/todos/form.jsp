<%@ page session="false" %>
<%@ taglib prefix="tl" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tl:page>
<div class="box">
  <div><p class="msg"><span id="feedback">${msg}</span></p></div>
  <div class="clearfix bar">
    <div class="rightcol"></div>
    <div class="leftcol"><span class="highlight">${action} Todo</span></div>
  </div>
  <form id="form_todo" name="todo" class="box" method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <ul>
      <li>
        <div><label>Title</label></div>
        <div><input class="title" name="title" type="text" value="${todo.title}" required="true"/></div>
      </li>
      <li>
        <div><label>Content</label></div>
        <div><textarea class="content" name="content" rows="5">${todo.content}</textarea></div>
      </li>
      <li>
        <div>
          <c:if test="${action=='Edit'}"><input type="hidden" name="sub" value="1"/></c:if>
          <button type="submit" class="submit"><div>Submit</div></button>
        </div>
      </li>
    </ul>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>