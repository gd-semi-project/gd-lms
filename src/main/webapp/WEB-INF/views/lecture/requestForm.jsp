<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="weekDays" value="MON,TUE,WED,THU,FRI" />

<h3 class="mb-4">📘 강의 개설 신청</h3>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        <strong>강의 개설 신청 불가</strong><br/>
        ${errorMessage}
    </div>
</c:if>

<form method="post"
      action="${pageContext.request.contextPath}/instructor/lecture/request"
      class="row g-3">

    <!-- 강의명 -->
    <div class="col-md-6">
        <label class="form-label">강의명</label>
        <input type="text" name="lectureTitle"
               class="form-control"
               value="${param.lectureTitle}"
               required>
    </div>

    <!-- 차수 -->
    <div class="col-md-3">
        <label class="form-label">강의 차수</label>
        <input type="number" name="lectureRound"
               class="form-control"
               value="${param.lectureRound}"
               min="1" required>
    </div>

    <!-- 분반 -->
    <div class="col-md-3">
        <label class="form-label">분반</label>
        <input type="text" name="section"
               class="form-control"
               value="${param.section}"
               placeholder="A / B">
    </div>

    <!-- 날짜 -->
    <div class="col-md-4">
        <label class="form-label">강의 시작일</label>
        <input type="date" name="startDate"
               class="form-control"
               value="${param.startDate}"
               required>
    </div>

    <div class="col-md-4">
        <label class="form-label">강의 종료일</label>
        <input type="date" name="endDate"
               class="form-control"
               value="${param.endDate}"
               required>
    </div>

    <!-- 강의실 -->
    <div class="col-md-4">
        <label class="form-label">강의실</label>
        <select name="room" class="form-select" required>
            <option value="">강의실 선택</option>
            <c:forEach var="room" items="${rooms}">
                <option value="${room.roomCode}"
                    <c:if test="${param.room eq room.roomCode}">
                        selected
                    </c:if>>
                    ${room.roomCode} (${room.capacity}명)
                </option>
            </c:forEach>
        </select>
    </div>

    <!-- 정원 -->
    <div class="col-md-3">
        <label class="form-label">정원</label>
        <input type="number" name="capacity"
               class="form-control"
               value="${param.capacity}"
               min="1" required>
    </div>

    <!-- 요일 -->
    <div class="col-md-5">
	    <label class="form-label">요일 (최대 2일)</label><br/>
	
	    <c:forEach var="d" items="${fn:split(weekDays, ',')}">
	        <label class="me-3">
	            <input type="checkbox"
	                   name="weekDay"
	                   value="${d}"
	                <c:if test="${not empty paramValues.weekDay
	                              and fn:contains(fn:join(paramValues.weekDay, ','), d)}">
	                    checked
	                </c:if>
	            >
	            ${d}
	        </label>
	    </c:forEach>
	</div>

    <!-- 시간 -->
    <div class="col-md-2">
        <label class="form-label">시작 시간</label>
        <input type="time" name="startTime"
               class="form-control"
               value="${param.startTime}"
               required>
    </div>

    <div class="col-md-2">
        <label class="form-label">종료 시간</label>
        <input type="time" name="endTime"
               class="form-control"
               value="${param.endTime}"
               required>
    </div>

    <!-- 성적 배점 -->
    <div class="col-12 mt-4">
        <label class="form-label fw-bold">성적 배점 (%)</label>
    </div>

    <c:set var="a" value="${empty param.attendanceWeight ? 20 : param.attendanceWeight}" />
    <c:set var="b" value="${empty param.assignmentWeight ? 20 : param.assignmentWeight}" />
    <c:set var="cwt" value="${empty param.midtermWeight ? 30 : param.midtermWeight}" />
    <c:set var="dwt" value="${empty param.finalWeight ? 30 : param.finalWeight}" />

    <div class="col-md-3">
        <input type="number" name="attendanceWeight"
               class="form-control score-weight"
               value="${a}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <input type="number" name="assignmentWeight"
               class="form-control score-weight"
               value="${b}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <input type="number" name="midtermWeight"
               class="form-control score-weight"
               value="${cwt}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <input type="number" name="finalWeight"
               class="form-control score-weight"
               value="${dwt}" min="0" max="100" required>
    </div>

    <div class="col-12">
        <small id="weightInfo" class="text-muted">
            ※ 성적 배점의 합은 반드시 100%여야 합니다.
        </small>
    </div>

    <div class="col-12 mt-4">
        <button id="submitBtn" class="btn btn-primary">신청하기</button>
        <a href="${pageContext.request.contextPath}/instructor/lecture/request"
           class="btn btn-secondary">취소</a>
    </div>
</form>


<script src="${pageContext.request.contextPath}/resources/js/lectureRequest.js"></script>