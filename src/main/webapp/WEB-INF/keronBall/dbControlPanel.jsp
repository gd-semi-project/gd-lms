<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<link rel="icon" type="image/x-icon" href="<%=request.getContextPath()%>/resources/images/keronBall.ico">
<style>
/* KeronBall "remote button" feel for Bootstrap buttons */

/* hover: 살짝 어두워지고 떠오름 */
.kb-btn:hover {
  filter: brightness(0.9);
}

/* active: 눌림 */
.kb-btn:active {
  filter: brightness(0.88);
  box-shadow: inset 0 6px 14px rgba(0,0,0,0.25);
  transform: translateY(0);
}
</style>
<jsp:include page="/WEB-INF/keronBall/keronBall.jsp" />
<div class="container my-5">
  <div class="db-control-wrap">

    <h3 class="mb-3 fw-bold">DB 조작</h3>

    <div class="row g-4">
		<div class="col-12 col-md-4">
		  <form method="post"
		        action="${pageContext.request.contextPath}/keronBall/db?action=CREATEALL">
		
		    <input type="hidden" name="command" value="CREATE_ALL">
		
		    <button
		      type="submit"
		      class="btn btn-success w-100 db-action kb-btn">
		      CREATE ALL
		    </button>
		  </form>
		</div>
		<div class="col-12 col-md-4">
		  <form method="post"
		        action="${pageContext.request.contextPath}/keronBall/db?action=DELETEALL">
		
		    <input type="hidden" name="command" value="DELETEALL">
		
	        <button
	          type="submit"
	          class="btn btn-danger w-100 db-action kb-btn"
	          >
	          DELETE ALL
	        </button>
		  </form>
		</div>
		<div class="col-12 col-md-4">
			<a class="btn btn-warning w-100 db-action kb-btn"
			   href="${pageContext.request.contextPath}/keronBall/updateDB">
			  UPDATE
			</a>
		</div>
    </div>
  </div>
</div>
