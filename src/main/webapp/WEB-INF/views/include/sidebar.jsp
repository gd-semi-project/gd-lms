<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
  String ctx = request.getContextPath();
%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${sessionScope.AccessInfo.role}" />


<aside class="col-12 col-md-3 col-lg-2 bg-dark text-white p-3 sidebar">
   <ul class="nav nav-pills flex-column gap-1">

      <li class="nav-item"><a class="nav-link text-white"
         href="<%=ctx%>/about"> 🎓 대학소개 </a></li>

		<!--     마이페이지(학생) -->
		<c:choose>
		 <c:when test="${AccessInfo.role == 'STUDENT'}">
		<li class="nav-item"><a class="nav-link text-white"
			href="<%=ctx%>/mypage/studentPage"> 학생정보 </a></li>
		</c:when>
		</c:choose>
		
		<li class="nav-item">
         <a class="nav-link text-white" href="${ctx}/notice/list">📢 공지사항</a>
       </li>
       <li class="nav-item">
         <a class="nav-link text-white" href="<%=ctx%>/calendar/view">학사일정 관리</a>
       </li>
<!--        권한별 개인성적 -->
       <c:choose>
       <c:when test="${AccessInfo.role == 'STUDENT'}">
       <li class="nav-item">
         <a class="nav-link text-white" href="<%=ctx%>/mypage/score"> 📝 성적 	
         </a>
       </li>
       </c:when>  
       
       <c:when test="${AccessInfo.role == 'INSTRUCTOR'}">
       <li class="nav-item">
         <a class="nav-link text-white" href="${ctx}//"> 📝 성적 	
         </a>
       </li>
       </c:when>
      
       <c:when test="${AccessInfo.role == 'ADMIN'}">
       <li class="nav-item">
         <a class="nav-link text-white" href="${ctx}//"> 📝 성적 	
         </a>
       </li>
       </c:when>
       </c:choose>
       
       <c:if test="${role == 'ADMIN'}">
          <li class="nav-item">
            <a class="nav-link text-white" href="<%=ctx%>/admin/dashboard">대시보드</a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white" href="<%=ctx%>/admin/lectureRequest">강의 개설 관리</a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white" href="<%=ctx%>/admin/campus">캠퍼스 관리</a>
          </li>
       </c:if>
       <li class="nav-item">
        <button
          type="button"
          class="nav-link text-white d-flex justify-content-between align-items-center w-100 bg-transparent border-0"
          data-bs-toggle="collapse"
          data-bs-target="#lectureMenu"
          aria-expanded="false"
          aria-controls="lectureMenu">
          📚 강의
          <span class="ms-auto">+</span>
        </button>
   
        <div class="collapse" id="lectureMenu">
          <ul class="nav flex-column ms-3 mt-2 gap-1">
      
            <c:choose>
      
              <%-- 교수 --%>
              <c:when test="${AccessInfo.role == 'INSTRUCTOR'}">
                <li class="nav-item">
                  <a class="nav-link text-white small"
                     href="<%=ctx%>/instructor/lectures">내 강의 목록</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link text-white small"
                     href="<%=ctx%>/instructor/lecture/request">강의 개설 신청</a>
                </li>
              </c:when>
      
              <%-- 학생 --%>
              <c:when test="${AccessInfo.role == 'STUDENT'}">
                <li class="nav-item">
                  <a class="nav-link text-white small"
                     href="<%=ctx%>/mypage/mySubjectPage">내 강의 목록</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link text-white small"
                     href="<%=ctx%>/mypage/enrollmentPage">수강 신청</a>
                </li>
					<!-- 내 시간표 -->
				<li class="nav-item"><a class="nav-link text-white small"
				href="<%=ctx%>/mypage/mySchedule"> 내시간표 </a>
				</li>
				</c:when>
      
              <%-- 관리자 --%>
              <c:when test="${role == 'ADMIN'}">
                <li class="nav-item">
                  <a class="nav-link text-white small"
                     href="<%=ctx%>/admin/lectures">전체 강의 목록</a>
                </li>
              </c:when>
      
            </c:choose>
      
          </ul>
        </div>
      </li>

         
         
         <!--  스코프 확인용 TEST -->
         <div class="text-warning small">
           pageScope.role = [${pageScope.role}]<br/>
           AccessInfo.role = [${sessionScope.AccessInfo.role}]<br/>
           sessionScope.role = [${sessionScope.AccessInfo.role}]
         </div>

   </ul>

   <hr class="border-light opacity-50 my-3">

   <div class="small opacity-75">
      로그인 사용자: ${sessionScope.AccessInfo.name}<br /> 권한:
      ${sessionScope.AccessInfo.role}
   </div>
</aside>