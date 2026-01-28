<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="${pageContext.request.contextPath}/instructor/profile/edit" method="post">

  <!-- 기본 정보 -->
  <div class="card">
    <div class="card-body">
      <h5 class="mb-3">기본 정보</h5>
      <table class="table table-bordered">
        <tbody>
          <tr>
            <th>로그인 아이디</th>
            <td>
              ${user.loginId}
              <input type="hidden" name="userId" value="${instructor.userId}">
            </td>
          </tr>
          <tr>
            <th>이름</th>
            <td>
              <input type="text" name="name" class="form-control"
                     value="${instructor.name}" required>
            </td>
          </tr>
          <tr>
            <th>이메일</th>
            <td>
              <input type="email" name="email" class="form-control"
                     value="${instructor.email}">
            </td>
          </tr>
          <tr>
            <th>전화번호</th>
            <td>
              <input type="text" name="phone" class="form-control"
                     value="${instructor.phone}">
            </td>
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
            <th>강사 번호</th>
            <td>${instructor.instructorNo}</td>
          </tr>
          <tr>
            <th>학과</th>
            <td>${instructor.department}</td>
          </tr>
          <tr>
            <th>연구실</th>
            <td>
              <input type="text" name="officeRoom" class="form-control"
                     value="${instructor.officeRoom}">
            </td>
          </tr>
          <tr>
            <th>연구실 전화</th>
            <td>
              <input type="text" name="officePhone" class="form-control"
                     value="${instructor.officePhone}">
            </td>
          </tr>
          <tr>
            <th>임용일</th>
            <td>${instructor.hireDate}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- 버튼 -->
  <div class="mt-3">
    <button type="submit" class="btn btn-primary">저장</button>
    <a href="${pageContext.request.contextPath}/instructor/profile"
       class="btn btn-secondary">취소</a>
  </div>

</form>