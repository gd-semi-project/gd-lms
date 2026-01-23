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
	<script src="${pageContext.request.contextPath}/resources/js/resetPassword.js" defer></script>
</head>
<body class="bg-light">

<div class="d-flex justify-content-center align-items-center vh-100">
    <div class="card shadow-sm p-4" style="width: 100%; max-width: 400px;">
        <h3 class="text-center mb-4">비밀번호 초기화</h3>

        <form id="passwdResetForm">
            <!-- 이메일 -->
            <div class="mb-3 input-group">
                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                <input type="email" class="form-control" id="email" name="email" placeholder="example@domain.com" required>
            </div>

            <!-- 생년월일 -->
            <div class="mb-3 input-group">
                <span class="input-group-text"><i class="bi bi-calendar"></i></span>
                <input type="date" class="form-control" id="birthDate" name="birthDate" required
                       max="<%= java.time.LocalDate.now() %>">
            </div>
            <button type="button" id="checkInfoBtn" class="btn btn-primary w-100 mb-3">인증</button>

            <!-- 비밀번호 초기화 버튼 -->
            <button type="button" id="passwdReset" class="btn btn-success w-100 mt-3">
                비밀번호 초기화
            </button>
        </form>
    </div>
</div>
</body>
</html>
