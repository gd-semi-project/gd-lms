<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<br><br>
<table style="width:100%; border-collapse:collapse; text-align:center;">
  <thead>
    <tr>
      <th style="border:1px solid #ccc; width:80px;">시간</th>
      <th style="border:1px solid #ccc;">월</th>
      <th style="border:1px solid #ccc;">화</th>
      <th style="border:1px solid #ccc;">수</th>
      <th style="border:1px solid #ccc;">목</th>
      <th style="border:1px solid #ccc;">금</th>
    </tr>
  </thead>

  <tbody>
    <%-- 시간 (09~18시) --%>
    <c:forEach var="hour" begin="9" end="18">
      <tr>
        <td style="border:1px solid #ccc;">
          ${hour}
        </td>

        
        <td style="border:1px solid #ccc;">
          <%-- 월 ${hour}시 강의 --%>
        </td>
        <td style="border:1px solid #ccc;">
          <%-- 화 ${hour}시 강의 --%>
        </td>
        <td style="border:1px solid #ccc;">
          <%-- 수 ${hour}시 강의 --%>
        </td>
        <td style="border:1px solid #ccc;">
          <%-- 목 ${hour}시 강의 --%>
        </td>
        <td style="border:1px solid #ccc;">
          <%-- 금 ${hour}시 강의 --%>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
