<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
  /* ===== KeronBall Remote Layout (viewport-based) ===== */

  /* 페이지 전체를 뷰포트 기준으로 잡음 */
  .keron-viewport {
    min-height: 100vh;
    position: relative;
  }

  /* 최상단 중앙 케론볼 이미지 */
  .keron-top {
    position: sticky;      /* 스크롤해도 상단 고정 느낌 */
    top: 0;
    display: flex;
    justify-content: center;
    padding: 16px 0 8px;
    background: transparent;
    z-index: 10;
  }

  .keron-top img {
    height: 56px;
    width: auto;
    user-select: none;
    -webkit-user-drag: none;
  }

  /* 가운데 큰 박스: 뷰포트 기준 중앙 배치 */
  .keron-center {
    min-height: calc(100vh - 56px - 24px); /* 상단 여백 감안 */
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16px;
  }

  /* 큰 디브 박스 */
  .keron-box {
    width: min(720px, 92vw);     /* 너무 커지지 않게 */
    height: min(520px, 60vh);    /* 뷰포트 기준 적당히 */
    border: 1px solid #ddd;
    border-radius: 16px;
    background: #fff;
    overflow: hidden;            /* 4등분 경계 깔끔 */
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  }

  /* 정확히 4등분(2x2) */
  .keron-grid-2x2 {
    width: 100%;
    height: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
  }

  /* 각 기능 버튼 타일 */
  .keron-tile {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;

    border: 0;
    border-right: 1px solid #eee;
    border-bottom: 1px solid #eee;

    background: #fafafa;
    cursor: pointer;
    user-select: none;

    font-weight: 600;
    font-size: 18px;
    color: #222;

    text-decoration: none; /* a태그일 때 밑줄 제거 */
  }

  /* 오른쪽/아래 경계선 제거(그리드 끝) */
  .keron-tile:nth-child(2),
  .keron-tile:nth-child(4) {
    border-right: 0;
  }
  .keron-tile:nth-child(3),
  .keron-tile:nth-child(4) {
    border-bottom: 0;
  }

  .keron-tile:hover {
    background: #f0f0f0;
  }

  .keron-tile:active {
    background: #e8e8e8;
  }

  /* 타일 안 텍스트(일단 비워둘 거라 높이 유지용) */
  .keron-tile span {
    padding: 12px;
    line-height: 1.2;
  }
</style>

<div class="keron-viewport">

  <!-- 최상단 중앙 케론볼 -->
  <div class="keron-top">
    <a href="${pageContext.request.contextPath}/" title="메인으로">
      <img src="${pageContext.request.contextPath}/resources/images/keronBall.png" alt="keronBall">
    </a>
  </div>

  <!-- 중앙 큰 박스 + 4분할 -->
  <div class="keron-center">
    <div class="keron-box">
      <div class="keron-grid-2x2">

        <!-- 1번 기능 -->
        <a class="keron-tile" href="${pageContext.request.contextPath}/keronBall/time">
          <span>시간 조작</span>
        </a>

        <!-- 2번 기능 -->
        <a class="keron-tile" href="javascript:void(0)">
          <span>기능 2</span>
        </a>

        <!-- 3번 기능 -->
        <a class="keron-tile" href="javascript:void(0)">
          <span>기능 3</span>
        </a>

        <!-- 4번 기능 -->
        <a class="keron-tile" href="javascript:void(0)">
          <span>기능 4</span>
        </a>

      </div>
    </div>
  </div>

</div>

