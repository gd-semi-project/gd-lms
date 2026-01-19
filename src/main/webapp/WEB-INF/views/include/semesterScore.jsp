<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="card mt-4">
  <div class="card-header d-flex justify-content-between align-items-center">
    <div>
      ▶ <strong>학년도, 학기별 집계 성적 목록</strong>
    </div>
    <div class="text-muted">
      [${scoreList.size()}]
    </div>
  </div>

  <div class="card-body p-0">
    <table class="table table-bordered table-hover text-center mb-0">
      <thead class="table-light">
        <tr>
          <th>No</th>
          <th>학년도</th>
          <th>학기</th>
          <th>과목명</th>
          <th>중간점수</th>
          <th>기말점수</th>
          <th>과제점수</th>
          <th>출석점수</th>
          <th>환산점수</th>
          <th>등급</th>
        </tr>
      </thead>

      <tbody>
        <c:forEach var="score" items="${scoreList}" varStatus="st">
          <tr>
            <td>${순번값}</td>
            <td>${학년값}학년도</td>
            <td>${학기값}학기</td>
            <td>${과목명값}</td>
            <td>${중간점수값}</td>
            <td>${기말점수값}</td>
            <td>${과제점수값}</td>
            <td>${출석점수값}</td>
            <td>${환산점수값}</td>
            <td>${등급값}</td>
          </tr>
        </c:forEach>

        <!-- 데이터 없을 때 -->
        <c:if test="${empty scoreList}">
          <tr>
            <td colspan="9" class="text-muted py-4">
              조회된 성적 정보가 없습니다.
            </td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>
</div>
