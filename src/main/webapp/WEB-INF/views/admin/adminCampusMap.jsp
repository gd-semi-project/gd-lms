<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="true" %>
<%
  String ctx = request.getContextPath();
%>
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>가산구디대학교 | 캠퍼스 맵</title>

  <!-- Bootstrap 5 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>

  <style>
    body { background: #f6f7f9; }
    .page-wrap { max-width: 1200px; margin: 0 auto; padding: 18px; }
    .card-soft { border: 1px solid #e7e7e7; border-radius: 16px; box-shadow: 0 2px 12px rgba(0,0,0,.04); }
    .map-shell { background: #fff; border-radius: 16px; border: 1px solid #e7e7e7; overflow: hidden; }
    .map-head { padding: 14px 16px; border-bottom: 1px solid #eee; display:flex; justify-content:space-between; align-items:baseline; gap:12px; }
    .map-head h1 { font-size: 18px; margin:0; }
    .map-head .sub { font-size: 12px; color:#666; margin:0; }

    /* SVG interactions */
    .bldg { cursor: pointer; }
    .bldg .box { transition: transform .12s ease, filter .12s ease; }
    .bldg:hover .box { transform: translateY(-2px); filter: drop-shadow(0 4px 10px rgba(0,0,0,.12)); }
    .bldg.selected .box { outline: 3px solid rgba(13,110,253,.35); outline-offset: 2px; }

    .badge-soft { background: #f1f3f5; border: 1px solid #e9ecef; color:#333; font-weight:600; }
    .table thead th { white-space: nowrap; }
    .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }

    /* Admin room list */
    .room-pill {
      display:inline-flex; align-items:center; gap:8px;
      padding: 6px 10px; border: 1px solid #e9ecef; border-radius: 999px;
      background: #fff; margin: 4px 6px 0 0; font-size: 12px;
    }
    .room-pill .tag { font-weight:700; }
    .room-pill .cap { color:#666; }
  </style>
</head>

<body>
<div class="page-wrap">
  <div class="d-flex align-items-center justify-content-between mb-3">
    <div>
      <div class="text-muted small">가상 LMS / 가산구디대학교</div>
      <h2 class="mb-0">캠퍼스 맵</h2>
    </div>
    <div class="text-muted small">ContextPath: <span class="mono"><%=ctx%></span></div>
  </div>

  <!-- Tabs: A (Map) + B (Admin) -->
  <ul class="nav nav-pills mb-3" id="campusTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active" id="tab-map" data-bs-toggle="pill" data-bs-target="#pane-map" type="button" role="tab">
        A) 사용자용 캠퍼스 맵
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link" id="tab-admin" data-bs-toggle="pill" data-bs-target="#pane-admin" type="button" role="tab">
        B) 관리자용 건물/강의실 관리
      </button>
    </li>
  </ul>

  <div class="tab-content">
    <!-- ===================== A) USER MAP ===================== -->
    <div class="tab-pane fade show active" id="pane-map" role="tabpanel" aria-labelledby="tab-map">
      <div class="map-shell card-soft">
        <div class="map-head">
          <h1 class="mb-0">가산구디대학교 캠퍼스 안내도</h1>
          <p class="sub mb-0">북쪽 ↑ / 남쪽 ↓ · 건물 클릭 시 상세 정보 표시</p>
        </div>

        <!-- SVG map -->
        <div class="p-2">
          <svg id="campusSvg" viewBox="0 0 1000 650" width="100%" height="auto" aria-label="캠퍼스 맵">
            <!-- background -->
            <rect x="0" y="0" width="1000" height="650" rx="18" fill="#eef5ee"/>

            <!-- roads -->
            <path d="M90 560 L910 560" stroke="#c9c9c9" stroke-width="26" stroke-linecap="round"/>
            <path d="M150 120 L150 560" stroke="#c9c9c9" stroke-width="22" stroke-linecap="round"/>
            <path d="M500 120 L500 560" stroke="#c9c9c9" stroke-width="22" stroke-linecap="round"/>
            <path d="M850 120 L850 560" stroke="#c9c9c9" stroke-width="22" stroke-linecap="round"/>
            <path d="M150 320 L850 320" stroke="#c9c9c9" stroke-width="18" stroke-linecap="round" opacity="0.9"/>

            <!-- plaza -->
            <circle cx="500" cy="320" r="70" fill="#e9e2d0" stroke="#d4c7a7" stroke-width="3"/>
            <text x="500" y="310" text-anchor="middle" font-size="16" font-weight="700" fill="#4a3b1f">중앙광장</text>
            <text x="500" y="332" text-anchor="middle" font-size="12" fill="#6b5a3a">분수/휴게</text>

            <!-- park -->
            <rect x="60" y="70" width="260" height="120" rx="18" fill="#d8f0d8" stroke="#b9dfb9"/>
            <text x="190" y="135" text-anchor="middle" font-size="14" font-weight="700" fill="#2d5b2d">북측 녹지공원</text>
            <text x="190" y="155" text-anchor="middle" font-size="12" fill="#2d5b2d">산책로/벤치</text>

            <!-- sports field -->
            <rect x="720" y="70" width="240" height="140" rx="18" fill="#d6e7ff" stroke="#b7cff5"/>
            <text x="840" y="135" text-anchor="middle" font-size="14" font-weight="700" fill="#224b88">운동장</text>
            <text x="840" y="155" text-anchor="middle" font-size="12" fill="#224b88">트랙/풋살</text>

            <!-- gate -->
            <rect x="70" y="585" width="220" height="45" rx="14" fill="#ffffff" stroke="#dddddd"/>
            <text x="180" y="613" text-anchor="middle" font-size="13" font-weight="700" fill="#333">정문 · 버스정류장</text>

            <!-- Buildings (Clickable) -->
            <!-- Helper: Each group has data-code="G-xx" and class="bldg" -->
            <!-- West -->
            <g class="bldg" data-code="G-02" transform="translate(70,210)">
              <rect class="box" width="180" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-02</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">인문사회관</text>
              <text x="82" y="54" font-size="11" fill="#555">인문/사회</text>
              <text x="82" y="74" font-size="11" fill="#555">5층 · 강 28 · 실 4</text>
            </g>

            <g class="bldg" data-code="G-03" transform="translate(70,330)">
              <rect class="box" width="180" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-03</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">경영경제관</text>
              <text x="82" y="54" font-size="11" fill="#555">경영/경제</text>
              <text x="82" y="74" font-size="11" fill="#555">5층 · 강 22 · 실 6</text>
            </g>

            <g class="bldg" data-code="G-13" transform="translate(70,450)">
              <rect class="box" width="180" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-13</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">학생회관</text>
              <text x="82" y="54" font-size="11" fill="#555">학생지원/동아리</text>
              <text x="82" y="74" font-size="11" fill="#555">4층 · 동아리 20</text>
            </g>

            <!-- Center -->
            <g class="bldg" data-code="G-01" transform="translate(410,180)">
              <rect class="box" width="180" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-01</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">본관</text>
              <text x="82" y="54" font-size="11" fill="#555">학사/행정</text>
              <text x="82" y="74" font-size="11" fill="#555">6층 · 강 6</text>
            </g>

            <g class="bldg" data-code="G-12" transform="translate(410,420)">
              <rect class="box" width="180" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-12</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">중앙도서관</text>
              <text x="82" y="54" font-size="11" fill="#555">열람/학습</text>
              <text x="82" y="74" font-size="11" fill="#555">7층 · 1,200석</text>
            </g>

            <g class="bldg" data-code="G-11" transform="translate(280,410)">
              <rect class="box" width="190" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-11</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">융합관</text>
              <text x="82" y="54" font-size="11" fill="#555">융합/자유전공</text>
              <text x="82" y="72" font-size="11" fill="#555">5층 · 강 16 · 실 8</text>
            </g>

            <!-- East -->
            <g class="bldg" data-code="G-08" transform="translate(610,240)">
              <rect class="box" width="210" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-08</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">미디어콘텐츠관</text>
              <text x="82" y="54" font-size="11" fill="#555">영상/게임/애니</text>
              <text x="82" y="72" font-size="11" fill="#555">5층 · 강 12 · 실 14</text>
            </g>

            <g class="bldg" data-code="G-14" transform="translate(610,410)">
              <rect class="box" width="210" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-14</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">산학협력관</text>
              <text x="82" y="54" font-size="11" fill="#555">기업/연구/스타트업</text>
              <text x="82" y="72" font-size="11" fill="#555">6층 · 연구 12 · 실 10</text>
            </g>

            <g class="bldg" data-code="G-04" transform="translate(740,220)">
              <rect class="box" width="190" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-04</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">자연과학관</text>
              <text x="82" y="54" font-size="11" fill="#555">자연과학</text>
              <text x="82" y="74" font-size="11" fill="#555">6층 · 강 14 · 실 18</text>
            </g>

            <g class="bldg" data-code="G-10" transform="translate(740,340)">
              <rect class="box" width="190" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-10</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">사범관</text>
              <text x="82" y="54" font-size="11" fill="#555">사범대</text>
              <text x="82" y="74" font-size="11" fill="#555">4층 · 강 14 · 실 6</text>
            </g>

            <g class="bldg" data-code="G-09" transform="translate(740,460)">
              <rect class="box" width="190" height="92" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-09</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">예술체육관</text>
              <text x="82" y="54" font-size="11" fill="#555">예술/체육</text>
              <text x="82" y="74" font-size="11" fill="#555">4층 · 강 6 · 실 10</text>
            </g>

            <!-- South engineering -->
            <g class="bldg" data-code="G-05" transform="translate(290,550)">
              <rect class="box" width="190" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-05</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">공학관 A</text>
              <text x="82" y="54" font-size="11" fill="#555">컴공/소웅/AI</text>
              <text x="82" y="72" font-size="11" fill="#555">7층 · 강 12 · 실 20</text>
            </g>

            <g class="bldg" data-code="G-06" transform="translate(500,550)">
              <rect class="box" width="190" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-06</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">공학관 B</text>
              <text x="82" y="54" font-size="11" fill="#555">전자/기계/산공</text>
              <text x="82" y="72" font-size="11" fill="#555">7층 · 강 10 · 실 22</text>
            </g>

            <g class="bldg" data-code="G-07" transform="translate(710,550)">
              <rect class="box" width="220" height="90" rx="14" fill="#fff" stroke="#cfcfcf" stroke-width="2"/>
              <rect x="12" y="12" width="60" height="26" rx="8" fill="#f3f3f3" stroke="#e3e3e3"/>
              <text x="42" y="30" text-anchor="middle" font-size="12" font-weight="800" fill="#333">G-07</text>
              <text x="82" y="34" font-size="12" font-weight="800" fill="#222">정보통신관</text>
              <text x="82" y="54" font-size="11" fill="#555">보안/데이터/클라우드</text>
              <text x="82" y="72" font-size="11" fill="#555">6층 · 강 10 · 실 16</text>
            </g>

            <text x="965" y="35" text-anchor="end" font-size="12" fill="#444">※ 프로젝트용 스키매틱(가상 배치)</text>
          </svg>
        </div>

        <div class="px-3 pb-3">
          <div class="d-flex flex-wrap gap-2">
            <span class="badge rounded-pill badge-soft">클릭: 건물 상세/강의실 목록</span>
            <span class="badge rounded-pill badge-soft">검색: 건물 코드/이름</span>
            <span class="badge rounded-pill badge-soft">표기 예: <span class="mono">G-05-403</span></span>
          </div>
        </div>
      </div>
    </div>

    <!-- ===================== B) ADMIN ===================== -->
    <div class="tab-pane fade" id="pane-admin" role="tabpanel" aria-labelledby="tab-admin">
      <div class="card-soft bg-white p-3">
        <div class="d-flex flex-column flex-lg-row gap-2 justify-content-between align-items-start align-items-lg-center">
          <div>
            <h4 class="mb-1">관리자용 건물/강의실 관리</h4>
            <div class="text-muted small">건물별 층/실 정보를 펼쳐보고, 방 코드를 복사하거나(예시) 수용인원을 확인합니다.</div>
          </div>
          <div class="d-flex gap-2">
            <input id="adminSearch" class="form-control form-control-sm" style="min-width:260px" placeholder="예: G-05, 공학관, 도서관..." />
            <button id="btnReset" class="btn btn-outline-secondary btn-sm">초기화</button>
          </div>
        </div>

        <hr/>

        <div class="table-responsive">
          <table class="table align-middle">
            <thead class="table-light">
              <tr>
                <th>건물코드</th>
                <th>건물명</th>
                <th>주요용도</th>
                <th>층수</th>
                <th>강의실</th>
                <th>실습실</th>
                <th>특수시설</th>
                <th class="text-end">상세</th>
              </tr>
            </thead>
            <tbody id="adminTbody"></tbody>
          </table>
        </div>

        <div class="accordion" id="adminAccordion"></div>
      </div>
    </div>
  </div>
</div>

<!-- ===================== MODAL (A) ===================== -->
<div class="modal fade" id="buildingModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <div>
          <div class="text-muted small" id="modalCode">G-00</div>
          <h5 class="modal-title mb-0" id="modalTitle">건물명</h5>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
      </div>
      <div class="modal-body">
        <div class="d-flex flex-wrap gap-2 mb-3">
          <span class="badge text-bg-primary" id="modalUse">용도</span>
          <span class="badge text-bg-secondary" id="modalFloors">층수</span>
          <span class="badge text-bg-secondary" id="modalCounts">강/실</span>
        </div>

        <div class="row g-3">
          <div class="col-lg-5">
            <div class="p-3 rounded-3 border bg-light">
              <div class="fw-bold mb-2">특수시설</div>
              <ul class="mb-0" id="modalSpecials"></ul>
            </div>

            <div class="mt-3 p-3 rounded-3 border">
              <div class="fw-bold mb-2">빠른 액션(예시)</div>
              <div class="d-grid gap-2">
                <button class="btn btn-outline-primary btn-sm" id="btnCopyBuildingCode">건물 코드 복사</button>
                <button class="btn btn-outline-secondary btn-sm" id="btnOpenAdmin">관리자 탭에서 열기</button>
              </div>
              <div class="text-muted small mt-2">
                ※ 실제 DB 연동 시 “강의실 목록”을 서버에서 내려주도록 교체하면 됩니다.
              </div>
            </div>
          </div>

          <div class="col-lg-7">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <div class="fw-bold">강의실/실습실 목록(가상 생성)</div>
              <div class="text-muted small">표기: <span class="mono">G-05-403</span></div>
            </div>
            <div class="border rounded-3 p-2" id="modalRoomList"></div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
  /**
   * =========================
   * Campus Data (DB대체용)
   * =========================
   * 나중에 DB 연동 시:
   * - buildings 배열을 AJAX로 받아오거나
   * - JSTL로 서버에서 뿌려도 됩니다.
   */
  const buildings = [
    { code:"G-01", name:"본관", use:"학사/행정", floors:6, lecture:6, lab:0, specials:["총장실","교무처/학사행정","대강당","회의실 8실"] },
    { code:"G-02", name:"인문사회관", use:"인문/사회", floors:5, lecture:28, lab:4, specials:["세미나실 6실","교수연구실"] },
    { code:"G-03", name:"경영경제관", use:"경영/경제", floors:5, lecture:22, lab:6, specials:["모의투자실 2실","취업/창업상담실"] },
    { code:"G-04", name:"자연과학관", use:"자연과학", floors:6, lecture:14, lab:18, specials:["공동실험실 4실","안전관리실"] },
    { code:"G-05", name:"공학관 A", use:"컴공/소프트웨어/AI", floors:7, lecture:12, lab:20, specials:["서버실","캡스톤실","프로젝트룸"] },
    { code:"G-06", name:"공학관 B", use:"전자/기계/산업", floors:7, lecture:10, lab:22, specials:["공작실","로봇실","계측실"] },
    { code:"G-07", name:"정보통신관", use:"보안/데이터/클라우드", floors:6, lecture:10, lab:16, specials:["보안관제실","네트워크실습실"] },
    { code:"G-08", name:"미디어콘텐츠관", use:"영상/게임/애니", floors:5, lecture:12, lab:14, specials:["촬영스튜디오 2실","편집실 2실","녹음부스"] },
    { code:"G-09", name:"예술체육관", use:"예술/체육", floors:4, lecture:6, lab:10, specials:["실내체육관","연습실","샤워/락커"] },
    { code:"G-10", name:"사범관", use:"사범대", floors:4, lecture:14, lab:6, specials:["모의수업실 4실","교생실습지원"] },
    { code:"G-11", name:"융합관", use:"융합/자유전공", floors:5, lecture:16, lab:8, specials:["프로젝트룸","멘토링룸"] },
    { code:"G-12", name:"중앙도서관", use:"열람/학습", floors:7, lecture:6, lab:0, specials:["열람석 1,200석","스터디룸","미디어열람실"] },
    { code:"G-13", name:"학생회관", use:"학생지원/동아리", floors:4, lecture:4, lab:0, specials:["동아리실 20실","학생식당/카페","학생상담센터"] },
    { code:"G-14", name:"산학협력관", use:"산학/스타트업", floors:6, lecture:6, lab:10, specials:["기업연구실 12실","세미나룸","공용회의실"] },
  ];

  /**
   * =========================
   * Room Generator (가상)
   * =========================
   * - lecture/lab 개수를 층에 분배
   * - 룸코드: G-05-403 (4층 03호)
   * - 수용 인원: 타입별 기본값
   */
  function generateRooms(building) {
    const floors = building.floors;
    const totalLecture = building.lecture;
    const totalLab = building.lab;

    // 층별 분배(대충 균등)
    const lecturePerFloor = distribute(totalLecture, floors);
    const labPerFloor = distribute(totalLab, floors);

    const rooms = [];
    for (let f = 1; f <= floors; f++) {
      // 강의실 생성
      for (let i = 1; i <= lecturePerFloor[f-1]; i++) {
        const roomNo = String(i).padStart(2, "0");
        rooms.push({
          code: `${building.code}-${f}${roomNo}`,
          floor: f,
          type: "LECTURE",
          name: `강의실 ${f}${roomNo}`,
          capacity: capacityByType(building, "LECTURE"),
        });
      }
      // 실습실 생성(강의실 뒤 번호로 이어붙임)
      const base = lecturePerFloor[f-1];
      for (let j = 1; j <= labPerFloor[f-1]; j++) {
        const roomNo = String(base + j).padStart(2, "0");
        rooms.push({
          code: `${building.code}-${f}${roomNo}`,
          floor: f,
          type: "LAB",
          name: `실습실 ${f}${roomNo}`,
          capacity: capacityByType(building, "LAB"),
        });
      }
    }

    // 특수시설은 별도 표기(코드 없이 리스트로만)
    return rooms;
  }

  function capacityByType(building, type) {
    // 건물 성격 따라 기본 수용 인원 가중(현실적인 느낌)
    const isEngineering = ["G-05","G-06","G-07"].includes(building.code);
    const isScience = ["G-04"].includes(building.code);
    const isLibrary = ["G-12"].includes(building.code);

    if (isLibrary) return 0;

    if (type === "LECTURE") {
      if (isEngineering) return 50;
      if (isScience) return 45;
      return 40;
    } else { // LAB
      if (isEngineering) return 30;
      if (isScience) return 28;
      return 25;
    }
  }

  // distribute n items across k buckets as evenly as possible
  function distribute(n, k) {
    const base = Math.floor(n / k);
    const rem = n % k;
    const arr = new Array(k).fill(base);
    for (let i = 0; i < rem; i++) arr[i] += 1;
    return arr;
  }

  /**
   * =========================
   * A) Map + Modal
   * =========================
   */
  const modalEl = document.getElementById("buildingModal");
  const modal = new bootstrap.Modal(modalEl);

  const modalCode = document.getElementById("modalCode");
  const modalTitle = document.getElementById("modalTitle");
  const modalUse = document.getElementById("modalUse");
  const modalFloors = document.getElementById("modalFloors");
  const modalCounts = document.getElementById("modalCounts");
  const modalSpecials = document.getElementById("modalSpecials");
  const modalRoomList = document.getElementById("modalRoomList");

  let currentBuildingCode = null;

  function openBuilding(code) {
    const b = buildings.find(x => x.code === code);
    if (!b) return;

    currentBuildingCode = code;

    // 선택 표시
    document.querySelectorAll(".bldg").forEach(el => el.classList.toggle("selected", el.dataset.code === code));

    // 모달 바인딩
    modalCode.textContent = b.code;
    modalTitle.textContent = b.name;
    modalUse.textContent = b.use;
    modalFloors.textContent = `${b.floors}층`;
    modalCounts.textContent = `강의 ${b.lecture} · 실습 ${b.lab}`;

    modalSpecials.innerHTML = "";
    (b.specials || []).forEach(s => {
      const li = document.createElement("li");
      li.textContent = s;
      modalSpecials.appendChild(li);
    });

    // rooms
    const rooms = generateRooms(b);
    modalRoomList.innerHTML = "";
    if (rooms.length === 0) {
      modalRoomList.innerHTML = `<div class="text-muted small p-2">강의실 정보 없음 (예: 도서관/공원 등)</div>`;
    } else {
      // 층별 그룹
      const byFloor = new Map();
      rooms.forEach(r => {
        if (!byFloor.has(r.floor)) byFloor.set(r.floor, []);
        byFloor.get(r.floor).push(r);
      });

      [...byFloor.keys()].sort((a,b)=>a-b).forEach(f => {
        const header = document.createElement("div");
        header.className = "fw-bold mt-2";
        header.textContent = `${f}층`;
        modalRoomList.appendChild(header);

        const row = document.createElement("div");
        row.className = "d-flex flex-wrap";
        byFloor.get(f).forEach(r => {
          const pill = document.createElement("div");
          pill.className = "room-pill";
          pill.innerHTML = `
            <span class="tag ${r.type === "LAB" ? "text-success" : "text-primary"}">${r.type === "LAB" ? "실습" : "강의"}</span>
            <span class="mono">${r.code}</span>
            <span class="cap">정원 ${r.capacity}</span>
            <button class="btn btn-sm btn-outline-secondary py-0 px-2" title="복사">Copy</button>
          `;
          pill.querySelector("button").addEventListener("click", () => copyText(r.code));
          row.appendChild(pill);
        });
        modalRoomList.appendChild(row);
      });
    }

    modal.show();
  }

  // SVG click binding
  document.querySelectorAll(".bldg").forEach(el => {
    el.addEventListener("click", () => openBuilding(el.dataset.code));
  });

  document.getElementById("btnCopyBuildingCode").addEventListener("click", () => {
    if (currentBuildingCode) copyText(currentBuildingCode);
  });

  document.getElementById("btnOpenAdmin").addEventListener("click", () => {
    // 관리자 탭으로 이동 + 해당 건물 펼치기
    const adminTabBtn = document.getElementById("tab-admin");
    bootstrap.Tab.getOrCreateInstance(adminTabBtn).show();
    modal.hide();
    setTimeout(() => expandAdminByCode(currentBuildingCode), 200);
  });

  async function copyText(text) {
    try {
      await navigator.clipboard.writeText(text);
      toast(`복사됨: ${text}`);
    } catch (e) {
      // fallback
      const ta = document.createElement("textarea");
      ta.value = text;
      document.body.appendChild(ta);
      ta.select();
      document.execCommand("copy");
      ta.remove();
      toast(`복사됨: ${text}`);
    }
  }

  /**
   * =========================
   * B) Admin UI
   * =========================
   */
  const adminTbody = document.getElementById("adminTbody");
  const adminAccordion = document.getElementById("adminAccordion");
  const adminSearch = document.getElementById("adminSearch");
  const btnReset = document.getElementById("btnReset");

  function renderAdmin(list) {
    // table
    adminTbody.innerHTML = "";
    list.forEach(b => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td class="mono">${b.code}</td>
        <td>${b.name}</td>
        <td>${b.use}</td>
        <td>${b.floors}</td>
        <td>${b.lecture}</td>
        <td>${b.lab}</td>
        <td class="text-muted small">${(b.specials||[]).slice(0,2).join(", ")}${(b.specials||[]).length>2 ? " 외" : ""}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary" data-open="${b.code}">펼치기</button>
        </td>
      `;
      adminTbody.appendChild(tr);
      tr.querySelector("button[data-open]").addEventListener("click", () => expandAdminByCode(b.code));
    });

    // accordion details
    adminAccordion.innerHTML = "";
    list.forEach((b, idx) => {
      const rooms = generateRooms(b);
      const byFloor = new Map();
      rooms.forEach(r => {
        if (!byFloor.has(r.floor)) byFloor.set(r.floor, []);
        byFloor.get(r.floor).push(r);
      });

      const accItemId = `acc-${b.code}`;
      const headingId = `heading-${b.code}`;
      const collapseId = `collapse-${b.code}`;

      const item = document.createElement("div");
      item.className = "accordion-item";
      item.id = accItemId;

      const specialText = (b.specials || []).map(s => `<span class="badge rounded-pill text-bg-light border me-1 mb-1">${escapeHtml(s)}</span>`).join("");

      item.innerHTML = `
        <h2 class="accordion-header" id="${headingId}">
          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
            data-bs-target="#${collapseId}" aria-expanded="false" aria-controls="${collapseId}">
            <span class="mono me-2">${b.code}</span>
            <span class="fw-bold me-2">${b.name}</span>
            <span class="text-muted small">${b.use} · ${b.floors}층 · 강의 ${b.lecture} · 실습 ${b.lab}</span>
          </button>
        </h2>

        <div id="${collapseId}" class="accordion-collapse collapse" aria-labelledby="${headingId}" data-bs-parent="#adminAccordion">
          <div class="accordion-body">
            <div class="mb-2">
              <div class="fw-bold mb-1">특수시설</div>
              <div>${specialText || '<span class="text-muted small">없음</span>'}</div>
            </div>

            <hr/>

            <div class="fw-bold mb-2">층별 강의실/실습실 (가상 생성)</div>
            <div id="floorWrap-${b.code}"></div>
          </div>
        </div>
      `;
      adminAccordion.appendChild(item);

      // floors render
      const floorWrap = item.querySelector(`#floorWrap-${CSS.escape(b.code)}`);
      if (rooms.length === 0) {
        floorWrap.innerHTML = `<div class="text-muted small">강의실 정보 없음</div>`;
      } else {
        [...byFloor.keys()].sort((a,b)=>a-b).forEach(f => {
          const row = document.createElement("div");
          row.className = "mb-2";
          row.innerHTML = `<div class="fw-bold">${f}층</div>`;
          const pills = document.createElement("div");
          pills.className = "d-flex flex-wrap";
          byFloor.get(f).forEach(r => {
            const pill = document.createElement("div");
            pill.className = "room-pill";
            pill.innerHTML = `
              <span class="tag ${r.type === "LAB" ? "text-success" : "text-primary"}">${r.type === "LAB" ? "실습" : "강의"}</span>
              <span class="mono">${r.code}</span>
              <span class="cap">정원 ${r.capacity}</span>
              <button class="btn btn-sm btn-outline-secondary py-0 px-2">Copy</button>
            `;
            pill.querySelector("button").addEventListener("click", () => copyText(r.code));
            pills.appendChild(pill);
          });
          row.appendChild(pills);
          floorWrap.appendChild(row);
        });
      }
    });
  }

  function expandAdminByCode(code) {
    if (!code) return;
    const collapseId = `collapse-${code}`;
    const el = document.getElementById(collapseId);
    if (!el) return;

    // scroll + expand
    const item = document.getElementById(`acc-${code}`);
    item?.scrollIntoView({ behavior: "smooth", block: "start" });

    const bsCollapse = bootstrap.Collapse.getOrCreateInstance(el);
    bsCollapse.show();
  }

  adminSearch.addEventListener("input", () => {
    const q = adminSearch.value.trim().toLowerCase();
    if (!q) { renderAdmin(buildings); return; }
    const filtered = buildings.filter(b =>
      b.code.toLowerCase().includes(q) ||
      b.name.toLowerCase().includes(q) ||
      b.use.toLowerCase().includes(q)
    );
    renderAdmin(filtered);
  });

  btnReset.addEventListener("click", () => {
    adminSearch.value = "";
    renderAdmin(buildings);
  });

  function escapeHtml(s) {
    return String(s)
      .replaceAll("&","&amp;")
      .replaceAll("<","&lt;")
      .replaceAll(">","&gt;")
      .replaceAll('"',"&quot;")
      .replaceAll("'","&#039;");
  }

  /**
   * =========================
   * Toast helper
   * =========================
   */
  function toast(msg) {
    let wrap = document.getElementById("toastWrap");
    if (!wrap) {
      wrap = document.createElement("div");
      wrap.id = "toastWrap";
      wrap.className = "toast-container position-fixed bottom-0 end-0 p-3";
      document.body.appendChild(wrap);
    }

    const t = document.createElement("div");
    t.className = "toast align-items-center text-bg-dark border-0";
    t.role = "alert";
    t.ariaLive = "assertive";
    t.ariaAtomic = "true";
    t.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${escapeHtml(msg)}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    `;
    wrap.appendChild(t);
    const bsToast = bootstrap.Toast.getOrCreateInstance(t, { delay: 1500 });
    bsToast.show();
    t.addEventListener("hidden.bs.toast", () => t.remove());
  }

  // initial render
  renderAdmin(buildings);
</script>
</body>
</html>
