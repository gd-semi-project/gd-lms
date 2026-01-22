<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3 class="mb-4">✏ 강의 개설 신청 수정</h3>

<form method="post"
      action="${pageContext.request.contextPath}/instructor/lecture/request/edit"
      class="row g-3">

    <input type="hidden" name="lectureId"
           value="${lecture.lectureId}">

    <div class="col-md-6">
        <label class="form-label">강의명</label>
        <input type="text" name="lectureTitle"
               class="form-control"
               value="${lecture.lectureTitle}" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">강의 총 차수</label>
        <input type="number" name="lectureRound"
               class="form-control"
               value="${lecture.lectureRound}" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">분반</label>
        <input type="text" name="section"
               class="form-control"
               value="${lecture.section}">
    </div>

    <div class="col-md-4">
        <label class="form-label">강의 시작일</label>
        <input type="date" name="startDate"
               class="form-control"
               value="${lecture.startDate}" required>
    </div>

    <div class="col-md-4">
        <label class="form-label">강의 종료일</label>
        <input type="date" name="endDate"
               class="form-control"
               value="${lecture.endDate}" required>
    </div>

    <div class="col-md-4">
        <label class="form-label">강의실</label>
        <input type="text" name="room"
               class="form-control"
               value="${lecture.room}" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">정원</label>
        <input type="number" name="capacity"
               class="form-control"
               value="${lecture.capacity}" required>
    </div>

    <div class="col-12 mt-4">
        <button class="btn btn-warning">수정 저장</button>
        <a href="${pageContext.request.contextPath}/instructor/lecture/request"
           class="btn btn-secondary">취소</a>
    </div>

</form>