<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="${ctx}/main">가산구디대학교</a>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    
    <jsp:include page="/WEB-INF/views/include/appClock.jsp"/>

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
      	<!-- 강사 -->
        <c:if test="${sessionScope.AccessInfo.role.name() == 'INSTRUCTOR'}">
          <li class="nav-item">
            <a class="nav-link"
               href="${pageContext.request.contextPath}/instructor/profile">
              강사 마이페이지
            </a>
          </li>
        </c:if>

        <!-- 관리자 -->
        <c:if test="${sessionScope.AccessInfo.role.name() == 'ADMIN'}">
          <li class="nav-item">
            <a class="nav-link"
               href="${pageContext.request.contextPath}/mypage">
              관리자 마이페이지
            </a>
          </li>
        </c:if>

		<c:if test="${not empty sessionScope.AccessInfo}">
		    <a class="nav-link d-flex align-items-center gap-1 logout-link"
		       href="${pageContext.request.contextPath}/login/logout">
		        <i class="bi bi-box-arrow-right"></i>
		        로그아웃
		    </a>
		</c:if>
      </ul>
    </div>
  </div>
</nav>

<style>
.logout-link {
  color: var(--bs-warning);
}

.logout-link:hover {
  color: #fff;
}
</style>