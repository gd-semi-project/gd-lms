<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 상단 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h3 class="mb-3">${lecture.lectureTitle}</h3>

<table class="table table-bordered">
  <tr>
    <th>강의 기간</th>
    <td>${lecture.startDate} ~ ${lecture.endDate}</td>
  </tr>
  <tr>
    <th>강의실</th>
    <td>${lecture.room}</td>
  </tr>
  <tr>
    <th>정원</th>
    <td>${lecture.capacity}</td>
  </tr>
</table>

<h5 class="mt-4">강의 시간표</h5>
<table class="table table-striped">
  <thead>
    <tr>
      <th>요일</th>
      <th>시간</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="s" items="${schedules}">
      <tr>
        <td>${s.weekDay}</td>
        <td>${s.startTime} ~ ${s.endTime}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>