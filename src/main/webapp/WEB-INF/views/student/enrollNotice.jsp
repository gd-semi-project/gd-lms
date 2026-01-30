<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String ctx = request.getContextPath();
%>
<style>

/* 헤더 */
.header {
	background: #0b5ed7;
	color: white;
	padding: 18px 30px;
	font-size: 20px;
	font-weight: bold;
}

/* 메인 */
.container {
	max-width: 1100px;
	margin: 30px auto;
	display: grid;
	grid-template-columns: 2fr 1fr;
	gap: 20px;
}

.box {
	background: #fff;
	border: 1px solid #ccc;
	padding: 20px;
}

.box h3 {
	margin-top: 0;
	font-size: 16px;
	color: #0b5ed7;
}

ul {
	padding-left: 18px;
	font-size: 14px;
}

li {
	margin-bottom: 6px;
	line-height: 1.6;
}

.highlight {
	color: red;
	font-weight: bold;
}

/* 확인 버튼 */
.confirm-area {
	text-align: center;
	margin: 30px 0;
}

.confirm-btn {
	padding: 12px 30px;
	font-size: 15px;
	background: #0b5ed7;
	color: white;
	border: none;
	cursor: pointer;
	border-radius: 4px;
}

.confirm-btn:hover {
	background: #084298;
}

/* 푸터 */
.footer {
	text-align: center;
	padding: 15px;
	font-size: 12px;
	color: #666;
}
</style>

<!-- 헤더 -->
<div class="header">구디대학교 수강신청시스템</div>

<!-- 본문 -->
<div class="container">

	<!-- 왼쪽 안내 -->
	<div class="box">
		<h3>▶ 수강신청 안내</h3>
		<ul>
			<li>수강신청은 정해진 기간 내에만 가능합니다.</li>
			<li>수강신청 대상 : 본교 학부 재학생</li>
			<li>정원 초과 과목은 신청할 수 없습니다.</li>
			<li>동일 시간대에 강의 시간이 겹치는 과목은 신청할 수 없습니다.</li>
			<li>이미 신청한 강의는 중복 신청할 수 없습니다.</li>
			<li>수강신청 취소는 수강신청 기간 내에만 가능합니다.(학사일정을 참고해 주세요)</li>
			<li>수업은 지정된 요일 및 시간에 진행됩니다.</li>
			<li class="highlight">타 대학(국내·국외) 교류 수강 승인자는 본교 수강신청이 제한될 수
				있습니다.</li>
		</ul>
	</div>


	<div class="box">
		<h3>자동순번대기 시스템이란?</h3>
		<ul>
			<li>수강신청 트래픽 분산을 위해 자동으로 순번을 부여하는 대기 시스템입니다.</li>
			<li>동시 접속자가 많을 경우 대기화면이 자동 표시됩니다.</li>
			<li>대기 중 새로고침(F5) 사용 시 순번이 초기화될 수 있습니다.</li>
			<li class="highlight">주의: 로그인·조회·신청 중에는 순번이 유지됩니다.</li>
		</ul>
	</div>

</div>

<!-- 확인 버튼 -->
<div class="confirm-area">
	<button class="confirm-btn"
		onclick="location.href='<%=ctx%>/enroll/enrollment'">안내사항을
		확인했습니다</button>
</div>


