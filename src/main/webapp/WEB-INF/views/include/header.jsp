<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String ctx = request.getContextPath();
%>

  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="<%=ctx%>/">가산구디대학교</a>

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
              관리자 페이지
            </a>
          </li>
        </c:if>

		<li class="nav-item">
		  <a class="nav-link d-flex align-items-center gap-1 logout-link"
   				href="<%=ctx%>/login/logout">
		    <i class="bi bi-box-arrow-right"></i>
		    로그아웃
		  </a>
		</li>
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