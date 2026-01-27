<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공통 탭 -->
<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="d-flex justify-content-between align-items-center mb-3">
  <h3 class="mb-0">💬 Q&amp;A</h3>
     <c:if test="${not empty errorMessage}">
     	<div class="alert alert-danger">${errorMessage}</div>
   	 </c:if>

  <c:if test="${sessionScope.AccessInfo.role == 'STUDENT'}">
    <a class="btn btn-primary"
       href="${ctx}/lecture/qna?lectureId=${lectureId}&action=writeForm">
      질문 작성
    </a>
  </c:if>
</div>

<div class="card">
  <div class="card-body p-0">
    <table class="table table-hover mb-0">
      <thead class="table-light">
        <tr>
          <th style="width:90px;">번호</th>
          <th>제목</th>
          <th style="width:140px;">상태</th>
          <th style="width:220px;">작성일</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty qnaList}">
            <tr>
              <td colspan="4" class="text-center text-muted py-4">
                등록된 질문이 없습니다.
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="row" items="${qnaList}" varStatus="st">
              <tr>
                <td>
                  <c:out value="${totalCount - ((page-1)*size) - st.index}" />
                </td>
                <td>
                  <c:if test="${row.isPrivate != null && row.isPrivate.toString() == 'Y'}">
                    <span class="badge text-bg-secondary me-2">비공개</span>
                  </c:if>
                  <a href="${ctx}/lecture/qna?lectureId=${lectureId}&action=view&qnaId=${row.qnaId}">
                    <c:out value="${row.title}" />
                  </a>
                </td>
                <td><c:out value="${row.status.displayName}" /></td>
                <td>
  					${fn:replace(fn:substring(row.createdAt.toString(), 0, 16), 'T', ' ')}
				</td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>
</div>

<c:if test="${totalPages > 1}">
  <nav class="mt-3">
    <ul class="pagination justify-content-center">
      <li class="page-item ${page <= 1 ? 'disabled' : ''}">
        <a class="page-link"
           href="${ctx}/lecture/qna?lectureId=${lectureId}&page=${page-1}&size=${size}">이전</a>
      </li>

      <c:forEach var="p" begin="1" end="${totalPages}">
        <li class="page-item ${p == page ? 'active' : ''}">
          <a class="page-link"
             href="${ctx}/lecture/qna?lectureId=${lectureId}&page=${p}&size=${size}">${p}</a>
        </li>
      </c:forEach>

      <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
        <a class="page-link"
           href="${ctx}/lecture/qna?lectureId=${lectureId}&page=${page+1}&size=${size}">다음</a>
      </li>
    </ul>
  </nav>
</c:if>