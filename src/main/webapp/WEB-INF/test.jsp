<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<h2>전체 사용자 목록 (role 기준 분리)</h2>

<sql:setDataSource
    var="db"
    driver="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/lms"
    user="root"
    password="test1234"
/>

<!-- users 전체 조회 -->
<sql:query var="user" dataSource="${db}">
    SELECT
        *
    FROM user
    ORDER BY role
</sql:query>

<hr>

<!-- ================= 교수 ================= -->
<h3>👨‍🏫 교수 목록</h3>

<table border="1">
    <tr>
        <th>ID</th>
        <th>로그인ID</th>
        <th>이름</th>
        <th>이메일</th>
        <th>전화번호</th>
        <th>성별</th>
        <th>상태</th>
        <th>주소</th>
        <th>생성일</th>
    </tr>

    <c:forEach var="u" items="${user.rows}">
        <c:if test="${u.role eq 'INSTRUCTOR'}">
            <tr>
                <td>${u.user_id}</td>
                <td>${u.login_id}</td>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
                <td>${u.gender}</td>
                <td>${u.status}</td>
                <td>${u.address}</td>
                <td>${u.created_at}</td>
            </tr>
        </c:if>
    </c:forEach>
</table>

<br><br>

<!-- ================= 학생 ================= -->
<h3>🎓 학생 목록</h3>

<table border="1">
    <tr>
        <th>ID</th>
        <th>로그인ID</th>
        <th>이름</th>
        <th>이메일</th>
        <th>전화번호</th>
        <th>성별</th>
        <th>상태</th>
        <th>생성일</th>
    </tr>

    <c:forEach var="u" items="${user.rows}">
        <c:if test="${u.role eq 'STUDENT'}">
            <tr>
                <td>${u.user_id}</td>
                <td>${u.login_id}</td>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
                <td>${u.gender}</td>
                <td>${u.status}</td>
                <td>${u.created_at}</td>
            </tr>
        </c:if>
    </c:forEach>
</table>
