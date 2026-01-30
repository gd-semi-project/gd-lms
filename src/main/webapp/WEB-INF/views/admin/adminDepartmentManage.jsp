<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<style>
	body{ padding-top : 40px; }
</style>
<div class="container-fluid my-4">

  <!-- ✅ status 기본값 + 전체 페이지 공용 -->
  <c:set var="currentStatus" value="${empty param.status ? 'ACTIVE' : param.status}" />

  <div class="row g-3">

    <!-- =========================
         LEFT (25%): 학과 카드 리스트
    ========================== -->
    <div class="col-12 col-lg-3">
      <div class="card shadow-sm h-100">
        <div class="card-body p-3">
          <div class="d-flex align-items-center justify-content-between mb-2">
            <h6 class="fw-bold mb-0">학과</h6>
            <span class="badge text-bg-secondary">${fn:length(departmentList)}</span>
          </div>

          <div class="input-group input-group-sm mb-2">
            <span class="input-group-text">검색</span>
            <input id="deptFilter" type="text" class="form-control" placeholder="학과명/코드">
          </div>

          <div id="deptCardWrap" style="max-height: calc(100vh - 220px); overflow:auto;">
            <c:choose>
              <c:when test="${empty departmentList}">
                <div class="text-muted small">학과 데이터가 없습니다.</div>
              </c:when>

              <c:otherwise>
                <div class="d-grid gap-2">
                  <c:forEach var="d" items="${departmentList}">
                    <a class="text-decoration-none"
                       href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${d.departmentId}&status=${currentStatus}">
                      <div class="card border ${param.departmentId == d.departmentId ? 'border-primary' : 'border-light'}">
                        <div class="card-body p-3">
                          <div class="d-flex justify-content-between align-items-start">
                            <div class="fw-semibold text-dark text-truncate" style="max-width: 140px;">
                              ${d.departmentName}
                            </div>

                            <span class="badge ${param.departmentId == d.departmentId ? 'text-bg-primary' : 'text-bg-light border'}">
                              ${d.departmentCode}
                            </span>
                          </div>

                          <div class="text-muted small mt-1">
                            정원: ${d.annualQuota}
                          </div>
                        </div>
                      </div>
                    </a>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

        </div>
      </div>
    </div>

    <div class="col-12 col-lg-9">
      <div class="card shadow-sm h-100">
        <div class="card-body">

          <!-- 학과 선택 시에만 상태 토글 표시 -->
          <c:if test="${not empty param.departmentId}">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <!-- 좌측: 상태 표시 -->
              <div class="text-muted small">
                현재 조회 상태:
                <span class="badge ${currentStatus == 'ACTIVE' ? 'text-bg-success' : 'text-bg-danger'}">
                  ${currentStatus}
                </span>
              </div>

              <!-- 우측: 토글 버튼 -->
              <div class="btn-group btn-group-sm" role="group" aria-label="status toggle">
                <a class="btn ${currentStatus == 'ACTIVE' ? 'btn-success' : 'btn-outline-secondary'}"
                   href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=ACTIVE">
                  ACTIVE
                </a>

                <a class="btn ${currentStatus == 'INACTIVE' ? 'btn-danger' : 'btn-outline-secondary'}"
                   href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=INACTIVE">
                  INACTIVE
                </a>
              </div>
            </div>

            <hr class="my-3"/>
          </c:if>

          <!-- 선택 학과 상세 -->
          <c:choose>
            <c:when test="${empty param.departmentId}">
            </c:when>

            <c:otherwise>
              <jsp:include page="/WEB-INF/views/admin/adminDepartmentDetail.jsp" />
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>
<c:choose>
	<c:when test="${empty param.departmentId}">
	</c:when>
	
	<c:otherwise>
		<jsp:include page="/WEB-INF/views/admin/adminDepartmentLectureList.jsp" />
	</c:otherwise>
	</c:choose>

<script>
  // 좌측 학과 검색 필터
  const input = document.getElementById('deptFilter');
  const wrap = document.getElementById('deptCardWrap');
  if (input && wrap) {
    input.addEventListener('input', () => {
      const q = input.value.trim().toLowerCase();
      for (const a of wrap.querySelectorAll('a')) {
        const text = a.innerText.toLowerCase();
        a.style.display = text.includes(q) ? '' : 'none';
      }
    });
  }
</script>
