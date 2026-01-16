<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="cpath" value="${pageContext.request.contextPath}" />

<h3 class="mb-3">공지사항 수정</h3>

<c:if test="${empty notice}">
  <div class="alert alert-warning">수정할 공지사항을 찾을 수 없습니다.</div>
</c:if>

<c:if test="${not empty notice}">
  <form method="post" action="${cpath}/notice/update">
    <input type="hidden" name="noticeId" value="${notice.noticeId}" />
    <c:if test="${not empty notice.lectureId}">
      <input type="hidden" name="lectureId" value="${notice.lectureId}" />
    </c:if>

    <div class="mb-3">
      <label class="form-label">유형</label>
      <select class="form-select" name="noticeType" required>
        <option value="GENERAL" ${notice.noticeType == 'GENERAL' ? 'selected' : ''}>GENERAL</option>
        <option value="IMPORTANT" ${notice.noticeType == 'IMPORTANT' ? 'selected' : ''}>IMPORTANT</option>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">제목</label>
      <input class="form-control" type="text" name="title" maxlength="200"
             value="<c:out value='${notice.title}'/>" required />
    </div>

    <div class="mb-3">
      <label class="form-label">내용</label>
      <textarea class="form-control" name="content" rows="10" required><c:out value="${notice.content}" /></textarea>
    </div>

    <div class="d-flex gap-2">
      <button class="btn btn-primary" type="submit">저장</button>

      <c:url var="viewUrl" value="/notice/view">
        <c:param name="noticeId" value="${notice.noticeId}" />
        <c:if test="${not empty notice.lectureId}">
          <c:param name="lectureId" value="${notice.lectureId}" />
        </c:if>
      </c:url>
      <a class="btn btn-outline-secondary" href="${cpath}${viewUrl}">취소</a>

      <!-- 삭제 -->
      <form method="post" action="${cpath}/notice/delete" class="ms-auto"
            onsubmit="return confirm('정말 삭제하시겠습니까?');">
        <input type="hidden" name="noticeId" value="${notice.noticeId}" />
        <c:if test="${not empty notice.lectureId}">
          <input type="hidden" name="lectureId" value="${notice.lectureId}" />
        </c:if>
        <button type="submit" class="btn btn-danger">삭제</button>
      </form>
    </div>
  </form>
</c:if>


