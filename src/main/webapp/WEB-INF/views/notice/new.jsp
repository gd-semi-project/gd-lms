<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">✏️ 새 공지사항 작성</h4>
                </div>
                <div class="card-body">
                    <form action="${ctx}/notice/create" method="post" onsubmit="return validateForm()">
                        
                        <!-- 공지 타입 선택 -->
                        <div class="mb-3">
                            <label for="noticeType" class="form-label">공지 분류 <span class="text-danger">*</span></label>
                            <select class="form-select" id="noticeType" name="noticeType" required>
                                <option value="">-- 선택하세요 --</option>
                                <option value="ANNOUNCEMENT">📢 전체 공지</option>
                                <option value="LECTURE">📚 강의 공지</option>
                            </select>
                        </div>

                        <!-- 강의 선택 (관리자만 선택 가능, 교수는 파라미터로 받음) -->
                        <c:if test="${role == 'ADMIN'}">
                            <div class="mb-3">
                                <label for="lectureId" class="form-label">공지 대상</label>
                                <select class="form-select" id="lectureId" name="lectureId">
                                    <option value="">전체 공지 (모든 사용자)</option>
									 	<c:forEach var="lec" items="${lectureList}">
									        <option value="${lec.lectureId}">
									            ${lec.lectureTitle}
									        </option>
									    </c:forEach>
                                </select>
                                <small class="form-text text-muted">
                                    전체 공지는 모든 사용자에게 표시되며, 강의 선택 시 해당 강의 수강생에게만 표시됩니다.
                                </small>
                            </div>
                        </c:if>

                        <!-- 교수인 경우 lectureId는 hidden으로 전달 -->
                        <c:if test="${role == 'INSTRUCTOR' && not empty lectureId}">
                            <input type="hidden" name="lectureId" value="${lectureId}">
                            <div class="alert alert-info">
                                📚 이 공지는 선택된 강의의 수강생에게만 표시됩니다.
                            </div>
                        </c:if>

                        <!-- 제목 -->
                        <div class="mb-3">
                            <label for="title" class="form-label">제목 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="title" name="title" 
                                   required maxlength="200" placeholder="공지사항 제목을 입력하세요">
                        </div>

                        <!-- 내용 -->
                        <div class="mb-4">
                            <label for="content" class="form-label">내용 <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="content" name="content" 
                                      rows="15" required placeholder="공지사항 내용을 입력하세요"></textarea>
                            <small class="form-text text-muted">
                                최대 5,000자까지 입력 가능합니다.
                            </small>
                        </div>

                        <!-- 버튼 -->
                        <div class="d-flex justify-content-between">
                            <button type="button" class="btn btn-secondary" 
                                    onclick="history.back()">취소</button>
                            <button type="submit" class="btn btn-success">✅ 작성 완료</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function validateForm() {
    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();
    const noticeType = document.getElementById('noticeType').value;

    if (!noticeType) {
        alert('공지 분류를 선택해주세요.');
        return false;
    }

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

    return confirm('공지사항을 작성하시겠습니까?');
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
</style>