<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="cpath" value="${pageContext.request.contextPath}" />
<c:set var="loginRole" value="${sessionScope.role}" />
<c:set var="loginUserId" value="${sessionScope.userId}" />

<c:if test="${empty notice}">
  <div class="alert alert-warning">공지사항을 찾을 수 없습니다.</div>
</c:if>

<c:if test="${not empty notice}">
  <div class="d-flex justify-content-between align-items-start mb-3">
    <div>
      <h3 class="mb-1"><c:out value="${notice.title}" /></h3>
      <div class="text-muted small">
        유형: ${notice.noticeType} |
        작성일: ${notice.createdAt} |
        조회수: ${notice.viewCount}
      </div>
    </div>

    <div class="d-flex gap-2">
      <!-- 목록 -->
      <c:url var="listUrl" value="/notice/list">
        <c:if test="${not empty notice.lectureId}">
          <c:param name="lectureId" value="${notice.lectureId}" />
        </c:if>
      </c:url>
      <a class="btn btn-outline-secondary" href="${listUrl}">목록</a>

      <!-- 관리 가능 여부 -->
      <c:set var="canManage"
             value="${loginRole == 'ADMIN' ||
                     (loginRole == 'INSTRUCTOR' && notice.lectureId != null && notice.authorId == loginUserId)}" />

      <c:if test="${canManage}">
        <c:url var="editUrl" value="/notice/edit">
          <c:param name="noticeId" value="${notice.noticeId}" />
          <c:if test="${not empty notice.lectureId}">
            <c:param name="lectureId" value="${notice.lectureId}" />
          </c:if>
        </c:url>
        <a class="btn btn-primary" href="${cpath}${editUrl}">수정</a>

        <form method="post" action="${cpath}/notice/delete" onsubmit="return confirm('삭제하시겠습니까?');">
          <input type="hidden" name="noticeId" value="${notice.noticeId}" />
          <c:if test="${not empty notice.lectureId}">
            <input type="hidden" name="lectureId" value="${notice.lectureId}" />
          </c:if>
          <button type="submit" class="btn btn-danger">삭제</button>
        </form>
      </c:if>
    </div>
  </div>

  <div class="card">
    <div class="card-body" style="white-space: pre-wrap;">
      <c:out value="${notice.content}" />
    </div>
  </div>
</c:if>
