<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/about.css">
<c:if test="${MPWC}">
	<script>
	  alert("비밀번호가 초기화되었습니다. 변경해주세요.");
	</script>
	<c:remove var="MPWC" scope="session"/>
</c:if>
<!-- HERO -->
<div class="hero">
  <div class="hero-content">
    <h1>가산구디대학교</h1>
    <p>
      실무 중심 교육 · 글로벌 경쟁력 · 창의적 융합 인재 양성<br>
      미래 사회를 선도하는 혁신 대학
    </p>

    <div class="hero-buttons">
      <a href="${pageContext.request.contextPath}/calendar/view" class="hero-btn primary">
        학사일정 →
      </a>
      <a href="${pageContext.request.contextPath}/notice/list" class="hero-btn secondary">
        공지사항 →
      </a>
    </div>
  </div>
</div>

<!-- INTRO -->
<section>
  <h2 class="section-title">🌟 대학 소개</h2>
  <p class="section-desc">
    가산구디대학교는 이론 중심 교육을 넘어 실무와 프로젝트 중심 학습을 통해
    산업 현장에서 바로 활용 가능한 전문 인재를 양성합니다.
    학생 개개인의 역량을 존중하고, 창의성과 협업 능력을 함께 키워가는
    미래형 교육 환경을 제공합니다.
  </p>
</section>

<!-- CAMPUS LIFE -->
<section class="campus-life">
  <h2 class="section-title">🌿 캠퍼스 라이프</h2>

  <div class="slider-wrap">
    <div class="slider-track">

      <!-- 이미지 2회 반복 (무한 슬라이드용) -->
      <c:forEach begin="1" end="2">
        <c:forEach begin="1" end="8" var="i">
          <div class="slide">
            <img src="${pageContext.request.contextPath}/resources/images/campus0${i}.jpg"
                 alt="캠퍼스 이미지 ${i}">
          </div>
        </c:forEach>
      </c:forEach>

    </div>
  </div>
</section>

<!-- VISION -->
<section class="vision">
  <h2 class="section-title light">우리의 비전</h2>

  <div class="vision-cards">
    <div class="vision-card">
      <h3>실무 중심 교육</h3>
      <p>현장형 프로젝트와 기업 연계를 통해 즉시 활용 가능한 역량을 기릅니다.</p>
    </div>

    <div class="vision-card">
      <h3>글로벌 인재 양성</h3>
      <p>국제 교류와 글로벌 커리큘럼으로 세계 무대에 도전합니다.</p>
    </div>

    <div class="vision-card">
      <h3>창의 융합 혁신</h3>
      <p>전공 간 융합 교육을 통해 창의적 문제 해결 능력을 강화합니다.</p>
    </div>
  </div>
</section>

<!-- INFO -->
<section class="info">
  <h2 class="section-title">교육 환경</h2>

  <div class="info-grid">
    <div class="info-box">
      <h3>최첨단 교육 시설</h3>
      <p>
        스마트 강의실, 최신 실험실, 디지털 도서관을 통해
        최고의 학습 환경을 제공합니다.
      </p>
    </div>

    <div class="info-box">
      <h3>산학 협력 프로그램</h3>
      <p>
        기업과 연계된 프로젝트 수업으로 실전 경험과 취업 경쟁력을 동시에 확보합니다.
      </p>
    </div>

    <div class="info-box">
      <h3>학생 중심 캠퍼스</h3>
      <p>
        문화 공간, 체육 시설, 학습 공간이 조화된 캠퍼스를 운영합니다.
      </p>
    </div>
  </div>
</section>