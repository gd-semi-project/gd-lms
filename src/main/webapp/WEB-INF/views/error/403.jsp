<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>403 Forbidden</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(rgba(0,0,0,.6), rgba(0,0,0,.6)),
                        url('${pageContext.request.contextPath}/resources/images/campus_image01.png')
                        center/cover no-repeat;
        }
    </style>
</head>
<body>

<div class="container vh-100 d-flex align-items-center justify-content-center">
    <div class="card shadow-lg text-center p-4" style="max-width: 520px;">
        <h1 class="display-4 text-danger fw-bold">403</h1>
        <h5>${errorMessage}</h5>
        <p class="text-muted">해당 페이지에 접근할 수 없습니다.</p>
        <a href="${pageContext.request.contextPath}/main" class="btn btn-secondary">메인으로</a>
    </div>
</div>

</body>
</html>