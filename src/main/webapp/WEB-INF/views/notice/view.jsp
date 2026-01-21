<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.AccessInfo}" />
<c:set var="role" value="${requestScope.role}" />

<%-- 목록 복귀용 파라미터 --%>
<c:set var="backTabType" value="${param.tabType}" />
<c:set var="backPage" value="${param.page}" />
<c:set var="backSize" value="${param.size}" />
<c:set var="backItems" value="${param.items}" />
<c:set var="backText" value="${param.text}" />

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
                            <span>📅 작성일: 
                                <c:if test="${not empty notice.createdAt}">
                                    ${fn:substring(notice.createdAt.toString(), 0, 10)} ${fn:substring(notice.createdAt.toString(), 11, 16)}
                                </c:if>
                            </span>
                            <span class="mx-2">|</span>
                            <span>👁️ 조회수: ${notice.viewCount}</span>
                            <c:if test="${not empty notice.updatedAt && notice.updatedAt != notice.createdAt}">
                                <span class="mx-2">|</span>
                                <span>✏️ 수정일: 
                                    ${fn:substring(notice.updatedAt.toString(), 0, 10)} ${fn:substring(notice.updatedAt.toString(), 11, 16)}
                                </span>
                            </c:if>
                        </div>
                    </div>

                    <hr>

                    <!-- 공지 내용 -->
                    <div class="notice-content my-4"><c:out value="${notice.content}" /></div>

                    <hr>

                    <!-- 버튼 영역 -->
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <%-- 목록 복귀 URL --%>
                            <c:url var="listUrl" value="/notice/list">
                                <c:choose>
                                    <c:when test="${not empty backTabType}">
                                        <c:param name="tabType" value="${backTabType}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:param name="tabType" value="${empty notice.lectureId ? 'all' : 'lecture'}" />
                                    </c:otherwise>
                                </c:choose>
                                <c:if test="${not empty notice.lectureId}">
                                    <c:param name="lectureId" value="${notice.lectureId}" />
                                </c:if>
                                <c:if test="${not empty backPage}">
                                    <c:param name="page" value="${backPage}" />
                                </c:if>
                                <c:if test="${not empty backSize}">
                                    <c:param name="size" value="${backSize}" />
                                </c:if>
                                <c:if test="${not empty backItems}">
                                    <c:param name="items" value="${backItems}" />
                                </c:if>
                                <c:if test="${not empty backText}">
                                    <c:param name="text" value="${backText}" />
                                </c:if>
                            </c:url>
                            <a href="${listUrl}" class="btn btn-secondary">📋 목록으로</a>
                        </div>
                        
                        <!-- 수정/삭제 버튼 (권한 있는 경우만 표시) -->
                        <c:if test="${role == 'ADMIN' || (role == 'INSTRUCTOR' && notice.authorId == user.userId)}">
                            <div>
                                <c:url var="editUrl" value="/notice/edit">
                                    <c:param name="noticeId" value="${notice.noticeId}" />
                                    <c:if test="${not empty notice.lectureId}">
                                        <c:param name="lectureId" value="${notice.lectureId}" />
                                    </c:if>
                                </c:url>
                                <a href="${editUrl}" class="btn btn-warning">✏️ 수정</a>
                                
                                <form action="${ctx}/notice/delete" method="post" class="d-inline">
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

<style>
    .notice-content {
        min-height: 200px;
        padding: 20px;
        background-color: #f8f9fa;
        border-radius: 5px;
        line-height: 1.8;
        white-space: pre-wrap;
    }
    .card-header h4 { font-weight: 600; }
    .badge { font-size: 0.9rem; padding: 0.4rem 0.8rem; }
</style>
