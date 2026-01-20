<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    


<div class="card mt-4">
  <div class="card-body">
    <h5 class="mb-3">학생 정보</h5>

    <table class="table table-bordered">
      <tbody>
        <tr>
          <th scope="row">학번</th>
          <td>${mypage.student.studentNumber}</td>
        </tr>
        <tr>
          <th scope="row">학년</th>
          <td>${mypage.student.studenGrade}</td>
        </tr>
        <tr>
          <th scope="row">학부 상태 (학부/대학원)</th>
          <td>${mypage.student.status}</td>
        </tr>
        <tr>
          <th scope="row">학적 상태</th>
          <td>${mypage.student.studentStatus}</td>
        </tr>
        <tr>
          <th scope="row">입학일자</th>
          <td>${mypage.student.enrollDate}</td>
        </tr>
        <tr>
          <th scope="row">졸업일자</th>
          <td>${mypage.student.endDate}</td>
        </tr>
        <tr>
          <th scope="row">등록금 계좌</th>
          <td>${mypage.student.tuitionAccount}</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
