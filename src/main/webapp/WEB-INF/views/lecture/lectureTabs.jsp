<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />

<div class="mb-4 border-bottom pb-2">
  <ul class="nav nav-tabs">

    <!-- ================= 상세보기 탭 ================= -->
    <li class="nav-item">
      <c:choose>
        <c:when test="${isRequest == true}">
          <a class="nav-link active"
             href="${ctx}/instructor/lecture/request/detail?lectureId=${lecture.lectureId}">
            📘 상세보기
          </a>
        </c:when>

        <c:otherwise>
          <a class="nav-link ${activeTab eq 'detail' ? 'active' : ''}"
             href="${ctx}/lecture/detail?lectureId=${lecture.lectureId}">
            📘 상세보기
          </a>
        </c:otherwise>
      </c:choose>
    </li>

    <!-- 강의 신청 상세일 때는 다른 탭 숨김 -->
    <c:if test="${isRequest != true}">

      <!-- 승인 + 진행중일 때만 -->
      <c:if test="${lecture.validation eq 'CONFIRMED' and lecture.status eq 'ONGOING'}">

        <!-- 출석 / 성적 : 학생 + 강사만 -->
        <c:if test="${role eq 'STUDENT' or role eq 'INSTRUCTOR'}">

          <li class="nav-item">
            <a class="nav-link ${activeTab eq 'attendance' ? 'active' : ''}"
               href="${ctx}/attendance/view?lectureId=${lecture.lectureId}">
              🕘 출석
            </a>
          </li>

          <li class="nav-item">
            <a class="nav-link ${activeTab eq 'grades' ? 'active' : ''}"
               href="${ctx}/score/grades?lectureId=${lecture.lectureId}">
              📊 성적
            </a>
          </li>

        </c:if>

        <!-- 과제 : 모두 가능 -->
        <li class="nav-item">
          <a class="nav-link ${activeTab eq 'assignments' ? 'active' : ''}"
             href="${ctx}/lecture/assignments?lectureId=${lecture.lectureId}">
            📂 과제
          </a>
        </li>

        <!-- QnA : 모두 가능 -->
        <li class="nav-item">
          <a class="nav-link ${activeTab eq 'qna' ? 'active' : ''}"
             href="${ctx}/lecture/qna?lectureId=${lecture.lectureId}">
            💬 QnA
          </a>
        </li>

        <!-- 수강생 : 강사 + 관리자만 -->
        <c:if test="${role eq 'INSTRUCTOR' or role eq 'ADMIN'}">
          <li class="nav-item">
            <a class="nav-link ${activeTab eq 'students' ? 'active' : ''}"
               href="${ctx}/lecture/students?lectureId=${lecture.lectureId}">
              👥 수강생
            </a>
          </li>
        </c:if>

      </c:if>
    </c:if>

  </ul>
</div>