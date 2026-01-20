<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
    <div class="row">
        <div class="col-md-12">

            <!-- 제목 -->
            <h2 class="mb-4">
                <c:choose>
                    <c:when test="${tabType == 'all'}">📢 전체 공지사항</c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${empty lectureId}">📚 모든 강의 공지사항</c:when>
                            <c:otherwise>📚 강의 공지사항</c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </h2>

            <!-- ================== 탭 메뉴 ================== -->
            <ul class="nav nav-tabs mb-3">

                <!-- 전체 공지 -->
                <li class="nav-item">
                    <a class="nav-link ${tabType == 'all' ? 'active' : ''}"
                       href="${ctx}/notice/list?tabType=all">
                        📢 전체 공지사항
                    </a>
                </li>

                <!-- 강의 공지 (이동 전용 링크) -->
                <li class="nav-item">
                    <a class="nav-link ${tabType == 'lecture' ? 'active' : ''}"
                       href="${ctx}/notice/list?tabType=lecture">
                        📚 강의 공지사항
                    </a>
                </li>
            </ul>

            <!-- ================== 강의 선택 메뉴 (하위) ================== -->
			  <c:if test="${tabType == 'lecture'}">
			    <div class="mb-3" style="max-width: 400px;">
			        <select class="form-select"
			                onchange="location.href=this.value">
			
			            <option value="${ctx}/notice/list?tabType=lecture"
			                    ${empty lectureId ? 'selected' : ''}>
			                전체 강의
			            </option>
			
			            <c:forEach var="lecture" items="${userLectures}">
			                <option value="${ctx}/notice/list?tabType=lecture&lectureId=${lecture.lectureId}"
			                        ${lectureId == lecture.lectureId ? 'selected' : ''}>
			                    ${lecture.lectureTitle} (${lecture.lectureRound}차)
			                </option>
			            </c:forEach>
			
			        </select>
			    </div>
			</c:if>


            <!-- ================== 검색 폼 ================== -->
            <form method="get" action="${ctx}/notice/list" class="mb-4">
                <input type="hidden" name="tabType" value="${tabType}">
                <c:if test="${not empty lectureId}">
                    <input type="hidden" name="lectureId" value="${lectureId}">
                </c:if>

                <div class="row g-2">
                    <div class="col-auto">
                        <select name="items" class="form-select">
                            <option value="all" ${items == 'all' ? 'selected' : ''}>전체</option>
                            <option value="title" ${items == 'title' ? 'selected' : ''}>제목</option>
                            <option value="content" ${items == 'content' ? 'selected' : ''}>내용</option>
                        </select>
                    </div>
                    <div class="col">
                        <input type="text" name="text" value="${text}"
                               class="form-control" placeholder="검색어 입력">
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-primary">🔍 검색</button>
                    </div>
                </div>
            </form>

            <!-- ================== 작성 버튼 ================== -->
            <c:if test="${role == 'ADMIN' || role == 'INSTRUCTOR'}">
                <div class="text-end mb-3">
                    <a href="${ctx}/notice/new" class="btn btn-success">
                        ✏️ 새 공지 작성
                    </a>
                </div>
            </c:if>

            <!-- ================== 목록 ================== -->
            <c:choose>
                <c:when test="${empty noticeList}">
                    <div class="alert alert-info text-center">
                        📭 등록된 공지사항이 없습니다.
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th width="8%">번호</th>
                                    <th width="10%">분류</th>
                                    <th width="45%">제목</th>
                                    <th width="12%">작성자</th>
                                    <th width="10%">조회수</th>
                                    <th width="15%">작성일</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="notice" items="${noticeList}" varStatus="status">
                                    <tr>
                                        <td class="text-center">
                                            ${totalCount - ((page - 1) * size + status.index)}
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${empty notice.lectureId}">
                                                    <span class="badge bg-danger">전체</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-info">강의</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${ctx}/notice/view?noticeId=${notice.noticeId}${not empty notice.lectureId ? '&lectureId='.concat(notice.lectureId) : ''}"
                                               class="text-decoration-none">
                                                <c:out value="${notice.title}" />
                                            </a>
                                        </td>
                                        <td class="text-center">${notice.authorId}</td>
                                        <td class="text-center">${notice.viewCount}</td>
                                        <td class="text-center">
                                            <c:set var="dateStr" value="${notice.createdAt.toString()}" />
                                            ${fn:substring(dateStr, 0, 10)}
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- ================== 페이징 ================== -->
                    <nav>
                        <ul class="pagination justify-content-center">
                            <c:if test="${page > 1}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="${ctx}/notice/list?tabType=${tabType}&page=${page-1}&size=${size}${not empty lectureId ? '&lectureId='.concat(lectureId) : ''}">
                                        이전
                                    </a>
                                </li>
                            </c:if>

                            <c:forEach var="i"
                                       begin="${page - 2 < 1 ? 1 : page - 2}"
                                       end="${page + 2 > totalPages ? totalPages : page + 2}">
                                <li class="page-item ${i == page ? 'active' : ''}">
                                    <a class="page-link"
                                       href="${ctx}/notice/list?tabType=${tabType}&page=${i}&size=${size}${not empty lectureId ? '&lectureId='.concat(lectureId) : ''}">
                                        ${i}
                                    </a>
                                </li>
                            </c:forEach>

                            <c:if test="${page < totalPages}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="${ctx}/notice/list?tabType=${tabType}&page=${page+1}&size=${size}${not empty lectureId ? '&lectureId='.concat(lectureId) : ''}">
                                        다음
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>

                    <div class="text-center text-muted">
                        전체 ${totalCount}개 | ${page} / ${totalPages} 페이지
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>