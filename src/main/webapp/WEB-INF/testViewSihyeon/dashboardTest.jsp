<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">강의 운영 통계</h1>
    <div class="text-muted small">강의개설 · 강의분반(Section) · 시간표 · 수강정원/수강인원</div>
  </div>
  <span class="text-muted small">기준일: <%= java.time.LocalDate.now() %></span>
</div>

<!-- 상단 요약 KPI (실무형) -->
<div class="row g-3 mb-4">
  <div class="col-12 col-md-3">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">개설 강의 수</div>
        <div class="fs-3 fw-bold">124</div>
        <div class="small text-muted">이번 학기 기준</div>
        <!-- 예: ${courseCount} -->
      </div>
    </div>
  </div>

  <div class="col-12 col-md-3">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">강의분반 수 (Section)</div>
        <div class="fs-3 fw-bold">312</div>
        <div class="small text-muted">운영 중 분반</div>
        <!-- 예: ${sectionCount} -->
      </div>
    </div>
  </div>

  <div class="col-12 col-md-3">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">평균 충원율</div>
        <div class="fs-3 fw-bold">78%</div>
        <div class="small text-muted">수강인원/수강정원</div>
        <!-- 예: ${avgFillRate}% -->
      </div>
    </div>
  </div>

  <div class="col-12 col-md-3">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">저충원 분반</div>
        <div class="fs-3 fw-bold text-danger">46</div>
        <div class="small text-muted">충원율 50% 미만</div>
        <!-- 예: ${lowFillSectionCount} -->
      </div>
    </div>
  </div>
</div>

<!-- 2열 KPI (요청/승인/정원 총량) -->
<div class="row g-3 mb-4">
  <div class="col-12 col-md-4">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">총 수강정원</div>
        <div class="fs-3 fw-bold">7,840</div>
        <div class="small text-muted">모든 강의분반 수강정원 합계</div>
        <!-- 예: ${totalCapacity} -->
      </div>
    </div>
  </div>

  <div class="col-12 col-md-4">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">총 수강인원</div>
        <div class="fs-3 fw-bold">6,155</div>
        <div class="small text-muted">현재 수강신청/등록 기준</div>
        <!-- 예: ${totalEnrolled} -->
      </div>
    </div>
  </div>

  <div class="col-12 col-md-4">
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="text-muted">강의 개설 요청(대기)</div>
        <div class="fs-3 fw-bold text-warning">12</div>
        <div class="small text-muted">처리 지연 방지 필요</div>
        <!-- 예: ${pendingOpenReqCount} -->
      </div>
    </div>
  </div>
</div>

