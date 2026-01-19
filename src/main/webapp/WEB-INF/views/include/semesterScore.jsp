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
          <th>신청<br/>학점</th>
          <th>취득<br/>학점</th>
          <th>합계<br/>평점</th>
          <th>평점<br/>평균</th>
          <th>총<br/>점수</th>
          <th>100점<br/>환산점수</th>
        </tr>
      </thead>

      <tbody>
        <c:forEach var="score" items="${scoreList}" varStatus="st">
          <tr>
            <td>${st.index + 1}</td>
            <td>${score.year}학년도</td>
            <td>${score.semester}학기</td>
            <td>${score.applyCredit}</td>
            <td>${score.earnedCredit}</td>
            <td>${score.totalGrade}</td>
            <td>${score.avgGrade}</td>
            <td>${score.totalScore}</td>
            <td>${score.convertedScore}</td>
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
