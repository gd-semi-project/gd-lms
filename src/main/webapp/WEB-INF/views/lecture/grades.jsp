<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-4">📝 성적 관리</h3>

<c:if test="${role == 'INSTRUCTOR' && not empty warningMessage}">
  <div class="alert alert-warning alert-dismissible fade show" role="alert">
    ⚠ ${warningMessage}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
</c:if>

<c:if test="${role == 'INSTRUCTOR'}">

    <div class="alert alert-info">
        ✔ 출석 점수는 자동 계산됩니다.<br/>
        ✔ 과제는 부분 저장이 가능합니다.<br/>
        ✔ 학점 계산 시 과제 / 중간 / 기말 점수가 모두 필요합니다.
    </div>

    <div class="mb-3">
    	<button id="btn-attendance" type="button" class="btn btn-primary btn-sm" onclick="showTab('attendance')">출석</button>
		<button id="btn-assignment" type="button" class="btn btn-outline-primary btn-sm" onclick="showTab('assignment')">과제</button>
		<button id="btn-midterm" type="button" class="btn btn-outline-primary btn-sm" onclick="showTab('midterm')">중간</button>
		<button id="btn-final" type="button" class="btn btn-outline-primary btn-sm" onclick="showTab('final')">기말</button>
    </div>

    <form id="scoreForm" method="post" action="${ctx}/score/grades/save">
        <input type="hidden" name="lectureId" value="${lectureId}">
        <input type="hidden" id="actionType" name="actionType" value="">

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

                    <td class="tab-attendance">${s.attendanceScore}</td>

                    <td class="tab-assignment d-none">
                        <input type="number"
                               name="assignmentScore_${s.studentId}"
                               value="${s.assignmentScore}"
                               class="form-control form-control-sm assignment-input">
                    </td>

                    <td class="tab-midterm d-none">
                        <input type="number"
                               name="midtermScore_${s.studentId}"
                               value="${s.midtermScore}"
                               class="form-control form-control-sm midterm-input">
                    </td>

                    <td class="tab-final d-none">
                        <input type="number"
                               name="finalScore_${s.studentId}"
                               value="${s.finalScore}"
                               class="form-control form-control-sm final-input">
                    </td>

                    <td>${s.totalScore != null ? s.totalScore : '-'}</td>
                    <td>${s.gradeLetter != null ? s.gradeLetter : '-'}</td>

                    <input type="hidden" name="studentId" value="${s.studentId}">
                    <input type="hidden" name="scoreId_${s.studentId}" value="${s.scoreId}">
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="text-end mt-3">
            <button type="submit" class="btn btn-primary" onclick="setAction('save')">
                💾 저장
            </button>
            <button type="submit"
                    formaction="${ctx}/score/grades/calculate"
                    class="btn btn-success"
                    onclick="setAction('calculate')">
                📊 학점 계산
            </button>
        </div>
    </form>
</c:if>

<c:if test="${role == 'STUDENT'}">

    <div class="alert alert-info">
        ✔ 현재 입력된 성적만 표시됩니다.<br/>
        ✔ 총점 및 학점은 추후 공지됩니다.
    </div>

    <table class="table table-bordered text-center">
        <thead class="table-light">
            <tr>
                <th>출석</th>
                <th>과제</th>
                <th>중간</th>
                <th>기말</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${myScore.attendanceScore}</td>
                <td>${myScore.assignmentScore != null ? myScore.assignmentScore : '-'}</td>
                <td>${myScore.midtermScore != null ? myScore.midtermScore : '-'}</td>
                <td>${myScore.finalScore != null ? myScore.finalScore : '-'}</td>
            </tr>
        </tbody>
    </table>
</c:if>

<script src="${ctx}/resources/js/grades.js"></script>