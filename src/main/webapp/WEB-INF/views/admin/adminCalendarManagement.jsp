<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">학사일정 관리</h1>
    <div class="text-muted small">일정 추가/수정/삭제</div>
  </div>

  <div class="d-flex gap-2">
    <form class="d-flex gap-2" method="get" action="${pageContext.request.contextPath}/admin/calendar">
      <input type="month" class="form-control form-control-sm" name="ym"
             value="${empty param.ym ? currentYm : param.ym}">
      <button class="btn btn-sm btn-outline-secondary" type="submit">이동</button>
    </form>

    <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">
      + 일정 추가
    </button>
  </div>
</div>

<!-- 월 이동(선택) -->
<div class="d-flex justify-content-between align-items-center mb-3">
  <a class="btn btn-sm btn-outline-secondary"
     href="${pageContext.request.contextPath}/admin/calendar?ym=${prevYm}">← 이전달</a>

  <div class="fw-semibold fs-5">${displayMonth}</div>

  <a class="btn btn-sm btn-outline-secondary"
     href="${pageContext.request.contextPath}/admin/calendar?ym=${nextYm}">다음달 →</a>
</div>

<div class="row g-4">
  <!-- 왼쪽: 간단 월 캘린더(텍스트형) -->
  <div class="col-12 col-lg-7">
    <div class="card shadow-sm">
      <div class="card-header bg-white fw-bold">월 캘린더</div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-bordered mb-0 text-center align-middle">
            <thead class="table-light">
              <tr>
                <th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
              </tr>
            </thead>
            <tbody>
              <!--
                서버에서 weeks(주 단위)로 내려주는 방식 추천:
                weeks = List<List<DayCell>>
                DayCell: date(YYYY-MM-DD), dayNumber, inMonth(boolean), events(List<EventSummary>)
              -->
              <c:forEach var="week" items="${weeks}">
                <tr style="height: 92px;">
                  <c:forEach var="cell" items="${week}">
                    <td class="${cell.inMonth ? '' : 'table-light'}" style="vertical-align: top;">
                      <div class="d-flex justify-content-between">
                        <span class="fw-semibold">${cell.dayNumber}</span>
                        <c:if test="${cell.today}">
                          <span class="badge bg-primary">오늘</span>
                        </c:if>
                      </div>

                      <!-- 해당 날짜 일정 요약(최대 2개만 노출) -->
                      <div class="mt-1 text-start" style="font-size: 12px;">
                        <c:forEach var="e" items="${cell.events}" varStatus="st">
                          <c:if test="${st.index lt 2}">
                            <div class="text-truncate">
                              <span class="badge bg-secondary me-1">${e.type}</span>
                              ${fn:escapeXml(e.title)}
                            </div>
                          </c:if>
                        </c:forEach>
                        <c:if test="${fn:length(cell.events) gt 2}">
                          <div class="text-muted">+ ${fn:length(cell.events)-2}개</div>
                        </c:if>
                      </div>

                    </td>
                  </c:forEach>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
        <div class="p-3 text-muted small">
          * 캘린더는 “월별 시각화용”. 실제 편집은 오른쪽 목록에서 진행합니다.
        </div>
      </div>
    </div>
  </div>

  <!-- 오른쪽: 일정 목록 -->
  <div class="col-12 col-lg-5">
    <div class="card shadow-sm">
      <div class="card-header bg-white fw-bold d-flex justify-content-between align-items-center">
        <span>이번 달 일정 목록</span>
        <span class="text-muted small">총 ${empty monthCount ? 0 : monthCount}건</span>
      </div>

      <div class="card-body p-0">
        <div class="list-group list-group-flush">
          <c:choose>
            <c:when test="${empty eventList}">
              <div class="p-4 text-center text-muted">이번 달 일정이 없습니다.</div>
            </c:when>
            <c:otherwise>
              <c:forEach var="e" items="${eventList}">
                <div class="list-group-item">
                  <div class="d-flex justify-content-between align-items-start gap-2">
                    <div class="flex-grow-1">
                      <div class="d-flex flex-wrap gap-2 align-items-center mb-1">
                        <span class="badge bg-secondary">${e.type}</span>
                        <span class="fw-semibold">${fn:escapeXml(e.title)}</span>
<%--                         <c:if test="${e.public}">
                          <span class="badge bg-success">공개</span>
                        </c:if>
                        <c:if test="${not e.public}">
                          <span class="badge bg-dark">비공개</span>
                        </c:if> --%>
                      </div>
                      <div class="text-muted small">
                        ${e.startDate} <c:if test="${e.endDate ne e.startDate}">~ ${e.endDate}</c:if>
                      </div>
                      <c:if test="${not empty e.memo}">
                        <div class="small mt-1" style="white-space: pre-wrap;">
                          ${fn:escapeXml(e.memo)}
                        </div>
                      </c:if>
                    </div>

                    <div class="d-flex flex-column gap-2">
                      <a class="btn btn-sm btn-outline-primary"
                         href="${pageContext.request.contextPath}/admin/calendar/edit?id=${e.id}">
                        수정
                      </a>

                      <form class="m-0" method="post"
                            action="${pageContext.request.contextPath}/admin/calendar/delete"
                            onsubmit="return confirm('삭제하시겠습니까?');">
                        <input type="hidden" name="id" value="${e.id}">
                        <button class="btn btn-sm btn-outline-danger" type="submit">삭제</button>
                      </form>
                    </div>
                  </div>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- 일정 추가 Modal -->
<div class="modal fade" id="createModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-scrollable">
    <div class="modal-content">
      <form method="post" action="${pageContext.request.contextPath}/admin/calendar/insert">
        <div class="modal-header">
          <h5 class="modal-title">일정 추가</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>

        <div class="modal-body">
          <div class="row g-3">
            <div class="col-12">
              <label class="form-label">제목 <span class="text-danger">*</span></label>
              <input type="text" class="form-control" name="title" maxlength="100" required>
            </div>

            <div class="col-12 col-md-6">
              <label class="form-label">시작일 <span class="text-danger">*</span></label>
              <input type="date" class="form-control" name="startDate" required>
            </div>

            <div class="col-12 col-md-6">
              <label class="form-label">종료일</label>
              <input type="date" class="form-control" name="endDate">
              <div class="form-text">비우면 시작일과 동일로 처리</div>
            </div>

            <div class="col-12 col-md-6">
              <label class="form-label">유형</label>
              <select class="form-select" name="type">
                <option value="학사">학사</option>
                <option value="시험">시험</option>
                <option value="휴강">휴강</option>
                <option value="행사">행사</option>
                <option value="기타">기타</option>
              </select>
            </div>

            <div class="col-12 col-md-6">
              <label class="form-label">공개 여부</label>
              <select class="form-select" name="publicFlag">
                <option value="Y">공개</option>
                <option value="N">비공개</option>
              </select>
            </div>

            <div class="col-12">
              <label class="form-label">메모</label>
              <textarea class="form-control" name="memo" rows="4"></textarea>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
          <button type="submit" class="btn btn-primary">등록</button>
        </div>
      </form>
    </div>
  </div>
</div>
