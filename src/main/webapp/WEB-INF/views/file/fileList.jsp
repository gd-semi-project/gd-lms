<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="f" items="${fileList}">
  <c:choose>
    <c:when test="${showIconsOnly}">
      <a href="${pageContext.request.contextPath}/fileUpload/down?filename=${f.uuid}" style="text-decoration: none;">
        <span title="${f.originalFilename}" style="font-size:1.2em; padding:0;">
          ${f.extenderIco}
        </span>
      </a>
    </c:when>
    <c:otherwise>
      <div class="mb-2">
        <a href="${pageContext.request.contextPath}/fileUpload/down?filename=${f.uuid}" class="btn btn-sm btn-outline-secondary">
          <span style="margin-right:4px;">${f.extenderIco}</span>
          <c:out value="${f.originalFilename}" />
        </a>
      </div>
    </c:otherwise>
  </c:choose>
</c:forEach>