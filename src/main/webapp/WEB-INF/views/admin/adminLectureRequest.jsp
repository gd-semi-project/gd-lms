<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script defer src="${pageContext.request.contextPath}/resources/js/adminLectureRequestPage.js"></script>


<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">강의 개설 요청 관리</h1>
    <div class="text-muted small">교수 측 개설 요청을 승인/반려합니다.</div>
  </div>

<form class="d-flex gap-2" id="quickSearchForm">
  <input class="form-control form-control-sm"
         id="quickSearchInput"
         placeholder="강의명/교수명 검색"
         style="width:220px;">
  <button class="btn btn-sm btn-primary" type="submit">검색</button>
</form>

</div>



<c:choose>
<c:when test="${policy.available}">



<!-- 요약 배지 (선택: 서블릿에서 count 내려주면 좋음) -->
<div class="d-flex flex-wrap gap-2 mb-3">
  <span class="badge bg-secondary">전체: ${empty totalCount ? 0 : totalCount}</span>
  <span class="badge bg-warning text-dark">대기: ${empty pendingCount ? 0 : pendingCount}</span>
  <span class="badge bg-success">승인: ${empty confirmedCount ? 0 : confirmedCount}</span>
  <span class="badge bg-danger">반려: ${empty canceledCount ? 0 : canceledCount}</span>
</div>

<div class="card shadow-sm">
<div class="card-header bg-white d-flex flex-wrap gap-2 align-items-center justify-content-between">
  <div class="fw-bold">요청 목록</div>

  <!-- 학과별 보기 (레이아웃만) -->
  <form class="d-flex align-items-center gap-2 m-0"
        method="post"
        action="${pageContext.request.contextPath}/admin/lectureRequest?action=selectDepartment">
    
    <!-- 기존 검색/필터 조건 유지용(선택) -->
    <label class="text-muted small mb-0" for="deptSelect">학과</label>
    <select id="deptSelect" name="departmentId" class="form-select form-select-sm" style="width: 200px;">
      <option value="all" ${empty param.departmentId ? 'selected' : ''}>전체 학과</option>

      <c:forEach var="d" items="${departmentList}">
        <option value="${d.departmentId}" ${param.departmentId == d.departmentId ? 'selected' : ''}>
          ${d.departmentName}
        </option>
      </c:forEach>
    </select>

    <button type="submit" class="btn btn-sm btn-outline-secondary">적용</button>
  </form>
</div>


  <div class="card-body p-0">
    <div class="table-responsive">
      <table id="pendingTable" class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th style="width: 22%;">강의명</th>
            <th style="width: 10%;">분반</th>
            <th style="width: 14%;">교수</th>
            <th style="width: 12%;">시간표</th>
            <th style="width: 12%;" class="text-center">정원</th>
            <th style="width: 12%;">상태</th>
            <th style="width: 18%;">처리</th>
        <!--     <th style="width: 6%;">요청일</th> -->
          </tr>
        </thead>

        <tbody>
        <c:choose>
          <c:when test="${empty requestScope.pendingLectureList}">
            <tr>
              <td colspan="7" class="text-center text-muted py-5">
                표시할 요청이 없습니다.
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="r" items="${requestScope.pendingLectureList}">
              <tr data-search="${fn:escapeXml(r.lectureTitle)} ${fn:escapeXml(r.instructorName)} ${fn:escapeXml(r.schedule)} ${fn:escapeXml(r.section)}"
    data-validation="${r.validation}">
                <td>
                  <div class="fw-semibold">${r.lectureTitle}</div>
                  <div class="text-muted small">요청일: ${r.createdAt}</div>
                </td>
                <td>
                  <div>${r.section}</div>
                </td>
                <td>${r.instructorName}</td>
                <td>
                  <div class="text-muted small">${r.schedule}</div>
                </td>
                <td class="text-center">${r.capacity}</td>
                <td>
                  <c:choose>
                    <c:when test="${r.validation == 'PENDING'}">
                      <span class="badge bg-warning text-dark">대기</span>
                    </c:when>
                    <c:when test="${r.validation == 'CONFIRMED'}">
                      <span class="badge bg-success">승인</span>
                    </c:when>
                    <c:when test="${r.validation == 'CANCELED'}">
                      <span class="badge bg-danger">반려</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge bg-secondary">${r.validation}</span>
                    </c:otherwise>
                  </c:choose>
                </td>

                <td>
                  <!-- 상태가 PENDING일 때만 승인/반려 버튼 노출 -->
					<c:if test="${r.validation == 'PENDING'}">
					  <div class="d-flex flex-wrap gap-2">
					
					    <!-- 승인 -->
					    <form method="post"
					          action="${pageContext.request.contextPath}/admin/lectureRequest?action=CONFIRMED"
					          class="m-0">
					      <input type="hidden" name="lectureId" value="${r.lectureId}">
					      <button type="submit" class="btn btn-sm btn-success"
					              onclick="return confirm('승인하시겠습니까?');">
					        승인
					      </button>
					    </form>
					
					    <!-- 반려 -->
					    <form method="post"
					          action="${pageContext.request.contextPath}/admin/lectureRequest?action=CANCELED"
					          class="m-0">
					      <input type="hidden" name="lectureId" value="${r.lectureId}">
					      <button type="submit" class="btn btn-sm btn-danger"
					              onclick="return confirm('반려하시겠습니까?');">
					        반려
					      </button>
					    </form>
					
					  </div>
					</c:if>

				
                  <!-- 이미 처리된 건 안내 -->
                  <c:if test="${r.validation != 'PENDING'}">
                    <span class="text-muted small">처리 완료</span>
                  </c:if>
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

