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

<form method="post" action="${ctx}/login/resetPassword">
    <button type="submit" class="btn btn-danger w-100">
        비밀번호 초기화
    </button>
</form>
</body>
</html>