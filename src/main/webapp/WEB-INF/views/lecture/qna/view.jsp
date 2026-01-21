<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.AccessInfo}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">Q&A 상세</h3>

  <div class="card mb-3">
    <div class="card-body">
      <h5 class="card-title"><c:out value="${post.title}" /></h5>
      <div class="text-muted mb-2">
        작성자: ${post.authorId} | 상태: ${post.status} | 공개: ${post.isPrivate}
      </div>
      <div class="card-text" style="white-space: pre-wrap;"><c:out value="${post.content}" /></div>
    </div>
  </div>

  <!-- 수정/삭제 버튼 -->
  <div class="mb-3">
    <c:if test="${user.role == 'ADMIN' || user.role == 'INSTRUCTOR' || (user.role == 'STUDENT' && user.userId == post.authorId)}">
      <a class="btn btn-outline-primary" href="${ctx}/lecture/qna?lectureId=${lectureId}&action=edit&qnaId=${post.qnaId}">수정</a>
      <form method="post" action="${ctx}/lecture/qna?action=delete" style="display:inline;">
        <input type="hidden" name="lectureId" value="${lectureId}" />
        <input type="hidden" name="qnaId" value="${post.qnaId}" />
        <button type="submit" class="btn btn-outline-danger" onclick="return confirm('삭제하시겠습니까?')">삭제</button>
      </form>
    </c:if>
    <a class="btn btn-secondary" href="${ctx}/lecture/qna?lectureId=${lectureId}">목록</a>
  </div>

  <!-- 답변 목록 -->
  <h5 class="mt-4">답변</h5>
  <c:choose>
    <c:when test="${empty answers}">
      <div class="alert alert-light">등록된 답변이 없습니다.</div>
    </c:when>
    <c:otherwise>
      <c:forEach var="a" items="${answers}">
        <div class="card mb-2">
          <div class="card-body">
            <div class="text-muted mb-2">답변자: ${a.instructorId}</div>
            <div style="white-space: pre-wrap;"><c:out value="${a.content}" /></div>
          </div>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>

  <!-- 답변 작성: 교수/관리자만 -->
  <c:if test="${user.role == 'INSTRUCTOR' || user.role == 'ADMIN'}">
    <div class="card mt-3">
      <div class="card-body">
        <form method="post" action="${ctx}/lecture/qna?action=answer">
          <input type="hidden" name="lectureId" value="${lectureId}" />
          <input type="hidden" name="qnaId" value="${post.qnaId}" />
          <div class="mb-2">
            <textarea class="form-control" name="content" rows="4" placeholder="답변 내용을 입력하세요" required></textarea>
          </div>
          <button class="btn btn-success" type="submit">답변 등록</button>
        </form>
      </div>
    </div>
  </c:if>
</div>