<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<style>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
</style>

<div class="card">
	<div class="card-body">
	<h5 class="mb-3">기본 정보</h5>
		<table class="table table-bordered">
			<tbody>
				<tr>
					<th scope="row">로그인 아이디</th>
					<td>${mypage.user.loginId}</td>
				</tr>
				<tr>
					<th scope="row">이름</th>
					<td>${mypage.user.name}</td>
				</tr>
				<tr>
					<th scope="row">성별</th>
					<td>${mypage.user.gender}</td>
				</tr>
				<tr>
					<th scope="row">생년월일</th>
					<td>${mypage.user.birthDate}</td>
				</tr>
				<tr>
					<th scope="row">이메일</th>
					<td>${mypage.user.email}</td>
				</tr>
				<tr>
					<th scope="row">전화번호</th>
					<td>${mypage.user.phone}</td>
				</tr>
				<tr>
					<th scope="row">신분</th>
					<td>${mypage.user.role}</td>
				</tr>
				<tr>
					<th scope="row">주소</th>
					<td>${mypage.user.address}</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>

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
          <th scope="row">전공</th>
          <td>${mypage.department.departmentName}</td>
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
<div class="mt-3">
	<a href="${pageContext.request.contextPath}/editUserInfoController/edit"
		class="btn btn-primary">정보 수정</a> <a
		href="${pageContext.request.contextPath}/changeUserPw/change"
		class="btn btn-warning">비밀번호 변경</a>
</div>
