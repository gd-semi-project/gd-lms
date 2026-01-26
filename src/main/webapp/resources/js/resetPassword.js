import { generateToken } from './tokenService.js';
import { checkUserInfo } from './UserInfoServices.js';

document.addEventListener("DOMContentLoaded", () => {
	const email = document.getElementById("email");
    const birthDate = document.getElementById("birthDate");
    const checkInfoBtn = document.getElementById("checkInfoBtn");
	
    checkInfoBtn.addEventListener("click", async () => {
        const emailValue = email.value.trim();
        const birthValue = birthDate.value;

        if (!emailValue) return alert("이메일 입력 필요");

		if (!isValidEmail(emailValue)) {
		    alert("올바른 이메일 형식으로 입력해주세요.");
		    email.focus();
		    return;
		}
		
        if (!birthValue) return alert("생년월일 입력 필요");

		// fetch 문 실행 중 버튼 비활성화
		// 최종 alert창 클릭시 window창 close
        try {
            // 1. 이메일 + 생년월일 검증
            const isMatch = await checkUserInfo(ctx, emailValue, birthValue);
            if (!isMatch) return alert("이메일 또는 생년월일 불일치");

			checkInfoBtn.disabled = true;
            // 3. 서버측 userId 조회해서 유효성 검증 후 토큰 발급
            const token = await generateToken(ctx, emailValue, birthValue);

			// 4. 반환값 확인
			if (token.status === true) {
	            alert("메일 전송 완료");
	        } else if (token.status === false) {
	            alert("메일 전송 실패: " + token.status);
	        } else {
	            alert("서버 오류 발생" + token.status);
	        }
	        // 창 닫기
	        window.close();
			
        } catch (err) {
            console.error(err);
            alert("오류 발생: " + err.message);
        } finally {
			if (!window.closed) checkInfoBtn.disabled = false;
		}
    });
});
function isValidEmail(email) {
    const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}