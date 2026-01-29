<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer class="border-top bg-white py-3">
  <div class="container-fluid d-flex justify-content-between align-items-center">
    <div class="text-muted small">
      © 2026 Gasangoodee University. All rights reserved.
    </div>

    <!-- 이미지 클릭 트리거 -->
    <a href="javascript:void(0)"
       id="keronBallLauncher"
       data-ctx="${pageContext.request.contextPath}"
       class="d-inline-block">
      <img
        alt="keronBall"
        src="${pageContext.request.contextPath}/resources/images/keronBall.png"
        style="height:32px; cursor:pointer;"
      >
    </a>
  </div>
</footer>