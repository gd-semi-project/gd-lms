<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<style>
  tr.lecture-row { position: relative; } /* stretched-link 필수 */
  tr.lecture-row:hover { background: rgba(13,110,253,.06); }
</style>

<div class="container-fluid my-4">

  <%-- ✅ departmentId 기본값: 전체(없으면 ALL) --%>
  <c:set var="deptId" value="${empty param.departmentId ? 'ALL' : param.departmentId}" />

  <!-- =======================
       헤더 + 학과 셀렉트 + 검색 (한 줄)
       - 강사/학생 관련 출력 없음
       - 리포트/분반관리 버튼 없음
  ======================= -->
  <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
      <h1 class="h5 fw-bold mb-0">분반별 결과 · 강의 목록</h1>
      <div class="text-muted small">학과 선택 후 강의(분반) 목록을 조회합니다.</div>
    </div>

    <div class="d-flex flex-wrap align-items-center gap-2">
      <!-- 학과 셀렉트 -->
      <select id="deptSelect" class="form-select form-select-sm" style="width: 220px;">
        <option value="ALL" ${deptId == 'ALL' ? 'selected' : ''}>전체 학과</option>
        <c:forEach var="d" items="${departmentList}">
          <option value="${d.departmentId}" ${deptId == d.departmentId ? 'selected' : ''}>
            ${d.departmentName} (${d.departmentCode})
          </option>
        </c:forEach>
      </select>

      <!-- 검색 -->
      <div class="input-group input-group-sm" style="width: 260px;">
        <span class="input-group-text">검색</span>
        <input id="lectureQ" class="form-control"
               placeholder="강의명/강의실 검색">
        <button class="btn btn-primary" type="button" id="lectureQBtn">검색</button>
      </div>

      <!-- 배지: 전체 개수만 (원하면 정원합/인원합도 여기서 추가 가능) -->
      <span class="badge bg-secondary">
        전체: ${empty lectureList ? 0 : fn:length(lectureList)}
      </span>
    </div>
  </div>

  <!-- =======================
       강의 목록 카드
       - 강사/학생 정보 제거
       - 분반(Section) + 시간표 + 인원/정원 + 강의실 + 기간 + 상태
  ======================= -->
  <div class="card shadow-sm">
    <div class="card-header bg-white d-flex justify-content-between align-items-center">
      <div class="fw-bold">강의 목록</div>

      <%-- (선택) 강의 상태 필터가 필요하면 유지. 필요 없으면 이 블록 통째로 삭제하세요. --%>
      <c:set var="lectureStatus" value="${empty param.lectureStatus ? 'ALL' : param.lectureStatus}" />
      <div class="btn-group btn-group-sm" role="group" aria-label="status">
        <a class="btn ${lectureStatus=='ALL' ? 'btn-success active' : 'btn-outline-secondary'}"
           href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${deptId}&lectureStatus=ALL">
          ALL
        </a>

        <a class="btn ${lectureStatus=='ONGOING' ? 'btn-success active' : 'btn-outline-secondary'}"
           href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${deptId}&lectureStatus=ONGOING">
          ONGOING
        </a>

        <a class="btn ${lectureStatus=='PLANNED' ? 'btn-success active' : 'btn-outline-secondary'}"
           href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${deptId}&lectureStatus=PLANNED">
          PLANNED
        </a>

        <a class="btn ${lectureStatus=='ENDED' ? 'btn-success active' : 'btn-outline-secondary'}"
           href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${deptId}&lectureStatus=ENDED">
          ENDED
        </a>
      </div>
    </div>

    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th style="width: 26%;">강의명</th>
              <th style="width: 7%;">분반</th>
              <th style="width: 18%;">시간표</th>
              <th style="width: 16%;">인원/정원</th>
              <th style="width: 12%;">강의실</th>
              <th style="width: 14%;">기간</th>
              <th style="width: 7%;">상태</th>
            </tr>
          </thead>

          <tbody id="lectureTbody">
            <c:choose>
              <c:when test="${empty lectureList}">
                <tr>
                  <td colspan="7" class="text-center text-muted py-5">
                    <c:choose>
                      <c:when test="${deptId == 'ALL'}">
                        표시할 강의가 없습니다.
                      </c:when>
                      <c:otherwise>
                        선택한 학과에 표시할 강의가 없습니다.
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:when>

              <c:otherwise>
                <c:forEach var="l" items="${lectureList}">
                  <tr class="lecture-row">
                    <td>
                      <div class="fw-semibold">
                        ${fn:escapeXml(l.lectureTitle)}
                        <a class="stretched-link"
                           href="${pageContext.request.contextPath}/lecture/detail?lectureId=${l.lectureId}">
                        </a>
                      </div>
                      <div class="text-muted small">강의 ID: ${l.lectureId}</div>
                    </td>

                    <td>
                      <span class="badge bg-light text-dark border">${l.section}</span>
                    </td>

                    <td>
                      <div class="text-muted small">
                        <c:choose>
                          <c:when test="${empty l.schedules}">
                            -
                          </c:when>
                          <c:otherwise>
                            <c:forEach var="s" items="${l.schedules}" varStatus="st">
                              ${s.weekDay} ${s.startTime}~${s.endTime}<c:if test="${not st.last}"><br/></c:if>
                            </c:forEach>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </td>

                    <td>
                      <c:set var="curRaw" value="${empty enrollCountMap ? null : enrollCountMap[l.lectureId]}" />
                      <c:set var="cur" value="${empty curRaw ? 0 : curRaw}" />
                      <c:set var="cap" value="${empty l.capacity ? 0 : l.capacity}" />

                      <div class="fw-semibold">
                        <span class="${cap > 0 && cur >= cap ? 'text-danger' : ''}">${cur}</span>
                        /
                        <span>${cap}</span>
                      </div>
                      <c:if test="${cap > 0 && cur >= cap}">
                        <div class="text-danger small mt-1">정원 마감</div>
                      </c:if>
                    </td>

                    <td>
                      <div>${empty l.room ? '-' : fn:escapeXml(l.room)}</div>
                    </td>

                    <td>
                      <div class="text-muted small">
                        ${l.startDate}
                        <c:if test="${l.endDate ne l.startDate}">
                          ~ ${l.endDate}
                        </c:if>
                      </div>
                    </td>

                    <td>
                      <c:choose>
                        <c:when test="${l.status == 'ONGOING'}">
                          <span class="badge bg-success">ONGOING</span>
                        </c:when>
                        <c:when test="${l.status == 'PLANNED'}">
                          <span class="badge bg-primary">PLANNED</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge bg-secondary">ENDED</span>
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

  <!-- =======================
       JS
       1) 학과 셀렉트 변경 → 페이지 이동 (departmentId 파라미터만 변경)
       2) 검색(클라 필터): 강의명/강의실 기준
  ======================= -->
  <script>
    // 학과 선택 변경 시 이동
    (function () {
      const sel = document.getElementById('deptSelect');
      if (!sel) return;

      sel.addEventListener('change', () => {
        const deptId = sel.value;
        const url = new URL(window.location.href);

        if (deptId === 'ALL') url.searchParams.delete('departmentId');
        else url.searchParams.set('departmentId', deptId);

        // 탭/필터 같은 다른 파라미터는 유지
        window.location.href = url.toString();
      });
    })();

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
          // 강의명/강의실만 대충 포함되게: innerText로 간단 처리
          const text = tr.innerText.toLowerCase();
          tr.style.display = text.includes(keyword) ? '' : 'none';
        });
      }

      q.addEventListener('input', apply);
      if (btn) btn.addEventListener('click', apply);
    })();
  </script>

</div>
