<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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

  <style>
    .sidebar {
      min-height: calc(100vh - 56px); /* navbar 기본 높이 */
    }
    .sidebar .nav-link:hover {
      background: rgba(255,255,255,0.15);
    }
  </style>
</head>

<body class="bg-light">

  <!-- 헤더 include -->
  <jsp:include page="/WEB-INF/testViewSihyeon/common/header.jsp" />

  <div class="container-fluid">
    <div class="row">

      <!-- 사이드바 include -->
      <jsp:include page="/WEB-INF/testViewSihyeon/common/sidebar.jsp" />

      <!-- 바디 -->
      <main class="col-12 col-md-9 col-lg-10 p-4">
      
      바디
      
      </main>

    </div>
  </div>

  <!-- 푸터 include (선택) -->
  <jsp:include page="/WEB-INF/testViewSihyeon/common/footer.jsp" />

  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
