document.addEventListener("DOMContentLoaded", () => {

    const email = document.getElementById("email");
    const checkEmailBtn = document.getElementById("checkEmailBtn");
	const checkLoginIdBtn = document.getElementById("checkLoginIdBtn");
    checkEmailBtn.addEventListener("click", () => {
		// Email 중복확인 로직 --------------------------------------------
        const emailValue = email.value.trim();

        // 1. 입력값 체크
        if (emailValue === "") {
            alert("이메일을 입력해주세요.");
            email.focus();
            return;
        }

        // 2. AJAX 중복확인 요청
        fetch(ctx + "/admin/check-email", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            },
            body: "email=" + encodeURIComponent(emailValue)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP error " + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.duplicate) {
                alert("이미 사용 중인 이메일입니다.");
                email.readOnly = false;
                email.focus();
            } else {
                alert("사용 가능한 이메일입니다.");
                email.readOnly = true;
                checkEmailBtn.disabled = true;
            }
        })
        .catch(error => {
            console.error(error);
            alert("중복 확인 중 오류가 발생했습니다.");
        });
    });

    // 3. 값 변경 시 중복확인 무효화
    email.addEventListener("input", () => {
        email.readOnly = false;
        checkEmailBtn.disabled = false;
    });
	
	// ID 중복확인 로직 --------------------------------------------
	checkLoginIdBtn.addEventListener("click", () => {
        const loginIdValue = loginId.value.trim();

        // 1. 입력값 체크
        if (loginIdValue === "") {
            alert("아이디를 입력해주세요.");
            loginId.focus();
            return;
        }

        // 2. AJAX 중복확인 요청
        fetch(ctx + "/admin/check-loginId", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            },
            body: "loginId=" + encodeURIComponent(loginIdValue)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP error " + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.duplicate) {
                alert("이미 사용 중인 아이디입니다.");
                idChecked.value = "false";
                loginId.readOnly = false;
                loginId.focus();
            } else {
                alert("사용 가능한 아이디입니다.");
                idChecked.value = "true";
                loginId.readOnly = true;
                checkLoginIdBtn.disabled = true;
            }
        })
        .catch(error => {
            console.error(error);
            alert("아이디 중복 확인 중 오류가 발생했습니다.");
        });
    });

    // 3. 값 변경 시 중복확인 무효화
    loginId.addEventListener("input", () => {
        idChecked.value = "false";
        loginId.readOnly = false;
        checkLoginIdBtn.disabled = false;
    });
});

function isValidEmail(email) {
    const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}