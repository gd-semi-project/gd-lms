<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h4>📋 출석부</h4>

<table class="table table-bordered">
  <thead>
    <tr>
      <th>학번</th>
      <th>이름</th>
      <th>학년</th>
      <th>상태</th>
      <th>수정</th>
    </tr>
  </thead>

  <tbody>
    <c:forEach var="s" items="${attendanceList}">
      <tr>
        <td>${s.studentNumber}</td>
        <td>${s.studentName}</td>
        <td>${s.studentGrade}</td>
        <td>${s.status}</td>

        <td>
          <form method="post"
                action="${pageContext.request.contextPath}/attendance/update">
            <input type="hidden" name="sessionId"
                   value="${param.sessionId}">
            <input type="hidden" name="studentId"
                   value="${s.studentId}">

            <select name="status"
                    class="form-select form-select-sm">
              <option value="PRESENT">출석</option>
              <option value="LATE">지각</option>
              <option value="ABSENT">결석</option>
            </select>

            <button class="btn btn-primary btn-sm mt-1">
              수정
            </button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>