<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-4">📝 성적 관리</h3>

<c:if test="${role == 'INSTRUCTOR'}">
    <div class="alert alert-info">
        ✔ 출석 점수는 자동 계산됩니다.<br/>
        ✔ 과제는 언제나 입력 가능합니다.<br/>
        ✔ 중간 / 기말은 입력 기간에만 수정 가능합니다.
    </div>
</c:if>

<div class="mb-3">
    <button class="btn btn-outline-primary btn-sm" onclick="showTab('attendance')">출석</button>
    <button class="btn btn-outline-primary btn-sm" onclick="showTab('assignment')">과제</button>
    <button class="btn btn-outline-primary btn-sm" onclick="showTab('midterm')">중간</button>
    <button class="btn btn-outline-primary btn-sm" onclick="showTab('final')">기말</button>
</div>

<form id="scoreForm" method="post" action="${ctx}/score/grades/save">

<input type="hidden" name="lectureId" value="${lectureId}">
<input type="hidden" name="midtermDisabled" value="${!midtermOpen}">
<input type="hidden" name="finalDisabled" value="${!finalOpen}">

<table class="table table-bordered align-middle">
    <thead class="table-light text-center">
        <tr>
            <th>학번</th>
            <th>이름</th>
            <th class="tab-attendance">출석</th>
            <th class="tab-assignment d-none">과제</th>
            <th class="tab-midterm d-none">중간</th>
            <th class="tab-final d-none">기말</th>
            <th>총점</th>
            <th>학점</th>
        </tr>
    </thead>

    <tbody>
    <c:forEach var="s" items="${scores}">
        <tr class="text-center score-row">

            <td>${s.studentNumber}</td>
            <td>${s.studentName}</td>

            <!-- 출석 -->
            <td class="tab-attendance">${s.attendanceScore}</td>

            <!-- 과제 -->
            <td class="tab-assignment d-none">
                <c:if test="${role == 'INSTRUCTOR'}">
                    <input type="number"
                           name="assignmentScore_${s.studentId}"
                           value="${s.assignmentScore}"
                           class="form-control form-control-sm assignment-input">
                </c:if>
                <c:if test="${role != 'INSTRUCTOR'}">
                    ${s.assignmentScore != null ? s.assignmentScore : '미입력'}
                </c:if>
            </td>

            <!-- 중간 -->
            <td class="tab-midterm d-none">
                <c:if test="${role == 'INSTRUCTOR'}">
                    <input type="number"
                           name="midtermScore_${s.studentId}"
                           value="${s.midtermScore}"
                           class="form-control form-control-sm midterm-input"
                           <c:if test="${!midtermOpen}">disabled</c:if>>
                </c:if>
                <c:if test="${role != 'INSTRUCTOR'}">
                    ${s.midtermScore != null ? s.midtermScore : '미입력'}
                </c:if>
            </td>

            <!-- 기말 -->
            <td class="tab-final d-none">
                <c:if test="${role == 'INSTRUCTOR'}">
                    <input type="number"
                           name="finalScore_${s.studentId}"
                           value="${s.finalScore}"
                           class="form-control form-control-sm final-input"
                           <c:if test="${!finalOpen}">disabled</c:if>>
                </c:if>
                <c:if test="${role != 'INSTRUCTOR'}">
                    ${s.finalScore != null ? s.finalScore : '미입력'}
                </c:if>
            </td>

            <td>${s.totalScore != null ? s.totalScore : '-'}</td>
            <td>${s.gradeLetter != null ? s.gradeLetter : '-'}</td>

            <!-- hidden -->
            <input type="hidden" name="studentId" value="${s.studentId}">
            <input type="hidden" name="scoreId_${s.studentId}" value="${s.scoreId}">
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${role == 'INSTRUCTOR'}">
    <div class="text-end mt-3">
        <button type="submit" class="btn btn-primary">💾 저장</button>
        <button type="submit"
                formaction="${ctx}/score/grades/calculate"
                class="btn btn-success">📊 학점 계산</button>
    </div>
</c:if>

</form>

<!-- =========================
     JS
     ========================= -->
<script>
function showTab(type) {
    ['attendance','assignment','midterm','final'].forEach(t => {
        document.querySelectorAll('.tab-' + t)
            .forEach(el => el.classList.add('d-none'));
    });
    document.querySelectorAll('.tab-' + type)
        .forEach(el => el.classList.remove('d-none'));
}

// 🔥 저장 전 미입력 검사
document.getElementById('scoreForm').addEventListener('submit', function (e) {

    const rows = document.querySelectorAll('.score-row');
    let hasError = false;

    rows.forEach(row => {
        row.classList.remove('table-danger');

        const assignment = row.querySelector('.assignment-input');
        const midterm = row.querySelector('.midterm-input');
        const finalExam = row.querySelector('.final-input');

        // 과제는 항상 필수
        if (assignment && assignment.value === '') {
            hasError = true;
        }

        // 중간 (disabled 아니면 검사)
        if (midterm && !midterm.disabled && midterm.value === '') {
            hasError = true;
        }

        // 기말 (disabled 아니면 검사)
        if (finalExam && !finalExam.disabled && finalExam.value === '') {
            hasError = true;
        }

        if (hasError) {
            row.classList.add('table-danger');
        }
    });

    if (hasError) {
        e.preventDefault();
        alert('미입력된 학생의 성적이 있습니다.\n빨간색으로 표시된 학생을 확인해주세요.');
    }
});
</script>