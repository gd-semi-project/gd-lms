<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- 상단: 선택 학과 정보 -->
<div class="d-flex flex-wrap align-items-start justify-content-between gap-2 mb-3">
  <div>
    <h4 class="fw-bold mb-1">
      <c:choose>
        <c:when test="${not empty selectedDepartment}">
          ${selectedDepartment.departmentName}
          <span class="text-muted fs-6">(${selectedDepartment.departmentCode})</span>
        </c:when>
        <c:otherwise>
          <span class="text-muted">학과 ID: ${param.departmentId}</span>
        </c:otherwise>
      </c:choose>
    </h4>

    <div class="text-muted small">
      현재 상태:
      <span class="badge ${empty param.status || param.status == 'ACTIVE' ? 'text-bg-success' : 'text-bg-danger'}">
        ${empty param.status ? 'ACTIVE' : param.status}
      </span>
      의 교수/학생 목록을 표시합니다.
    </div>
  </div>

  <div class="d-flex gap-2">
    <a class="btn btn-sm btn-outline-secondary"
       href="${pageContext.request.contextPath}/admin/departmentManage?status=${empty param.status ? 'ACTIVE' : param.status}">
      선택 해제
    </a>
    <button class="btn btn-sm btn-outline-primary" type="button" disabled>학과 수정</button>
  </div>
</div>

<hr class="my-3"/>

<div class="row g-3">

  <!-- =========================
       강사 리스트
  ========================== -->
  <div class="col-12 col-xl-6">
    <div class="card border-0 bg-light-subtle h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="fw-bold mb-0">강사진</h6>
          <span class="badge text-bg-secondary">${fn:length(instructorList)}명</span>
        </div>

        <div class="table-responsive" style="max-height: 60vh; overflow:auto;">
          <table class="table table-sm align-middle mb-0">
            <thead class="table-light position-sticky top-0">
              <tr>
                <th style="width: 120px;">교번</th>
                <th>이름</th>
                <th class="text-muted" style="width: 140px;">휴대폰</th>
                <th class="text-muted" style="width: 110px;">연구실</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty instructorList}">
                  <tr><td colspan="4" class="text-muted small">데이터 없음</td></tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="p" items="${instructorList}">
                    <tr>
                      <td class="text-muted">${p.instructorNo}</td>
                      <td class="fw-semibold">${p.name}</td>
                      <td class="text-muted">${p.phone}</td>
                      <td class="text-muted">
                        <c:out value="${p.officeRoom}"/>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>

      </div>
    </div>
  </div>

  <!-- =========================
       학생 리스트
  ========================== -->
  <div class="col-12 col-xl-6">
    <div class="card border-0 bg-light-subtle h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="fw-bold mb-0">학생</h6>
          <span class="badge text-bg-secondary">${fn:length(studentList)}명</span>
        </div>

        <div class="table-responsive" style="max-height: 60vh; overflow:auto;">
          <table class="table table-sm align-middle mb-0">
            <thead class="table-light position-sticky top-0">
              <tr>
                <th style="width: 120px;">학번</th>
                <th>이름</th>
                <th class="text-muted" style="width: 70px;">학년</th>
                <th class="text-muted" style="width: 80px;">과정</th>
                <th class="text-muted" style="width: 80px;">상태</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty studentList}">
                  <tr><td colspan="5" class="text-muted small">데이터 없음</td></tr>
                </c:when>

                <c:otherwise>
                  <c:forEach var="s" items="${studentList}">
                    <tr>
                      <!-- ✅ DTO: studentNumber -->
                      <td class="text-muted">${s.studentNumber}</td>

                      <td class="fw-semibold">${s.name}</td>

                      <!-- ✅ DTO: studentGrade(Integer) -->
                      <td class="text-muted">
                        <c:choose>
                          <c:when test="${empty s.studentGrade}">-</c:when>
                          <c:otherwise>${s.studentGrade}</c:otherwise>
                        </c:choose>
                      </td>

                      <!-- ✅ DTO: status (StudentType enum) -->
                      <td class="text-muted">${s.status}</td>

                      <!-- ✅ DTO: studentStatus (StudentStatus enum) -->
                      <td class="text-muted">${s.studentStatus}</td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>

      </div>
    </div>
  </div>

</div>
