<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<link
  href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
  rel="stylesheet" />
<br><br><br>
<div class="card">
  <div class="card-body">
    <h5 class="mb-3">비밀번호 변경</h5>

    <form action="${pageContext.request.contextPath}/changeUserPw/change"
          method="post">

      <table class="table table-bordered">
        <tbody>

          <!-- 학번 (본인 확인용) -->
          <tr>
            <th scope="row">아이디</th>
            <td>
              <input type="text"
                     name="inputLoginId"
                     class="form-control"
                     placeholder="본인 아이디를 입력하세요"
                     required>
                     ※ 아이디과 비밀번호를 모두 입력하세요.
            </td>
          </tr>

          <!-- 비밀번호 변경 -->
           <tr>
            <th scope="row">현재 비밀번호</th>
            <td>
              <input type="password"
                     name="Pw"
                     class="form-control"
                     placeholder="현재 비밀번호"
                     required>
            </td>
          </tr>
          
          <tr>
            <th scope="row">새 비밀번호</th>
            <td>
              <input type="password"
                     name="newPw"
                     class="form-control"
                     placeholder="새 비밀번호"
                     required>
            </td>
          </tr>

          <tr>
            <th scope="row">새 비밀번호 확인</th>
            <td>
              <input type="password"
                     name="confirmPw"
                     class="form-control"
                     placeholder="새 비밀번호 확인"
                     required>
              <small class="text-muted">
              </small>
            </td>
          </tr>

        </tbody>
      </table>

			<!-- 에러 메시지 -->
			<c:if test="${not empty error}">
				<script>
				alert("${error}");
				</script>
			</c:if>

			<!--       성공 메시지 -->
			<c:if test="${not empty alertMsg}">
				<script>
					alert("${alertMsg}");
					location.href = "${pageContext.request.contextPath}/login/logout";
				</script>
			</c:if>

			<div class="mt-3">
        <button type="submit" class="btn btn-warning">변경</button>
        <c:choose>
      	  <c:when test="${sessionScope.AccessInfo.role == 'ADMIN' or sessionScope.AccessInfo.role == 'STUDENT'}">
      	  <a href="${pageContext.request.contextPath}/mypage/studentPage"
       	    class="btn btn-secondary">취소</a>
       	 </c:when>
       	 <c:when test="${sessionScope.AccessInfo.role == 'INSTRUCTOR'}">
      	  <a href="${pageContext.request.contextPath}/instructor/profile"
       	    class="btn btn-secondary">취소</a>
       	 </c:when>
        </c:choose>
 
      </div>

    </form>
  </div>
</div>
