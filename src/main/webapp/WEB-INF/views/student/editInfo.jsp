<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
.read-only {
  background-color: #e9ecef;
}
</style>

<div class="card">
  <div class="card-body">
    <h5 class="mb-3">기본 정보</h5>

    <form action="${pageContext.request.contextPath}/editUserInfoController/edit" method="post">

      <table class="table table-bordered align-middle">
        <tbody>
          <tr>
            <th>로그인 아이디</th>
            <td>
              <input class="form-control read-only" value="${mypage.user.loginId}" readonly>
            </td>
          </tr>

          <tr>
            <th>이름</th>
            <td>
              <input class="form-control read-only" value="${mypage.user.name}" readonly>
            </td>
          </tr>

          <tr>
            <th>성별</th>
            <td>
              <input class="form-control read-only" value="${mypage.user.gender}" readonly>
            </td>
          </tr>

          <tr>
            <th>생년월일</th>
            <td>
              <input type="date" name="birthDate" class="form-control"
                     value="${mypage.user.birthDate}">
            </td>
          </tr>

          <tr>
            <th>이메일</th>
            <td>
              <input type="email" name="email" class="form-control"
                     value="${mypage.user.email}">
            </td>
          </tr>

          <tr>
            <th>전화번호</th>
            <td>
              <input type="text" name="phone" class="form-control"
                     value="${mypage.user.phone}">
            </td>
          </tr>

          <tr>
            <th>신분</th>
            <td>
              <input class="form-control read-only" value="${mypage.user.role}" readonly>
            </td>
          </tr>

          <tr>
            <th>주소</th>
            <td>
              <input type="text" name="address" class="form-control"
                     value="${mypage.user.address}">
            </td>
          </tr>
        </tbody>
      </table>
  </div>
</div>


<div class="card mt-4">
  <div class="card-body">
<c:if test="${not empty mypage.student}">
    <h5 class="mb-3">학생 정보</h5>

    <table class="table table-bordered align-middle">
      <tbody>

        <tr>
          <th>학번</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.studentNumber}" readonly>
          </td>
        </tr>

        <tr>
          <th>전공</th>
          <td>
            <input class="form-control read-only" value="${mypage.department.departmentName}" readonly>
          </td>
        </tr>

        <tr>
          <th>학년</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.studentGrade}" readonly>
          </td>
        </tr>

        <tr>
          <th>학부 상태</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.status}" readonly>
          </td>
        </tr>

        <tr>
          <th>학적 상태</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.studentStatus}" readonly>
          </td>
        </tr>

        <tr>
          <th>입학일자</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.enrollDate}" readonly>
          </td>
        </tr>

        <tr>
          <th>졸업일자</th>
          <td>
            <input class="form-control read-only" value="${mypage.student.endDate}" readonly>
          </td>
        </tr>

        <tr>
          <th>등록금 계좌</th>
          <td>
            <input type="text" name="tuitionAccount" class="form-control"
                   value="${mypage.student.tuitionAccount}">
          </td>
        </tr>

      </tbody>
    </table>
</c:if>
    <div class="d-flex gap-2 mt-3">
      <button type="submit" class="btn btn-primary">변경</button>

      <a href="${pageContext.request.contextPath}/mypage/studentPage"
         class="btn btn-outline-secondary">취소</a>
<c:choose>
    <c:when test="${AccessInfo.role == 'STUDENT'}">
      <a href="${pageContext.request.contextPath}/student/updateInfo"
         class="btn btn-warning">중요 정보 수정 요청</a>
         </c:when></c:choose>
    </div>

    </form>
  </div>
  
</div>