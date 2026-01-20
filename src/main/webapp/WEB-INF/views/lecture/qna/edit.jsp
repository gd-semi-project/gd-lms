<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<c:choose>
  <c:when test="${empty post}">
    <div class="alert alert-warning">수정할 글을 찾을 수 없습니다.</div>
    <a class="btn btn-secondary" href="${ctx}/lecture/qna?lectureId=${lectureId}">목록</a>
  </c:when>
  <c:otherwise>
    <h3 class="mb-3">Q&amp;A 수정</h3>

    <form method="post" action="${ctx}/lecture/qna?action=update">
      <input type="hidden" name="lectureId" value="${lectureId}" />
      <input type="hidden" name="qnaId" value="${post.qnaId}" />

      <div class="mb-3">
        <label class="form-label">제목</label>
        <input type="text" name="title" class="form-control" maxlength="200"
               value="<c:out value='${post.title}'/>" required />
      </div>

      <div class="mb-3">
        <label class="form-label">내용</label>
        <textarea name="content" class="form-control" rows="8" required><c:out value="${post.content}"/></textarea>
      </div>

      <div class="mb-3">
        <label class="form-label">비공개</label>
        <select name="isPrivate" class="form-select">
          <option value="N" ${post.isPrivate == 'N' ? 'selected' : ''}>공개</option>
          <option value="Y" ${post.isPrivate == 'Y' ? 'selected' : ''}>비공개</option>
        </select>
      </div>

      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">저장</button>
        <a class="btn btn-outline-secondary"
           href="${ctx}/lecture/qna?lectureId=${lectureId}&action=view&qnaId=${post.qnaId}">
          취소
        </a>
      </div>
    </form>
  </c:otherwise>
</c:choose>