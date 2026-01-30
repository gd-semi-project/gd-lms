<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<style>
  .req-table { table-layout: fixed; width: 100%; }
  .req-table th, .req-table td { vertical-align: top; }
  .req-table th { white-space: nowrap; }
  .cell-old {
    background: #f8f9fa;
    vertical-align: middle !important;
    padding: 1rem 0.75rem;
  }
  .cell-old .old-value{
    display: flex;
    align-items: center;
    min-height: 38px;
    line-height: 1.4;
  }
  .cell-new { vertical-align: middle !important; }
  .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
  .help { color: #6c757d; font-size: .85rem; }
</style>

<c:if test="${not empty errors}">
  <div class="alert alert-danger">
    <ul class="mb-0">
      <c:forEach var="e" items="${errors}">
        <li>${fn:escapeXml(e)}</li>
      </c:forEach>
    </ul>
  </div>
</c:if>

<div class="container my-4" style="max-width: 1100px;">
  <!-- 상단 -->
  <div class="d-flex align-items-center justify-content-between mb-3">
    <div>
      <h3 class="mb-1">학생 중요 정보 변경 요청 - 처리</h3>
      <div class="text-muted small">
        요청된 항목만 표시됩니다. 관리자가 최종 반영할 값을 입력 후 저장합니다.
      </div>
    </div>
    <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/studentInfoUpdateRequests">목록</a>
  </div>

  <!-- 요청 메타 -->
  <div class="alert alert-light border small mb-3">
    <div class="d-flex flex-wrap gap-3">
      <div><b>요청ID</b> : ${req.requestId}</div>
      <div><b>학생ID</b> : ${req.studentId}</div>
      <c:if test="${not empty req.createdAt}">
        <div><b>신청일</b> : ${fn:substring(req.createdAt, 0, 16)}</div>
      </c:if>
    </div>
    <c:if test="${not empty req.reason}">
      <div class="mt-2">
        <b>사유</b> : ${fn:escapeXml(req.reason)}
      </div>
    </c:if>
  </div>

  <!-- 처리 폼 (관리자가 반영) -->
  <form method="post" action="${ctx}/admin/updateStudentProcess" class="card shadow-sm">
    <input type="hidden" name="requestId" value="${req.requestId}" />
    <input type="hidden" name="studentId" value="${req.studentId}" />

    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table mb-0 req-table">
          <thead class="table-light">
            <tr>
              <th style="width: 16%;">항목</th>
              <th style="width: 28%;">현재 값</th>
              <th style="width: 28%;">요청 값</th>
              <th style="width: 28%;">증빙 서류</th>
            </tr>
          </thead>

          <tbody>

            <!-- 이름 (요청이 있을 때만) -->
            <c:if test="${not empty req.newName}">
              <tr>
                <th class="align-middle">이름</th>
                <td class="cell-old">
                  <div class="old-value">${fn:escapeXml(currentUser.name)}</div>
                </td>
                <td class="cell-new">
                  <input type="text" class="form-control" name="name"
                         value="${fn:escapeXml(req.newName)}" />
                </td>
                <td>
				  <c:set var="fileList" value="${filesByType['CHANGE_NAME']}" scope="request" />
				  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
				</td>
              </tr>
            </c:if>

            <!-- 성별 (요청이 있을 때만) -->
            <c:if test="${not empty req.newGender}">
              <tr>
                <th class="align-middle">성별</th>
                <td class="cell-old">
                  <div class="old-value">
                    <c:choose>
                      <c:when test="${currentUser.gender.name() == 'M'}">남</c:when>
                      <c:when test="${currentUser.gender.name() == 'F'}">여</c:when>
                    </c:choose>
                  </div>
                </td>
                <td class="cell-new">
                  <select class="form-select" name="gender">
                    <option value="M" ${req.newGender == 'M' ? 'selected' : ''}>남</option>
                    <option value="F" ${req.newGender == 'F' ? 'selected' : ''}>여</option>
                  </select>
                </td>
                <td>
				  <c:set var="fileList" value="${filesByType['CHANGE_GENDER']}" scope="request" />
				  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
				</td>
              </tr>
            </c:if>

            <!-- 계좌번호 (요청이 있을 때만) -->
            <c:if test="${not empty req.newAccountNo}">
              <tr>
                <th class="align-middle">계좌번호</th>
                <td class="cell-old">
                  <div class="old-value">
                    <span class="mono">${fn:escapeXml(currentStudent.tuitionAccount)}</span>
                  </div>
                </td>
                <td class="cell-new">
                  <input type="text" class="form-control" name="accountNo"
                         value="${fn:escapeXml(req.newAccountNo)}" />
                </td>
                <td>
				  <c:set var="fileList" value="${filesByType['CHANGE_ACCOUNT']}" scope="request" />
				  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
				</td>
              </tr>
            </c:if>

            <!-- 학과 (요청이 있을 때만) -->
            <c:if test="${not empty req.newDepartmentId}">
              <tr>
                <th class="align-middle">학과</th>
                <td class="cell-old">
                  <div class="old-value">${fn:escapeXml(currentDept.departmentName)}</div>
                </td>
                <td class="cell-new">
                  <select class="form-select" name="departmentId">
                    <c:forEach var="d" items="${departments}">
                      <option value="${d.departmentId}"
                        ${d.departmentId == req.newDepartmentId ? 'selected' : ''}>
                        ${fn:escapeXml(d.departmentName)}
                      </option>
                    </c:forEach>
                  </select>
                </td>
				<td>
				  <c:set var="fileList" value="${filesByType['CHANGE_DEPARTMENT']}" />
				  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
				</td>
              </tr>
            </c:if>

            <!-- 학적 상태 (요청이 있을 때만) -->
            <c:if test="${not empty req.newAcademicStatus}">
              <tr>
                <th class="align-middle">학적 상태</th>
                <td class="cell-old">
                  <div class="old-value">
				    <c:choose>
				      <c:when test="${currentStudent.studentStatus == 'ENROLLED'}">재학</c:when>
				      <c:when test="${currentStudent.studentStatus == 'BREAK'}">휴학</c:when>
				      <c:when test="${currentStudent.studentStatus == 'LEAVE'}">자퇴</c:when>
				      <c:when test="${currentStudent.studentStatus == 'GRADUATED'}">졸업</c:when>
				      <c:otherwise>-</c:otherwise>
				    </c:choose>
                  </div>
                </td>
                <td class="cell-new">
                  <select class="form-select" name="academicStatus">
                    <option value="ENROLLED" ${req.newAcademicStatus.name() == 'ENROLLED' ? 'selected' : ''}>재학</option>
                    <option value="BREAK"    ${req.newAcademicStatus.name() == 'BREAK' ? 'selected' : ''}>휴학</option>
                    <option value="LEAVE"    ${req.newAcademicStatus.name() == 'LEAVE' ? 'selected' : ''}>자퇴</option>
                  </select>
                </td>
                <td>
				  <c:set var="fileList" value="${filesByType['CHANGE_DEPARTMENT']}" scope="request"/>
				  <jsp:include page="/WEB-INF/views/file/fileList.jsp" />
				</td>

				                
              </tr>
            </c:if>

            <!-- 아무 변경도 없을 때 -->
            <c:if test="${empty req.newName and empty req.newGender and empty req.newAccountNo and empty req.newDepartmentId and empty req.newAcademicStatus}">
              <tr>
                <td colspan="4" class="text-center text-muted py-4">
                  표시할 변경 요청 항목이 없습니다.
                </td>
              </tr>
            </c:if>

          </tbody>
        </table>
      </div>
    </div>

    <div class="card-body">
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">저장(반영)</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/studentInfoUpdateRequests">취소</a>
      </div>
    </div>
  </form>
</div>
