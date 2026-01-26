<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8">
  <title>학교소개 | 가산구디대학교</title>

  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
    rel="stylesheet">

  <style>
    body {
      background: #f6f7f9;
    }
    .page-wrap {
      max-width: 900px;
      margin: 0 auto;
      padding: 32px 16px;
      text-align: center; /* ← 전체 텍스트 중앙 */
    }
    .hero img {
      width: 100%;
      object-fit: cover;
      border-radius: 16px;
    }
    .section {
      height: auto;
      object-fit: contain;
    }
    .section img {
      width: 100%;
      border-radius: 14px;
    }
    .section-title {
      font-weight: 800;
      letter-spacing: -.3px;
      margin-bottom: 12px;
    }
    .section p {
      color: #555;
      line-height: 1.7;
      margin-left: auto;
      margin-right: auto;
      max-width: 680px; /* 문단 폭 제한 (가독성) */
    }
  </style>
</head>

<body>
  <div class="page-wrap">

    <!-- 상단 대표 이미지 -->
    <div class="hero mb-4">
      <img src="${pageContext.request.contextPath}/resources/images/campus_image01.png" alt="가산구디대학교 전경">
    </div>

    <!-- 학교 이름 / 한 줄 소개 -->
    <h1 class="fw-bold mb-2">가산구디대학교</h1>
    <p class="text-muted">
      실무 중심 교육과 프로젝트 기반 학습을 지향하는 교육 기관
    </p>

    <!-- 학교 소개 -->
    <div class="section">
      <h3 class="section-title">학교 소개</h3>
      <p>
        가산구디대학교는 이론에 머무르지 않는 교육을 목표로 합니다.
        학생들은 강의실 안에서 배우는 것을 넘어, 직접 만들고
        시행착오를 겪으며 성장합니다.
      </p>
      <p>
        우리는 결과보다 과정의 밀도를 중요하게 생각하며,
        스스로 사고하고 협업할 수 있는 인재를 키우는 데 집중합니다.
      </p>
    </div>

    <!-- 사진 섹션 1 -->
    <div class="section">
      <img src="${pageContext.request.contextPath}/resources/images/campus_image02.png" alt="캠퍼스 사진 1">
      <p class="mt-3">
        캠퍼스는 학습과 휴식이 자연스럽게 공존하도록 설계되어 있습니다.
      </p>
    </div>

    <!-- 사진 섹션 2 -->
    <div class="section">
      <img src="${pageContext.request.contextPath}/resources/images/campus_image03.png" alt="캠퍼스 사진 2">
      <p class="mt-3">
        학생들은 다양한 공간에서 토론하고, 실습하고,
        자신만의 결과물을 만들어 갑니다.
      </p>
    </div>

    <!-- 마무리 -->
    <div class="section">
      <h3 class="section-title">우리가 지향하는 것</h3>
      <p>
        가산구디대학교는 배우는 곳이 아니라,
        해내는 방법을 익히는 곳입니다.
      </p>
    </div>

    <div class="text-center text-muted small mt-5">
      © 2026 가산구디대학교
    </div>

  </div>
</body>
</html>
