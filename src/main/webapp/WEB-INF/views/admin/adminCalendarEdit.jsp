<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="d-flex justify-content-between align-items-center mb-3">
  <div>
    <h1 class="h4 mb-1">📅 일정 추가</h1>
    <div class="text-muted small">필수: 제목, 시작일, 일정 코드</div>
  </div>

  <a class="btn btn-sm btn-outline-secondary"
     href="${pageContext.request.contextPath}/admin/calendar?ym=${backYm}">
    목록
  </a>
</div>

<c:if test="${not empty errorMessage}">
  <div class="alert alert-danger d-flex justify-content-between align-items-center">
    <div>${fn:escapeXml(errorMessage)}</div>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
</c:if>

<c:if test="${empty codeOptions}">
</c:if>

<div class="card shadow-sm">
  <div class="card-body">

    <form method="post"
          action="${pageContext.request.contextPath}/admin/calendarEdit?action=CREATE"
          class="row g-3">

      <!-- 제목 -->
      <div class="col-12">
        <label class="form-label">제목 <span class="text-danger">*</span></label>
        <input type="text" name="title" class="form-control" maxlength="100" required
               value="${fn:escapeXml(event.title)}"
               placeholder="예: 2026-1 수강신청 기간">
      </div>

      <!-- 날짜 -->
      <div class="col-12 col-md-6">
        <label class="form-label">시작일 <span class="text-danger">*</span></label>
        <input type="date" name="startDate" class="form-control" required
               value="${event.startDate}">
      </div>

      <div class="col-12 col-md-6">
        <label class="form-label">종료일 (선택)</label>
        <input type="date" name="endDate" class="form-control"
               value="${event.endDate}">
        <div class="form-text">비워두면 시작일과 동일하게 처리합니다.</div>
      </div>

      <!-- 일정 코드(ENUM) -->
      <div class="col-12 col-md-6">
        <label class="form-label">일정 코드 <span class="text-danger">*</span></label>
        <select class="form-select" name="scheduleCode" required>
          <option value="" disabled selected>선택하세요</option>

          <c:forEach var="opt" items="${codeOptions}">
            <option value="${opt.value}">
              ${fn:escapeXml(opt.label)}
              <c:if test="${opt.label ne opt.value}">
                <span class="text-muted">(${fn:escapeXml(opt.value)})</span>
              </c:if>
            </option>
          </c:forEach>
        </select>
      </div>

      <!-- 메모 -->
      <div class="col-12">
        <label class="form-label">정보</label>
        <textarea class="form-control" name="memo" rows="5"
                  placeholder="추가 설명을 입력하세요.">${fn:escapeXml(event.memo)}</textarea>
      </div>

      <!-- 버튼 -->
      <div class="col-12 d-flex gap-2 mt-2">
        <button type="submit" class="btn btn-primary">추가</button>

        <a class="btn btn-outline-secondary"
           href="${pageContext.request.contextPath}/calendar/view">
          취소
        </a>
      </div>

    </form>
  </div>
</div>
