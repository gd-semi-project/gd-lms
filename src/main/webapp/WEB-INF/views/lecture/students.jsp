<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h4 class="mb-3">👥 수강생 목록</h4>

<c:if test="${empty students}">
	<div class="alert alert-info">수강 중인 학생이 없습니다.</div>
</c:if>

<c:if test="${not empty students}">
	<table class="table table-bordered table-hover">
		<thead class="table-light">
			<tr>
				<th>학번</th>
				<th>이름</th>
				<th>학년</th>
				<th>수강 상태</th>
				<th>신청일</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="s" items="${students}">
				<tr>
					<td>${s.studentNumber}</td>
					<td>${s.studentName}</td>
					<td>${s.studenGrade}</td>
					<td><span class="badge bg-secondary">
							${s.enrollmentStatus} </span></td>
					<td>${s.appliedAt}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>