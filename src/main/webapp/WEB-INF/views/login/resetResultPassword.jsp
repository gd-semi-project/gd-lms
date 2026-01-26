<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 초기화</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <script>
      const ctx = "${pageContext.request.contextPath}";
    </script>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/resetPassword.js" defer></script>
</head>
<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center vh-100">
    <div class="card shadow-sm p-4 text-center" style="max-width: 400px; width: 100%;">
        <div class="card-body">
            <i class="bi bi-key-fill text-warning" style="font-size: 2rem;"></i>
            <h3 class="card-title mt-2 mb-3">비밀번호 초기화 완료</h3>
            <p class="text-muted">아래 임시 비밀번호로 로그인 후 반드시 변경해주세요.</p>
            <div class="alert alert-primary fw-bold fs-4" role="alert">
                ${tempPassword}
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
