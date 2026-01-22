<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="f" items="${fileList}">
  <div class="mb-2">s
    <a href="${ctx}/fileUpload/Down?fileId=${f.Uuid}" class="btn btn-sm btn-outline-secondary">
      <i class="bi bi-download"></i> <c:out value="${f.OriginalFilename}" />
    </a>
  </div>
</c:forEach>