<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>내 성적 조회</title>

<style>
    table {
        border-collapse: collapse;
        width: 60%;
        margin-top: 20px;
    }
    th, td {
        border: 1px solid #ccc;
        padding: 10px;
        text-align: center;
    }
    th {
        background-color: #f2f2f2;
        width: 30%;
    }
    .not-input {
        color: #999;
        font-style: italic;
    }
    .notice {
        margin-top: 15px;
        color: #d9534f;
        font-size: 14px;
    }
</style>

</head>
<body>

<h2>내 성적 조회</h2>

<c:choose>
    <!-- 성적 데이터 자체가 없는 경우 -->
    <c:when test="${score == null}">
        <p class="notice">
            아직 성적이 입력되지 않았습니다.
        </p>
    </c:when>

    <c:otherwise>
        <table>
            <tr>
                <th>출석</th>
                <td>
                    <c:choose>
                        <c:when test="${score.attendanceScore != null}">
                            ${score.attendanceScore}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">미입력</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th>과제</th>
                <td>
                    <c:choose>
                        <c:when test="${score.assignmentScore != null}">
                            ${score.assignmentScore}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">미입력</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th>중간고사</th>
                <td>
                    <c:choose>
                        <c:when test="${score.midtermScore != null}">
                            ${score.midtermScore}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">미입력</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th>기말고사</th>
                <td>
                    <c:choose>
                        <c:when test="${score.finalScore != null}">
                            ${score.finalScore}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">미입력</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th>총점</th>
                <td>
                    <c:choose>
                        <c:when test="${score.confirmed}">
                            ${score.totalScore}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">성적 확정 후 공개</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <th>학점</th>
                <td>
                    <c:choose>
                        <c:when test="${score.confirmed}">
                            ${score.gradeLetter}
                        </c:when>
                        <c:otherwise>
                            <span class="not-input">성적 확정 후 공개</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>

        <c:if test="${!score.confirmed}">
            <p class="notice">
                ※ 성적은 교수 확정 후 공개됩니다.
            </p>
        </c:if>
    </c:otherwise>
</c:choose>

</body>
</html>