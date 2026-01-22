<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach var="f" items="${fileList}">
  <div class="mb-2">
    <a href="${pageContext.request.contextPath}/fileUpload/down?filename=${f.uuid}" class="btn btn-sm btn-outline-secondary">
      <i class="bi bi-download"></i> <c:out value="${f.originalFilename}" />
    </a>
  </div>
</c:forEach>