<div class="d-flex justify-content-between align-items-center mt-5 mb-3">
  <div>
    <h2 class="h5 mb-1">처리된 요청</h2>
    <div class="text-muted small">승인/반려 완료된 요청</div>
  </div>

  <div class="btn-group" role="group" aria-label="processed-filter">
    <button type="button" class="btn btn-sm btn-outline-primary active"
            data-filter="all">전체</button>
    <button type="button" class="btn btn-sm btn-outline-primary"
            data-filter="confirmed">승인</button>
    <button type="button" class="btn btn-sm btn-outline-primary"
            data-filter="canceled">반려</button>
  </div>
</div>

<div class="card shadow-sm" id="processedBox">
  <div class="card-header bg-white fw-bold">처리 완료 목록</div>

  <div class="card-body p-0">
    <div class="table-responsive">
      <table id="pendingTable" class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th style="width: 22%;">강의명</th>
            <th style="width: 10%;">분반</th>
            <th style="width: 14%;">교수</th>
            <th style="width: 12%;">시간표</th>
            <th style="width: 12%;" class="text-center">정원</th>
            <th style="width: 12%;">결과</th>
            <th style="width: 18%;">비고</th>
          </tr>
        </thead>

        <tbody data-body="all">
          <c:choose>
            <c:when test="${empty confirmedLectureList and empty canceledLectureList}">
              <tr>
                <td colspan="7" class="text-center text-muted py-4">처리된 요청이 없습니다.</td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="r" items="${canceledLectureList}">
                <tr data-search="${fn:escapeXml(r.lectureTitle)} ${fn:escapeXml(r.instructorName)} ${fn:escapeXml(r.schedule)} ${fn:escapeXml(r.section)}"
    data-validation="${r.validation}">
                  <td>
                    <div class="fw-semibold">${r.lectureTitle}</div>
                    <div class="text-muted small">요청일: ${r.createdAt}</div>
                  </td>
                  <td>${r.section}</td>
                  <td>${r.instructorName}</td>
                  <td><div class="text-muted small">${r.schedule}</div></td>
                  <td class="text-center">${r.capacity}</td>
                  <td><span class="badge bg-danger">반려</span></td>
                  <td><span class="text-muted small">처리 완료</span></td>
                </tr>
              </c:forEach>

              <c:forEach var="r" items="${confirmedLectureList}">
                <tr data-search="${fn:escapeXml(r.lectureTitle)} ${fn:escapeXml(r.instructorName)} ${fn:escapeXml(r.schedule)} ${fn:escapeXml(r.section)}"
    data-validation="${r.validation}">
                  <td>
                    <div class="fw-semibold">${r.lectureTitle}</div>
                    <div class="text-muted small">요청일: ${r.createdAt}</div>
                  </td>
                  <td>${r.section}</td>
                  <td>${r.instructorName}</td>
                  <td><div class="text-muted small">${r.schedule}</div></td>
                  <td class="text-center">${r.capacity}</td>
                  <td><span class="badge bg-success">승인</span></td>
                  <td><span class="text-muted small">처리 완료</span></td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>

        <tbody data-body="confirmed" style="display:none;">
          <c:choose>
            <c:when test="${empty confirmedLectureList}">
              <tr>
                <td colspan="7" class="text-center text-muted py-4">승인된 요청이 없습니다.</td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="r" items="${confirmedLectureList}">
                <tr data-search="${fn:escapeXml(r.lectureTitle)} ${fn:escapeXml(r.instructorName)} ${fn:escapeXml(r.schedule)} ${fn:escapeXml(r.section)}"
    data-validation="${r.validation}">
                  <td>
                    <div class="fw-semibold">${r.lectureTitle}</div>
                    <div class="text-muted small">요청일: ${r.createdAt}</div>
                  </td>
                  <td>${r.section}</td>
                  <td>${r.instructorName}</td>
                  <td><div class="text-muted small">${r.schedule}</div></td>
                  <td class="text-center">${r.capacity}</td>
                  <td><span class="badge bg-success">승인</span></td>
                  <td><span class="text-muted small">처리 완료</span></td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>

        <tbody data-body="canceled" style="display:none;">
          <c:choose>
            <c:when test="${empty canceledLectureList}">
              <tr>
                <td colspan="7" class="text-center text-muted py-4">반려된 요청이 없습니다.</td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="r" items="${canceledLectureList}">
                <tr>
                  <td>
                    <div class="fw-semibold">${r.lectureTitle}</div>
                    <div class="text-muted small">요청일: ${r.createdAt}</div>
                  </td>
                  <td>${r.section}</td>
                  <td>${r.instructorName}</td>
                  <td><div class="text-muted small">${r.schedule}</div></td>
                  <td class="text-center">${r.capacity}</td>
                  <td><span class="badge bg-danger">반려</span></td>
                  <td><span class="text-muted small">처리 완료</span></td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</div>


