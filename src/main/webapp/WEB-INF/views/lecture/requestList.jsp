<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<h3 class="mb-4">📘 강의 개설 신청</h3>

<!-- 성공 메시지 -->
<c:if test="${param.success == 'created'}">
    <div class="alert alert-success alert-dismissible fade show">
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

<!-- 신청 기간 아닐 때 -->
<c:if test="${!isLectureRequestOpen}">
    <div class="alert alert-warning border-start border-4 border-warning p-4 mb-4">
        <h5 class="fw-bold mb-2">⚠ 강의 개설 신청 기간이 아닙니다</h5>
        <p class="mb-1">현재는 강의 개설 신청 기간이 아닙니다.</p>

        <c:if test="${not empty requestStartDate}">
            <p class="mb-0 text-muted">
                📅 신청 가능 기간 :
                <strong>${requestStartDate} ~ ${requestEndDate}</strong>
            </p>
        </c:if>
    </div>
</c:if>

<!-- 신청 기간일 때만 -->
<c:if test="${isLectureRequestOpen}">

    <!-- 신청 버튼 -->
    <div class="mb-3 text-end">
        <a href="${ctx}/instructor/lecture/request/new"
           class="btn btn-primary">
            강의 개설 신청
        </a>
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
                <th style="width:160px;">관리</th>
            </tr>
        </thead>

        <tbody>
            <c:choose>
                <c:when test="${empty requests}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">
                            아직 강의 개설 신청 내역이 없습니다.
                        </td>
                    </tr>
                </c:when>

                <c:otherwise>
                    <c:forEach var="req" items="${requests}">
                        <tr class="text-center">

                            <!-- 강의명 클릭 = 상세 -->
                            <td class="text-start">
                                <a href="${ctx}/instructor/lecture/request/detail?lectureId=${req.lectureId}"
                                   class="text-decoration-none fw-semibold">
                                    ${req.lectureTitle}
                                </a>
                            </td>

                            <td>${req.startDate} ~ ${req.endDate}</td>
                            <td>${req.section}</td>
                            <td>${req.capacity}</td>

                            <!-- 상태 -->
                            <td>
                                <span class="badge
                                    <c:choose>
                                        <c:when test="${req.validation == 'PENDING'}">bg-warning</c:when>
                                        <c:when test="${req.validation == 'CONFIRMED'}">bg-success</c:when>
                                        <c:otherwise>bg-danger</c:otherwise>
                                    </c:choose>">
                                    ${req.validation}
                                </span>
                            </td>

                            <!-- 관리 버튼 -->
                            <td>
							    <!-- 수정 : 항상 가능 -->
							    <a class="btn btn-sm btn-warning me-1"
							       href="${ctx}/instructor/lecture/request/edit?lectureId=${req.lectureId}">
							        수정
							    </a>
							
							    <!-- 삭제 : PENDING만 -->
							    <c:if test="${req.validation eq 'PENDING'}">
							        <form method="post"
							              action="${ctx}/instructor/lecture/request/delete"
							              style="display:inline;"
							              onsubmit="return confirm('정말 삭제하시겠습니까?');">
							            <input type="hidden" name="lectureId" value="${req.lectureId}">
							            <button class="btn btn-sm btn-danger">
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