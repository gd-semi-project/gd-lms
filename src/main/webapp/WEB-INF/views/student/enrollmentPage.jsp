<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String ctx = request.getContextPath();
%>

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

.btn-approve {
	background-color: #28a745; /* 초록색 */
	color: #fff;
	border: none;
	border-radius: 6px; /* 둥근 모서리 */
	padding: 6px 14px;
	font-size: 13px;
	font-weight: 600;
	cursor: pointer;
	line-height: 1;
}

.btn-cancel {
	background-color: #ff5252; /* 빨강색 */
	color: #fff;
	border: none;
	border-radius: 6px; /* 둥근 모서리 */
	padding: 6px 14px;
	font-size: 13px;
	font-weight: 600;
	cursor: pointer;
	line-height: 1;
}

.btn-approve:hover {
	background-color: #218838; /* hover 시 더 진한 초록 */
}

.btn-approve:active {
	background-color: #1e7e34; /* 클릭 시 */
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
				<th>강의실</th>
				<th>강의시간</th>
				<th>정원</th>

			</tr>
		</thead>

		<tbody>
			<c:forEach var="lecture" items="${lectureList}" varStatus="status">
				<c:if test="${not myLectureId.contains(lecture.lectureId)}">
					<tr>
						<td>${status.index + 1}</td>
						<td>
							<form method="post" action="<%=ctx%>/enroll/apply">
								<input type="hidden" name="lectureId"
									value="${lecture.lectureId}">
								<button type="submit" class="btn-approve">신청</button>
							</form>
						</td>
						<td>${lecture.departmentName}</td>
						<td>${lecture.lectureTitle}</td>
						<td>${lecture.instructorName}</td>
						<td>${lecture.room}</td>
						<td>${lecture.schedule}</td>
						<td>${lecture.capacity}</td>
					</tr>
				</c:if>
			</c:forEach>

			<c:if test="${empty lectureList}">
				<tr>
					<td colspan="7">개설된 강좌가 없습니다.</td>
				</tr>
			</c:if>
			<c:if test="${not empty alertMsg}">
				<script>
					alert("${alertMsg}");
				</script>
				<c:remove var="alertMsg" scope="session" />
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
				<th>NO</th>
				<th>신청</th>
				<th>과목</th>
				<th>강의명</th>
				<th>담당교수</th>
				<th>강의실</th>
				<th>강의시간</th>
				<th>정원</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach var="enroll" items="${enrollList}" varStatus="status">
				<tr>
				<td>${status.index + 1}</td>
				<td>
					<form method="post" action="<%=ctx%>/enroll/cancel">
						<input type="hidden" name="lectureId" value="${enroll.lectureId}">
						<button type="submit" class="btn-cancel">취소</button>
					</form>
					</td>
					<td>${enroll.departmentName}</td>
					<td>${enroll.lectureTitle}</td>
					<td>${enroll.instructorName}</td>
					<td>${enroll.room}</td>
					<td>${enroll.schedule}</td>
					<td>${enroll.capacity}</td>
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