</c:when>
<c:otherwise>
	<div class = "alert alert-warning">${policy.message}</div>
</c:otherwise>
</c:choose>


<script>
/* ===============================
   처리된 요청 필터 (승인/반려)
   =============================== */
(function () {
  var box = document.getElementById("processedBox");
  if (!box) return;

  // 버튼은 processedBox 위의 btn-group에 있음
  var wrapper = box.previousElementSibling;
  if (!wrapper) return;

  var buttons = wrapper.querySelectorAll("[data-filter]");
  var bodies  = box.querySelectorAll("tbody[data-body]");
  if (!buttons.length || !bodies.length) return;

  function setFilter(filter) {
    var i, tb, btn, active;

    for (i = 0; i < bodies.length; i++) {
      tb = bodies[i];
      tb.style.display =
        (tb.getAttribute("data-body") === filter) ? "" : "none";
    }

    for (i = 0; i < buttons.length; i++) {
      btn = buttons[i];
      active = (btn.getAttribute("data-filter") === filter);

      if (btn.classList) {
        btn.classList.toggle("active", active);
        btn.classList.toggle("btn-primary", active);
        btn.classList.toggle("btn-outline-primary", !active);
      }
    }
  }

  for (var i = 0; i < buttons.length; i++) {
    (function (btn) {
      btn.addEventListener("click", function () {
        setFilter(btn.getAttribute("data-filter"));
      });
    })(buttons[i]);
  }

  setFilter("all");
}());


/* ===============================
   빠른 검색 (대기 목록만)
   =============================== */
(function () {
  function norm(s) {
    if (s === undefined || s === null) s = "";
    s = String(s).toLowerCase().replace(/\s+/g, " ");
    return s.trim ? s.trim() : s.replace(/^\s+|\s+$/g, "");
  }

  function getRows() {
    var nodes = document.querySelectorAll("#pendingTable tbody tr");
    var rows = [];
    for (var i = 0; i < nodes.length; i++) {
      if (nodes[i].getElementsByTagName("td").length > 0) {
        rows.push(nodes[i]);
      }
    }
    return rows;
  }

  function applyFilter(query) {
    var q = norm(query);
    var rows = getRows();
    var i, tr, hay;

    for (i = 0; i < rows.length; i++) {
      tr = rows[i];
      hay = tr.getAttribute("data-search");
      if (!hay) hay = tr.textContent || tr.innerText || "";
      hay = norm(hay);

      tr.style.display =
        (!q || hay.indexOf(q) !== -1) ? "" : "none";
    }
  }

  function onReady(fn) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else {
      fn();
    }
  }

  onReady(function () {
    var form  = document.getElementById("quickSearchForm");
    var input = document.getElementById("quickSearchInput");
    if (!form || !input) return;

    form.addEventListener("submit", function (e) {
      e.preventDefault();
      applyFilter(input.value);
    });

    var t = null;
    input.addEventListener("input", function () {
      if (t) clearTimeout(t);
      t = setTimeout(function () {
        applyFilter(input.value);
      }, 80);
    });
  });
}());
</script>
