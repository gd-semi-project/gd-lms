<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
  table {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
    margin-bottom: 30px;
  }
  th, td {
    border: 1px solid #ccc;
    padding: 8px;
    font-size: 14px;
  }
  th {
    background: #f5f5f5;
    font-weight: bold;
  }
  .clickable-row {
    cursor: pointer;
  }
  .clickable-row:hover {
    background-color: #f1f1f1;
  }
</style>

<!-- ================= 스케줄 ================= -->
<h3>스케줄</h3>

<table>
  <thead>
    <tr>
      <th style="width:80px;">시간</th>
      <th>월</th>
      <th>화</th>
      <th>수</th>
      <th>목</th>
      <th>금</th>
    </tr>
  </thead>

  <tbody>
    <!-- 요일 배열 -->
    <c:set var="days" value="${fn:split('MON,TUE,WED,THU,FRI', ',')}" />

    <!-- 시간 (09~18) -->
    <c:forEach var="hour" begin="9" end="18">
      <tr>
        <td>${hour}:00</td>

        <c:forEach var="day" items="${days}">
          <td>
            <c:if test="${not empty scheduleMap[day][hour]}">
              ${scheduleMap[day][hour]}
            </c:if>
          </td>
        </c:forEach>

      </tr>
    </c:forEach>

    <!-- 스케줄 없을 때 -->
    <c:if test="${empty scheduleMap}">
      <tr>
        <td colspan="6">시간표 정보가 없습니다.</td>
      </tr>
    </c:if>

  </tbody>
</table>

<!-- ================= 나의 강의목록 ================= -->
<h3>강의목록</h3>

<table>
  <thead>
    <tr>
      <th>과목명</th>
      <th>담당교수</th>
      <th>강의실</th>
      <th>강의시간</th>
    </tr>
  </thead>

  <tbody>
    <!-- 수강 중인 강의 -->
    <c:forEach var="course" items="${myCourseList}">
      <tr class="clickable-row"
          data-href="${pageContext.request.contextPath}/lecture/detail?lectureId=${course.lectureId}">
        <td>${course.lectureTitle}</td>
        <td>${course.professorName}</td>
        <td>${course.room}</td>
        <td>${course.scheduleText}</td>
      </tr>
    </c:forEach>

    <!-- 강의 없을 때 -->
    <c:if test="${empty myCourseList}">
      <tr>
        <td colspan="4">수강 중인 강의가 없습니다.</td>
      </tr>
    </c:if>
  </tbody>
</table>

<script>
  document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll(".clickable-row");
    rows.forEach(row => {
      row.addEventListener("click", () => {
        window.location.href = row.dataset.href;
      });
    });
  });
</script>