<!-- 중단: 강의분반별 수강정원/수강인원(운영 효율) -->
<div class="card shadow-sm mb-4">
  <div class="card-header bg-white d-flex justify-content-between align-items-center">
    <span class="fw-bold">강의분반 운영 효율 (수강정원 대비 충원율)</span>
    <span class="text-muted small">마감임박/저충원 분반을 빠르게 식별</span>
  </div>

  <div class="card-body p-0">
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th style="width: 14%">강의분반</th>
            <th style="width: 22%">과목</th>
            <th style="width: 18%">시간표</th>
            <th style="width: 12%" class="text-end">수강정원</th>
            <th style="width: 12%" class="text-end">수강인원</th>
            <th style="width: 22%">충원율</th>
          </tr>
        </thead>
        <tbody>
          <!-- 예시 데이터 (실데이터는 forEach로 교체) -->
          <tr>
            <td class="fw-semibold">CS101-01</td>
            <td>자료구조</td>
            <td>월·수 09:00~10:15</td>
            <td class="text-end">30</td>
            <td class="text-end">28</td>
            <td>
              <div class="d-flex align-items-center gap-2">
                <div class="progress flex-grow-1" style="height: 10px;">
                  <div class="progress-bar" style="width: 93%"></div>
                </div>
                <span class="small text-muted">93%</span>
                <span class="badge bg-warning text-dark ms-1">마감 임박</span>
              </div>
            </td>
          </tr>

          <tr>
            <td class="fw-semibold">CS101-02</td>
            <td>자료구조</td>
            <td>화·목 14:00~15:15</td>
            <td class="text-end">30</td>
            <td class="text-end">22</td>
            <td>
              <div class="d-flex align-items-center gap-2">
                <div class="progress flex-grow-1" style="height: 10px;">
                  <div class="progress-bar" style="width: 73%"></div>
                </div>
                <span class="small text-muted">73%</span>
                <span class="badge bg-success ms-1">적정</span>
              </div>
            </td>
          </tr>

          <tr>
            <td class="fw-semibold">DB201-01</td>
            <td>데이터베이스</td>
            <td>월·수 14:00~15:15</td>
            <td class="text-end">25</td>
            <td class="text-end">25</td>
            <td>
              <div class="d-flex align-items-center gap-2">
                <div class="progress flex-grow-1" style="height: 10px;">
                  <div class="progress-bar" style="width: 100%"></div>
                </div>
                <span class="small text-muted">100%</span>
                <span class="badge bg-danger ms-1">정원 초과/마감</span>
              </div>
            </td>
          </tr>

          <tr>
            <td class="fw-semibold">SP301-01</td>
            <td>Spring</td>
            <td>금 10:00~12:00</td>
            <td class="text-end">20</td>
            <td class="text-end">8</td>
            <td>
              <div class="d-flex align-items-center gap-2">
                <div class="progress flex-grow-1" style="height: 10px;">
                  <div class="progress-bar" style="width: 40%"></div>
                </div>
                <span class="small text-muted">40%</span>
                <span class="badge bg-secondary ms-1">저충원</span>
              </div>
            </td>
          </tr>
          <!-- /예시 데이터 -->
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- 하단: 시간표/자원 관점 요약 + 운영 리스크 요약 -->
<div class="row g-4">
  <div class="col-12 col-lg-6">
    <div class="card shadow-sm h-100">
      <div class="card-header bg-white fw-bold">시간대별 강의분반 수</div>
      <div class="card-body">
        <div class="mb-3">
          <div class="d-flex justify-content-between">
            <span>09:00 ~ 12:00</span>
            <span class="fw-semibold">86개</span>
          </div>
          <div class="progress" style="height: 10px;">
            <div class="progress-bar" style="width: 60%"></div>
          </div>
        </div>

        <div class="mb-3">
          <div class="d-flex justify-content-between">
            <span>13:00 ~ 16:00</span>
            <span class="fw-semibold">132개</span>
          </div>
          <div class="progress" style="height: 10px;">
            <div class="progress-bar" style="width: 80%"></div>
          </div>
        </div>

        <div>
          <div class="d-flex justify-content-between">
            <span>17:00 이후</span>
            <span class="fw-semibold">94개</span>
          </div>
          <div class="progress" style="height: 10px;">
            <div class="progress-bar" style="width: 57%"></div>
          </div>
        </div>

        <hr>

        <div class="small text-muted">
          * 특정 시간대 집중은 강의실/강사 배정 충돌 가능성을 높입니다.
        </div>
      </div>
    </div>
  </div>

  <div class="col-12 col-lg-6">
    <div class="card shadow-sm h-100">
      <div class="card-header bg-white fw-bold">운영 리스크 요약</div>
      <div class="card-body">
        <div class="d-flex flex-wrap gap-2 mb-3">
          <span class="badge bg-danger">정원 초과 3</span>
          <span class="badge bg-warning text-dark">마감 임박(90%↑) 21</span>
          <span class="badge bg-secondary">저충원(50%↓) 46</span>
          <span class="badge bg-success">적정(50~89%) 242</span>
        </div>

        <ul class="list-group">
          <li class="list-group-item d-flex justify-content-between align-items-center">
            마감 임박 강의분반
            <span class="badge bg-warning text-dark rounded-pill">CS101-01 외 20</span>
          </li>
          <li class="list-group-item d-flex justify-content-between align-items-center">
            저충원 강의분반(폐강/통합 검토)
            <span class="badge bg-secondary rounded-pill">SP301-01 외 45</span>
          </li>
          <li class="list-group-item d-flex justify-content-between align-items-center">
            개설 요청 대기(처리 지연)
            <span class="badge bg-warning text-dark rounded-pill">12</span>
          </li>
        </ul>

        <div class="mt-3 text-end d-flex justify-content-end gap-2">
          <a class="btn btn-outline-primary btn-sm" href="<%=request.getContextPath()%>/admin/lecture-requests">
            개설 요청 관리
          </a>
          <a class="btn btn-outline-secondary btn-sm" href="<%=request.getContextPath()%>/admin/sections">
            강의분반 관리
          </a>
        </div>
      </div>
    </div>
  </div>
</div>
