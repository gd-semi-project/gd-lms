<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">📂 과제 수정</h3>

  <c:choose>
    <c:when test="${empty assignment}">
      <div class="alert alert-warning">수정할 과제를 찾을 수 없습니다.</div>
      <a class="btn btn-secondary" href="${ctx}/lecture/assignments?lectureId=${lectureId}">목록</a>
    </c:when>
    <c:otherwise>
      <form method="post" action="${ctx}/lecture/assignments">
        <input type="hidden" name="action" value="update" />
        <input type="hidden" name="lectureId" value="${lectureId}" />
        <input type="hidden" name="assignmentId" value="${assignment.assignmentId}" />

        <div class="mb-3">
          <label class="form-label">제목 <span class="text-danger">*</span></label>
          <input type="text" name="title" class="form-control" maxlength="200" 
                 value="<c:out value='${assignment.title}'/>" required />
        </div>

        <div class="mb-3">
          <label class="form-label">내용 <span class="text-danger">*</span></label>
          <textarea name="content" class="form-control" rows="8" required><c:out value="${assignment.content}"/></textarea>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">마감일 <span class="text-danger">*</span></label>
            <input type="datetime-local" name="dueDate" class="form-control" 
                   value="${assignment.dueDate}" required />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">배점</label>
            <input type="number" name="maxScore" class="form-control" 
                   value="${assignment.maxScore}" min="1" max="1000" />
          </div>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">저장</button>
          <a class="btn btn-outline-secondary" 
             href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=view&assignmentId=${assignment.assignmentId}">
            취소
          </a>
        </div>
      </form>
    </c:otherwise>
  </c:choose>
</div>