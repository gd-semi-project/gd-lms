<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <h3 class="mb-3">📝 채점</h3>

  <c:choose>
    <c:when test="${empty submission}">
      <div class="alert alert-warning">제출물을 찾을 수 없습니다.</div>
      <a class="btn btn-secondary" 
         href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=view&assignmentId=${assignment.assignmentId}">
        돌아가기
      </a>
    </c:when>
    <c:otherwise>
      <!-- 과제 정보 -->
      <div class="card mb-3">
        <div class="card-header">과제: <c:out value="${assignment.title}" /></div>
        <div class="card-body">
          <p><strong>배점:</strong> ${assignment.maxScore}점</p>
        </div>
      </div>

      <!-- 학생 제출 내용 -->
      <div class="card mb-3">
        <div class="card-header">
          제출자: <c:out value="${submission.studentName}" />
          <span class="text-muted ms-2">
            (제출일: 
            <fmt:parseDate value="${submission.submittedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="subDate" type="both" />
            <fmt:formatDate value="${subDate}" pattern="yyyy-MM-dd HH:mm" />)
          </span>
        </div>
        <div class="card-body">
          <div class="border p-3 bg-light" style="white-space: pre-wrap;"><c:out value="${submission.content}" /></div>
        </div>
      </div>

      <!-- 채점 폼 -->
      <div class="card">
        <div class="card-header">채점</div>
        <div class="card-body">
          <form method="post" action="${ctx}/lecture/assignments">
            <input type="hidden" name="action" value="grade" />
            <input type="hidden" name="lectureId" value="${lectureId}" />
            <input type="hidden" name="assignmentId" value="${assignment.assignmentId}" />
            <input type="hidden" name="submissionId" value="${submission.submissionId}" />

            <div class="row">
              <div class="col-md-4 mb-3">
                <label class="form-label">점수 <span class="text-danger">*</span></label>
                <div class="input-group">
                  <input type="number" name="score" class="form-control" 
                         min="0" max="${assignment.maxScore}" 
                         value="${submission.score}" required />
                  <span class="input-group-text">/ ${assignment.maxScore}</span>
                </div>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">피드백</label>
              <textarea name="feedback" class="form-control" rows="5"><c:out value="${submission.feedback}"/></textarea>
            </div>

            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-primary">저장</button>
              <a class="btn btn-outline-secondary" 
                 href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=view&assignmentId=${assignment.assignmentId}">
                취소
              </a>
            </div>
          </form>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>
```

---

## 파일 구조 최종
```
webapp/WEB-INF/views/lecture/assignment/
├── list.jsp      ← 과제 목록 (공통)
├── view.jsp      ← 과제 상세 (교수: 제출목록, 학생: 본인제출)
├── write.jsp     ← 과제 작성 (교수)
├── edit.jsp      ← 과제 수정 (교수)
├── submit.jsp    ← 과제 제출 (학생)
└── grade.jsp     ← 채점 (교수)