<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container">

  <h2 class="mb-3">
    ${lecture.lectureTitle}
    <span class="badge bg-secondary">${lecture.lectureRound}차</span>
    <span class="badge bg-info">분반 ${lecture.section}</span>
  </h2>

  <table class="table table-bordered">
    <tbody>
      <tr>
        <th style="width:20%">강의 기간</th>
        <td>${lecture.startDate} ~ ${lecture.endDate}</td>
      </tr>
      <tr>
        <th>강의실</th>
        <td>${lecture.room}</td>
      </tr>
      <tr>
        <th>정원</th>
        <td>${lecture.capacity}명</td>
      </tr>
      <tr>
        <th>상태</th>
        <td>
          <span class="badge bg-light text-dark">
            ${lecture.status}
          </span>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- 교수 전용 -->
  <c:if test="${sessionScope.role eq 'INSTRUCTOR'}">
    <div class="mt-4 d-flex gap-2">
      <a class="btn btn-primary"
         href="${pageContext.request.contextPath}/lecture/attendance?lectureId=${lecture.lectureId}">
         출석 관리
      </a>

      <a class="btn btn-secondary"
         href="${pageContext.request.contextPath}/lecture/grades?lectureId=${lecture.lectureId}">
         성적 관리
      </a>
    </div>
  </c:if>

</div>