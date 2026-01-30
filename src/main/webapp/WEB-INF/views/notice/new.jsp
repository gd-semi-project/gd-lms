<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.AccessInfo}" />
<c:set var="role" value="${requestScope.role}" />

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">✏️ 새 공지사항 작성</h4>
                </div>
                <div class="card-body">
                    <form action="${ctx}/notice/create" method="post">
                        
                        <%-- ========== 관리자: 전체공지 + 모든 강의 선택 가능 ========== --%>
                        <c:if test="${role == 'ADMIN'}">
                            <div class="mb-3">
                                <label for="noticeType" class="form-label">공지 분류 <span class="text-danger">*</span></label>
                                <select class="form-select" id="noticeType" name="noticeType" required>
                                    <option value="">-- 선택하세요 --</option>
                                    <option value="ANNOUNCEMENT">📢 전체 공지</option>
                                    <option value="LECTURE">📚 강의 공지</option>
                                </select>
                            </div>

							 <div class="mb-3">
							  <label for="lectureId" class="form-label">강의 선택 (강의 공지 시 필수)</label>
							  <select class="form-select" id="lectureId" name="lectureId">
							      <option value="">-- 전체 공지 (강의 선택 안함) --</option>
							      <c:forEach var="lec" items="${lectureList}">
							          <option value="${lec.lectureId}">
							              ${lec.lectureTitle} (${lec.lectureRound}차)
							              <c:if test="${not empty lec.section}"> - ${lec.section}분반</c:if>
							          </option>
							      </c:forEach>
							  </select>
							
							  <small id="lectureHelp" class="form-text text-muted">
							      강의 공지 선택 시 반드시 강의를 선택해주세요.
							  </small>
							</div>
                        </c:if>

                        <%-- ========== 교수: 본인 강의만 선택 가능 ========== --%>
                        <c:if test="${role == 'INSTRUCTOR'}">
                            <input type="hidden" name="noticeType" value="LECTURE" />
                            
                            <c:choose>
                                <c:when test="${not empty lectureList}">
                                    <div class="mb-3">
                                        <label for="lectureId" class="form-label">강의 선택 <span class="text-danger">*</span></label>
                                        <select class="form-select" id="lectureId" name="lectureId" required>
                                            <option value="">-- 강의를 선택하세요 --</option>
                                            <c:forEach var="lec" items="${lectureList}">
                                                <option value="${lec.lectureId}">
                                                    ${lec.lectureTitle} (${lec.lectureRound}차)
                                                    <c:if test="${not empty lec.section}"> - ${lec.section}분반</c:if>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <small class="form-text text-muted">
                                            본인이 담당하는 강의에만 공지를 작성할 수 있습니다.
                                        </small>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-warning">
                                        ⚠️ 담당하고 있는 강의가 없습니다. 강의가 배정된 후 공지를 작성할 수 있습니다.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:if>

                        <!-- 제목 -->
                        <div class="mb-3">
                            <label for="title" class="form-label">제목 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="title" name="title" 
                                   required minlength="2" maxlength="200" placeholder="공지사항 제목을 입력하세요">
                        </div>

                        <!-- 내용 -->
                        <div class="mb-4">
                            <label for="content" class="form-label">내용 <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="content" name="content" 
                                      rows="15" required minlength="10" maxlength="5000" 
                                      placeholder="공지사항 내용을 입력하세요"></textarea>
                            <small class="form-text text-muted">
                                최대 5,000자까지 입력 가능합니다.
                            </small>
                        </div>

                        <!-- 버튼 -->
                        <div class="d-flex justify-content-between">
                            <a href="${ctx}/notice/list" class="btn btn-secondary">취소</a>
                            <c:choose>
                                <c:when test="${role == 'INSTRUCTOR' && empty lectureList}">
                                    <button type="button" class="btn btn-secondary" disabled>✅ 작성 완료</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn btn-success">✅ 작성 완료</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .form-label { font-weight: 600; color: #333; }
    .text-danger { font-weight: bold; }
    textarea { resize: vertical; }
</style>
