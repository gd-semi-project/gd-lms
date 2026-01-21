<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />

<!-- ===================== 제목 ===================== -->
<c:choose>
    <c:when test="${role == 'INSTRUCTOR'}">
        <h3 class="mb-3">📚 내 강의 목록</h3>
    </c:when>
    <c:when test="${role == 'STUDENT'}">
        <h3 class="mb-3">📚 수강 중인 강의</h3>
    </c:when>
    <c:otherwise>
        <h3 class="mb-3">📚 강의 목록</h3>
    </c:otherwise>
</c:choose>

<!-- ===================== 비어있을 때 ===================== -->
<c:if test="${empty lectures}">
    <div class="alert alert-info">
        <c:choose>
            <c:when test="${role == 'INSTRUCTOR'}">
                담당 중인 강의가 없습니다.
            </c:when>
            <c:when test="${role == 'STUDENT'}">
                수강 중인 강의가 없습니다.
            </c:when>
            <c:otherwise>
                강의 정보가 없습니다.
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<!-- ===================== 강의 목록 ===================== -->
<c:if test="${not empty lectures}">
    <table class="table table-bordered table-hover align-middle">
        <thead class="table-light text-center">
            <tr>
                <th>강의명</th>
                <th>분반</th>
                <th>기간</th>
                <th>강의실</th>

                <!-- 교수 전용 -->
                <c:if test="${role == 'INSTRUCTOR'}">
                    <th>정원</th>
                </c:if>

                <!-- 학생 전용 -->
                <c:if test="${role == 'STUDENT'}">
                    <th>수강상태</th>
                </c:if>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="lec" items="${lectures}">
                <tr class="text-center">

                    <!-- 강의명 -->
                    <td class="text-start">
                        <a href="${ctx}/lecture/detail?lectureId=${lec.lectureId}"
                           class="text-decoration-none fw-semibold">
                            ${lec.lectureTitle}
                        </a>
                    </td>

                    <!-- 분반 -->
                    <td>${lec.section}</td>

                    <!-- 기간 -->
                    <td>
                        ${lec.startDate} ~ ${lec.endDate}
                    </td>

                    <!-- 강의실 -->
                    <td>${lec.room}</td>

                    <!-- 교수 전용 컬럼 -->
                    <c:if test="${role == 'INSTRUCTOR'}">
                        <td>${lec.capacity}</td>
                    </c:if>

                    <!-- 학생 전용 컬럼 -->
                    <c:if test="${role == 'STUDENT'}">
                        <td>
                            <span class="badge bg-success">수강중</span>
                        </td>
                    </c:if>

                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>