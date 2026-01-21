<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">📂 과제 등록</h3>

  <form method="post" action="${ctx}/lecture/assignments">
    <input type="hidden" name="action" value="create" />
    <input type="hidden" name="lectureId" value="${lectureId}" />

    <div class="mb-3">
      <label class="form-label">제목 <span class="text-danger">*</span></label>
      <input type="text" name="title" class="form-control" maxlength="200" required />
    </div>

    <div class="mb-3">
      <label class="form-label">내용 <span class="text-danger">*</span></label>
      <textarea name="content" class="form-control" rows="8" required></textarea>
    </div>

    <div class="row">
      <div class="col-md-6 mb-3">
        <label class="form-label">마감일 <span class="text-danger">*</span></label>
        <input type="datetime-local" name="dueDate" class="form-control" required />
      </div>
      <div class="col-md-6 mb-3">
        <label class="form-label">배점</label>
        <input type="number" name="maxScore" class="form-control" value="100" min="1" max="1000" />
      </div>
    </div>

    <%-- TODO: 파일 업로드 연동 (ref_type=ASSIGNMENT, ref_id=생성될 assignmentId) --%>
    <div class="mb-3">
      <label class="form-label">첨부파일</label>
      <input type="file" name="attachments" class="form-control" multiple disabled />
      <div class="form-text text-muted">파일 업로드 기능 준비 중입니다.</div>
    </div>

    <div class="d-flex gap-2">
      <button type="submit" class="btn btn-primary">등록</button>
      <a class="btn btn-outline-secondary" href="${ctx}/lecture/assignments?lectureId=${lectureId}">취소</a>
    </div>
  </form>
</div>