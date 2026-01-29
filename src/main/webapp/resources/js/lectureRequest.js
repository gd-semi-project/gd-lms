document.addEventListener("DOMContentLoaded", function () {
    console.log("lectureRequest.js loaded");

    /* ==========================
       요일 최대 2개 제한
       ========================== */
    const days = document.querySelectorAll('input[name="weekDay"]');

    days.forEach(cb => {
        cb.addEventListener('change', () => {
            const checkedCount =
                document.querySelectorAll('input[name="weekDay"]:checked').length;

            if (checkedCount > 2) {
                cb.checked = false;
                alert('요일은 최대 2일까지 선택 가능합니다.');
            }
        });
    });

    /* ==========================
       성적 배점 합계 체크
       ========================== */
    const weights = document.querySelectorAll(".score-weight");
    const submitBtn = document.getElementById("submitBtn");
    const info = document.getElementById("weightInfo");

    console.log("weights =", weights);
    console.log("submitBtn =", submitBtn);
    console.log("weightInfo =", info);

    // 🔒 배점 관련 요소가 없으면 JS 종료 (다른 페이지용 방어)
    if (!submitBtn || !info || weights.length === 0) {
        console.log("배점 요소 없음 - 스킵");
        return;
    }

    function checkTotal() {
        let total = 0;

        weights.forEach(input => {
            total += Number(input.value) || 0;
        });

        console.log("total =", total);
		console.log("weights =", weights);
		console.log("submitBtn =", submitBtn);
		console.log("weightInfo =", info);

        info.classList.remove("text-muted");

        if (total === 100) {
            submitBtn.disabled = false;
            info.textContent = "※ 성적 배점의 합은 100%입니다.";
            info.classList.remove("text-danger");
            info.classList.add("text-success");
            console.log("버튼 활성화");
        } else {
            submitBtn.disabled = true;
            info.textContent = `※ 현재 합계: ${total}% (100%여야 합니다)`;
            info.classList.remove("text-success");
            info.classList.add("text-danger");
            console.log("버튼 비활성화");
        }
    }

    // 처음 로딩 시 검사
    checkTotal();

    // 입력할 때마다 검사
    weights.forEach(input => {
        input.addEventListener("input", checkTotal);
    });
	
	
	form.addEventListener("submit", function (e) {

	        // 요일 최소 1개 체크
	        const checkedDays = document.querySelectorAll('input[name="weekDay"]:checked').length;
	        if (checkedDays === 0) {
	            alert("요일을 최소 1개 이상 선택하세요.");
	            e.preventDefault();
	            return;
	        }

	        // 성적 배점 합 100 확인
	        if (!checkTotal()) {
	            alert("성적 배점의 합은 반드시 100%여야 합니다.");
	            e.preventDefault();
	            return;
	        }
	    });
});