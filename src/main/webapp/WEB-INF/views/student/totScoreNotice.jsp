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

    .score-page ul {
        padding-left: 18px;
        font-size: 14px;
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
<div class="header">
    이수교과목별 성적 목록
</div>

<!-- 본문 -->
<div class="d-flex justify-content-center mt-5">
    <div class="card" style="max-width: 800px; width: 100%;">
        <div class="card-body p-4">

            <h5 class="fw-bold mb-3">
                ▶ 전체 학기 성적 열람 안내
            </h5>

            <ul class="mb-3 score-page">
                <li>
                    본 화면에 표시되는 성적은
                    <strong>해당 학기의 종강이 완료되고</strong>,
                    <strong>교수의 성적 확정이 모두 완료된 이후</strong>에만
                    열람이 가능합니다.
                </li>
                <li class="mt-2">
                    성적에 대한 <strong>이의신청</strong>은
                    시스템을 통한 접수가 아닌,
                    <span class="text-danger fw-bold">
                        담당 교수님과 개인적으로 진행
                    </span>해야 합니다.
                </li>
            </ul>

            <p class="text-danger fw-bold mb-0">
                ※ 성적 확정 전에는 일부 과목이 표시되지 않을 수 있습니다.
            </p>

        </div>
    </div>
</div>

<!-- 확인 버튼 -->
<div class="confirm-area">
    <button class="confirm-btn"
        onclick="location.href='<%=ctx%>/score/totscore'">
        안내사항을 확인했습니다
    </button>
</div>


