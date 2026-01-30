<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<h3>이수교과목별 성적 목록</h3>
<div class="card mt-4">
    <div class="card-header bg-light fw-bold">
        성적조회
        <span class="text-muted float-end">
            총 ${fn:length(myScores)}과목
        </span>
    </div>

    <div class="card-body p-0">
        <div class="table-responsive" style="max-height: 500px;">
            <table
                class="table table-bordered table-hover table-sm text-center align-middle mb-0">
                <thead class="table-secondary sticky-top">
                    <tr>
                        <th>No</th>
                        <th>년도</th>
                        <th>교과목명</th>
                        <th>출석</th>
                        <th>과제</th>
                        <th>중간</th>
                        <th>기말</th>
                        <th>총점</th>
                        <th>등급</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="score" items="${myScores}" varStatus="st">
                        <tr>
                            <!-- No -->
                            <td>${st.index + 1}</td>

                            <!-- 년도 (start_date 기준) -->
                            <td>
                                ${score.startDate.year}
                            </td>

                            <!-- 교과목명 -->
                            <td>${score.lectureTitle}</td>

                            <!-- 출석 -->
                            <td>${score.attendanceScore}</td>

                            <!-- 과제 -->
                            <td>${score.assignmentScore}</td>

                            <!-- 중간 -->
                            <td>${score.midtermScore}</td>

                            <!-- 기말 -->
                            <td>${score.finalScore}</td>

                            <!-- 총점 -->
                            <td>
                                <c:choose>
                                    <c:when test="${score.confirmed}">
                                        ${score.totalScore}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">미확정</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- 등급 -->
                            <td>
                                <c:choose>
                                    <c:when test="${score.confirmed}">
                                        ${score.gradeLetter}
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    <!-- 데이터 없을 때 -->
                    <c:if test="${empty myScores}">
                        <tr>
                            <td colspan="9" class="text-muted py-4">
                                조회된 성적 정보가 없습니다.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
