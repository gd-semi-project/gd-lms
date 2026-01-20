<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-3">Q&amp;A 작성</h3>

<form method="post" action="${ctx}/lecture/qna?action=create">
  <input type="hidden" name="lectureId" value="${lectureId}" />

  <div class="mb-3">
    <label class="form-label">제목</label>
    <input type="text" name="title" class="form-control" maxlength="200" required />
  </div>

  <div class="mb-3">
    <label class="form-label">내용</label>
    <textarea name="content" class="form-control" rows="8" required></textarea>
  </div>

  <div class="mb-3">
    <label class="form-label">공개 여부</label>
    <select name="isPrivate" class="form-select">
      <option value="N">공개</option>
      <option value="Y">비공개</option>
    </select>
  </div>

  <div class="d-flex gap-2">
    <button type="submit" class="btn btn-primary">등록</button>
    <a class="btn btn-outline-secondary"
       href="${ctx}/lecture/qna?lectureId=${lectureId}">
      취소
    </a>
  </div>
</form>