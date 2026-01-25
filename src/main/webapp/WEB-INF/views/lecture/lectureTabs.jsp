<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />

<div class="mb-4 border-bottom pb-2">
  <ul class="nav nav-tabs">

    <!-- 상세보기 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'detail' ? 'active' : ''}"
         href="${ctx}/lecture/detail?lectureId=${lecture.lectureId}">
        📘 상세보기
      </a>
    </li>

    <!-- 출석 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'attendance' ? 'active' : ''}"
   		href="${ctx}/attendance/view?lectureId=${lecture.lectureId}">
  		🕘 출석
	 </a>
    </li>

    <!-- 성적 -->
    <li class="nav-item">
        <a class="nav-link ${activeTab == 'grades' ? 'active' : ''}"
           href="${ctx}/score/grades?lectureId=${lecture.lectureId}">
            성적
        </a>
    </li>

    <!-- 과제 -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'assignments' ? 'active' : ''}"
         href="${ctx}/lecture/assignments?lectureId=${lecture.lectureId}">
        📂 과제
      </a>
    </li>

    <!-- QnA -->
    <li class="nav-item">
      <a class="nav-link ${activeTab eq 'qna' ? 'active' : ''}"
         href="${ctx}/lecture/qna?lectureId=${lecture.lectureId}">
        💬 QnA
      </a>
    </li>

    <!-- 수강생 (교수 / 관리자만) -->
    <c:if test="${role eq 'INSTRUCTOR' or role eq 'ADMIN'}">
      <li class="nav-item">
        <a class="nav-link ${activeTab eq 'students' ? 'active' : ''}"
           href="${ctx}/lecture/students?lectureId=${lecture.lectureId}">
          👥 수강생
        </a>
      </li>
    </c:if>

  </ul>
</div>