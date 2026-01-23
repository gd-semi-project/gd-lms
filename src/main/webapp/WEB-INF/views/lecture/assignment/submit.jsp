<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">📂 과제 제출</h3>

  <!-- 과제 정보 -->
  <div class="card mb-4">
    <div class="card-header">
      <c:out value="${assignment.title}" />
    </div>
    <div class="card-body">
      <p><strong>마감일:</strong> 
        <fmt:parseDate value="${assignment.dueDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
        <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd HH:mm" />
      </p>
      <p><strong>배점:</strong> ${assignment.maxScore}점</p>
      <hr>
      <div style="white-space: pre-wrap;"><c:out value="${assignment.content}" /></div>
    </div>
  </div>

  <!-- 제출 폼 -->
  <div class="card">
    <div class="card-header">
      <c:choose>
        <c:when test="${empty mySubmission}">과제 제출</c:when>
        <c:otherwise>재제출</c:otherwise>
      </c:choose>
    </div>
    <div class="card-body">
      <form method="post" action="${ctx}/lecture/assignments"  
      enctype="multipart/form-data">
	  	<input type="hidden" name="action" value="submit" />
		  <div class="mb-3">
		    <label class="form-label">제출 내용 <span class="text-danger">*</span></label>
		    <textarea name="content" class="form-control" rows="10" required><c:if test="${not empty mySubmission}"><c:out value="${mySubmission.content}"/></c:if></textarea>
		  </div>
		
		  <input type="hidden" name="lectureId" value="${lectureId}" />
		  <input type="hidden" name="assignmentId" value="${assignment.assignmentId}" />
		  <jsp:include page="/WEB-INF/views/file/fileUpload.jsp" />
		
		  <div class="d-flex gap-2">
		    <button type="submit" class="btn btn-success">제출</button>
		    <a class="btn btn-outline-secondary"
		       href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=view&assignmentId=${assignment.assignmentId}">
		      취소
		    </a>
		  </div>
	  </form>
    </div>
  </div>
</div>