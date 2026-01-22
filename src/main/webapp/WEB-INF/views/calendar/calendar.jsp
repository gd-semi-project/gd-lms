<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
  /* ===== 캘린더: 어느 환경에서도 한 눈에 ===== */
  .calendar-wrap { overflow: hidden; }

  /* 가로 스크롤 방지 + 7열 고정 */
  .calendar-table { table-layout: fixed; width: 100%; }

  .calendar-table th,
  .calendar-table td { padding: .25rem !important; }

  /* 6주가 떠도 화면에서 덜 짤리게: 뷰포트 기준 높이 */
  .calendar-table td {
    height: clamp(56px, 9vh, 86px);
    vertical-align: top;
  }

  .calendar-daynum { font-size: .85rem; font-weight: 700; line-height: 1; }

  /* 일정은 한 줄만 보여서 높이 초과 방지 */
  .calendar-event {
    font-size: .75rem;
    line-height: 1.1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .calendar-more { font-size: .72rem; line-height: 1; }

  @media (max-width: 576px) {
    .calendar-table th { font-size: .8rem; }
    .calendar-table td { height: clamp(52px, 8.5vh, 78px); }
    .calendar-event { font-size: .72rem; }
  }
  td.row-click:hover {
    background-color: #f1f3f5;
  }
</style>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">학사일정</h1>
    <div class="text-muted small">일정 추가/수정/삭제 <!-- 추후 admin if 문으로 노출 제어 --></div>
  </div>

  <div class="d-flex gap-2">
    <form class="d-flex gap-2" method="get"
          action="${pageContext.request.contextPath}/calendar/view">

      <select class="form-select form-select-sm" name="year" style="width: 110px;">
        <c:forEach var="y" begin="2024" end="2026">
          <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}년</option>
        </c:forEach>
      </select>

      <select class="form-select form-select-sm" name="month" style="width: 90px;">
        <c:forEach var="m" begin="1" end="12">
          <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>${m}월</option>
        </c:forEach>
      </select>

      <button class="btn btn-sm btn-outline-secondary" type="submit">이동</button>
    </form>

    <button class="btn btn-sm btn-primary" type="button" disabled title="추후 구현">
      + 일정 추가
    </button>
  </div>
</div>

<!-- ✅ 월 이동: prev/next를 year+month로 분리해서 안전하게 -->
<div class="d-flex justify-content-between align-items-center mb-3">
  <a class="btn btn-sm btn-outline-secondary"
     href="${pageContext.request.contextPath}/calendar/view?year=${prevYear}&month=${prevMonth}">
    ← 이전달
  </a>

  <div class="fw-semibold fs-5">${displayMonth}</div>

  <a class="btn btn-sm btn-outline-secondary"
     href="${pageContext.request.contextPath}/calendar/view?year=${nextYear}&month=${nextMonth}">
    다음달 →
  </a>
</div>

<div class="row g-4">
  <!-- 왼쪽: 월 캘린더 -->
  <div class="col-12 col-lg-8">
    <div class="card shadow-sm">
      <div class="card-header bg-white fw-bold">월 캘린더</div>

      <div class="card-body p-0 calendar-wrap">
        <table class="table table-bordered table-sm mb-0 text-center align-middle calendar-table">
          <thead class="table-light">
            <tr>
              <th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="week" items="${weeks}">
              <tr>
                <c:forEach var="cell" items="${week}">
                  <td 
                  class="${cell.inCurrentMonth ? '' : 'table-light'} calendar-cell row-click" 
                  data-date="${cell.date}"
                  style="vertical-align: top; cursor:pointer;">
					  <div class="d-flex justify-content-between">
					    <span class="fw-semibold">${cell.dayNumber}</span>
					    <c:if test="${cell.today}">
					      <span class="badge bg-primary">오늘</span>
					    </c:if>
					  </div>
					
					  <!-- ✅ 일정 전부 표시 + 줄바꿈 -->
					  <div class="mt-1 text-start">
					    <c:forEach var="e" items="${cell.events}">
					      <div class="calendar-event">
					        ${fn:escapeXml(e.title)}
					      </div>
					    </c:forEach>
					  </div>
					</td>
                </c:forEach>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- 오른쪽: 일정 목록 -->
  <div class="col-12 col-lg-4">
    <div class="card shadow-sm">
      <div class="card-header bg-white fw-bold d-flex justify-content-between">
        <span>이번 달 일정</span>
        <span class="text-muted small">${monthCount}건</span>
      </div>

      <div class="card-body p-0">
        <div class="list-group list-group-flush">
          <c:choose>
            <c:when test="${empty eventList}">
              <div class="p-4 text-center text-muted">이번 달 일정이 없습니다.</div>
            </c:when>
            <c:otherwise>
              <c:forEach var="e" items="${eventList}">
                <div class="list-group-item">
                  <div class="fw-semibold">${fn:escapeXml(e.title)}</div>
                  <c:if test="${not empty e.memo}">
                    <div class="text-muted small">
                      ${fn:escapeXml(e.memo)}
                    </div>
                  </c:if>
                  <div class="text-muted small opacity-50">
                    ${e.startDate}
                    <c:if test="${e.endDate ne e.startDate}"> ~ ${e.endDate}</c:if>
                  </div>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="card shadow-sm mt-4" id="dayPanel">
  <div class="card-header bg-white fw-bold d-flex justify-content-between align-items-center">
    <span id="dayPanelTitle">상세 일정</span>
    <span class="text-muted small">년 월 일</span>
  </div>

  <div class="card-body p-0">
    <div class="list-group list-group-flush" id="dayPanelList">
      <div class="p-4 text-center text-muted">날짜를 선택하면 일정이 표시됩니다.</div>
    </div>
  </div>
</div>