<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">강의 개설 요청 관리</h1>
    <div class="text-muted small">교수 측 개설 요청을 승인/반려합니다.</div>
  </div>

  <form class="d-flex gap-2" method="get" action="${pageContext.request.contextPath}/admin/lecture-requests">
    <select class="form-select form-select-sm" name="status" style="width: 160px;">
      <option value=""  ${empty param.status ? 'selected' : ''}>전체</option>
      <option value="PENDING"  ${param.status=='PENDING' ? 'selected' : ''}>대기</option>
      <option value="APPROVED" ${param.status=='APPROVED' ? 'selected' : ''}>승인</option>
      <option value="REJECTED" ${param.status=='REJECTED' ? 'selected' : ''}>반려</option>
    </select>

    <input class="form-control form-control-sm" name="q" value="${fn:escapeXml(param.q)}"
           placeholder="강의명/교수명 검색" style="width: 220px;"/>

    <button class="btn btn-sm btn-primary" type="submit">검색</button>
  </form>
</div>

<!-- 요약 배지 (선택: 서블릿에서 count 내려주면 좋음) -->
<div class="d-flex flex-wrap gap-2 mb-3">
  <span class="badge bg-secondary">전체: ${empty totalCount ? 0 : totalCount}</span>
  <span class="badge bg-warning text-dark">대기: ${empty pendingCount ? 0 : pendingCount}</span>
  <span class="badge bg-success">승인: ${empty approvedCount ? 0 : approvedCount}</span>
  <span class="badge bg-danger">반려: ${empty rejectedCount ? 0 : rejectedCount}</span>
</div>

<div class="card shadow-sm">
  <div class="card-header bg-white fw-bold">
    요청 목록
  </div>

  <div class="card-body p-0">
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th style="width: 10%;">요청ID</th>
            <th style="width: 22%;">강의명</th>
            <th style="width: 14%;">교수</th>
            <th style="width: 14%;">분반/시간</th>
            <th style="width: 10%;" class="text-end">정원</th>
            <th style="width: 12%;">상태</th>
            <th style="width: 18%;">처리</th>
          </tr>
        </thead>

        <tbody>
        <c:choose>
          <c:when test="${empty requestList}">
            <tr>
              <td colspan="7" class="text-center text-muted py-5">
                표시할 요청이 없습니다.
              </td>
            </tr>
          </c:when>

          <c:otherwise>
            <c:forEach var="r" items="${requestList}">
              <tr>
                <td class="fw-semibold">${r.requestId}</td>
                <td>
                  <div class="fw-semibold">${r.lectureName}</div>
                  <div class="text-muted small">요청일: ${r.createdAt}</div>
                </td>
                <td>${r.professorName}</td>
                <td>
                  <div>${r.sectionName}</div>
                  <div class="text-muted small">${r.scheduleText}</div>
                </td>
                <td class="text-end">${r.capacity}</td>
                <td>
                  <c:choose>
                    <c:when test="${r.status == 'PENDING'}">
                      <span class="badge bg-warning text-dark">대기</span>
                    </c:when>
                    <c:when test="${r.status == 'APPROVED'}">
                      <span class="badge bg-success">승인</span>
                    </c:when>
                    <c:when test="${r.status == 'REJECTED'}">
                      <span class="badge bg-danger">반려</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge bg-secondary">${r.status}</span>
                    </c:otherwise>
                  </c:choose>
                </td>

                <td>
                  <!-- 상태가 PENDING일 때만 승인/반려 버튼 노출 -->
                  <c:if test="${r.status == 'PENDING'}">
                    <div class="d-flex flex-wrap gap-2">
                      <!-- 승인 -->
                      <form method="post" action="${pageContext.request.contextPath}/admin/lecture-requests/approve" class="m-0">
                        <input type="hidden" name="requestId" value="${r.requestId}">
                        <button type="submit" class="btn btn-sm btn-success"
                                onclick="return confirm('승인하시겠습니까?');">
                          승인
                        </button>
                      </form>

                      <!-- 반려: 모달 없이 간단 input -->
                      <form method="post" action="${pageContext.request.contextPath}/admin/lecture-requests/reject" class="m-0 d-flex gap-2">
                        <input type="hidden" name="requestId" value="${r.requestId}">
                        <input type="text" name="reason" class="form-control form-control-sm"
                               placeholder="반려 사유(선택)" style="width: 160px;">
                        <button type="submit" class="btn btn-sm btn-outline-danger"
                                onclick="return confirm('반려하시겠습니까?');">
                          반려
                        </button>
                      </form>
                    </div>
                  </c:if>

                  <!-- 이미 처리된 건 안내 -->
                  <c:if test="${r.status != 'PENDING'}">
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

<!-- 안내 -->
<div class="mt-3 text-muted small">
  * 승인 시 강의/분반이 실제로 개설되며, 반려 시 교수에게 반려 사유가 전달됩니다(구현 여부에 따라).
</div>
