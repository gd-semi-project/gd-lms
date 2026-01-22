<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h3 class="mb-4">📘 강의 개설 신청</h3>


<c:if test="${param.success == 'created'}">
    <div class="alert alert-success alert-dismissible fade show">
        <strong>신청 완료</strong><br/>
        강의 개설 신청이 정상적으로 접수되었습니다.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${param.success == 'deleted'}">
    <div class="alert alert-success alert-dismissible fade show">
        강의 신청이 삭제되었습니다.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- =========================
     신청 기간 아닐 때 경고 UI
     ========================= -->
<c:if test="${!isLectureRequestOpen}">
	<div
		class="alert alert-warning border-start border-4 border-warning p-4 mb-4">
		<h5 class="fw-bold mb-2">⚠ 강의 개설 신청 기간이 아닙니다</h5>

		<p class="mb-1">
			현재는 <strong>강의 개설 신청 기간</strong>이 아닙니다.
		</p>

		<c:if test="${not empty requestStartDate}">
			<p class="mb-0 text-muted">
				📅 신청 가능 기간 : <strong> ${requestStartDate} ~
					${requestEndDate} </strong>
			</p>
		</c:if>
	</div>
</c:if>

<!-- =========================
     신청 기간일 때만 표시
     ========================= -->
<c:if test="${isLectureRequestOpen}">

	<!-- 신청 버튼 -->
	<div class="mb-3 text-end">
		<a
			href="${pageContext.request.contextPath}/instructor/lecture/request/new"
			class="btn btn-primary"> ➕ 강의 개설 신청 </a>
	</div>

	<!-- 신청 목록 -->
	<table class="table table-bordered table-hover align-middle">
		<thead class="table-light text-center">
			<tr>
				<th>강의명</th>
				<th>기간</th>
				<th>분반</th>
				<th>정원</th>
				<th>상태</th>
				<th style="width: 140px;">관리</th>
			</tr>
		</thead>

		<tbody>
			<c:choose>
				<c:when test="${empty requests}">
					<tr>
						<td colspan="6" class="text-center text-muted py-4">아직 강의 개설
							신청 내역이 없습니다.</td>
					</tr>
				</c:when>

				<c:otherwise>
					<c:forEach var="req" items="${requests}">
						<tr class="text-center">
							<td class="text-start">${req.lectureTitle}</td>
							<td>${req.startDate} ~ ${req.endDate}</td>
							<td>${req.section}</td>
							<td>${req.capacity}</td>
							<td><span
								class="badge
                                <c:choose>
                                    <c:when test="${req.validation == 'PENDING'}">bg-warning</c:when>
                                    <c:when test="${req.validation == 'CONFIRMED'}">bg-success</c:when>
                                    <c:otherwise>bg-danger</c:otherwise>
                                </c:choose>">
									${req.validation} </span></td>
							<td><a
								href="${pageContext.request.contextPath}/instructor/lecture/request/edit?lectureId=${req.lectureId}"
								class="btn btn-sm btn-outline-secondary"> 수정 </a> 
								<c:if
									test="${req.validation == 'PENDING'}">
									<form method="post"
										action="${pageContext.request.contextPath}/instructor/lecture/request/delete"
										style="display: inline;"
										onsubmit="return confirm('정말 삭제하시겠습니까?');">
										<input type="hidden" name="lectureId" value="${req.lectureId}">
										<button type="submit" class="btn btn-sm btn-outline-danger">
											삭제
										</button>
									</form>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</c:if>