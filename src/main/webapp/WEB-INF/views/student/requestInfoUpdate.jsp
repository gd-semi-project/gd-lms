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
                  <th style="width:28%;">증빙 서류 / 제출 안내</th>
                </tr>
              </thead>

              <tbody>

                <!-- 이름 -->
                <tr>
                  <th>이름</th>
                  <td class="cell-old"><div class="old-value">${fn:escapeXml(mypage.user.name)}</div></td>
                  <td><input type="text" class="form-control" name="newName"
                         placeholder="변경 후 이름(개명 등)"
                         value="${fn:escapeXml(param.newName)}" />
</td>
                  <td>
                    <input type="file" class="form-control" name="docName">
                     <div class="help mt-1">개명 허가/주민등록초본(변경 이력 포함)</div>

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
                      <option value="M" ${param.newGender=='M' ? 'selected' : ''}>남</option>
                    <option value="F" ${param.newGender=='F' ? 'selected' : ''}>여</option>

                    </select>
                  </td>
                  <td><input type="file" class="form-control" name="docGender"></td>
                </tr>

                <!-- 계좌 -->
                <tr>
                  <th>계좌번호</th>
                  <td class="cell-old"><div class="old-value mono">${mypage.student.tuitionAccount}</div></td>
                  <td><input type="text" class="form-control" name="newAccountNo"
                         placeholder="변경 후 계좌번호"
                         value="${fn:escapeXml(param.newAccountNo)}" /></td>
                  <td> <input type="file" class="form-control" name="docAccountNo" />
                  <div class="help mt-1">통장 사본(예금주/계좌번호 보이게)</div>
</td>
                </tr>

                <!-- 학과 -->
                              <tr>
                <th class="align-middle">학과</th>
                <td class="cell-old">
                <div class="old-value">
                  ${fn:escapeXml(mypage.department.departmentName)}
                </div>
                </td>
                <td class="cell-new">
                  <!-- departments: 학과 목록 -->
                  <select class="form-select" name="newDepartmentId">
                    <option value="">변경 없음</option>
                    <c:forEach var="d" items="${departments}">
                      <option value="${d.departmentId}"
                              ${param.newDepartmentId == d.departmentId ? 'selected' : ''}>
                        ${fn:escapeXml(d.departmentName)}
                      </option>
                    </c:forEach>
                  </select>
                  <div class="help mt-1">전과/전공 변경은 규정에 따라 승인됩니다.</div>
                </td>
                <td>
				  <input type="file" class="form-control" name="docDepartment" />
				  <div class="help mt-1">전과 승인서/학과 변경 신청서(서명 포함)</div>
				
				  <div class="mt-2">
				    <a class="btn btn-sm btn-outline-primary"
				       href="${ctx}/resources/forms/department-change-form.pdf"
				       download>
				      신청서 양식
				    </a>
				  </div>
				</td>

              </tr>

                <!-- 학적 -->
                             <tr>
                <th class="align-middle">학적 상태</th>
                <td class="cell-old">
                <div class="old-value">
                  <c:choose>
                    <c:when test="${mypage.student.studentStatus == 'ENROLLED'}">재학</c:when>
                    <c:when test="${mypage.student.studentStatus == 'BREAK'}">휴학</c:when>
                    <c:when test="${mypage.student.studentStatus == 'LEAVE'}">자퇴</c:when>
                    <c:when test="${mypage.student.studentStatus == 'GRADUATED'}">졸업</c:when>
                  </c:choose>
                </div>
                </td>
                <td class="cell-new">
                  <select class="form-select" name="newAcademicStatus">
                    <option value="">변경 없음</option>
                    <option value="ENROLLED" ${param.newAcademicStatus=='ENROLLED' ? 'selected' : ''}>재학</option>
                    <option value="BREAK" ${param.newAcademicStatus=='BREAK' ? 'selected' : ''}>휴학</option>
                    <option value="LEAVE" ${param.newAcademicStatus=='LEAVE' ? 'selected' : ''}>자퇴</option>
                  </select>
                  <div class="help mt-1">학적 상태 변경은 승인 후 반영됩니다.</div>
                </td>
                <td>
				  <input type="file" class="form-control" name="docAcademicStatus" />
				  <div class="help mt-1">휴학/자퇴 신청서, 관련 증빙(해당 시)</div>
				
				  <div class="mt-2">
				    <a class="btn btn-sm btn-outline-primary"
				       href="${ctx}/resources/forms/academic-status-change-form.pdf"
				       download>
				      신청서 양식
				    </a>
				  </div>
				</td>
              </tr>

            </tbody>
          </table>
        </div>

      </div>


        <div class="card-body">
          <textarea class="form-control mb-3" id="reason" name="reason" rows="3" placeholder="변경 사유/추가 설명을 적어주세요.">${fn:escapeXml(param.reason)}</textarea>

          <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" value="Y" id="agree" name="agree" required>
            <label class="form-check-label" for="agree">위 내용이 사실이며, 증빙 서류를 제출했습니다.
</label>
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">변경 신청 제출</button>
            <a class="btn btn-outline-secondary" href="${ctx}/mypage">취소</a>
          </div>
        </div>

      </div>
    </form>
  </div>
</div>