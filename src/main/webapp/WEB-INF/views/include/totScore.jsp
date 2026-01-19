<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="card mt-4">
	<div class="card-header bg-light fw-bold">
		▶ 이수교과목별 성적 목록 <span class="text-muted float-end">과목총갯수값</span>
	</div>

	<div class="card-body p-0">
		<div class="table-responsive" style="max-height: 500px;">
			<table
				class="table table-bordered table-hover table-sm text-center align-middle mb-0">
				<thead class="table-secondary sticky-top">
					<tr>
						<th>No</th>
						<th>학년도</th>
						<th>학기</th>
						<th>교과목 코드</th>
						<th>교과목명</th>
						<th>이수구분</th>
						<th>신청학점</th>
						<th>취득학점</th>
						<th>점수</th>
						<th>등급</th>
						<th>평점</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="score" items="${scoreList}" varStatus="status">
						<tr>
							<td>${status.count}</td>
							<td>${score.year}</td>
							<td>${score.semester}</td>
							<td>${score.subjectCode}</td>
							<td class="text-start">${score.subjectName}</td>
							<td>${score.type}</td>
							<td>${score.applyCredit}</td>
							<td>${score.earnedCredit}</td>
							<td>${score.score}</td>
							<td>${score.grade}</td>
							<td>${score.gpa}</td>
						</tr>
					</c:forEach>
					<!-- 데이터 없을 때 -->
					<c:if test="${empty scoreList}">
						<tr>
							<td colspan="9" class="text-muted py-4">조회된 성적 정보가 없습니다.</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
</div>
