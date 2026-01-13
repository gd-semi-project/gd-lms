<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx = request.getContextPath();
%>

<aside class="col-12 col-md-3 col-lg-2 bg-secondary text-white p-3 sidebar">
  <ul class="nav nav-pills flex-column gap-1">
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/dashboard">테스트1</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/users">테스트2</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/board">테스트3</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/stats">테스트4</a>
    </li>
  </ul>
  <hr class="border-light opacity-50 my-3">
  <div class="small opacity-75">
    로그인 사용자: 홍길동<br/>
    권한: ADMIN
  </div>
</aside>
