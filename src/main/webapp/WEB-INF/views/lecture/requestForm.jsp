<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3 class="mb-4">📘 강의 개설 신청</h3>

<form method="post"
      action="${pageContext.request.contextPath}/instructor/lecture/request"
      class="row g-3">

    <!-- 강의명 -->
    <div class="col-md-6">
        <label class="form-label">강의명</label>
        <input type="text" name="lectureTitle"
               class="form-control" required>
    </div>

    <!-- 총 차수 -->
    <div class="col-md-3">
        <label class="form-label">강의 총 차수</label>
        <input type="number" name="lectureRound"
               class="form-control" min="1" required>
    </div>

    <!-- 분반 -->
    <div class="col-md-3">
        <label class="form-label">분반</label>
        <input type="text" name="section"
               class="form-control" placeholder="A / B">
    </div>

    <!-- 시작일 -->
    <div class="col-md-4">
        <label class="form-label">강의 시작일</label>
        <input type="date" name="startDate"
               class="form-control" required>
    </div>

    <!-- 종료일 -->
    <div class="col-md-4">
        <label class="form-label">강의 종료일</label>
        <input type="date" name="endDate"
               class="form-control" required>
    </div>

    <!-- 강의실 -->
    <div class="col-md-4">
        <label class="form-label">강의실</label>
        <input type="text" name="room"
               class="form-control" required>
    </div>

    <!-- 수강 인원 -->
    <div class="col-md-3">
        <label class="form-label">정원</label>
        <input type="number" name="capacity"
               class="form-control" min="1" required>
    </div>

    <!-- 요일 -->
    <div class="col-md-3">
        <label class="form-label">요일</label>
        <select name="weekDay" class="form-select">
            <option value="MON">월</option>
            <option value="TUE">화</option>
            <option value="WED">수</option>
            <option value="THU">목</option>
            <option value="FRI">금</option>
        </select>
    </div>

    <!-- 시작 시간 -->
    <div class="col-md-3">
        <label class="form-label">시작 시간</label>
        <input type="time" name="startTime"
               class="form-control" required>
    </div>

    <!-- 종료 시간 -->
    <div class="col-md-3">
        <label class="form-label">종료 시간</label>
        <input type="time" name="endTime"
               class="form-control" required>
    </div>

    <!-- 버튼 -->
    <div class="col-12 mt-4">
        <button class="btn btn-primary">신청하기</button>
        <a href="${pageContext.request.contextPath}/instructor/lecture/request"
           class="btn btn-secondary">취소</a>
    </div>

</form>