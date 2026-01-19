<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctx = request.getContextPath();
%>

<aside class="col-12 col-md-3 col-lg-2 bg-secondary text-white p-3 sidebar">

  <ul class="nav nav-pills flex-column gap-1">

    <!-- 대학소개 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/about">
        🎓 대학소개
      </a>
    </li>

    <!-- 강의 (토글 메뉴) -->
    <li class="nav-item">
      <a class="nav-link text-white d-flex justify-content-between align-items-center"
         data-bs-toggle="collapse"
         href="#lectureMenu"
         role="button"
         aria-expanded="false"
         aria-controls="lectureMenu">
        📚 강의
        <span>▾</span>
      </a>

      <!-- 하위 메뉴 -->
      <div class="collapse ps-3" id="lectureMenu">
        <ul class="nav flex-column mt-1">

          <!-- 내 강의 목록 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="<%=ctx%>/instructor/lectures">
              ▸ 내 강의 목록
            </a>
          </li>

          <!-- 강의 개설 신청 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="<%=ctx%>/lecture/request">
              ▸ 강의 개설 신청
            </a>
          </li>

          <!-- 수강신청 -->
          <li class="nav-item">
            <a class="nav-link text-white small"
               href="<%=ctx%>/lecture/enroll">
              ▸ 수강신청
            </a>
          </li>

        </ul>
      </div>
    </li>

    <!-- 공지사항 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/notice/list">
        📢 공지사항
      </a>
    </li>

    <!-- 성적 -->
    <li class="nav-item">
      <a class="nav-link text-white" href="<%=ctx%>/score/my">
        📝 성적
      </a>
    </li>

  </ul>

</aside>