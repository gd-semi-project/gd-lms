<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="weekDays" value="MON,TUE,WED,THU,FRI" />

<h3 class="mb-4">✏️ 강의 개설 신청 수정</h3>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        ${errorMessage}
    </div>
</c:if>

<form method="post"
      action="${pageContext.request.contextPath}/instructor/lecture/request/edit"
      class="row g-3">

    <input type="hidden" name="lectureId" value="${lecture.lectureId}" />

    <!-- 강의명 -->
    <div class="col-md-6">
        <label class="form-label">강의명</label>
        <input type="text" name="lectureTitle"
               class="form-control"
               value="${lecture.lectureTitle}" required>
    </div>

    <!-- 차수 -->
    <div class="col-md-3">
        <label class="form-label">강의 차수</label>
        <input type="number" name="lectureRound"
               class="form-control"
               value="${lecture.lectureRound}" min="1" required>
    </div>

    <!-- 분반 -->
    <div class="col-md-3">
        <label class="form-label">분반</label>
        <input type="text" name="section"
               class="form-control"
               value="${lecture.section}">
    </div>

    <!-- 시작일 -->
    <div class="col-md-4">
        <label class="form-label">강의 시작일</label>
        <input type="date" name="startDate"
               class="form-control"
               value="${lecture.startDate}" required>
    </div>

    <!-- 종료일 -->
    <div class="col-md-4">
        <label class="form-label">강의 종료일</label>
        <input type="date" name="endDate"
               class="form-control"
               value="${lecture.endDate}" required>
    </div>

    <!-- 강의실 -->
    <div class="col-md-4">
        <label class="form-label">강의실</label>
        <select name="room" class="form-select" required>
            <option value="">강의실 선택</option>
            <c:forEach var="room" items="${rooms}">
                <option value="${room.roomCode}"
                    <c:if test="${room.roomCode eq lecture.room}">
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
               value="${lecture.capacity}" min="1" required>
    </div>

    <!-- 요일 -->
    <div class="col-md-9">
        <label class="form-label">요일 (최대 2일)</label><br/>

        <c:forEach var="d" items="${fn:split(weekDays, ',')}">
		    <label class="me-3">
		        <input type="checkbox" name="weekDay" value="${d}"
		            <c:forEach var="s" items="${schedules}">
		                <c:if test="${s.weekDay.name() eq d}">
		                    checked
		                </c:if>
		            </c:forEach>
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
               value="${schedules[0].startTime}" required>
    </div>

    <div class="col-md-2">
        <label class="form-label">종료 시간</label>
        <input type="time" name="endTime"
               class="form-control"
               value="${schedules[0].endTime}" required>
    </div>

    <!-- ================= 성적 배점 ================= -->
    <div class="col-12 mt-4">
        <label class="form-label fw-bold">성적 배점 (%)</label>
    </div>

    <div class="col-md-3">
        <label class="form-label">출석</label>
        <input type="number" name="attendanceWeight"
               class="form-control score-weight"
               value="${scorePolicy.attendanceWeight}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">과제</label>
        <input type="number" name="assignmentWeight"
               class="form-control score-weight"
               value="${scorePolicy.assignmentWeight}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">중간</label>
        <input type="number" name="midtermWeight"
               class="form-control score-weight"
               value="${scorePolicy.midtermWeight}" min="0" max="100" required>
    </div>

    <div class="col-md-3">
        <label class="form-label">기말</label>
        <input type="number" name="finalWeight"
               class="form-control score-weight"
               value="${scorePolicy.finalWeight}" min="0" max="100" required>
    </div>

    <div class="col-12">
        <small id="weightInfo" class="text-muted">
            ※ 성적 배점의 합은 반드시 100%여야 합니다.
        </small>
    </div>

    <!-- 버튼 -->
    <div class="col-12 mt-4">
        <button class="btn btn-primary">수정하기</button>
        <a href="${pageContext.request.contextPath}/instructor/lecture/request"
           class="btn btn-secondary">취소</a>
    </div>

</form>

<script>
/* 요일 최대 2개 */
const days = document.querySelectorAll('input[name="weekDay"]');
days.forEach(cb => {
    cb.addEventListener('change', () => {
        if (document.querySelectorAll('input[name="weekDay"]:checked').length > 2) {
            cb.checked = false;
            alert('요일은 최대 2일까지 선택 가능합니다.');
        }
    });
});

/* 배점 합계 */
const weights = document.querySelectorAll('.score-weight');
const info = document.getElementById('weightInfo');

function checkTotal() {
    let total = 0;
    weights.forEach(w => total += Number(w.value || 0));
    if (total !== 100) {
        info.classList.add('text-danger');
        info.innerText = `⚠ 현재 합계: ${total}%`;
        return false;
    }
    info.classList.remove('text-danger');
    info.innerText = '※ 성적 배점의 합은 반드시 100%여야 합니다.';
    return true;
}

weights.forEach(w => w.addEventListener('input', checkTotal));
document.querySelector('form').addEventListener('submit', e => {
    if (!checkTotal()) {
        e.preventDefault();
        alert('성적 배점 합계를 100%로 맞춰주세요.');
    }
});
</script>