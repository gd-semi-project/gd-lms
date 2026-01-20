<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
  <h3 class="mb-3">Q&A</h3>

  <div class="d-flex justify-content-between mb-3">
    <div class="text-muted">
      총 ${totalCount}건
    </div>
    <c:if test="${role == 'STUDENT'}">
      <a class="btn btn-primary" href="${ctx}/qna/new?lectureId=${lectureId}">질문 작성</a>
    </c:if>
  </div>

  <c:choose>
    <c:when test="${empty qnaList}">
      <div class="alert alert-info text-center">등록된 Q&A가 없습니다.</div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table table-hover">
          <thead class="table-light">
          <tr>
            <th width="10%">번호</th>
            <th width="10%">공개</th>
            <th>제목</th>
            <th width="15%">작성자</th>
            <th width="15%">상태</th>
            <th width="15%">작성일</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="q" items="${qnaList}" varStatus="st">
            <tr>
              <td class="text-center">${totalCount - ((page - 1) * size + st.index)}</td>
              <td class="text-center">
                <c:choose>
                  <c:when test="${q.isPrivate == 'Y'}"><span class="badge bg-secondary">비공개</span></c:when>
                  <c:otherwise><span class="badge bg-success">공개</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <a class="text-decoration-none"
                   href="${ctx}/qna/view?lectureId=${lectureId}&qnaId=${q.qnaId}">
                  <c:out value="${q.title}" />
                </a>
              </td>
              <td class="text-center">${q.authorId}</td>
              <td class="text-center">${q.status}</td>
              <td class="text-center">
                <c:set var="ds" value="${q.createdAt.toString()}" />
                ${fn:substring(ds, 0, 10)}
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>

      <!-- paging -->
      <nav>
        <ul class="pagination justify-content-center">
          <c:if test="${page > 1}">
            <li class="page-item">
              <a class="page-link" href="${ctx}/qna/list?lectureId=${lectureId}&page=${page-1}&size=${size}">이전</a>
            </li>
          </c:if>

          <c:forEach var="i" begin="${page-2 < 1 ? 1 : page-2}" end="${page+2 > totalPages ? totalPages : page+2}">
            <li class="page-item ${i == page ? 'active' : ''}">
              <a class="page-link" href="${ctx}/qna/list?lectureId=${lectureId}&page=${i}&size=${size}">${i}</a>
            </li>
          </c:forEach>

          <c:if test="${page < totalPages}">
            <li class="page-item">
              <a class="page-link" href="${ctx}/qna/list?lectureId=${lectureId}&page=${page+1}&size=${size}">다음</a>
            </li>
          </c:if>
        </ul>
      </nav>
    </c:otherwise>
  </c:choose>
</div>
