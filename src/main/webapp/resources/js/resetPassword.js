import { generateToken } from './tokenService.js';
import { checkUserInfo, getUserId } from './UserInfoServices.js';

document.addEventListener("DOMContentLoaded", () => {
	const email = document.getElementById("email");
    const birthDate = document.getElementById("birthDate");
    const checkInfoBtn = document.getElementById("checkInfoBtn");
    const tokenOutput = document.getElementById("tokenOutput");
	
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

        try {
            // 1. 이메일 + 생년월일 검증
            const isMatch = await checkUserInfo(ctx, emailValue, birthValue);
            if (!isMatch) return alert("이메일 또는 생년월일 불일치");

            // 2. userId 조회
            const userId = await getUserId(ctx, emailValue, birthValue);

            // 3. 토큰 발급
            const token = await generateToken(ctx, userId);
			
			// 4. 인증완료 후 페이지 이동
            window.location.href = `${ctx}/login/resetPasswordForm`;

        } catch (err) {
            console.error(err);
            alert("오류 발생: " + err.message);
        }
    });
});
function isValidEmail(email) {
    const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}