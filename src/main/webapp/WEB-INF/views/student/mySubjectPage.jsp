<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
  .box {
    border: 1px solid #ddd;
    background: #fff;
    padding: 12px;
    margin-bottom: 20px;
  }
  table {
    width: 100%;
    border-collapse: collapse;
    background: #fff;
  }
  th, td {
    border: 1px solid #ddd;
    padding: 8px;
    font-size: 14px;
  }
  th {
    background: #f9f9f9;
    font-weight: bold;
  }
  h4 {
    margin: 0 0 10px 0;
  }
  button {
    padding: 4px 8px;
    cursor: pointer;
  }
</style>
<h4>강의목록</h4>

<table style="width:100%; border-collapse:collapse; text-align:center;">
  <thead>
    <tr style="background:#f5f5f5;">
    <a>
      <th style="border:1px solid #ccc;">과목명</th>
      <th style="border:1px solid #ccc;">담당교수</th>
      <th style="border:1px solid #ccc;">강의시간</th>
    </a></tr>
  </thead>

  <tbody>
    <%-- 내가 수강 중인 강의 목록 --%>
    <c:forEach var="course" items="${myCourseList}">
      <tr>
        <td style="border:1px solid #ccc;">
          ${course.category}
        </td>
        <td style="border:1px solid #ccc; text-align:left; padding-left:10px;">
          ${course.courseName}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.round}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.startDate}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.endDate}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.days}일 / ${course.totalHours}시간
        </td>
        <td style="border:1px solid #ccc;">
          <a href="${pageContext.request.contextPath}/course/detail?id=${course.id}">
            상세정보
          </a>
        </td>
      </tr>
    </c:forEach>

    <%-- 수강 중인 강의가 없을 경우 --%>
    <c:if test="${empty myCourseList}">
      <tr>
        <td colspan="7">수강 중인 강의가 없습니다.</td>
      </tr>
    </c:if>
  </tbody>
</table>
