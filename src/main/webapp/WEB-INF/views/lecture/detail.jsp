<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<div class="container mt-4">

  <!-- ================= 제목 ================= -->
  <h2 class="mb-4">
    ${lecture.lectureTitle}
    <span class="badge bg-secondary">${lecture.lectureRound}차</span>
    <span class="badge bg-info">분반 ${lecture.section}</span>
  </h2>

  <!-- ================= 담당 강사 정보 ================= -->
  <h4 class="mt-4">👤 담당 강사 정보</h4>

  <table class="table table-bordered align-middle">
    <tbody>
      <tr>
        <th style="width:20%">이름</th>
        <td>${instructor.name}</td>
      </tr>
      <tr>
        <th>이메일</th>
        <td>${instructor.email}</td>
      </tr>
      <tr>
        <th>연구실</th>
        <td>
          <c:choose>
            <c:when test="${not empty instructor.officeRoom}">
              ${instructor.officeRoom}
            </c:when>
            <c:otherwise>
              <span class="text-muted">미등록</span>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <th>연락처</th>
        <td>
          <c:choose>
            <c:when test="${not empty instructor.officePhone}">
              ${instructor.officePhone}
            </c:when>
            <c:otherwise>
              <span class="text-muted">미등록</span>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <th>소속 학과</th>
        <td>${instructor.department}</td>
      </tr>
    </tbody>
  </table>

  <!-- ================= 강의 기본 정보 ================= -->
  <h4 class="mt-5">📘 강의 기본 정보</h4>

  <table class="table table-bordered align-middle">
    <tbody>
      <tr>
        <th style="width:20%">강의 기간</th>
        <td>${lecture.startDate} ~ ${lecture.endDate}</td>
      </tr>
      <tr>
        <th>강의실</th>
        <td>${lecture.room}</td>
      </tr>
      <tr>
        <th>정원</th>
        <td>${lecture.capacity}명</td>
      </tr>
      <tr>
        <th>상태</th>
        <td>
          <span class="badge
            <c:choose>
              <c:when test="${lecture.status eq 'ONGOING'}">bg-success</c:when>
              <c:when test="${lecture.status eq 'PLANNED'}">bg-warning</c:when>
              <c:when test="${lecture.status eq 'ENDED'}">bg-secondary</c:when>
              <c:otherwise>bg-light text-dark</c:otherwise>
            </c:choose>
          ">
            ${lecture.status}
          </span>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- ================= 강의 요일 / 시간 ================= -->
  <h4 class="mt-5">🕒 강의 요일 및 시간</h4>

  <table class="table table-striped mt-2">
    <thead>
      <tr>
        <th style="width:30%">요일</th>
        <th>시간</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="s" items="${schedules}">
        <tr>
          <td>
            <c:choose>
              <c:when test="${s.weekDay eq 'MON'}">월</c:when>
              <c:when test="${s.weekDay eq 'TUE'}">화</c:when>
              <c:when test="${s.weekDay eq 'WED'}">수</c:when>
              <c:when test="${s.weekDay eq 'THU'}">목</c:when>
              <c:when test="${s.weekDay eq 'FRI'}">금</c:when>
              <c:otherwise>${s.weekDay}</c:otherwise>
            </c:choose>
          </td>
          <td>${s.startTime} ~ ${s.endTime}</td>
        </tr>
      </c:forEach>

      <c:if test="${empty schedules}">
        <tr>
          <td colspan="2" class="text-muted text-center">
            등록된 강의 시간이 없습니다.
          </td>
        </tr>
      </c:if>
    </tbody>
  </table>

  <!-- ================= 성적 배점 ================= -->
  <h4 class="mt-5">📊 성적 배점</h4>

  <c:choose>
    <c:when test="${not empty scorePolicy}">
      <table class="table table-bordered text-center mt-2">
        <thead class="table-light">
          <tr>
            <th>출석</th>
            <th>과제</th>
            <th>중간</th>
            <th>기말</th>
            <th>합계</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>${scorePolicy.attendanceWeight}%</td>
            <td>${scorePolicy.assignmentWeight}%</td>
            <td>${scorePolicy.midtermWeight}%</td>
            <td>${scorePolicy.finalWeight}%</td>
            <td><strong>100%</strong></td>
          </tr>
        </tbody>
      </table>
    </c:when>

    <c:otherwise>
      <div class="alert alert-warning mt-2">
        성적 배점이 아직 등록되지 않았습니다.
      </div>
    </c:otherwise>
  </c:choose>

  <!-- ================= 수정/삭제 버튼 ================= -->
<c:if test="${isRequest == true}">
  <div class="mt-4 text-end">

    <!-- ✅ 수정 : 항상 가능 -->
    <a href="${ctx}/instructor/lecture/request/edit?lectureId=${lecture.lectureId}"
       class="btn btn-warning">수정</a>

    <!-- ✅ 삭제 : 승인 전(PENDING)만 가능 -->
    <c:if test="${lecture.validation eq 'PENDING'}">
      <form method="post"
            action="${ctx}/instructor/lecture/request/delete"
            style="display:inline;"
            onsubmit="return confirm('삭제하시겠습니까?');">
        <input type="hidden" name="lectureId" value="${lecture.lectureId}">
        <button class="btn btn-danger">삭제</button>
      </form>
    </c:if>

  </div>
</c:if>

</div>