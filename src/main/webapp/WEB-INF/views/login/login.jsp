<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>LMS 로그인</title>

  <!-- Bootstrap CSS -->
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
    rel="stylesheet"
  />

  <style>
    .login-container {
      max-width: 400px;
      margin: 80px auto;
      padding: 30px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 0 15px rgba(0,0,0,0.1);
    }
  </style>
</head>

<body class="bg-light">

  <!-- 헤더 include -->
  <jsp:include page="/WEB-INF/views/include/header.jsp" />

  <div class="container-fluid">
    <div class="row">
      <!-- 바디 -->
      <main class="col-12 col-md-9 col-lg-10 p-4">

        <!-- 로그인 폼 -->
        <div class="login-container">
          <h3 class="text-center mb-4">LMS 로그인</h3>
          <form action="/gd-lms/login/login.do" method="post">
            <div class="mb-3">
              <label for="userId" class="form-label">아이디</label>
              <input type="text" class="form-control" id="userId" name="id" required />
            </div>
            <div class="mb-3">
              <label for="userPw" class="form-label">비밀번호</label>
              <input type="password" class="form-control" id="userPw" name="pw" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">로그인</button>
          </form>

          <!-- 로그인 실패 메시지 -->
          <c:if test="${not empty sessionScope.LoginErrorMsg}">
            <div class="alert alert-danger mt-3">
              ${sessionScope.LoginErrorMsg}
              <c:remove var="LoginErrorMsg" />
            </div>
          </c:if>
        </div>

      </main>
    </div>
  </div>

  <!-- 푸터 include -->
  <jsp:include page="/WEB-INF/views/include/footer.jsp" />

  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>