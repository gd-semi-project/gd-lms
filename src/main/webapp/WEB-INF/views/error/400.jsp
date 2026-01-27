<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>400 Bad Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container vh-100 d-flex justify-content-center align-items-center">
    <div class="card shadow-sm text-center p-4" style="max-width: 480px;">
        <h1 class="display-5 text-danger fw-bold">400</h1>
        <h5>${errorMessage}</h5>
        <p class="text-muted">요청 정보가 올바르지 않습니다.</p>
        <a href="${pageContext.request.contextPath}/main" class="btn btn-secondary">메인으로</a>
    </div>
</div>

</body>
</html>