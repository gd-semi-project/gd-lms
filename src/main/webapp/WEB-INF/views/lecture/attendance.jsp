<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-4">출석</h3>

<!-- ================= 플래시 메시지 ================= -->
<c:if test="${not empty flashMsg}">
    <div class="alert alert-warning">
        ${flashMsg}
    </div>
</c:if>

<!-- ================================================= -->
<!-- ====================== 학생 ===================== -->
<!-- ================================================= -->
<c:if test="${role eq 'STUDENT'}">

    <h5 class="mb-3">오늘 출석</h5>

    <c:if test="${empty todaySession}">
        <div class="alert alert-secondary">
            아직 출석이 시작되지 않았습니다.
        </div>
    </c:if>

    <c:if test="${not empty todaySession}">
        <div class="card mb-4">
            <div class="card-body">

                <p>
                    <strong>출석 시간</strong><br/>
                    ${todaySession.startTime} ~ ${todaySession.endTime}
                </p>

                <div class="text-muted mb-2">
                    출석 가능 시간: 수업 시작 후 10분 이내
                </div>

                <c:choose>
                    <c:when test="${alreadyChecked}">
                        <button class="btn btn-outline-secondary" disabled>
                            출석 완료
                        </button>
                    </c:when>

                    <c:otherwise>
                        <form method="post" action="${ctx}/attendance/check">
                            <input type="hidden" name="lectureId" value="${lectureId}" />
                            <input type="hidden" name="sessionId" value="${todaySession.sessionId}" />
                            <button class="btn btn-success">
                                출석하기
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </c:if>

    <!-- ================= 나의 출석 기록 ================= -->
    <h5 class="mt-4">나의 출석 기록</h5>

    <table class="table table-bordered text-center">
        <thead class="table-light">
            <tr>
                <th>날짜</th>
                <th>시간</th>
                <th>출결</th>
                <th>체크 시간</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="a" items="${attendanceList}">
                <tr>
                    <td>${a.sessionDate}</td>
                    <td>${a.startTime} ~ ${a.endTime}</td>
                    <td>
                        <span class="badge
                            ${a.status == 'PRESENT' ? 'bg-success' :
                              a.status == 'LATE' ? 'bg-warning text-dark' :
                              'bg-danger'}">
                            ${a.status}
                        </span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty a.checkedAt}">
                                ${a.checkedAt}
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty attendanceList}">
                <tr>
                    <td colspan="4">출석 기록이 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

</c:if>

<!-- ================================================= -->
<!-- ====================== 교수 ===================== -->
<!-- ================================================= -->
<c:if test="${role eq 'INSTRUCTOR'}">

    <h5 class="mb-3">출석 관리</h5>

    <!-- 출석 시작 / 종료 -->
    <c:choose>

	    <c:when test="${empty todaySession}">
	        <form method="post" action="${ctx}/attendance/open" class="mb-4">
	            <input type="hidden" name="lectureId" value="${lectureId}" />
	            <button class="btn btn-success">
	                출석 시작
	            </button>
	        </form>
	    </c:when>
	
	    <c:when test="${not empty todaySession and attendanceOpen}">
	        <form method="post" action="${ctx}/attendance/close" class="mb-4">
	            <input type="hidden" name="lectureId" value="${lectureId}" />
	            <input type="hidden" name="sessionId" value="${todaySession.sessionId}" />
	            <button class="btn btn-danger">
	                출석 종료
	            </button>
	        </form>
	    </c:when>
	
	    <c:otherwise>
	        <div class="alert alert-secondary mb-3">
	            오늘 회차의 출석은 종료되었습니다.
	        </div>
	    </c:otherwise>
	
	</c:choose>

    <!-- ================= 회차별 출석부 ================= -->
    <h5 class="mt-4">회차별 출석부</h5>

    <form method="get" class="mb-3">
        <input type="hidden" name="lectureId" value="${lectureId}" />
        <select name="sessionId" class="form-select w-25 d-inline">
            <option value="">회차 선택</option>
            <c:forEach var="s" items="${sessions}">
                <option value="${s.sessionId}"
                    ${s.sessionId == selectedSessionId ? 'selected' : ''}>
                    ${s.sessionDate} (${s.startTime}~${s.endTime})
                </option>
            </c:forEach>
        </select>
        <button class="btn btn-primary ms-2">조회</button>
    </form>
    
     <div class="mb-3">
	    <input type="text" id="studentSearch"
	           class="form-control w-25"
	           placeholder="학생 이름으로 검색">
	</div>

    <c:if test="${not empty selectedSessionId}">
        <table id="attendanceTable" class="table table-bordered text-center align-middle">
            <thead class="table-light">
                <tr>
                    <th>학번</th>
                    <th>이름</th>
                    <th>학년</th>
                    <th>출결</th>
                    <th>수정</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${sessionAttendance}">
                    <tr>
                        <td>${s.studentNumber}</td>
                        <td>${s.studentName}</td>
                        <td>${s.studentGrade}</td>
                        <td>${s.status}</td>
                        <td>
                            <form method="post"
                                  action="${ctx}/attendance/update"
                                  class="d-inline">
                                <input type="hidden" name="attendanceId" value="${s.attendanceId}" />
                                <input type="hidden" name="lectureId" value="${lectureId}" />
                                <input type="hidden" name="sessionId" value="${selectedSessionId}" />

                                <select name="status" class="form-select d-inline w-auto">
                                    <option value="PRESENT" ${s.status == 'PRESENT' ? 'selected' : ''}>출석</option>
                                    <option value="LATE" ${s.status == 'LATE' ? 'selected' : ''}>지각</option>
                                    <option value="ABSENT" ${s.status == 'ABSENT' ? 'selected' : ''}>결석</option>
                                </select>
                                <button class="btn btn-sm btn-outline-primary ms-1">
                                    변경
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty sessionAttendance}">
                    <tr>
                        <td colspan="5">출석 데이터가 없습니다.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </c:if>

</c:if>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("studentSearch");
    const table = document.getElementById("attendanceTable");

    if (!searchInput || !table) return;

    searchInput.addEventListener("keyup", function () {
        const keyword = searchInput.value.toLowerCase();
        const rows = table.querySelectorAll("tbody tr");

        rows.forEach(row => {
            const nameCell = row.children[1]; // 이름 컬럼 (두 번째 td)
            if (!nameCell) return;

            const name = nameCell.textContent.toLowerCase();

            if (name.includes(keyword)) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });
});
</script>