<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<style>
  
  .req-table { table-layout: fixed; width: 100%; }
  .req-table th, .req-table td { vertical-align: top; }
  .req-table th { white-space: nowrap; }

  .cell-old {
    background: #f8f9fa;
    vertical-align: middle !important;
    padding: 1rem 0.75rem;
  }

  .cell-old .old-value{
    display: flex;
    align-items: center;
    min-height: 38px;
    line-height: 1.4;
  }

  .cell-new { vertical-align: middle !important; }

  .mono {
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  }

  .help {
    color: #6c757d;
    font-size: .85rem;
  }
</style>


<c:if test="${not empty errors}">
  <div class="alert alert-danger">
    <ul class="mb-0">
      <c:forEach var="e" items="${errors}">
        <li>${fn:escapeXml(e)}</li>
      </c:forEach>
    </ul>
  </div>
</c:if>


<div class="page-wrapper">

  <div class="container" style="max-width:1100px;">

    <div class="d-flex align-items-center justify-content-between mb-3">
      <div>
        <h3 class="mb-1">중요 정보 변경 신청</h3>
        <div class="text-muted small">
          변경 후 값을 입력하고, 항목별 증빙 서류를 첨부해 제출하세요. (관리자 승인 후 반영)
        </div>
      </div>
      <a class="btn btn-outline-secondary btn-sm" href="${ctx}/mypage">뒤로</a>
    </div>


    <form method="post" action="${ctx}/student/requestInfoUpdate" enctype="multipart/form-data">

      <div class="alert alert-warning py-2 small mb-3">
        <b>주의:</b> 허위 서류 제출/도용이 확인될 경우 신청이 반려되거나 제재될 수 있습니다.
      </div>

      <div class="card shadow-sm">
        <div class="card-body p-0">

          <div class="table-responsive">
            <table class="table mb-0 req-table">
              <thead class="table-light">
                <tr>
                  <th style="width:16%;">항목</th>
                  <th style="width:28%;">변경 전</th>
                  <th style="width:28%;">변경 후</th>
                  <th style="width:28%;">증빙 서류</th>
                </tr>
              </thead>

              <tbody>

                <!-- 이름 -->
                <tr>
                  <th>이름</th>
                  <td class="cell-old"><div class="old-value">${fn:escapeXml(mypage.user.name)}</div></td>
                  <td><input class="form-control" name="newName"></td>
                  <td>
                    <input type="file" class="form-control" name="docName">
                    <div class="help mt-1">개명 허가 서류</div>
                  </td>
                </tr>

                <!-- 성별 -->
                <tr>
                  <th>성별</th>
                  <td class="cell-old">
                    <div class="old-value">
                      <c:choose>
                        <c:when test="${mypage.user.gender=='M'}">남</c:when>
                        <c:when test="${mypage.user.gender=='F'}">여</c:when>
                        <c:otherwise>미지정</c:otherwise>
                      </c:choose>
                    </div>
                  </td>
                  <td>
                    <select class="form-select" name="newGender">
                      <option value="">변경 없음</option>
                      <option value="M">남</option>
                      <option value="F">여</option>
                    </select>
                  </td>
                  <td><input type="file" class="form-control" name="docGender"></td>
                </tr>

                <!-- 계좌 -->
                <tr>
                  <th>계좌번호</th>
                  <td class="cell-old"><div class="old-value mono">${mypage.student.tuitionAccount}</div></td>
                  <td><input class="form-control" name="newAccountNo"></td>
                  <td><input type="file" class="form-control" name="docAccountNo"></td>
                </tr>

                <!-- 학과 -->
                <tr>
                  <th>학과</th>
                  <td class="cell-old"><div class="old-value">${mypage.department.departmentName}</div></td>
                  <td>
                    <select class="form-select" name="newDepartmentId">
                      <option value="">변경 없음</option>
                      <c:forEach var="d" items="${departments}">
                        <option value="${d.departmentId}">${d.departmentName}</option>
                      </c:forEach>
                    </select>
                  </td>
                  <td><input type="file" class="form-control" name="docDepartment"></td>
                </tr>

                <!-- 학적 -->
                <tr>
                  <th>학적 상태</th>
                  <td class="cell-old"><div class="old-value">${mypage.student.studentStatus}</div></td>
                  <td>
                    <select class="form-select" name="newAcademicStatus">
                      <option value="">변경 없음</option>
                      <option value="ENROLLED">재학</option>
                      <option value="BREAK">휴학</option>
                      <option value="LEAVE">자퇴</option>
                    </select>
                  </td>
                  <td><input type="file" class="form-control" name="docAcademicStatus"></td>
                </tr>

              </tbody>
            </table>
          </div>
        </div>


        <div class="card-body">
          <textarea class="form-control mb-3" name="reason" rows="3" placeholder="신청 사유"></textarea>

          <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" required>
            <label class="form-check-label">위 내용이 사실입니다.</label>
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">변경 신청</button>
            <a class="btn btn-outline-secondary" href="${ctx}/mypage">취소</a>
          </div>
        </div>

      </div>
    </form>
  </div>
</div>