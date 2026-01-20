<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="mb-4 border-bottom pb-2">
  <ul class="nav nav-tabs">

    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'detail' ? 'active' : ''}"
         href="${ctx}/lecture/detail?lectureId=${lecture.lectureId}">
        📘 상세보기
      </a>
    </li>

    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'attendance' ? 'active' : ''}"
         href="${ctx}/lecture/attendance?lectureId=${lecture.lectureId}">
        🕘 출석
      </a>
    </li>

    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'grades' ? 'active' : ''}"
         href="${ctx}/lecture/grades?lectureId=${lecture.lectureId}">
        📝 성적
      </a>
    </li>

    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'assignments' ? 'active' : ''}"
         href="${ctx}/lecture/assignments?lectureId=${lecture.lectureId}">
        📂 과제
      </a>
    </li>

    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'qna' ? 'active' : ''}"
         href="${ctx}/lecture/qna?lectureId=${lecture.lectureId}">
        💬 QnA
      </a>
    </li>

    <c:if test="${sessionScope.AccessInfo.role eq 'INSTRUCTOR' or sessionScope.AccessInfo.role eq 'ADMIN'}">
      <li class="nav-item">
        <a class="nav-link ${activeTab eq 'students' ? 'active' : ''}"
           href="${ctx}/lecture/students?lectureId=${lecture.lectureId}">
          👥 수강생
        </a>
      </li>
    </c:if>

  </ul>
</div>