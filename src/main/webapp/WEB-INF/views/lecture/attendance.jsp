<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 상단 강의 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-4">🕘 출석</h3>

<!-- =========================
     학생 화면
========================= -->
<c:if test="${sessionScope.UserInfo.role eq 'STUDENT'}">

    <c:if test="${not empty todaySession}">
        <div class="card mb-4">
            <div class="card-body">
                <h5>오늘 수업</h5>
                <p>
                    ${todaySession.startTime}
                    ~
                    ${todaySession.endTime}
                </p>

                <form method="post"
                      action="${pageContext.request.contextPath}/attendance/check">
                    <input type="hidden" name="sessionId"
                           value="${todaySession.sessionId}" />
                    <input type="hidden" name="lectureId"
                           value="${lecture.lectureId}" />

                    <button class="btn btn-success">
                        출석하기
                    </button>
                </form>
            </div>
        </div>
    </c:if>

    <c:if test="${empty todaySession}">
        <div class="alert alert-secondary">
            오늘은 수업이 없습니다.
        </div>
    </c:if>

    <h5 class="mt-4">📊 나의 출석 기록</h5>

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
                    <td>${a.status}</td>
                    <td>${a.checkedAt}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty attendanceList}">
                <tr>
                    <td colspan="4">
                        출석 기록이 없습니다.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
</c:if>

<!-- =========================
     교수 화면
========================= -->
<c:if test="${sessionScope.UserInfo.role eq 'INSTRUCTOR'}">

    <h5>📋 회차별 출석부</h5>

    <form method="get" class="mb-3">
        <input type="hidden" name="id"
               value="${lecture.lectureId}" />

        <select name="sessionId"
                class="form-select w-25 d-inline">
            <c:forEach var="s" items="${sessions}">
                <option value="${s.sessionId}">
                    ${s.sessionDate}
                    (${s.startTime}~${s.endTime})
                </option>
            </c:forEach>
        </select>

        <button class="btn btn-primary ms-2">
            조회
        </button>
    </form>

    <table class="table table-bordered text-center">
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
                              action="${pageContext.request.contextPath}/attendance/update">
                            <input type="hidden" name="sessionId"
                                   value="${param.sessionId}" />
                            <input type="hidden" name="studentId"
                                   value="${s.studentId}" />

                            <select name="status"
                                    class="form-select d-inline w-auto">
                                <option value="PRESENT">출석</option>
                                <option value="LATE">지각</option>
                                <option value="ABSENT">결석</option>
                            </select>

                            <button class="btn btn-sm btn-outline-primary">
                                저장
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty sessionAttendance}">
                <tr>
                    <td colspan="5">
                        출석 데이터가 없습니다.
                    </td>
                </tr>
            </c:if>
        </tbody>
    </table>
</c:if>