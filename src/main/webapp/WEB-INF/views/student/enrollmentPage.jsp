<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
.box {
	border: 1px solid #ddd;
	background: #fff;
	padding: 12px;
	margin-bottom: 20px;
}

table {
	width: 100%;
	border-collapse: collapse;
	background: #fff;
}

th, td {
	border: 1px solid #ddd;
	padding: 8px;
	font-size: 14px;
}

th {
	background: #f9f9f9;
	font-weight: bold;
}

h4 {
	margin: 0 0 10px 0;
}

button {
	padding: 4px 8px;
	cursor: pointer;
}
</style>

<!-- ===================== -->
<!-- 수강신청 검색 영역 -->
<!-- ===================== -->
<div class="box">
	<form method="get" action="">
		학과 <select name="type">
			<option value="">전체</option>
		</select> 반 <select name="dept">
			<option value="">전체</option>
		</select> 과목명 <input type="text" name="subjectName" />

		<button type="button">검색</button>
	</form>
</div>

<!-- ===================== -->
<!-- 개설 강좌 목록 -->
<!-- ===================== -->
<div class="box">
	<h4>개설강좌</h4>

	<table>
		<thead>
			<tr>
				<th>NO</th>
				<th>신청</th>
				<th>과목</th>
				<th>강의명</th>
				<th>담당교수</th>
				<th>강의시간</th>
				<th>정원</th>

			</tr>
		</thead>

		<tbody>
			<c:forEach var="lecture" items="${lectureList}">
				<tr>
					<form method="post" action="${ctx}/enroll/apply">
						<input type="hidden" name="lectureId" value="${lecture.lectureId}">
						<button type="submit">신청</button>
					</form>
					<td>${lecture.semester}</td>
					<td>${lecture.subjectCode}</td>
					<td>${lecture.subjectName}</td>
					<td>${lecture.credit}/${lecture.hours}</td>
					<td>${lecture.professorName}</td>
					<td>${lecture.schedule}</td>
				</tr>
			</c:forEach>

			<c:if test="${empty lectureList}">
				<tr>
					<td colspan="7">개설된 강좌가 없습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>


</div>

<!-- ===================== -->
<!-- 수강신청 내역 -->
<!-- ===================== -->
<div class="box">
	<h4>수강신청내역</h4>

	<table>
		<thead>
			<tr>
				<th>취소</th>
				<th>년도/학기</th>
				<th>과목번호</th>
				<th>과목명</th>
				<th>학점/시간</th>
				<th>담당교수</th>
				<th>강의시간</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach var="enroll" items="${enrollList}">
				<tr>
					<form method="post" action="${ctx}/enroll/cancel">
						<input type="hidden" name="lectureId" value="${enroll.lectureId}">
						<button type="submit">취소</button>
					</form>
					<td>${enroll.semester}</td>
					<td>${enroll.subjectCode}</td>
					<td>${enroll.subjectName}</td>
					<td>${enroll.credit}/${enroll.hours}</td>
					<td>${enroll.professorName}</td>
					<td>${enroll.schedule}</td>
				</tr>
			</c:forEach>

			<c:if test="${empty enrollList}">
				<tr>
					<td colspan="7">신청한 강좌가 없습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>
