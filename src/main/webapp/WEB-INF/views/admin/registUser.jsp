<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>
    const ctx = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/resources/js/registUserNotDuplicated.js"></script>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">사용자 등록</h1>
    <div class="text-muted small">새로운 사용자 정보를 입력하고 등록합니다.</div>
  </div>
</div>

<div class="card shadow-lg border-0">
  <div class="card-header bg-primary text-white fw-bold">
    사용자 정보 입력
  </div>

  <div class="card-body">
    <form method="post" action="/gd-lms/admin/registUserRequest" class="needs-validation" novalidate>
      
      <!-- 이름 -->
      <div class="mb-3 input-group">
        <span class="input-group-text"><i class="bi bi-person"></i></span>
        <input type="text" class="form-control" id="name" name="name" placeholder="이름 입력" required>
        <div class="invalid-feedback">이름을 입력하세요.</div>
      </div>

      <!-- 아이디 -->
      <div class="mb-3 input-group">
        <input type="hidden" id="idChecked" value="false">
        <span class="input-group-text"><i class="bi bi-person-badge"></i></span>
        <input type="text" class="form-control" id="loginId" name="loginId" placeholder="아이디 입력" required>
        <button type="button" id="checkLoginIdBtn" class="btn btn-outline-secondary">중복확인</button>
        <div class="invalid-feedback">아이디를 입력하세요.</div>
      </div>

      <!-- 비밀번호 -->
      <div class="mb-3 input-group">
        <span class="input-group-text"><i class="bi bi-lock"></i></span>
        <input type="password" class="form-control" id="password" name="password" placeholder="비밀번호 입력" required>
        <div class="invalid-feedback">비밀번호를 입력하세요.</div>
      </div>

      <!-- 이메일 -->
      <div class="mb-3 input-group">
        <input type="hidden" id="emailChecked" value="false">
        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
        <input type="email" class="form-control" id="email" name="email" placeholder="example@domain.com" required>
        <button type="button" id="checkEmailBtn" class="btn btn-outline-secondary">중복확인</button>
        <div class="invalid-feedback">올바른 이메일을 입력하세요.</div>
      </div>

      <!-- 생년월일 -->
      <!-- max값을 동적으로 변경되게 설정 변경 필요 -->
      <div class="mb-3">
        <label for="birthDate" class="form-label">생년월일</label>
        <input type="date" class="form-control" id="birthDate" name="birthDate" min="1900-01-01"
       max="2026-12-31"> 
      </div>

      <!-- 역할 선택 -->
      <div class="mb-3">
        <label for="role" class="form-label">사용자 역할</label>
        <select class="form-select" id="role" name="role" required>
          <option value="">선택하세요</option>
          <option value="학생">학생</option>
          <option value="교수">교수</option>
          <option value="관리자">관리자</option>
        </select>
        <div class="invalid-feedback">사용자 역할을 선택하세요.</div>
      </div>

      <!-- 버튼 -->
      <div class="d-flex justify-content-end gap-2 mt-4">
        <button type="reset" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-counterclockwise"></i> 초기화
        </button>
        <button type="submit" class="btn btn-primary" id="submitBtn">
          <i class="bi bi-check-circle"></i> 등록
        </button>
      </div>
    </form>
  </div>
</div>

<!-- 안내 -->
<div class="mt-3 text-muted small">
  * 등록된 사용자는 시스템 로그인 및 권한 관리에 사용됩니다.
</div>

<script>
  // Bootstrap validation
  (function () {
    'use strict'
    const forms = document.querySelectorAll('.needs-validation')
    Array.from(forms).forEach(function (form) {
      form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
        }
        form.classList.add('was-validated')
      }, false)
    })
  })()
</script>