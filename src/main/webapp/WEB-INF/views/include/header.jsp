<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/layout.css">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
	<div class="container-fluid">

		<a class="navbar-brand" href="${ctx}/main">가산구디대학교</a>

		<!-- 햄버거 버튼 -->
		<button class="navbar-toggler" type="button" data-bs-toggle="collapse"
			data-bs-target="#navbarMenu">
			<span class="navbar-toggler-icon"></span>
		</button>

		<jsp:include page="/WEB-INF/views/include/appClock.jsp" />

		<div class="collapse navbar-collapse" id="navbarMenu">

			<!-- ================= PC 전용 메뉴 ================= -->
			<ul class="navbar-nav ms-auto d-none d-lg-flex">

				<c:if test="${sessionScope.AccessInfo.role.name() == 'INSTRUCTOR'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/instructor/profile">강사 마이페이지</a></li>
				</c:if>

				<c:if test="${sessionScope.AccessInfo.role.name() == 'ADMIN'}">
					<li class="nav-item"><a class="nav-link" href="${ctx}/mypage">관리자
							마이페이지</a></li>
				</c:if>
				<c:if test="${sessionScope.AccessInfo.role.name() == 'STUDENT'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/mypage/studentPage">학생 마이페이지</a></li>
				</c:if>

				<li class="nav-item"><a class="nav-link"
					href="${ctx}/login/logout">로그아웃</a></li>
        <c:if test="${sessionScope.AccessInfo.role.name() == 'ADMIN'}">
          <c:if test="${not empty pendingInfoUpdateCount and pendingInfoUpdateCount > 0}">
            <a href="${ctx}/admin/studentInfoUpdateRequests"
               class="position-relative d-inline-flex align-items-center justify-content-center
                      me-3 text-decoration-none text-dark">

              🔔

              <span class="position-absolute top-20 start-100 translate-middle
                      badge rounded-pill bg-danger">
                ${pendingInfoUpdateCount}
              </span>
            </a>
          </c:if>
        </c:if>
			</ul>

			<!-- ================= 모바일 통합 메뉴 (헤더+사이드바) ================= -->
			<ul class="navbar-nav mt-3 d-lg-none">

				<li class="nav-item"><a class="nav-link" href="${ctx}/about">🎓
						대학소개</a></li>

				<li class="nav-item"><a class="nav-link"
					href="${ctx}/notice/list">📢 공지사항</a></li>

				<li class="nav-item"><a class="nav-link"
					href="${ctx}/calendar/view">📅 학사일정</a></li>

				<!-- 학생 -->
				<c:if test="${sessionScope.AccessInfo.role == 'STUDENT'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/student/lectures">내 강의 목록</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/mypage/enrollmentPage">수강 신청</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/mypage/mySchedule">내 시간표</a></li>
				</c:if>

				<!-- 강사 -->
				<c:if test="${sessionScope.AccessInfo.role == 'INSTRUCTOR'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/instructor/lectures">내 강의 목록</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/instructor/lecture/request">강의 개설 신청</a></li>
				</c:if>

				<!-- 관리자 -->
				<c:if test="${sessionScope.AccessInfo.role == 'ADMIN'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/admin/dashboard">수강 대시보드</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/admin/lectureRequest">강의 개설 관리</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/admin/departmentManage">학과 관리</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/admin/campus">캠퍼스 관리</a></li>
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/admin/registUser">사용자 등록</a></li>
				</c:if>

				<hr>

				<c:if test="${sessionScope.AccessInfo.role.name() == 'INSTRUCTOR'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/instructor/profile">강사 마이페이지</a></li>
				</c:if>

				<c:if test="${sessionScope.AccessInfo.role.name() == 'ADMIN'}">
					<li class="nav-item"><a class="nav-link" href="${ctx}/mypage">관리자
							마이페이지</a></li>
				</c:if>

				<c:if test="${sessionScope.AccessInfo.role.name() == 'STUDENT'}">
					<li class="nav-item"><a class="nav-link"
						href="${ctx}/mypage/studentPage">학생 마이페이지</a></li>
				</c:if>

				<li class="nav-item"><a class="nav-link"
					href="${ctx}/login/logout">로그아웃</a></li>

			</ul>

		</div>
	</div>
</nav>
