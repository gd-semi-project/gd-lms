<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
      
<div class="container my-4" style="max-width: 920px;">
  <div class="card shadow-sm">
    <div class="card-body">

      <div class="d-flex align-items-center justify-content-between mb-3">
        <h4 class="mb-0 fw-bold">DB 더미데이터 추가</h4>
        <span class="badge text-bg-secondary">KeronBall</span>
      </div>

      <p class="text-muted small mb-4">
        테이블을 선택하고, 추가할 더미데이터 개수를 선택하세요.
      </p>
      <form method="post" action="${pageContext.request.contextPath}/keronBall/update">
        <input type="hidden" name="action" value="ADD_DUMMY">
      <!-- 선택 영역 -->
      <div class="row g-3">

        <!-- 테이블 선택 -->
        <div class="col-12 col-md-7">
          <label class="form-label fw-semibold" for="tableNameSelect">테이블</label>

		<select class="form-select" id="tableNameSelect" name="tableName">
		  <option value="" selected disabled>테이블을 선택하세요</option>
		
		  <c:forEach var="table" items="${tableNames}">
		    <option value="${table}">${table}</option>
		  </c:forEach>
		</select>

          <div class="form-text">
          </div>
        </div>

        <!-- 더미데이터 개수 선택 -->
        <div class="col-12 col-md-5">
          <label class="form-label fw-semibold" for="dummyCountSelect">더미데이터 개수</label>

          <select class="form-select" id="dummyCountSelect" name="count">
            <option value="" selected disabled>개수를 선택하세요</option>
            <option value="25">25개</option>
            <option value="50">50개</option>
            <option value="100">100개</option>
          </select>

          <div class="form-text">
            선택한 개수만큼 해당 테이블에 더미데이터를 추가합니다.
          </div>
        </div>
      </div>
      <hr class="my-4">

      <!-- 실행 영역(레이아웃) -->
        <div class="d-flex gap-2">
          <!-- ✅ submit으로 변경 -->
          <button type="submit" class="btn btn-primary kb-btn">
            실행
          </button>

          <!-- ✅ reset: form 안의 입력값 초기화 -->
          <button type="reset" class="btn btn-outline-secondary kb-btn">
            초기화
          </button>
        </div>
      </form>

      <div class="alert alert-warning mt-4 mb-0">
        <div class="fw-semibold mb-1">주의</div>
        <div class="small">
          이 기능은 테스트/개발 목적입니다. 운영 환경에서는 사용하지 마세요.
        </div>
      </div>

    </div>
  </div>
</div>
