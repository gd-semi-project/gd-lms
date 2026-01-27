<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>401 Unauthorized</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(rgba(0,0,0,.5), rgba(0,0,0,.5)),
                        url('${pageContext.request.contextPath}/resources/images/campus_image01.png')
                        center/cover no-repeat;
        }
    </style>
</head>
<body>

<div class="container vh-100 d-flex align-items-center justify-content-center">
    <div class="card shadow-lg text-center p-4" style="max-width: 520px;">
        <h1 class="display-4 text-primary fw-bold">401</h1>
        <h5>${errorMessage}</h5>
        <p class="text-muted">로그인 후 다시 시도해주세요.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">로그인</a>
    </div>
</div>

</body>
</html>