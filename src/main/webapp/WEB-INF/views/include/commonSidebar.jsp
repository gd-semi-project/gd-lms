<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
  String ctx = request.getContextPath();
%>

<aside class="col-12 col-md-3 col-lg-2 bg-dark text-white p-3 sidebar">
	<ul class="nav nav-pills flex-column gap-1">

		<li class="nav-item"><a class="nav-link text-white"
			href="<%=ctx%>/about"> 🎓 대학소개 </a></li>

		<li class="nav-item"><a
			class="nav-link text-white d-flex justify-content-between align-items-center"
			data-bs-toggle="collapse" href="#lectureMenu" role="button"> 📚
				강의 <span>+</span>
		</a>

			<div class="collapse" id="lectureMenu">
				<ul class="nav flex-column ms-3 mt-2 gap-1">

					<c:choose>

						<%-- 교수 --%>
						<c:when test="${sessionScope.UserInfo.role == 'INSTRUCTOR'}">
							<li class="nav-item"><a class="nav-link text-white small"
								href="<%=ctx%>/instructor/lectures"> 내 강의 목록 </a></li>
							<li class="nav-item"><a class="nav-link text-white small"
								href="<%=ctx%>/instructor/lecture/request"> 강의 개설 신청 </a></li>
						</c:when>

						<%-- 학생 --%>
						<c:when test="${sessionScope.UserInfo.role == 'STUDENT'}">
							<li class="nav-item"><a class="nav-link text-white small"
								href="<%=ctx%>/student/lectures"> 내 강의 목록 </a></li>
							<li class="nav-item"><a class="nav-link text-white small"
								href="<%=ctx%>/student/lecture/enroll"> 수강 신청 </a></li>
						</c:when>

						<%-- 관리자 --%>
						<c:when test="${sessionScope.UserInfo.role == 'ADMIN'}">
							<li class="nav-item"><a class="nav-link text-white small"
								href="<%=ctx%>/admin/lectures"> 전체 강의 목록 </a></li>
						</c:when>

					</c:choose>

				</ul>
			</div></li>

		<li class="nav-item"><a class="nav-link text-white"
			href="<%=ctx%>/notice/list"> 📢 공지사항 </a></li>

		<li class="nav-item"><a class="nav-link text-white"
			href="<%=ctx%>/grade/my"> 📝 성적 </a></li>

	</ul>

	<hr class="border-light opacity-50 my-3">

	<div class="small opacity-75">
		로그인 사용자: ${sessionScope.UserInfo.name}<br /> 권한:
		${sessionScope.UserInfo.role}
	</div>
</aside>