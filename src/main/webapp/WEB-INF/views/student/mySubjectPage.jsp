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
      <th style="border:1px solid #ccc;">과목명</th>
      <th style="border:1px solid #ccc;">담당교수</th>
      <th style="border:1px solid #ccc;">강의실</th>
      <th style="border:1px solid #ccc;">강의시간</th>
    </tr>
  </thead>

  <tbody>
    <%-- 내가 수강 중인 강의 목록 --%>
    <c:forEach var="course" items="${myCourseList}">
      <tr class="clickable-row"
      data-href="${pageContext.request.contextPath}/강의상세내역주소">
        <td style="border:1px solid #ccc;">
          ${course.lectureTitle}
        </td>
        <td style="border:1px solid #ccc; text-align:left; padding-left:10px;">
          ${course.professorName}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.room}
        </td>
        <td style="border:1px solid #ccc;">
          ${course.scheduleText}
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
