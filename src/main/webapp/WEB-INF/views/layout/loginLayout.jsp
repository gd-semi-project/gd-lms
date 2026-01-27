<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Layout</title>

  <!-- Bootstrap CSS -->
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
    rel="stylesheet"
  />
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  
  <script src="${pageContext.request.contextPath}/resources/js/appClock.js"></script>
  <script src="${pageContext.request.contextPath}/resources/js/keronBallLauncher.js"></script>
  
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/loginlayout.css">
  

</head>

<body class="bg-light">

  <jsp:include page="/WEB-INF/views/include/header.jsp" />
  <div class="container-fluid">
    <div class="row">

      <!-- 바디 -->
      <main class="col-12 p-4 login-main">
      
      <c:if test="${not empty requestScope.contentPage}">
	      <jsp:include page = "${requestScope.contentPage}"/>
      </c:if>
      
      </main>

    </div>
  </div>

  <!-- 푸터 include (선택) -->
  <jsp:include page="/WEB-INF/views/include/footer.jsp" />

  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>