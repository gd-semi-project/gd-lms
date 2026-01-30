<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/lecture/lectureTabs.jsp" />

<h4 class="mb-3">👥 수강생 목록</h4>

<div class="mb-3">
  <input type="text" id="studentSearch"
         class="form-control w-25"
         placeholder="학생 이름으로 검색">
</div>

<c:if test="${empty students}">
  <div class="alert alert-info">
    수강 중인 학생이 없습니다.
  </div>
</c:if>

<c:if test="${not empty students}">
  <table id="studentTable" class="table table-bordered">
    <thead class="table-light">
      <tr>
        <th>학번</th>
        <th>이름</th>
        <th>학년</th>
        <th>상태</th>
        <th>신청일</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="s" items="${students}">
        <tr>
          <td>${s.studentNumber}</td>
          <td>${s.studentName}</td>
          <td>${s.studentGrade}</td>
          <td>${s.enrollmentStatus}</td>
          <td>${s.appliedAt}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</c:if>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("studentSearch");
    const table = document.getElementById("studentTable");

    if (!searchInput || !table) return;

    searchInput.addEventListener("keyup", function () {
        const keyword = searchInput.value.toLowerCase();
        const rows = table.querySelectorAll("tbody tr");

        rows.forEach(row => {
            const nameCell = row.children[1]; // 이름 컬럼 (2번째 td)
            if (!nameCell) return;

            const name = nameCell.textContent.toLowerCase();

            if (name.includes(keyword)) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });
});
</script>