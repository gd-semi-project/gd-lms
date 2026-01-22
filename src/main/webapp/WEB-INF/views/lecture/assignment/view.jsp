<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.AccessInfo}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">📂 과제 상세</h3>

  <!-- 과제 정보 -->
  <div class="card mb-4">
    <div class="card-header">
      <h5 class="mb-0"><c:out value="${assignment.title}" /></h5>
    </div>
    <div class="card-body">
      <div class="row mb-3">
        <div class="col-md-4">
          <strong>마감일:</strong>
          <fmt:parseDate value="${assignment.dueDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
          <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd HH:mm" />
        </div>
        <div class="col-md-4">
          <strong>배점:</strong> ${assignment.maxScore}점
        </div>
      </div>
      <hr>
      <div style="white-space: pre-wrap;"><c:out value="${assignment.content}" /></div>
      <div>
      	<p><strong>제출파일:</strong></p>
	    <c:set var="fileList" value="${assignment.fileList}" scope="request" />
	    <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
      </div>   
      
      
    </div>
  </div>

  <!-- 버튼 영역 -->
  <div class="mb-4">
    <!-- 교수: 수정/삭제 -->
    <c:if test="${user.role == 'INSTRUCTOR' || user.role == 'ADMIN'}">
      <a class="btn btn-outline-primary" 
         href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=edit&assignmentId=${assignment.assignmentId}">
        수정
      </a>
      <form method="post" action="${ctx}/lecture/assignments" style="display:inline;">
        <input type="hidden" name="action" value="delete" />
        <input type="hidden" name="lectureId" value="${lectureId}" />
        <input type="hidden" name="assignmentId" value="${assignment.assignmentId}" />
        <button type="submit" class="btn btn-outline-danger" onclick="return confirm('삭제하시겠습니까?')">
          삭제
        </button>
      </form>
    </c:if>

    <!-- 학생: 제출 -->
    <c:if test="${user.role == 'STUDENT'}">
      <a class="btn btn-success" 
         href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=submitForm&assignmentId=${assignment.assignmentId}">
        <c:choose>
          <c:when test="${empty mySubmission}">과제 제출</c:when>
          <c:otherwise>재제출</c:otherwise>
        </c:choose>
      </a>
    </c:if>

    <a class="btn btn-secondary" href="${ctx}/lecture/assignments?lectureId=${lectureId}">목록</a>
  </div>

  <!-- 학생: 본인 제출 현황 -->
  <c:if test="${user.role == 'STUDENT'}">
    <div class="card mb-4">
      <div class="card-header">내 제출 현황</div>
      <div class="card-body">
        <c:choose>
          <c:when test="${empty mySubmission}">
            <p class="text-muted mb-0">아직 제출하지 않았습니다.</p>
          </c:when>
          <c:otherwise>
            <p><strong>제출일:</strong> 
              <fmt:parseDate value="${mySubmission.submittedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="subDate" type="both" />
              <fmt:formatDate value="${subDate}" pattern="yyyy-MM-dd HH:mm" />
            </p>
            <p><strong>내용:</strong></p>
            <div class="border p-2 bg-light" style="white-space: pre-wrap;"><c:out value="${mySubmission.content}" /></div>
            
            <!-- 제출파일 목록 -->
            <p><strong>제출파일:</strong></p>
            <c:set var="fileList" value="${mySubmission.fileList}" scope="request" />
            <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
            
            <c:if test="${mySubmission.score != null}">
              <hr>
              <p><strong>점수:</strong> ${mySubmission.score} / ${assignment.maxScore}</p>
              <p><strong>피드백:</strong></p>
              <div class="border p-2 bg-light" style="white-space: pre-wrap;"><c:out value="${mySubmission.feedback}" /></div>
            </c:if>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:if>

  <!-- 교수: 제출 목록 -->
  <c:if test="${user.role == 'INSTRUCTOR' || user.role == 'ADMIN'}">
    <div class="card">
      <div class="card-header">제출 현황 (${submissions.size()}명)</div>
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead class="table-light">
            <tr>
              <th>학생명</th>
              <th>제출일</th>
              <th>점수</th>
              <th>파일</th>
              <th style="width:120px;">채점</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty submissions}">
                <tr>
                  <td colspan="5" class="text-center text-muted py-3">제출한 학생이 없습니다.</td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="s" items="${submissions}">
                  <tr>
                    <td><c:out value="${s.studentName}" /></td>
                    <td>
                      <fmt:parseDate value="${s.submittedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="subDate" type="both" />
                      <fmt:formatDate value="${subDate}" pattern="yyyy-MM-dd HH:mm" />
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${s.score != null}">${s.score} / ${assignment.maxScore}</c:when>
                        <c:otherwise><span class="text-muted">미채점</span></c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <c:set var="fileList" value="${s.fileList}" scope="request" />
                      <c:set var="showIconsOnly" value="true" scope="request" />
            		  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
                    </td>
                    <td>
                      <a class="btn btn-sm btn-outline-primary"
                         href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=gradeForm&assignmentId=${assignment.assignmentId}&submissionId=${s.submissionId}">
                        채점
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>
  </c:if>
</div>