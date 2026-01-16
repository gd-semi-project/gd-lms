<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script defer src="${pageContext.request.contextPath}/resources/js/adminLectureRequestPage.js"></script>


<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">강의 개설 요청 관리</h1>
    <div class="text-muted small">교수 측 개설 요청을 승인/반려합니다.</div>
  </div>

  <form class="d-flex gap-2" method="post" action="${pageContext.request.contextPath}/admin/lectureValidationProcess">
    <select class="form-select form-select-sm" name="validation" style="width: 160px;">
      <option value="lectureId"  ${empty param.status ? 'selected' : ''}>전체</option>
      <option value="PENDING"  ${param.validation=='PENDING' ? 'selected' : ''}>대기</option>
      <option value="CONFIRMED" ${param.validation=='CONFIRMED' ? 'selected' : ''}>승인</option>
      <option value="CANCELED" ${param.validation=='CANCELED' ? 'selected' : ''}>반려</option>
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
              <tr>
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
                    <!-- 승인 -->
                    <form method="post" action="${pageContext.request.contextPath}/admin/lecture-requests/approve" class="m-0">
	                   <div class="d-flex flex-wrap gap-2">
	                       <input type="hidden" name="lectureId" value="${r.lectureId}">
	                       <button type="submit" class="btn btn-sm btn-success"
	                               onclick="return confirm('승인하시겠습니까?');">
	                         승인
	                       </button>
	                       <input type="hidden" name="lectureId" value="${r.lectureId}">
                        <button type="submit" class="btn btn-sm btn-danger"
                                onclick="return confirm('반려하시겠습니까?');">
                          반려
                        </button>
	                   </div>
                        <input type="text" name="reason" class="form-control form-control-sm"
                               placeholder="반려 사유(선택)">
                      </form>
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
      <table class="table table-hover align-middle mb-0">
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

              <c:forEach var="r" items="${confirmedLectureList}">
                <tr>
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
                <tr>
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


<!-- 안내 -->
<div class="mt-3 text-muted small">
  * 승인 시 강의/분반이 실제로 개설되며, 반려 시 교수에게 반려 사유가 전달됩니다(구현 여부에 따라).
</div>
