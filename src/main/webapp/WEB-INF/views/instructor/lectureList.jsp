<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3 class="mb-3">📚 내 강의 목록</h3>

<c:if test="${empty lectures}">
  <div class="alert alert-info">
    담당 중인 강의가 없습니다.
  </div>
</c:if>

<c:if test="${not empty lectures}">
  <table class="table table-bordered table-hover">
    <thead class="table-light">
      <tr>
        <th>강의명</th>
        <th>차수</th>
        <th>분반</th>
        <th>기간</th>
        <th>강의실</th>
        <th>정원</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="lec" items="${lectures}">
        <tr>
          <td>
            <a href="${pageContext.request.contextPath}/lecture/detail?id=${lec.lectureId}">
              ${lec.lectureTitle}
            </a>
          </td>
          <td>${lec.lectureRound}</td>
          <td>${lec.section}</td>
          <td>${lec.startDate} ~ ${lec.endDate}</td>
          <td>${lec.room}</td>
          <td>${lec.capacity}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</c:if>