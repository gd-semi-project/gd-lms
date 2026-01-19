<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">

  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h4">👨‍🏫 강사 정보</h2>
  </div>

  <!-- 강사 정보 없음 -->
  <c:if test="${empty instructor}">
    <div class="alert alert-warning">
      강사 정보를 불러오지 못했습니다.
    </div>
  </c:if>

  <!-- 강사 정보 출력 -->
  <c:if test="${not empty instructor}">
    <div class="card shadow-sm">
      <div class="card-body">

        <table class="table table-bordered mb-0">
          <tbody>

            <!-- ================= 사용자 정보 ================= -->
            <tr>
              <th class="table-light" style="width: 25%;">이름</th>
              <td>${userName}</td>
            </tr>

            <tr>
              <th class="table-light">이메일</th>
              <td>${userEmail}</td>
            </tr>

            <tr>
              <th class="table-light">전화번호</th>
              <td>${userPhone}</td>
            </tr>

            <!-- ================= 강사 정보 ================= -->
            <tr>
              <th class="table-light">강사 ID</th>
              <td>${instructor.userId}</td>
            </tr>

            <tr>
              <th class="table-light">강사 교번</th>
              <td>${instructor.instructorNo}</td>
            </tr>

            <tr>
              <th class="table-light">소속 학과</th>
              <td>${instructor.department}</td>
            </tr>

            <tr>
              <th class="table-light">연구실</th>
              <td>${instructor.officeRoom}</td>
            </tr>

            <tr>
              <th class="table-light">연구실 전화</th>
              <td>${instructor.officePhone}</td>
            </tr>

            <tr>
              <th class="table-light">임용일</th>
              <td>${instructor.hireDate}</td>
            </tr>

            
          </tbody>
        </table>

      </div>
    </div>
  </c:if>

</div>