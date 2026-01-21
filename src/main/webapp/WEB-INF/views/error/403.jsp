<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
  <div class="alert alert-danger">
    <h4 class="alert-heading">접근 권한이 없습니다 (403)</h4>
    <p class="mb-0">
      <c:out value="${requestScope.errorMessage}" />
    </p>
  </div>

  <a class="btn btn-secondary" href="${pageContext.request.contextPath}/notice/list">공지사항으로</a>
</div>
