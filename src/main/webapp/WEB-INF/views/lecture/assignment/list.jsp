<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.AccessInfo}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">📂 과제</h3>

    <c:if test="${user.role == 'INSTRUCTOR' || user.role == 'ADMIN'}">
      <a class="btn btn-primary" href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=writeForm">
        과제 등록
      </a>
    </c:if>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-hover mb-0">
        <thead class="table-light">
          <tr>
            <th style="width:60px;">번호</th>
            <th>제목</th>
            <th style="width:100px;">배점</th>
            <th style="width:180px;">마감일</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty assignments}">
              <tr>
                <td colspan="4" class="text-center text-muted py-4">
                  등록된 과제가 없습니다.
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="a" items="${assignments}" varStatus="st">
                <tr>
                  <td>${st.count}</td>
                  <td>
                    <a href="${ctx}/lecture/assignments?lectureId=${lectureId}&action=view&assignmentId=${a.assignmentId}">
                      <c:out value="${a.title}" />
                    </a>
                  </td>
                  <td>${a.maxScore}점</td>
                  <td>
                    <fmt:parseDate value="${a.dueDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
                    <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd HH:mm" />
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</div>