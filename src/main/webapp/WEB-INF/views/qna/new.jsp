<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<div class="container mt-4">
  <h3 class="mb-3">Q&A 질문 작성</h3>

  <form method="post" action="${ctx}/qna/create">
    <input type="hidden" name="lectureId" value="${lectureId}" />
    <input type="hidden" name="returnUrl" value="${param.returnUrl}" />

    <div class="mb-3">
      <label class="form-label">제목</label>
      <input class="form-control" type="text" name="title" maxlength="200" required />
    </div>

    <div class="mb-3">
      <label class="form-label">내용</label>
      <textarea class="form-control" name="content" rows="8" required></textarea>
    </div>

    <div class="form-check mb-3">
      <input class="form-check-input" type="checkbox" id="isPrivate" name="isPrivate" value="Y">
      <label class="form-check-label" for="isPrivate">비공개</label>
    </div>

    <button class="btn btn-primary" type="submit">등록</button>
    <a class="btn btn-secondary" href="${ctx}/lecture/view?lectureId=${lectureId}&tab=qna">취소</a>
  </form>
</div>
