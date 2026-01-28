<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 기본 정보 -->
<div class="card">
  <div class="card-body">
    <h5 class="mb-3">기본 정보</h5>
    <table class="table table-bordered">
      <tbody>
        <tr>
          <th scope="row">로그인 아이디</th>
          <td>${user.loginId}</td>
        </tr>
        <tr>
          <th scope="row">이름</th>
          <td>${instructor.name}</td>
        </tr>
        <tr>
          <th scope="row">이메일</th>
          <td>${instructor.email}</td>
        </tr>
        <tr>
          <th scope="row">전화번호</th>
          <td>${instructor.phone}</td>
        </tr>
        <tr>
          <th scope="row">신분</th>
          <td>강사</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

<!-- 강사 정보 -->
<div class="card mt-4">
  <div class="card-body">
    <h5 class="mb-3">강사 정보</h5>
    <table class="table table-bordered">
      <tbody>
        <tr>
          <th scope="row">강사 번호</th>
          <td>${instructor.instructorNo}</td>
        </tr>
        <tr>
          <th scope="row">학과</th>
          <td>${instructor.department}</td>
        </tr>
        <tr>
          <th scope="row">연구실</th>
          <td>${instructor.officeRoom}</td>
        </tr>
        <tr>
          <th scope="row">연구실 전화</th>
          <td>${instructor.officePhone}</td>
        </tr>
        <tr>
          <th scope="row">임용일</th>
          <td>${instructor.hireDate}</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

<!-- 버튼 영역 -->
<div class="mt-3">
  <a href="${pageContext.request.contextPath}/instructor/profile/edit"
     class="btn btn-primary">
     정보 수정
  </a>
    <a href="${pageContext.request.contextPath}/changeUserPw/change"
     class="btn btn-warning">
     비밀번호 변경
  </a>


    <a href="${pageContext.request.contextPath}/instructor/lectures"
       class="btn btn-outline-secondary">
       내 강의 보기
    </a>
  </div>
</c:if>