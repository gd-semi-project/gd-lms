<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<style>
  tr.lecture-row {
    position: relative; /* stretched-link 필수 */
  }

  tr.lecture-row:hover {
    background: rgba(13,110,253,.06);
  }
</style>


<div class="container-fluid my-4">

      <!-- =======================
           헤더 + 검색
      ======================= -->
      <div class="d-flex align-items-center mb-4">
        <div class="d-flex gap-2">
          <input id="lectureQ" class="form-control form-control-sm"
                 placeholder="강의명/강사명/강의실 검색" style="width: 240px;">
          <button class="btn btn-sm btn-primary" type="button" id="lectureQBtn">검색</button>
        </div>
      <div class="d-flex gap-2 ms-3">
        <span class="badge bg-secondary">
          전체: ${empty lectureList ? 0 : fn:length(lectureList)}
        </span>

        <!-- 필요하면 아래 배지 추가: 예) 정원합계/수강합계 등 -->
        <c:if test="${not empty capacitySum}">
          <span class="badge bg-dark">정원 합계: ${capacitySum}</span>
        </c:if>
        <c:if test="${not empty currentSum}">
          <span class="badge bg-info text-dark">현재 인원 합계: ${currentSum}</span>
        </c:if>
      </div>
      </div>


      <div class="card shadow-sm">
        <div class="card-header bg-white d-flex flex-wrap gap-2 align-items-center justify-content-between">
          <div class="fw-bold">강의 목록</div>
			<c:set var="lectureStatus" value="${empty param.lectureStatus ? 'ALL' : param.lectureStatus}" />
			
			<div class="btn-group btn-group-sm" role="group" aria-label="status">
			
			  <a class="btn ${lectureStatus=='ALL' ? 'btn-success active' : 'btn-outline-secondary'}"
			     href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=${currentStatus}">
			    ALL
			  </a>
			
			  <a class="btn ${lectureStatus=='ONGOING' ? 'btn-success active' : 'btn-outline-secondary'}"
			     href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=${currentStatus}&lectureStatus=ONGOING">
			    ONGOING
			  </a>
			
			  <a class="btn ${lectureStatus=='PLANNED' ? 'btn-success active' : 'btn-outline-secondary'}"
			     href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=${currentStatus}&lectureStatus=PLANNED">
			    PLANNED
			  </a>
			
			  <a class="btn ${lectureStatus=='ENDED' ? 'btn-success active' : 'btn-outline-secondary'}"
			     href="${pageContext.request.contextPath}/admin/departmentManage?departmentId=${param.departmentId}&status=${currentStatus}&lectureStatus=ENDED">
			    ENDED
			  </a>
			</div>

        </div>

        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th style="width: 22%;">강의명</th>
                  <th style="width: 7%;">분반</th>
                  <th style="width: 12%;">강사</th>
                  <th style="width: 16%;">시간표</th>
                  <th style="width: 16%;">인원/정원</th>
                  <th style="width: 10%;">강의실</th>
                  <th style="width: 12%;">기간</th>
                  <th style="width: 5%;">상태</th>
                </tr>
              </thead>

              <tbody id="lectureTbody">
                <c:choose>
                  <c:when test="${empty param.departmentId}">
                    <tr>
                      <td colspan="8" class="text-center text-muted py-5">
                        학과를 선택하면 강의 목록이 표시됩니다.
                      </td>
                    </tr>
                  </c:when>

                  <c:when test="${empty lectureList}">
                    <tr>
                      <td colspan="8" class="text-center text-muted py-5">
                        표시할 강의가 없습니다.
                      </td>
                    </tr>
                  </c:when>

                  <c:otherwise>
                    <c:forEach var="l" items="${lectureList}">

                      <tr class="lecture-row">
                        <td>
                          <div class="fw-semibold">${fn:escapeXml(l.lectureTitle)}
                              <a 
	                              class="stretched-link"
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
                          <!-- instructorName을 DTO에 넣는 형태를 추천 -->
                          <div>${fn:escapeXml(l.instructorName)}</div>
                          <div class="text-muted small">강사ID: ${l.userId}</div>
                        </td>

                        <td>
						  <div class="text-muted small">
						    <c:choose>
						      <c:when test="${empty l.schedules}">
						        -
						      </c:when>
						      <c:otherwise>
						        <c:forEach var="s" items="${l.schedules}" varStatus="st">
						          ${s.weekDay} ${s.startTime}~${s.endTime}<c:if test="${not st.last}"></c:if>
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
						    <span class="${cap > 0 && cur >= cap ? 'text-danger' : ''}">

						      ${cur}
						    </span>
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
           검색 JS (클라 필터)
      ======================= -->
      <script>
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
