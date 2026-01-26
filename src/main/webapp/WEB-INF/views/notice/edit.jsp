<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header bg-warning text-dark">
                    <h4 class="mb-0">✏️ 공지사항 수정</h4>
                </div>
                <div class="card-body">
                    <form action="${ctx}/notice/update" method="post" onsubmit="return validateForm()">
                        
                        <!-- Hidden Fields -->
                        <input type="hidden" name="noticeId" value="${notice.noticeId}">
                        <c:if test="${notice.noticeType == 'LECTURE'}">
                            <input type="hidden" name="lectureId" value="${notice.lectureId}">
                        </c:if>

                        <!-- 공지 타입 -->
							<div class="mb-3">
							    <label class="form-label">공지 분류</label>
							    <div class="form-control-plaintext">
							        <c:choose>
							            <c:when test="${notice.noticeType == 'ANNOUNCEMENT'}">📢 전체 공지</c:when>
							            <c:when test="${notice.noticeType == 'LECTURE'}">📚 강의 공지</c:when>
							        </c:choose>
							    </div>
							
							    <!-- 서버 전송용 (변경 불가 값) -->
							    <input type="hidden" name="noticeType" value="${notice.noticeType}" />
							</div>

                        <!-- 공지 대상 표시 (읽기 전용) -->
                        <div class="mb-3">
                            <label class="form-label">공지 대상</label>
                            <div class="form-control-plaintext">
                                <c:choose>
                                    <c:when test="${empty notice.lectureId}">
                                        <span class="badge bg-danger">전체 공지 (모든 사용자)</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-info">강의 공지: ${notice.lectureTitle} (강의 ID: ${notice.lectureId})</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <small class="form-text text-muted">
                                공지 분류 및 공지 대상은 변경 불가합니다. 새로 작성해주세요.
                            </small>
                        </div>

                        <!-- 제목 -->
                        <div class="mb-3">
                            <label for="title" class="form-label">제목 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="title" name="title" 
                                   required maxlength="200" value="<c:out value='${notice.title}' />">
                        </div>

                        <!-- 내용 -->
                        <div class="mb-4">
                            <label for="content" class="form-label">내용 <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="content" name="content" 
                                      rows="15" required><c:out value="${notice.content}" /></textarea>
                            <small class="form-text text-muted">
                                최대 5,000자까지 입력 가능합니다.
                            </small>
                        </div>

                        <!-- 버튼 -->
                        <div class="d-flex justify-content-between">
                        <c:url var="cancelUrl" value="/notice/view">
						  <c:param name="noticeId" value="${notice.noticeId}" />
						  <c:if test="${not empty notice.lectureId}">
						    <c:param name="lectureId" value="${notice.lectureId}" />
						  </c:if>
						</c:url>
                       <button type="button" class="btn btn-secondary" onclick="location.href='${cancelUrl}'">
						  취소
					   </button>
                            <div>
                                <button type="submit" class="btn btn-warning">✅ 수정 완료</button>
                                <button type="button" class="btn btn-danger ms-2" 
                                        onclick="if(confirm('정말 삭제하시겠습니까?')) { document.getElementById('deleteForm').submit(); }">
                                    🗑️ 삭제
                                </button>
                            </div>
                        </div>
                    </form>

                    <!-- 삭제 폼 (별도) -->
                    <form id="deleteForm" action="${ctx}/notice/delete" method="post" style="display: none;">
                        <input type="hidden" name="noticeId" value="${notice.noticeId}">
                        <c:if test="${not empty notice.lectureId}">
                            <input type="hidden" name="lectureId" value="${notice.lectureId}">
                        </c:if>
                    </form>
                </div>
            </div>

            <!-- 수정 이력 정보 -->
            <div class="card mt-3">
                <div class="card-body">
                    <small class="text-muted">
                        📅 최초 작성: 
                        <c:set var="createdStr" value="${notice.createdAt.toString()}" />
                        ${fn:substring(createdStr, 0, 10)} ${fn:substring(createdStr, 11, 16)}
                        
                        <c:if test="${not empty notice.updatedAt && notice.updatedAt != notice.createdAt}">
                            | ✏️ 마지막 수정: 
                            <c:set var="updatedStr" value="${notice.updatedAt.toString()}" />
                            ${fn:substring(updatedStr, 0, 10)} ${fn:substring(updatedStr, 11, 16)}
                        </c:if>
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function validateForm() {
    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();

    if (title.length < 2) {
        alert('제목은 최소 2자 이상 입력해주세요.');
        return false;
    }

    if (content.length < 10) {
        alert('내용은 최소 10자 이상 입력해주세요.');
        return false;
    }

    if (content.length > 5000) {
        alert('내용은 최대 5,000자까지 입력 가능합니다.');
        return false;
    }

    return confirm('공지사항을 수정하시겠습니까?');
}
</script>

<style>
    .form-label {
        font-weight: 600;
        color: #333;
    }
    .text-danger {
        font-weight: bold;
    }
    textarea {
        resize: vertical;
    }
    .badge {
        font-size: 0.9rem;
        padding: 0.4rem 0.8rem;
    }
</style>