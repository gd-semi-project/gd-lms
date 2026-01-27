<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container my-4" style="max-width: 1100px;">

  <!-- 제목 -->
  <div class="d-flex align-items-center justify-content-between mb-3">
    <div>
      <h3 class="mb-1">학생 중요 정보 변경 요청</h3>
      <div class="text-muted small">
        학생이 신청한 중요 정보 변경 요청을 검토하고 승인/반려합니다.
      </div>
    </div>
  </div>

  <!-- 목록 테이블 -->
  <div class="card shadow-sm">
    <div class="table-responsive">
      <table class="table table-hover mb-0">
        <thead class="table-light">
          <tr>
            <th style="width: 8%;">ID</th>
            <th style="width: 12%;">학생ID</th>
            <th>변경 항목</th>
            <th style="width: 16%;">신청일</th>
          </tr>
        </thead>
        <tbody>

          <c:if test="${empty requestList}">
            <tr>
              <td colspan="5" class="text-center text-muted py-4">
                요청 내역이 없습니다.
              </td>
            </tr>
          </c:if>

          <c:forEach var="r" items="${requestList}">
            <tr style="cursor:pointer"
                onclick="location.href='${ctx}/admin/updateStudent?requestId=${r.requestId}'">

              <td class="fw-semibold">${r.requestId}</td>
              <td>${r.studentId}</td>

              <!-- 변경 항목 뱃지 -->
              <td>
                <c:if test="${not empty r.newName}">
                  <span class="badge bg-secondary me-1">이름</span>
                </c:if>
                <c:if test="${not empty r.newGender}">
                  <span class="badge bg-secondary me-1">성별</span>
                </c:if>
                <c:if test="${not empty r.newAccountNo}">
                  <span class="badge bg-secondary me-1">계좌</span>
                </c:if>
                <c:if test="${not empty r.newDepartmentId}">
                  <span class="badge bg-secondary me-1">학과</span>
                </c:if>
                <c:if test="${not empty r.newAcademicStatus}">
                  <span class="badge bg-secondary me-1">학적</span>
                </c:if>
              </td>

              <td class="text-muted">
                ${fn:substring(r.createdAt, 0, 16)}
              </td>

            </tr>
          </c:forEach>

        </tbody>
      </table>
    </div>
  </div>

</div>
