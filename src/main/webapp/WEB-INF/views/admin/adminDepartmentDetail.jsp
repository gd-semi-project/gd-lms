<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<style>
  .req-table { table-layout: fixed; width: 100%; }
  .req-table th, .req-table td { vertical-align: middle; }
  .req-table th { white-space: nowrap; }
  .cell-old { background: #f8f9fa; }
  .help { color: #6c757d; font-size: .85rem; }
  .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
  .doc-pill { font-size: .85rem; }
  .sticky-actions { position: sticky; bottom: 0; background: white; border-top: 1px solid #eee; }
</style>

<div class="container my-4" style="max-width: 1100px;">

  <!-- 헤더 -->
  <div class="d-flex align-items-start justify-content-between gap-2 mb-3">
    <div>
      <h3 class="mb-1">학생 중요 정보 변경 요청 상세</h3>
      <div class="text-muted small">
        요청ID: <span class="fw-semibold">${req.requestId}</span>
        <span class="mx-2">•</span>
        신청일: <span class="fw-semibold">${fn:escapeXml(req.createdAt)}</span>
      </div>
    </div>

    <div class="d-flex gap-2">
      <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/studentInfoUpdateRequests">목록</a>
    </div>
  </div>

  <!-- 기본 정보 카드 -->
  <div class="card shadow-sm mb-3">
    <div class="card-body">
      <div class="row g-2">
        <div class="col-12 col-md-6">
          <div class="text-muted small">학생ID</div>
          <div class="fw-semibold">${req.studentId}</div>
        </div>
        <div class="col-12 col-md-6">
          <div class="text-muted small">로그인ID</div>
          <div class="fw-semibold">${fn:escapeXml(currentUser.loginId)}</div>
        </div>

        <div class="col-12 col-md-6">
          <div class="text-muted small">학생 이름(현재)</div>
          <div class="fw-semibold">${fn:escapeXml(currentUser.name)}</div>
        </div>

        <div class="col-12 col-md-6">
          <div class="text-muted small">학과(현재)</div>
          <div class="fw-semibold">
            <c:choose>
              <c:when test="${not empty currentDept}">
                ${fn:escapeXml(currentDept.departmentName)}
              </c:when>
              <c:otherwise>
                <span class="text-muted">-</span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 변경 요청 표 -->
  <div class="card shadow-sm">
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table mb-0 req-table">
          <thead class="table-light">
            <tr>
              <th style="width:16%;">항목</th>
              <th style="width:28%;">변경 전</th>
              <th style="width:28%;">변경 후</th>
              <th style="width:28%;">첨부 서류</th>
            </tr>
          </thead>

          <tbody>
            <!-- 이름 -->
            <tr>
              <th>이름</th>
              <td class="cell-old">${fn:escapeXml(currentUser.name)}</td>
              <td>
                <c:choose>
                  <c:when test="${not empty req.newName}">
                    <span class="badge bg-info text-dark me-1">변경</span>
                    ${fn:escapeXml(req.newName)}
                  </c:when>
                  <c:otherwise><span class="text-muted">변경 없음</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:set var="hasDoc" value="false" />
                <c:forEach var="f" items="${files}">
                  <c:if test="${f.boardType == 'CHANGE_NAME'}">
                    <c:set var="hasDoc" value="true" />
                    <!-- 다운로드는 나중: 링크만 미리 -->
                    <a class="btn btn-sm btn-outline-primary me-1 mb-1"
                       href="${ctx}/admin/files/download?uuid=${f.uuid}">
                      ${fn:escapeXml(f.originalFilename)}
                    </a>
                  </c:if>
                </c:forEach>
                <c:if test="${hasDoc == false}">
                  <span class="text-muted">-</span>
                </c:if>
              </td>
            </tr>

            <!-- 성별 -->
            <tr>
              <th>성별</th>
              <td class="cell-old">
                <c:choose>
                  <c:when test="${currentUser.gender == 'M'}">남</c:when>
                  <c:when test="${currentUser.gender == 'F'}">여</c:when>
                  <c:otherwise><span class="text-muted">미지정</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty req.newGender}">
                    <span class="badge bg-info text-dark me-1">변경</span>
                    <c:choose>
                      <c:when test="${req.newGender == 'M'}">남</c:when>
                      <c:when test="${req.newGender == 'F'}">여</c:when>
                      <c:otherwise>${req.newGender}</c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise><span class="text-muted">변경 없음</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:set var="hasDoc" value="false" />
                <c:forEach var="f" items="${files}">
                  <c:if test="${f.boardType == 'CHANGE_GENDER'}">
                    <c:set var="hasDoc" value="true" />
                    <a class="btn btn-sm btn-outline-primary me-1 mb-1"
                       href="${ctx}/admin/files/download?uuid=${f.uuid}">
                      ${fn:escapeXml(f.originalFilename)}
                    </a>
                  </c:if>
                </c:forEach>
                <c:if test="${hasDoc == false}">
                  <span class="text-muted">-</span>
                </c:if>
              </td>
            </tr>

            <!-- 계좌번호 -->
            <tr>
              <th>계좌번호</th>
              <td class="cell-old">
                <span class="mono">${fn:escapeXml(currentStudent.tuitionAccount)}</span>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty req.newAccountNo}">
                    <span class="badge bg-info text-dark me-1">변경</span>
                    <span class="mono">${fn:escapeXml(req.newAccountNo)}</span>
                  </c:when>
                  <c:otherwise><span class="text-muted">변경 없음</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:set var="hasDoc" value="false" />
                <c:forEach var="f" items="${files}">
                  <c:if test="${f.boardType == 'CHANGE_ACCOUNT'}">
                    <c:set var="hasDoc" value="true" />
                    <a class="btn btn-sm btn-outline-primary me-1 mb-1"
                       href="${ctx}/admin/files/download?uuid=${f.uuid}">
                      ${fn:escapeXml(f.originalFilename)}
                    </a>
                  </c:if>
                </c:forEach>
                <c:if test="${hasDoc == false}">
                  <span class="text-muted">-</span>
                </c:if>
              </td>
            </tr>

            <!-- 학과 -->
            <tr>
              <th>학과</th>
              <td class="cell-old">
                <c:choose>
                  <c:when test="${not empty currentDept}">
                    ${fn:escapeXml(currentDept.departmentName)}
                  </c:when>
                  <c:otherwise><span class="text-muted">-</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty req.newDepartmentId}">
                    <span class="badge bg-info text-dark me-1">변경</span>
                    학과ID: ${req.newDepartmentId}
                    <div class="help mt-1">※ 학과명 표시는 departmentId→departmentName 매핑 후 출력하면 됩니다.</div>
                  </c:when>
                  <c:otherwise><span class="text-muted">변경 없음</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:set var="hasDoc" value="false" />
                <c:forEach var="f" items="${files}">
                  <c:if test="${f.boardType == 'CHANGE_DEPARTMENT'}">
                    <c:set var="hasDoc" value="true" />
                    <a class="btn btn-sm btn-outline-primary me-1 mb-1"
                       href="${ctx}/admin/files/download?uuid=${f.uuid}">
                      ${fn:escapeXml(f.originalFilename)}
                    </a>
                  </c:if>
                </c:forEach>
                <c:if test="${hasDoc == false}">
                  <span class="text-muted">-</span>
                </c:if>
              </td>
            </tr>

            <!-- 학적 상태 -->
            <tr>
              <th>학적 상태</th>
              <td class="cell-old">
                <c:choose>
                  <c:when test="${currentStudent.studentStatus == 'ENROLLED'}">재학</c:when>
                  <c:when test="${currentStudent.studentStatus == 'BREAK'}">휴학</c:when>
                  <c:when test="${currentStudent.studentStatus == 'LEAVE'}">자퇴</c:when>
                  <c:when test="${currentStudent.studentStatus == 'GRADUATED'}">졸업</c:when>
                  <c:otherwise>${currentStudent.studentStatus}</c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty req.newAcademicStatus}">
                    <span class="badge bg-info text-dark me-1">변경</span>
                    <c:choose>
                      <c:when test="${req.newAcademicStatus == 'ENROLLED'}">재학</c:when>
                      <c:when test="${req.newAcademicStatus == 'BREAK'}">휴학</c:when>
                      <c:when test="${req.newAcademicStatus == 'LEAVE'}">자퇴</c:when>
                      <c:when test="${req.newAcademicStatus == 'GRADUATED'}">졸업</c:when>
                      <c:otherwise>${req.newAcademicStatus}</c:otherwise>
                    </c:choose>
                  </c:when>
                  <c:otherwise><span class="text-muted">변경 없음</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:set var="hasDoc" value="false" />
                <c:forEach var="f" items="${files}">
                  <c:if test="${f.boardType == 'CHANGE_ACASTATUS'}">
                    <c:set var="hasDoc" value="true" />
                    <a class="btn btn-sm btn-outline-primary me-1 mb-1"
                       href="${ctx}/admin/files/download?uuid=${f.uuid}">
                      ${fn:escapeXml(f.originalFilename)}
                    </a>
                  </c:if>
                </c:forEach>
                <c:if test="${hasDoc == false}">
                  <span class="text-muted">-</span>
                </c:if>
              </td>
            </tr>

          </tbody>
        </table>
      </div>
    </div>

    <!-- 사유 -->
    <div class="card-body">
      <div class="fw-semibold mb-1">학생 신청 사유</div>
      <div class="text-muted" style="white-space: pre-wrap;">
        <c:choose>
          <c:when test="${not empty req.reason}">
            ${fn:escapeXml(req.reason)}
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- 하단 액션 -->
    <div class="card-body sticky-actions">
      <div class="d-flex flex-wrap gap-2 align-items-center justify-content-between">
        <div class="text-muted small">
          승인 시: user/student 테이블에 변경사항이 반영됩니다.
        </div>

        <div class="d-flex flex-wrap gap-2">
          <!-- 승인 -->
          <form method="post" action="${ctx}/admin/studentInfoUpdateRequests/approve" class="m-0">
            <input type="hidden" name="requestId" value="${req.requestId}" />
            <button type="submit" class="btn btn-success">승인</button>
          </form>

          <!-- 반려 -->
          <form method="post" action="${ctx}/admin/studentInfoUpdateRequests/reject" class="m-0 d-flex gap-2">
            <input type="hidden" name="requestId" value="${req.requestId}" />
            <input type="text" name="rejectReason" class="form-control"
                   style="width: 360px;"
                   placeholder="반려 사유(필수)" required />
            <button type="submit" class="btn btn-danger">반려</button>
          </form>
        </div>
      </div>
    </div>

  </div>
</div>
