<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="module" src="${pageContext.request.contextPath}/resources/js/resetPasswordView.js"></script>

<script>
  const ctx = "${pageContext.request.contextPath}";
</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">

<div class="login-wrapper">
  <div class="login-container">

    <h3 class="text-center mb-4">LMS 로그인</h3>

    <form action="${pageContext.request.contextPath}/login/login.do" method="post">
      <div class="mb-3">
        <label class="form-label">아이디</label>
        <input type="text" class="form-control" name="id" required />
      </div>

      <div class="mb-3">
        <label class="form-label">비밀번호</label>
        <input type="password" class="form-control" name="pw" required />
      </div>

      <button type="submit" class="btn btn-primary w-100">로그인</button>
    </form>

    <c:if test="${not empty requestScope.LoginErrorMsg}">
      <div class="alert alert-danger mt-3">
        ${requestScope.LoginErrorMsg}
      </div>
    </c:if>

    <a href="javascript:void(0);" id="resetLoginPassword">비밀번호 초기화</a>

  </div>
</div>