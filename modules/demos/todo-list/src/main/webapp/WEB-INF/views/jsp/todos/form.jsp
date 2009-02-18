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
  <form id="form_todo" name="todo" method="POST" onsubmit="return Utils.validateForm(this, document.getElementById('feedback'), true);">
    <table cellspacing="5">
      <tbody>
        <tr>
          <td><span>Title</span></td>
          <td><input class="title" name="title" type="text" value="${todo.title}" required="true"/></td>
        </tr>
        <tr>
          <td><span>Content</span></td>
          <td><textarea class="content" name="content">${todo.content}</textarea></td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><input class="submit" type="submit" value="Submit"/></td>
        </tr>
      </tbody>
    </table>
  </form>
</div>
<script type="text/javascript" src="<c:url value="/js/dyuproject.js"/>"></script>
</tl:page>