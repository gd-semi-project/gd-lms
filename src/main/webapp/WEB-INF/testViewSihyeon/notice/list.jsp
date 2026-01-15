<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="cpath" value="${pageContext.request.contextPath}" />
<c:set var="loginRole" value="${sessionScope.role}" />
<c:set var="loginUserId" value="${sessionScope.userId}" />

<div class="d-flex justify-content-between align-items-center mb-3">
  <div>
    <h3 class="mb-0">공지사항</h3>
    <div class="text-muted small">
      <c:if test="${not empty lectureId}">강의 공지 (lectureId: ${lectureId})</c:if>
      <c:if test="${empty lectureId}">전체 공지</c:if>
    </div>
  </div>


	<!-- 글쓰기 버튼 정책 -->
	<c:set var="loginRole" value="${sessionScope.role}" />
	
	<c:if test="${loginRole == 'ADMIN' || (loginRole == 'INSTRUCTOR' && not empty lectureId)}">
	  <c:url var="newUrl" value="/notice/new">
	    <c:if test="${not empty lectureId}">
	      <c:param name="lectureId" value="${lectureId}" />
	    </c:if>
	  </c:url>
	

	  <a class="btn btn-primary" href="${newUrl}">글쓰기</a>
	</c:if>
</div>

<!-- 검색 -->
<form class="row g-2 mb-3" method="get" action="${cpath}/notice/list">
  <c:if test="${not empty lectureId}">
    <input type="hidden" name="lectureId" value="${lectureId}" />
  </c:if>

  <div class="col-auto">
    <select class="form-select" name="items">
      <option value="" ${empty items ? 'selected' : ''}>전체</option>
      <option value="title" ${items == 'title' ? 'selected' : ''}>제목</option>
      <option value="content" ${items == 'content' ? 'selected' : ''}>내용</option>
      <option value="notice_type" ${items == 'notice_type' ? 'selected' : ''}>유형</option>
    </select>
  </div>

  <div class="col-auto">
    <input class="form-control" type="text" name="text" value="${text}" placeholder="검색어" />
  </div>

  <div class="col-auto">
    <input type="hidden" name="page" value="1" />
    <input type="hidden" name="size" value="${size}" />
    <button class="btn btn-outline-secondary" type="submit">검색</button>
  </div>

  <div class="col-auto ms-auto text-muted align-self-center">
    총 <strong>${totalCount}</strong>건
  </div>
</form>

<!-- 목록 -->
<div class="table-responsive">
  <table class="table table-hover align-middle">
    <thead class="table-light">
      <tr>
        <th style="width: 90px;">ID</th>
        <th style="width: 140px;">유형</th>
        <th>제목</th>
        <th style="width: 180px;">작성일</th>
        <th style="width: 100px;">조회수</th>
      </tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty noticeList}">
          <tr>
            <td colspan="5" class="text-center text-muted py-5">등록된 공지사항이 없습니다.</td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="n" items="${noticeList}">
            <c:url var="viewUrl" value="/notice/view">
              <c:param name="noticeId" value="${n.noticeId}" />
              <c:if test="${not empty lectureId}">
                <c:param name="lectureId" value="${lectureId}" />
              </c:if>
            </c:url>
            <tr>
              <td>${n.noticeId}</td>
              <td>${n.noticeType}</td>
              <td>
                <a class="text-decoration-none" href="${cpath}${viewUrl}">
                  <c:out value="${n.title}" />
                </a>
              </td>
              <td>${n.createdAt}</td>
              <td>${n.viewCount}</td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>

<!-- 페이징 -->
<c:if test="${totalPages > 1}">
  <nav>
    <ul class="pagination justify-content-center">

      <!-- Prev -->
      <li class="page-item ${page <= 1 ? 'disabled' : ''}">
        <c:url var="prevUrl" value="/notice/list">
          <c:if test="${not empty lectureId}"><c:param name="lectureId" value="${lectureId}" /></c:if>
          <c:if test="${not empty items}"><c:param name="items" value="${items}" /></c:if>
          <c:if test="${not empty text}"><c:param name="text" value="${text}" /></c:if>
          <c:param name="page" value="${page - 1}" />
          <c:param name="size" value="${size}" />
        </c:url>
        <a class="page-link" href="${cpath}${prevUrl}">이전</a>
      </li>

      <!-- Pages -->
      <c:forEach var="p" begin="1" end="${totalPages}">
        <li class="page-item ${p == page ? 'active' : ''}">
          <c:url var="pageUrl" value="/notice/list">
            <c:if test="${not empty lectureId}"><c:param name="lectureId" value="${lectureId}" /></c:if>
            <c:if test="${not empty items}"><c:param name="items" value="${items}" /></c:if>
            <c:if test="${not empty text}"><c:param name="text" value="${text}" /></c:if>
            <c:param name="page" value="${p}" />
            <c:param name="size" value="${size}" />
          </c:url>
          <a class="page-link" href="${cpath}${pageUrl}">${p}</a>
        </li>
      </c:forEach>

      <!-- Next -->
      <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
        <c:url var="nextUrl" value="/notice/list">
          <c:if test="${not empty lectureId}"><c:param name="lectureId" value="${lectureId}" /></c:if>
          <c:if test="${not empty items}"><c:param name="items" value="${items}" /></c:if>
          <c:if test="${not empty text}"><c:param name="text" value="${text}" /></c:if>
          <c:param name="page" value="${page + 1}" />
          <c:param name="size" value="${size}" />
        </c:url>
        <a class="page-link" href="${cpath}${nextUrl}">다음</a>
      </li>

    </ul>
  </nav>
</c:if>
