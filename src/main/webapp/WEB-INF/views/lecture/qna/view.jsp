<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="me" value="${sessionScope.UserInfo}" />

<c:if test="${empty post}">
  <div class="alert alert-warning">존재하지 않는 글입니다.</div>
  <a class="btn btn-secondary" href="${ctx}/lecture/qna?lectureId=${lectureId}">목록</a>
  <c:return/>
</c:if>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h3 class="mb-0">
    <c:if test="${post.isPrivate != null && post.isPrivate.toString() == 'Y'}">
      <span class="badge text-bg-secondary me-2">비공개</span>
    </c:if>
    <c:out value="${post.title}" />
  </h3>

  <a class="btn btn-outline-secondary"
     href="${ctx}/lecture/qna?lectureId=${lectureId}">목록</a>
</div>

<div class="card mb-3">
  <div class="card-body">
    <div class="text-muted small mb-2">
      작성자ID: <c:out value="${post.authorId}" />
      · 상태: <c:out value="${post.status}" />
      · 작성일: <c:out value="${post.createdAt}" />
    </div>

    <div style="white-space: pre-wrap;">
      <c:out value="${post.content}" />
    </div>

    <hr/>

    <!-- 학생: 본인 글만 수정/삭제 (서비스에서 최종 검증) -->
    <!-- 관리자/교수: 정책에 맞게 수정/삭제 허용 시 노출 -->
    <div class="d-flex gap-2">
      <c:if test="${me.role == 'STUDENT' && me.userId == post.authorId}">
        <a class="btn btn-sm btn-outline-primary"
           href="${ctx}/lecture/qna/edit?qnaId=${post.qnaId}&lectureId=${lectureId}">
          수정
        </a>

        <form method="post" action="${ctx}/lecture/qna/delete" onsubmit="return confirm('삭제하시겠습니까?');">
          <input type="hidden" name="qnaId" value="${post.qnaId}" />
          <input type="hidden" name="lectureId" value="${lectureId}" />
          <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
        </form>
      </c:if>

      <c:if test="${me.role == 'INSTRUCTOR' || me.role == 'ADMIN'}">
        <!-- 교수/관리자도 삭제/수정 허용 정책이면 버튼 노출 -->
        <a class="btn btn-sm btn-outline-primary"
           href="${ctx}/lecture/qna/edit?qnaId=${post.qnaId}&lectureId=${lectureId}">
          수정
        </a>

        <form method="post" action="${ctx}/lecture/qna/delete" onsubmit="return confirm('삭제하시겠습니까?');">
          <input type="hidden" name="qnaId" value="${post.qnaId}" />
          <input type="hidden" name="lectureId" value="${lectureId}" />
          <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
        </form>
      </c:if>
    </div>
  </div>
</div>

<!-- 답변 영역 -->
<div class="card">
  <div class="card-header">
    답변
  </div>
  <div class="card-body">
    <c:choose>
      <c:when test="${empty answers}">
        <div class="text-muted">등록된 답변이 없습니다.</div>
      </c:when>
      <c:otherwise>
        <c:forEach var="a" items="${answers}">
          <div class="mb-3">
            <div class="text-muted small mb-1">
              작성자ID: <c:out value="${a.instructorId}" />
              · 작성일: <c:out value="${a.createdAt}" />
            </div>
            <div style="white-space: pre-wrap;">
              <c:out value="${a.content}" />
            </div>
          </div>
          <hr/>
        </c:forEach>
      </c:otherwise>
    </c:choose>

    <!-- 교수/관리자만 답변 작성 -->
    <c:if test="${me.role == 'INSTRUCTOR' || me.role == 'ADMIN'}">
      <form method="post" action="${ctx}/lecture/qna/answer/create" class="mt-3">
        <input type="hidden" name="lectureId" value="${lectureId}" />
        <input type="hidden" name="qnaId" value="${post.qnaId}" />

        <div class="mb-2">
          <label class="form-label">답변 내용</label>
          <textarea name="content" class="form-control" rows="4" required></textarea>
        </div>

        <button type="submit" class="btn btn-primary">답변 등록</button>
      </form>
    </c:if>
  </div>
</div>
