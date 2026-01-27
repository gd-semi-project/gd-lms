<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
  <div class="alert alert-danger">
    <h4 class="alert-heading">로그인이 필요한 페이지입니다. (401)</h4>
    <p class="mb-0">
      <c:out value="${requestScope.errorMessage}" />
    </p>
  </div>

  <a class="btn btn-secondary" href="${pageContext.request.contextPath}/login">로그인 화면으로</a>
</div>