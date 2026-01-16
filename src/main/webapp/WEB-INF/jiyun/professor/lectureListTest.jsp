<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <div>
    <h1 class="h3 mb-1">담당 강의 목록</h1>
    <div class="text-muted small">교수가 담당 중인 강의 목록입니다.</div>
  </div>
</div>

<c:if test="${empty lectures}">
  <div class="alert alert-warning">
    담당 중인 강의가 없습니다.
  </div>
</c:if>

<c:if test="${not empty lectures}">
  <div class="card shadow-sm">
    <div class="card-body p-0">
      <table class="table table-hover mb-0">
        <thead class="table-light">
          <tr>
            <th>강의명</th>
            <th>기수</th>
            <th>기간</th>
            <th>강의실</th>
            <th>정원</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="lec" items="${lectures}">
            <tr>
              <td>${lec.lecture_title}</td>
              <td>${lec.lecture_round}</td>
              <td>
                ${lec.start_date} ~ ${lec.end_date}
              </td>
              <td>${lec.room}</td>
              <td>${lec.capacity}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</c:if>