<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">공지사항</h1>
    <div class="text-muted small">작성/열람/수정/삭제</div>
  </div>

  <div class="d-flex gap-2">
    <form class="d-flex gap-2" method="get" action="${pageContext.request.contextPath}/admin/notice/list">
      <input class="form-control form-control-sm" name="q" value="${fn:escapeXml(param.q)}"
             placeholder="제목 검색" style="width: 220px;">
      <button class="btn btn-sm btn-outline-secondary" type="submit">검색</button>
    </form>
    <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/admin/notice/write">작성</a>
  </div>
</div>

<div class="card shadow-sm">
  <div class="card-body p-0">
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th style="width:10%">번호</th>
            <th>제목</th>
            <th style="width:14%">작성자</th>
            <th style="width:18%">작성일</th>
            <th style="width:10%" class="text-end">조회</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty noticeList}">
              <tr>
                <td colspan="5" class="text-center text-muted py-5">공지사항이 없습니다.</td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="n" items="${noticeList}">
                <tr>
                  <td>${n.noticeId}</td>
                  <td>
                    <a class="text-decoration-none"
                       href="${pageContext.request.contextPath}/admin/notice/detail?id=${n.noticeId}">
                      ${fn:escapeXml(n.title)}
                    </a>
                    <c:if test="${n.pinned}">
                      <span class="badge bg-warning text-dark ms-2">상단고정</span>
                    </c:if>
                  </td>
                  <td>${fn:escapeXml(n.writerName)}</td>
                  <td>${n.createdAt}</td>
                  <td class="text-end">${n.viewCount}</td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- (선택) 페이징 영역 -->
<c:if test="${not empty pagingHtml}">
  <div class="mt-3 d-flex justify-content-center">
    ${pagingHtml}
  </div>
</c:if>
