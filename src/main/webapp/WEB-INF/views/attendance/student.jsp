<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h4>🕘 출석 현황</h4>

<table class="table table-bordered">
  <thead>
    <tr>
      <th>날짜</th>
      <th>시간</th>
      <th>상태</th>
      <th>출석</th>
    </tr>
  </thead>

  <tbody>
    <c:forEach var="s" items="${attendanceList}">
      <tr>
        <td>${s.sessionDate}</td>
        <td>${s.startTime} ~ ${s.endTime}</td>

        <td>
          <c:choose>
            <c:when test="${empty s.status}">
              미출석
            </c:when>
            <c:otherwise>
              ${s.status}
            </c:otherwise>
          </c:choose>
        </td>

        <td>
          <c:if test="${empty s.status}">
            <form method="post"
                  action="${pageContext.request.contextPath}/attendance/check">
              <input type="hidden" name="sessionId"
                     value="${s.sessionId}">
              <button class="btn btn-success btn-sm">
                출석
              </button>
            </form>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>