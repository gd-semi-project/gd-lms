<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx = request.getContextPath();
%>

<aside class="col-12 col-md-3 col-lg-2 bg-secondary text-white p-3 sidebar">
  <ul class="nav nav-pills flex-column gap-1">
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/admin/dashboard">대시보드</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/admin/lectureRequest">강의 개설 관리</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/admin/noticeList">공지사항 관리</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="${pageContext.request.contextPath}/notice/list">공지사항</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="${pageContext.request.contextPath}/calendar/view">학사일정</a>
    </li>
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/admin/campus">캠퍼스 관리</a>
    </li>
  </ul>
  
  <hr class="border-light opacity-50 my-3">

  <div>
  관리자의 TODO  <br>
  1. 학생 개개인 열람 페이지 <br>
  2. 강사진 개개인 열람 페이지 <br>
  3. 특정 강의 개별 열람 페이지 <br>
  4. 푸터에 케론볼 만들기 <br>
  </div>
  
  <hr class="border-light opacity-50 my-3">
  <div class="small opacity-75">
    로그인 사용자: 홍길동<br/>
    권한: ADMIN
  </div>
</aside>
