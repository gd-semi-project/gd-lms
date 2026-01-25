<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>성적 관리</title>

<style>
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
    th { background-color: #f2f2f2; }
    input[type=number] { width: 70px; }
    input[readonly], input:disabled {
        background-color: #eee;
        color: #666;
    }
    .warning { color: red; font-size: 14px; }
</style>

<script>
function validateBeforeSubmit() {
    const rows = document.querySelectorAll("tbody tr");

    for (let row of rows) {
        const assignment = row.querySelector("input[name='assignmentScore']");
        const midterm = row.querySelector("input[name='midtermScore']");
        const finalExam = row.querySelector("input[name='finalScore']");

        // disabled는 검사 제외
        if (!assignment.value) {
            alert("과제 점수를 모두 입력하세요.");
            return false;
        }
        if (!midterm.disabled && !midterm.value) {
            alert("중간고사 점수를 모두 입력하세요.");
            return false;
        }
        if (!finalExam.disabled && !finalExam.value) {
            alert("기말고사 점수를 모두 입력하세요.");
            return false;
        }
    }
    return true;
}
</script>

</head>
<body>

<h2>성적 관리</h2>

<c:if test="${!midtermOpen || !finalOpen}">
    <p class="warning">
        ※ 현재 입력 기간이 아닌 항목은 수정할 수 없습니다.
    </p>
</c:if>

<!-- ================= 성적 저장 ================= -->
<form method="post"
      action="${pageContext.request.contextPath}/score/save"
      onsubmit="return validateBeforeSubmit();">

    <input type="hidden" name="lectureId" value="${lectureId}">

    <table>
        <thead>
            <tr>
                <th>학번</th>
                <th>이름</th>
                <th>출석</th>
                <th>과제</th>
                <th>중간</th>
                <th>기말</th>
                <th>총점</th>
                <th>학점</th>
            </tr>
        </thead>

        <tbody>
        <c:forEach var="score" items="${scores}">
            <tr>
                <!-- hidden -->
                <input type="hidden" name="studentId" value="${score.studentId}">
                <input type="hidden" name="scoreId" value="${score.scoreId}">

                <td>${score.studentNumber}</td>
                <td>${score.studentName}</td>

                <!-- 출석 (자동 계산) -->
                <td>
                    <input type="number"
                           value="${score.attendanceScore}"
                           readonly>
                </td>

                <!-- 과제 -->
                <td>
                    <input type="number"
                           name="assignmentScore"
                           value="${score.assignmentScore}"
                           <c:if test="${score.confirmed}">disabled</c:if>>
                </td>

                <!-- 중간 -->
                <td>
                    <input type="number"
                           name="midtermScore"
                           value="${score.midtermScore}"
                           <c:if test="${!midtermOpen || score.confirmed}">disabled</c:if>>
                </td>

                <!-- 기말 -->
                <td>
                    <input type="number"
                           name="finalScore"
                           value="${score.finalScore}"
                           <c:if test="${!finalOpen || score.confirmed}">disabled</c:if>>
                </td>

                <!-- 총점 -->
                <td>
                    <c:choose>
                        <c:when test="${score.confirmed}">
                            ${score.totalScore}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>

                <!-- 학점 -->
                <td>
                    <c:choose>
                        <c:when test="${score.confirmed}">
                            ${score.gradeLetter}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <br>

    <button type="submit">저장</button>
</form>

<!-- ================= 학점 계산 ================= -->
<form method="post"
      action="${pageContext.request.contextPath}/score/confirm">

    <input type="hidden" name="lectureId" value="${lectureId}">

    <button type="submit"
        <c:if test="${!allCompleted}">disabled</c:if>>
        학점 계산
    </button>
</form>

</body>
</html>