<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Layout</title>

<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />


<script
	src="${pageContext.request.contextPath}/resources/js/appClock.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/keronBallLauncher.js"></script>


<style>
.sidebar {
	min-height: calc(100vh - 56px); /* navbar 기본 높이 */
}

.sidebar .nav-link:hover {
	background: rgba(255, 255, 255, 0.15);
}
</style>
</head>

<body class="bg-light">

	<!-- 헤더 include -->
	<jsp:include page="/WEB-INF/views/include/header.jsp" />
	<div class="container-fluid">
		<div class="row">

			<!-- 사이드바 include -->
			<jsp:include page="/WEB-INF/views/include/sidebar.jsp" />
			<%-- <jsp:include page="/WEB-INF/views/include/commonSidebar.jsp" /> --%>
			<%--  <jsp:include page="/WEB-INF/views/include/testSidebarForAdmin.jsp"/> --%>

			<!-- 바디 -->
			<main class="col-12 col-md-9 col-lg-10 p-4">

				<c:if test="${not empty requestScope.contentPage}">
					<jsp:include page="${requestScope.contentPage}" />
				</c:if>

				<jsp:include page="/WEB-INF/views/include/basicInfo.jsp" />

				<%-- 기본 페이지 --%>
				<c:set var="contentPage" value="studentInfo" />
				<%-- 2. 파라미터로 덮어쓰기 --%>


				<c:if test="${param.page eq 'totScore'}">
					<c:set var="contentPage" value="totScore" />
				</c:if>

				<c:if test="${param.page eq 'mySchedule'}">
					<c:set var="contentPage" value="mySchedule" />
				</c:if>

				<c:if test="${param.page eq 'enrollmentPage'}">
					<c:set var="contentPage" value="enrollmentPage" />
				</c:if>

				<c:if test="${param.page eq 'mySubjectPage'}">
					<c:set var="contentPage" value="mySubjectPage" />
				</c:if>


				<%-- 3. 실제 화면 출력 --%>
				<c:choose>
					<c:when test="${contentPage eq 'studentInfo'}">
						<jsp:include page="/WEB-INF/views/include/studentPage.jsp" />
					</c:when>

					<c:when test="${contentPage eq 'totScore'}">
						<jsp:include page="/WEB-INF/views/include/totScore.jsp" />
					</c:when>

					<c:when test="${contentPage eq 'mySchedule'}">
						<jsp:include page="/WEB-INF/views/include/mySchedule.jsp" />
					</c:when>

					<c:when test="${contentPage eq 'enrollmentPage'}">
						<jsp:include page="/WEB-INF/views/include/enrollmentPage.jsp" />
					</c:when>

					<c:when test="${contentPage eq 'mySubjectPage'}">
						<jsp:include page="/WEB-INF/views/include/mySubjectPage.jsp" />
					</c:when>
				</c:choose>

			</main>

		</div>
	</div>

	<!-- 푸터 include (선택) -->
	<jsp:include page="/WEB-INF/views/include/footer.jsp" />

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
