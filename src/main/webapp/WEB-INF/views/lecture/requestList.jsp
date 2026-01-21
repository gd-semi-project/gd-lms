<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<h3 class="mb-4">📘 강의 개설 신청</h3>

<div class="mb-3 text-end">
    <a class="btn btn-primary"
       href="${ctx}/instructor/lecture/request/new">
        + 신청하기
    </a>
</div>

<table class="table table-bordered text-center align-middle">
    <thead class="table-light">
        <tr>
            <th>강의명</th>
            <th>분반</th>
            <th>정원</th>
            <th>상태</th>
            <th>신청일</th>
            <th>관리</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="r" items="${requests}">
            <tr>
                <td>${r.lectureTitle}</td>
                <td>${r.section}</td>
                <td>${r.capacity}</td>

                <td>
                    <span class="badge
                        ${r.validation == 'CONFIRMED' ? 'bg-success' :
                          r.validation == 'PENDING'   ? 'bg-warning' :
                                                       'bg-danger'}">
                        ${r.validation}
                    </span>
                </td>

                <td>${r.createdAt}</td>

                <td>
                    <c:choose>
                        <c:when test="${r.validation == 'PENDING'}">
                            <a class="btn btn-sm btn-warning"
                               href="${ctx}/instructor/lecture/request/edit?lectureId=${r.lectureId}">
                                수정
                            </a>

                            <form method="post"
                                  action="${ctx}/instructor/lecture/request/delete"
                                  style="display:inline">
                                <input type="hidden" name="lectureId"
                                       value="${r.lectureId}" />
                                <button type="submit"
                                        class="btn btn-sm btn-danger"
                                        onclick="return confirm('삭제하시겠습니까?')">
                                    삭제
                                </button>
                            </form>
                        </c:when>

                        <c:when test="${r.validation == 'CONFIRMED'}">
                            <a class="btn btn-sm btn-warning"
                               href="${ctx}/instructor/lecture/request/edit?lectureId=${r.lectureId}">
                                수정
                            </a>
                        </c:when>

                        <c:otherwise>
                            <span class="text-muted">수정 불가</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty requests}">
            <tr>
                <td colspan="6">신청한 강의가 없습니다.</td>
            </tr>
        </c:if>
    </tbody>
</table>