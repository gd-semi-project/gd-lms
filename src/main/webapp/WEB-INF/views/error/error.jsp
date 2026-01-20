<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>에러 발생</h2>
<p>코드: ${errorCode}</p>
<p>메시지: ${errorMessage}</p>
<p>5초 후 메인 페이지로 이동합니다.</p>

<!-- 즉시 이동 버튼 -->
<form action="${pageContext.request.contextPath}/main" method="get">
    <button type="submit">메인페이지</button>
</form>

<!-- 자동 이동 -->
<meta http-equiv="refresh" content="5;url=${pageContext.request.contextPath}/main" />
