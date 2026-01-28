<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<style>
.read-only {
	background-color: #d9d9d9;
	border: 1px solid #8a8888;
}
</style>

<div class="card">
	<div class="card-body">
		<h5 class="mb-3">기본 정보</h5>

		<form
			action="${pageContext.request.contextPath}/editUserInfoController/edit"
			method="post">
			<table class="table table-bordered">
				<tbody>
					<tr>
						<th scope="row">로그인 아이디</th>
						<td><input class="form-control read-only"
							value="${mypage.user.loginId}" readonly="readonly"></td>
					</tr>
					<tr>
						<th scope="row">이름</th>
						<td><input class="form-control read-only"
							value="${mypage.user.name}" readonly="readonly"></td>
					</tr>
					<tr>
						<th scope="row">성별</th>
						<td><input class="form-control read-only"
							value="${mypage.user.gender}" readonly="readonly"></td>
					</tr>
					<tr>
						<th scope="row">생년월일</th>
						<td><input type="date" name="birthDate" class="form-control"
							value="${mypage.user.birthDate}"></td>
					</tr>
					<tr>
						<th scope="row">이메일</th>
						<td><input type="email" name="email" class="form-control"
							value="${mypage.user.email}"></td>
					</tr>
					<tr>
						<th scope="row">전화번호</th>
						<td><input type="text" name="phone" class="form-control"
							value="${mypage.user.phone}"></td>
					</tr>
					<tr>
						<th scope="row">신분</th>
						<td><input class="form-control read-only"
							value="${mypage.user.role}" readonly="readonly"></td>
					</tr>
					<tr>
						<th scope="row">주소</th>
						<td><input type="text" name="address" class="form-control"
							value="${mypage.user.address}"></td>
					</tr>
				</tbody>
			</table>
	</div>
</div>
<c:choose>
	<c:when test="${AccessInfo.role == 'STUDENT'}">
		<div class="card mt-4">
			<div class="card-body">
				<h5 class="mb-3">학생 정보</h5>

				<table class="table table-bordered">
					<tbody>
						<tr>
							<th scope="row">학번</th>
							<td><input class="form-control read-only"
								value="${mypage.student.studentNumber}" readonly="readonly">
							</td>
						</tr>
						<tr>
							<th scope="row">전공</th>
							<td><input class="form-control read-only"
								value="${mypage.department.departmentName}" readonly="readonly">
							</td>
						</tr>
						<tr>
							<th scope="row">학년</th>
							<td><input class="form-control read-only"
								value="${mypage.student.studentGrade}" readonly="readonly">
							</td>
						</tr>
						<tr>
							<th scope="row">학부 상태 (학부/대학원)</th>
							<td><input class="form-control read-only"
								value="${mypage.student.status}" readonly="readonly"></td>
						</tr>
						<tr>
							<th scope="row">학적 상태</th>
							<td><input class="form-control read-only"
								value="${mypage.student.studentStatus}" readonly="readonly">
							</td>
						</tr>
						<tr>
							<th scope="row">입학일자</th>
							<td><input class="form-control read-only"
								value="${mypage.student.enrollDate}" readonly="readonly">
							</td>
						</tr>
						<tr>
							<th scope="row">졸업일자</th>
							<td><input class="form-control read-only"
								value="${mypage.student.endDate}" readonly="readonly"></td>
						</tr>
						<tr>
							<th scope="row">등록금 계좌</th>
							<td><input type="text" name="tuitionAccount"
								class="form-control" value="${mypage.student.tuitionAccount}">
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</c:when>
</c:choose>
<div class="mt-3">
	<button type="submit" class="btn btn-primary">변경</button>
	<a href="${pageContext.request.contextPath}/mypage/studentPage"
		class="btn btn-warning">취소</a>
	<c:choose>
	<c:when test="${AccessInfo.role == 'STUDENT'}">
	 <a class="btn btn-secondary"
		href="${pageContext.request.contextPath}/student/updateInfo">중요
		정보 수정 요청 <input type="hidden" name="studentUpdateId"
		value="${mypage.user.loginId}" />
	</a>
	</c:when>
	</c:choose>
	
</div>
</form>
