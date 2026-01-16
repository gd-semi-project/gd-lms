<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">회원 등록</h1>
    <div class="text-muted small">새로운 회원 정보를 입력하고 등록합니다.</div>
  </div>
</div>

<div class="card shadow-lg border-0">
  <div class="card-header bg-primary text-white fw-bold">
    회원 정보 입력
  </div>

  <div class="card-body">
    <form method="post" action="/gd-lms/login/registUser.do" class="needs-validation" novalidate>
      
      <!-- 이름 -->
      <div class="mb-3 input-group">
        <span class="input-group-text"><i class="bi bi-person"></i></span>
        <input type="text" class="form-control" id="name" name="name" placeholder="이름 입력" required>
        <div class="invalid-feedback">이름을 입력하세요.</div>
      </div>

      <!-- 아이디 -->
      <div class="mb-3 input-group">
        <span class="input-group-text"><i class="bi bi-person-badge"></i></span>
        <input type="text" class="form-control" id="loginId" name="loginId" placeholder="아이디 입력" required>
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
        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
        <input type="email" class="form-control" id="email" name="email" placeholder="example@domain.com" required>
        <div class="invalid-feedback">올바른 이메일을 입력하세요.</div>
      </div>

      <!-- 생년월일 -->
      <div class="mb-3">
        <label for="birthDate" class="form-label">생년월일</label>
        <input type="date" class="form-control" id="birthDate" name="birthDate">
      </div>

      <!-- 역할 선택 -->
      <div class="mb-3">
        <label for="role" class="form-label">회원 역할</label>
        <select class="form-select" id="role" name="role" required>
          <option value="">선택하세요</option>
          <option value="학생">학생</option>
          <option value="교수">교수</option>
          <option value="관리자">관리자</option>
        </select>
        <div class="invalid-feedback">회원 역할을 선택하세요.</div>
      </div>

      <!-- 버튼 -->
      <div class="d-flex justify-content-end gap-2 mt-4">
        <button type="reset" class="btn btn-outline-secondary">
          <i class="bi bi-arrow-counterclockwise"></i> 초기화
        </button>
        <button type="submit" class="btn btn-primary">
          <i class="bi bi-check-circle"></i> 등록
        </button>
      </div>
    </form>
  </div>
</div>

<!-- 안내 -->
<div class="mt-3 text-muted small">
  * 등록된 회원은 시스템 로그인 및 권한 관리에 사용됩니다.
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