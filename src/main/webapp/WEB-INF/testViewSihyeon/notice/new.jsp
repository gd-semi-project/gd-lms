<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3 class="mb-3">공지사항 등록</h3>

<%-- create / list URL은 c:url로 통일 (contextPath 자동 포함) --%>
<c:url var="createUrl" value="/notice/create" />
<c:url var="listUrl" value="/notice/list">
  <c:if test="${not empty lectureId}">
    <c:param name="lectureId" value="${lectureId}" />
  </c:if>
</c:url>

<form method="post" action="${createUrl}">
  <c:if test="${not empty lectureId}">
    <input type="hidden" name="lectureId" value="${lectureId}" />
  </c:if>

  <div class="mb-3">
    <label class="form-label">유형</label>
    <select class="form-select" name="noticeType" required>
      <option value="GENERAL">GENERAL</option>
      <option value="IMPORTANT">IMPORTANT</option>
    </select>
  </div>

  <div class="mb-3">
    <label class="form-label">제목</label>
    <input class="form-control" type="text" name="title" maxlength="200" required />
  </div>

  <div class="mb-3">
    <label class="form-label">내용</label>
    <textarea class="form-control" name="content" rows="10" required></textarea>
  </div>

  <div class="d-flex gap-2">
    <button class="btn btn-primary" type="submit">등록</button>
    <a class="btn btn-outline-secondary" href="${listUrl}">취소</a>
  </div>
</form>
