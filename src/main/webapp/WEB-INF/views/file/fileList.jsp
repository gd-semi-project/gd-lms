<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="f" items="${fileList}">

  <c:url var="downUrl" value="/fileUpload/down">
    <c:param name="filename" value="${f.uuid}" />
    <c:if test="${not empty f.boardType}">
      <c:param name="boardType" value="${f.boardType}" />
    </c:if>
  </c:url>
  <c:choose>
    <c:when test="${showIconsOnly}">
      <a href="${downUrl}" style="text-decoration: none;">
        <span title="${f.originalFilename}" style="font-size:1.2em; padding:0;">
          ${f.extenderIco}
        </span>
      </a>
    </c:when>
    <c:otherwise>
      <div class="mb-2">
        <a href="${downUrl}" class="btn btn-sm btn-outline-secondary">
          <span style="margin-right:4px;">${f.extenderIco}</span>
          <c:out value="${f.originalFilename}" />
        </a>
      </div>
    </c:otherwise>
  </c:choose>
</c:forEach>