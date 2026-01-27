document.addEventListener("DOMContentLoaded", function () {

    /* ==========================
       요일 최대 2개 제한
       ========================== */
    const days = document.querySelectorAll('input[name="weekDay"]');

    days.forEach(cb => {
        cb.addEventListener('change', () => {
            const checkedCount = document.querySelectorAll('input[name="weekDay"]:checked').length;

            if (checkedCount > 2) {
                cb.checked = false;
                alert('요일은 최대 2일까지 선택 가능합니다.');
            }
        });
    });

    /* ==========================
       배점 합계 100% 체크
       ========================== */
    const weights = document.querySelectorAll('.score-weight');
    const info = document.getElementById('weightInfo');
    const form = document.querySelector('form');

    function checkTotal() {
        let total = 0;
        weights.forEach(w => {
            total += Number(w.value || 0);
        });

        if (total !== 100) {
            info.classList.add('text-danger');
            info.innerText = `⚠ 현재 합계: ${total}%`;
            return false;
        }

        info.classList.remove('text-danger');
        info.innerText = '※ 성적 배점의 합은 반드시 100%여야 합니다.';
        return true;
    }

    weights.forEach(w => w.addEventListener('input', checkTotal));

    form.addEventListener('submit', e => {
        if (!checkTotal()) {
            e.preventDefault();
            alert('성적 배점 합계를 100%로 맞춰주세요.');
        }
    });

});