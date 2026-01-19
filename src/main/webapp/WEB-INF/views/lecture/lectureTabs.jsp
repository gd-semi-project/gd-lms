<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="mb-4 border-bottom pb-2">
  <ul class="nav nav-tabs">

    <!-- 상세보기 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'detail' ? 'active' : ''}"
         href="${ctx}/lecture/detail?id=${lecture.lectureId}">
        📘 상세보기
      </a>
    </li>

    <!-- 출석 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'attendance' ? 'active' : ''}"
         href="${ctx}/lecture/attendance?id=${lecture.lectureId}">
        🕘 출석
      </a>
    </li>

    <!-- 성적 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'grades' ? 'active' : ''}"
         href="${ctx}/lecture/grades?id=${lecture.lectureId}">
        📝 성적
      </a>
    </li>

    <!-- 과제 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'assignments' ? 'active' : ''}"
         href="${ctx}/lecture/assignments?id=${lecture.lectureId}">
        📂 과제
      </a>
    </li>

    <!-- QnA -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'qna' ? 'active' : ''}"
         href="${ctx}/lecture/qna?id=${lecture.lectureId}">
        💬 QnA
      </a>
    </li>

    <!-- 수강생 정보 (강사/관리자만) -->
    <c:if test="${sessionScope.UserInfo.role eq 'INSTRUCTOR' 
              or sessionScope.UserInfo.role eq 'ADMIN'}">
      <li class="nav-item">
        <a class="nav-link ${activeTab eq 'students' ? 'active' : ''}"
           href="${ctx}/lecture/students?id=${lecture.lectureId}">
          👥 수강생
        </a>
      </li>
    </c:if>

  </ul>
</div>