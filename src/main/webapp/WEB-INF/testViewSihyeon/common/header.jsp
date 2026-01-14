<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link active" href="<%=ctx%>/">admin관리자</a></li>
        <li class="nav-item"><a class="nav-link" href="<%=ctx%>/about">로그아웃?</a></li>
        <li class="nav-item"><a class="nav-link" href="<%=ctx%>/settings">알림</a></li>
      </ul>
    </div>
  </div>
</nav>