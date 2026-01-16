<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">📢 공지사항</h4>
                </div>
                <div class="card-body">
                    <!-- 공지 정보 헤더 -->
                    <div class="mb-4">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <h3 class="mb-0"><c:out value="${notice.title}" /></h3>
                            <div>
                                <c:choose>
                                    <c:when test="${empty notice.lectureId}">
                                        <span class="badge bg-danger">전체 공지</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-info">강의 공지</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="text-muted small">
                            <span>👤 작성자: ${notice.authorId}</span>
                            <span class="mx-2">|</span>
                            <span>📅 작성일: 
                                <c:set var="createdStr" value="${notice.createdAt.toString()}" />
                                ${fn:substring(createdStr, 0, 10)} ${fn:substring(createdStr, 11, 16)}
                            </span>
                            <span class="mx-2">|</span>
                            <span>👁️ 조회수: ${notice.viewCount}</span>
                            <c:if test="${not empty notice.updatedAt && notice.updatedAt != notice.createdAt}">
                                <span class="mx-2">|</span>
                                <span>✏️ 수정일: 
                                    <c:set var="updatedStr" value="${notice.updatedAt.toString()}" />
                                    ${fn:substring(updatedStr, 0, 10)} ${fn:substring(updatedStr, 11, 16)}
                                </span>
                            </c:if>
                        </div>
                    </div>

                    <hr>

                    <!-- 공지 내용 -->
                    <div class="notice-content my-4">
                        <%-- 줄바꿈을 <br>로 변환 --%>
                        <c:set var="contentWithBr" value="${fn:replace(notice.content, newLineChar, '<br>')}" />
                        ${contentWithBr}
                    </div>

                    <hr>

                    <!-- 버튼 영역 -->
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <a href="${ctx}/notice/list${not empty notice.lectureId ? '?lectureId='.concat(notice.lectureId) : ''}" 
                               class="btn btn-secondary">📋 목록으로</a>
                        </div>
                        
                        <!-- 수정/삭제 버튼 (권한 있는 경우만 표시) -->
                        <c:if test="${role == 'ADMIN' || (role == 'INSTRUCTOR' && notice.authorId == userId)}">
                            <div>
                                <a href="${ctx}/notice/edit?noticeId=${notice.noticeId}${not empty notice.lectureId ? '&lectureId='.concat(notice.lectureId) : ''}" 
                                   class="btn btn-warning">✏️ 수정</a>
                                
                                <form action="${ctx}/notice/delete" method="post" class="d-inline"
                                      onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                    <input type="hidden" name="noticeId" value="${notice.noticeId}">
                                    <c:if test="${not empty notice.lectureId}">
                                        <input type="hidden" name="lectureId" value="${notice.lectureId}">
                                    </c:if>
                                    <button type="submit" class="btn btn-danger">🗑️ 삭제</button>
                                </form>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<c:set var="newLineChar" value="
" />

<style>
    .notice-content {
        min-height: 200px;
        padding: 20px;
        background-color: #f8f9fa;
        border-radius: 5px;
        line-height: 1.8;
    }
    .card-header h4 {
        font-weight: 600;
    }
    .badge {
        font-size: 0.9rem;
        padding: 0.4rem 0.8rem;
    }
</style>