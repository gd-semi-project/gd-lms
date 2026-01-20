<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
</style>
<br><br>
			<!-- 바디 -->
				<h3 class="mb-4">마이페이지</h3>

				<div class="card">
					<div class="card-body">
						<table class="table table-bordered">
							<tbody>
								<tr>
									<th scope="row">로그인 아이디</th>
									<td>${mypage.user.login_id}</td>
								</tr>
								<tr>
									<th scope="row">이름</th>
									<td>${mypage.user.name}</td>
								</tr>
								<tr>
									<th scope="row">생년월일</th>
									<td>${mypage.user.birth_date}</td>
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
									<th scope="row">비밀번호 변경 필요 여부</th>
									<td>${mypage.user.must_change_pw}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="mt-3">
					<a href="${pageContext.request.contextPath}/user/edit"
						class="btn btn-primary">정보 수정</a> <a
						href="${pageContext.request.contextPath}/user/changePassword"
						class="btn btn-warning">비밀번호 변경</a>
				</div>


