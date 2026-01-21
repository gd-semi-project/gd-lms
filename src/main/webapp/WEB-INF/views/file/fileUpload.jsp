<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form method="post" action="${pageContext.request.contextPath}/FileUpload" 
      enctype="multipart/form-data">
    
    <!-- 게시판 타입 / 게시글 ID -->
    <input type="hidden" name="boardType" value="${boardType}">
    <input type="hidden" name="refId" value="${refId}">

    <!-- 파일 선택 -->
    <label for="files">첨부파일 선택</label>
    <input type="file" name="files" id="files" multiple>

    <!-- 업로드 버튼 -->
    <button type="submit">첨부 업로드</button>
</form>