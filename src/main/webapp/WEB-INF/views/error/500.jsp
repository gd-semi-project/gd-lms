<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>500 Server Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container vh-100 d-flex justify-content-center align-items-center">
    <div class="card border-danger shadow-sm text-center p-4" style="max-width: 520px;">
        <h1 class="display-4 text-danger fw-bold">500</h1>
        <h5>${errorMessage}</h5>
        <p class="text-muted">잠시 후 다시 시도해주세요.</p>
        <a href="${pageContext.request.contextPath}/main" class="btn btn-outline-danger">메인으로</a>
    </div>
</div>

</body>
</html>