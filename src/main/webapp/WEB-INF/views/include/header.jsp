<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String ctx = request.getContextPath();
%>

  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="<%=ctx%>/">테스트</a>

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
          <a class="nav-link" href="<%=ctx%>/login/logout">로그아웃</a>
        </li>
        <li class="nav-item"><a class="nav-link" href="<%=ctx%>/settings">알림</a></li>
      </ul>
    </div>
  </div>
</nav>