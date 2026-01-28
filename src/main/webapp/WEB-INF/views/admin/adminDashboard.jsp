<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<style>
	body{ padding-top : 40px; }
  tr.lecture-row { position: relative; }
  tr.lecture-row:hover { background: rgba(13,110,253,.06); }

  .metric-card .label { font-size: .85rem; color: #6c757d; }
  .metric-card .value { font-size: 1.9rem; font-weight: 800; line-height: 1.05; }
  .metric-card .sub { font-size: .825rem; color: #6c757d; }

  .risk-pill { font-weight: 800; letter-spacing: .2px; }
  .progress-thin { height: 8px; }
  .table thead th { white-space: nowrap; }
</style>

<c:choose>
<c:when test="${policy.available}">


<div class="container-fluid my-4">

  <!-- =======================
       헤더 (컨트롤러 값 사용)
  ======================= -->
<div class="d-flex flex-column flex-lg-row align-items-start align-items-lg-end justify-content-between gap-2 mb-3">
  <div>
    <h1 class="h5 fw-bold mb-1">수강신청 결과 · 강의 목록</h1>
    <div class="text-muted small">학과 선택 후 통계/목록을 한 번에 확인합니다.</div>
  </div>

  <div class="d-flex flex-wrap gap-2 justify-content-start justify-content-lg-end">
    <span class="badge bg-secondary">개설(중복X): ${empty lectureCount ? 0 : lectureCount}</span>
    <span class="badge bg-dark">전체(중복O): ${empty totalLectureCount ? 0 : totalLectureCount}</span>
    <span class="badge bg-danger">폐강위기: ${empty lowFillRateLecture ? 0 : lowFillRateLecture}</span>
    <span class="badge bg-light text-dark border">기준일: <%= java.time.LocalDate.now() %></span>
  </div>
</div>


  <!-- =======================
       KPI (4개) - 컨트롤러 값
  ======================= -->
<div class="row g-3 mb-3">
  <div class="col-12 col-md-4">
    <div class="card shadow-sm metric-card">
      <div class="card-body">
        <div class="label">개설된 강의(중복X)</div>
        <div class="value">${empty lectureCount ? 0 : lectureCount}</div>
        <div class="sub">강의명 기준</div>
      </div>
    </div>
  </div>

  <div class="col-12 col-md-4">
    <div class="card shadow-sm metric-card">
      <div class="card-body">
        <div class="label">전체 강의(중복O)</div>
        <div class="value text-dark">${empty totalLectureCount ? 0 : totalLectureCount}</div>
        <div class="sub">분반 포함</div>
      </div>
    </div>
  </div>

  <div class="col-12 col-md-4">
    <div class="card shadow-sm metric-card">
      <div class="card-body">
        <div class="label">폐강 위기</div>
        <div class="value text-danger">${empty lowFillRateLecture ? 0 : lowFillRateLecture}</div>
        <div class="sub">충원율 50% 미만</div>
      </div>
    </div>
  </div>
</div>

  <!-- =======================
       KPI (3개) - 컨트롤러 값
  ======================= -->
  <div class="row g-3 mb-4">
    <div class="col-12 col-md-4">
      <div class="card shadow-sm metric-card">
        <div class="card-body">
          <div class="label">총 정원</div>
          <div class="value">${empty totalLectureCapacity ? 0 : totalLectureCapacity}</div>
          <div class="sub">정원 합계</div>
        </div>
      </div>
    </div>

    <div class="col-12 col-md-4">
      <div class="card shadow-sm metric-card">
        <div class="card-body">
          <div class="label">전체 학생 수(Active)</div>
          <div class="value">${empty totalEnrollment ? 0 : totalEnrollment}</div>
          <div class="sub">재학 중 학생</div>
        </div>
      </div>
    </div>

    <div class="col-12 col-md-4">
      <div class="card shadow-sm metric-card">
        <div class="card-body">
          <div class="label">평균 충원율</div>
          <div class="value">${empty lectureFillRate ? 0 : lectureFillRate}%</div>
          <div class="sub">정원 대비 인원</div>
        </div>
      </div>
    </div>
  </div>

  <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div class="d-flex flex-wrap align-items-center gap-2">
    <form action="${pageContext.request.contextPath}/admin/dashboard" method="get">
      <select name="departmentId" onchange="this.form.submit()" id="deptSelect" class="form-select form-select-sm" style="width: 280px;">
        <option value="" ${empty param.departmentId ? 'selected' : ''}>전체 학과</option>
        <c:forEach var="d" items="${departmentList}">
          <option value="${d.departmentId}" ${param.departmentId == d.departmentId ? 'selected' : ''}>
            ${d.departmentName} (${d.departmentCode})
          </option>
        </c:forEach>
      </select>
	</form>
      <div class="input-group input-group-sm" style="width: 340px;">
        <span class="input-group-text">검색</span>
        <input id="lectureQ" class="form-control" placeholder="강의명/강의실 검색">
        <button class="btn btn-primary" type="button" id="lectureQBtn">검색</button>
      </div>
    </div>

    <div class="text-muted small">
      <c:choose>
        <c:when test="${empty param.departmentId}">전체 학과 조회</c:when>
        <c:otherwise>학과 ID: ${param.departmentId}</c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="card shadow-sm">
    <div class="card-header bg-white d-flex align-items-center justify-content-between">
      <div class="fw-bold">강의 목록</div>
      <div class="text-muted small">* 인원/정원 기준 운영 상태를 표시합니다.</div>
    </div>

    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th style="width: 28%;">강의명</th>
              <th style="width: 7%;">분반</th>
              <th style="width: 18%;">시간표</th>
              <th style="width: 22%;">인원/정원</th>
              <th style="width: 11%;">강의실</th>
              <th style="width: 12%;">기간</th>
              <th style="width: 2%;">운영</th>
            </tr>
          </thead>

          <tbody id="lectureTbody">
            <c:choose>
              <c:when test="${empty lectureList}">
                <tr>
                  <td colspan="7" class="text-center text-muted py-5">표시할 강의가 없습니다.</td>
                </tr>
              </c:when>

              <c:otherwise>
                <c:forEach var="l" items="${lectureList}">
                  <tr class="lecture-row">
                    <td>
					  <div class="fw-semibold">
					    ${fn:escapeXml(l.lectureTitle)}
					    <span class="text-muted fw-normal ms-1">
					      (${fn:escapeXml(l.instructorName)})
					    </span>
					    <a class="stretched-link"
					       href="${pageContext.request.contextPath}/lecture/detail?lectureId=${l.lectureId}">
					    </a>
					  </div>
					  <div class="text-muted small">
					    강의 ID: ${l.lectureId} · 학과 ID: ${l.departmentId}
					  </div>
					</td>

                    <td>
                      <span class="badge bg-light text-dark border">${l.section}</span>
                    </td>

                    <td>
                      <div class="text-muted small">
                        <c:out value="${empty l.scheduleHtml ? '-' : l.scheduleHtml}" escapeXml="false"/>
                      </div>
                    </td>

                    <td>
					  <div class="d-flex justify-content-between align-items-center">
					    <div class="fw-semibold">
					      <span
					        <c:if test="${(empty l.capacity ? 0 : l.capacity) > 0
					                     and (empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) >= (empty l.capacity ? 0 : l.capacity)}">
					          class="text-danger"
					        </c:if>
					      >
					        ${empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])}
					      </span>
					      /
					      <span>${empty l.capacity ? 0 : l.capacity}</span>
					    </div>
					
					    <span class="small text-muted">
					      ${ (empty l.capacity ? 0 : l.capacity) > 0
					          ? ((empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) * 100) / (empty l.capacity ? 0 : l.capacity)
					          : 0
					      }%
					    </span>
					  </div>
					
					  <div class="progress progress-thin mt-2">
					    <div class="progress-bar"
					         style="width: ${
					            ((empty l.capacity ? 0 : l.capacity) > 0
					              ? ((empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) * 100) / (empty l.capacity ? 0 : l.capacity)
					              : 0
					            ) > 100
					            ? 100
					            : ((empty l.capacity ? 0 : l.capacity) > 0
					                ? ((empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) * 100) / (empty l.capacity ? 0 : l.capacity)
					                : 0
					              )
					         }%"></div>
					  </div>
					
					  <c:if test="${(empty l.capacity ? 0 : l.capacity) > 0
					               and (empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) >= (empty l.capacity ? 0 : l.capacity)}">
					    <div class="text-danger small mt-1">정원 마감</div>
					  </c:if>
					</td>

                    <td>${empty l.room ? '-' : fn:escapeXml(l.room)}</td>

                    <td>
                      <div class="text-muted small">
                        ${l.startDate}<c:if test="${l.endDate ne l.startDate}"> ~ ${l.endDate}</c:if>
                      </div>
                    </td>

                    <td>
                      <c:choose>
                        <c:when test="${
                          (empty l.capacity ? 0 : l.capacity) > 0
                          && (empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) >= (empty l.capacity ? 0 : l.capacity)
                        }">
                          <span class="badge bg-dark risk-pill">정원마감</span>
                        </c:when>

                        <c:when test="${
                          (empty l.capacity ? 0 : l.capacity) > 0
                          && ((empty enrollCountMap ? 0 : (empty enrollCountMap[l.lectureId] ? 0 : enrollCountMap[l.lectureId])) * 100) < ((empty l.capacity ? 0 : l.capacity) * 50)
                        }">
                          <span class="badge bg-danger risk-pill">폐강위기</span>
                        </c:when>

                        <c:otherwise>
                          <span class="badge bg-success risk-pill">정상</span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>

        </table>
      </div>
    </div>
  </div>
  <script>
    // 검색(클라 필터)
    (function () {
      const q = document.getElementById('lectureQ');
      const btn = document.getElementById('lectureQBtn');
      const tbody = document.getElementById('lectureTbody');
      if (!q || !tbody) return;

      function apply() {
        const keyword = q.value.trim().toLowerCase();
        const rows = tbody.querySelectorAll('tr.lecture-row');
        rows.forEach(tr => {
          const text = tr.innerText.toLowerCase();
          tr.style.display = text.includes(keyword) ? '' : 'none';
        });
      }

      q.addEventListener('input', apply);
      if (btn) btn.addEventListener('click', apply);
    })();
  </script>

</div>
</c:when>
<c:otherwise>
	<div class = "alert alert-warning">${policy.message}</div>
</c:otherwise>
</c:choose>
