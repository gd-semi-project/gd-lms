<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<aside class="col-12 col-md-3 col-lg-2 bg-secondary text-white p-3 sidebar">

  <ul class="nav nav-pills flex-column gap-1">

    <!-- 대학소개 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/about">
        🎓 대학소개
      </a>
    </li>
    
<!--     마이페이지 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="?page=student">
        내정보
      </a>
    </li>

    <!-- 강의 (토글 메뉴) -->
    <li class="nav-item">
      <a class="nav-link text-white d-flex justify-content-between align-items-center"
         data-bs-toggle="collapse"
         href="#lectureMenu"
         role="button"
         aria-expanded="false"
         aria-controls="lectureMenu">
        📚 강의
        <span>▾</span>
      </a>

      <!-- 하위 메뉴 -->
      <div class="collapse ps-3" id="lectureMenu">
        <ul class="nav flex-column mt-1">

          <!-- 내 강의 목록 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="?page=mySubjectPage">
              ▸ 내 강의 목록
            </a>
          </li>

          <!-- 강의 개설 신청(교수면) -->
          <c:if test="${mypage.user.role eq 'INSTRUCTOR'}">
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="<%=ctx%>/lecture/request">
              ▸ 강의 개설 신청
            </a>
          </li>
          </c:if>
	
          <!-- 수강신청 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="?page=enrollmentPage">
              ▸ 수강신청
            </a>
          </li>
          
          <!-- 내 시간표 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="?page=mySchedule">
              ▸ 내시간표
            </a>
          </li>
          
        </ul>
      </div>
    </li>

    <!-- 공지사항 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="${pageContext.request.contextPath}/calendar/view">학사일정</a>
    </li>

    <!-- 성적 -->
    <li class="nav-item">
      <a class="nav-link text-white d-flex justify-content-between align-items-center"
         data-bs-toggle="collapse"
         href="#ScoreMenu"
         role="button"
         aria-expanded="false"
         aria-controls="ScoreMenu">
        📝 성적
        <span>▾</span>
      </a>
    
	<div class="collapse ps-3" id="ScoreMenu">
        <ul class="nav flex-column mt-1">
    	<!-- 수강신청 -->
          <li class="nav-item">
            <a href="?page=semesterScore" class="nav-link text-white small">
              ▸ 학기별 집계성적
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="?page=totScore">
              ▸ 전체성적조회
            </a>
          </li>
        </ul>
    </div>
  </li>

</aside>