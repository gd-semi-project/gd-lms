<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${empty event}">
  <div class="alert alert-warning">존재하지 않는 일정입니다.</div>
</c:if>

<c:if test="${not empty event}">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h1 class="h3 mb-1">일정 수정</h1>
      <div class="text-muted small">필수: 제목, 시작일</div>
    </div>
    <a class="btn btn-sm btn-outline-secondary"
       href="${pageContext.request.contextPath}/admin/calendar?ym=${backYm}">목록</a>
  </div>

  <div class="card shadow-sm">
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/admin/calendar/update">
        <input type="hidden" name="id" value="${event.id}">

        <div class="mb-3">
          <label class="form-label">제목 <span class="text-danger">*</span></label>
          <input type="text" class="form-control" name="title" maxlength="100" required
                 value="${fn:escapeXml(event.title)}">
        </div>

        <div class="row g-3 mb-3">
          <div class="col-12 col-md-6">
            <label class="form-label">시작일 <span class="text-danger">*</span></label>
            <input type="date" class="form-control" name="startDate" required
                   value="${event.startDate}">
          </div>
          <div class="col-12 col-md-6">
            <label class="form-label">종료일</label>
            <input type="date" class="form-control" name="endDate"
                   value="${event.endDate}">
          </div>
        </div>

        <div class="row g-3 mb-3">
          <div class="col-12 col-md-6">
            <label class="form-label">유형</label>
            <select class="form-select" name="type">
              <option value="학사" ${event.type=='학사'?'selected':''}>학사</option>
              <option value="시험" ${event.type=='시험'?'selected':''}>시험</option>
              <option value="휴강" ${event.type=='휴강'?'selected':''}>휴강</option>
              <option value="행사" ${event.type=='행사'?'selected':''}>행사</option>
              <option value="기타" ${event.type=='기타'?'selected':''}>기타</option>
            </select>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">공개 여부</label>
            <select class="form-select" name="publicFlag">
              <option value="Y" ${event.public?'selected':''}>공개</option>
              <option value="N" ${not event.public?'selected':''}>비공개</option>
            </select>
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label">메모</label>
          <textarea class="form-control" name="memo" rows="5">${fn:escapeXml(event.memo)}</textarea>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">저장</button>
          <a class="btn btn-outline-secondary"
             href="${pageContext.request.contextPath}/admin/calendar?ym=${backYm}">취소</a>
        </div>
      </form>
    </div>
  </div>
</c:if>
