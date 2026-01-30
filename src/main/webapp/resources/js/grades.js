let currentTab = 'attendance';

function setAction(type) {
    document.getElementById('actionType').value = type;
}

function showTab(type) {
    currentTab = type;

    ['attendance','assignment','midterm','final'].forEach(t => {
        document.querySelectorAll('.tab-' + t)
            .forEach(el => el.classList.add('d-none'));

        const btn = document.getElementById('btn-' + t);
        if (btn) {
            btn.classList.remove('btn-primary');
            btn.classList.add('btn-outline-primary');
        }
    });

    document.querySelectorAll('.tab-' + type)
        .forEach(el => el.classList.remove('d-none'));

    const activeBtn = document.getElementById('btn-' + type);
    if (activeBtn) {
        activeBtn.classList.remove('btn-outline-primary');
        activeBtn.classList.add('btn-primary');
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById('scoreForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {

        const actionType = document.getElementById('actionType').value;

        const rows = document.querySelectorAll('.score-row');
        rows.forEach(r => r.classList.remove('table-danger'));

        const assignmentInputs = document.querySelectorAll('.assignment-input');
        const midtermInputs = document.querySelectorAll('.midterm-input');
        const finalInputs = document.querySelectorAll('.final-input');

        function highlightEmpty(inputs) {
            inputs.forEach(input => {
                if (input.value === '') {
                    input.closest('tr').classList.add('table-danger');
                }
            });
        }

        function isAllFilled(inputs) {
            return [...inputs].every(i => i.value !== '');
        }

        function isPartialFilled(inputs) {
            const filled = [...inputs].filter(i => i.value !== '');
            return filled.length > 0 && filled.length < inputs.length;
        }

        // ===== 학점 계산 =====
        if (actionType === 'calculate') {

            if (!isAllFilled(assignmentInputs) ||
                !isAllFilled(midtermInputs) ||
                !isAllFilled(finalInputs)) {

                e.preventDefault();

                highlightEmpty(assignmentInputs);
                highlightEmpty(midtermInputs);
                highlightEmpty(finalInputs);

                alert(
                    '학점 계산 불가\n\n' +
                    '과제 / 중간 / 기말 점수는\n' +
                    '모든 학생에게 전부 입력되어야 합니다.'
                );
                return;
            }
        }

        // ===== 저장 =====
        if (actionType === 'save') {

            if (isPartialFilled(assignmentInputs)) highlightEmpty(assignmentInputs);
            if (isPartialFilled(midtermInputs)) highlightEmpty(midtermInputs);
            if (isPartialFilled(finalInputs)) highlightEmpty(finalInputs);

            if (isPartialFilled(assignmentInputs) ||
                isPartialFilled(midtermInputs) ||
                isPartialFilled(finalInputs)) {

                e.preventDefault();

                alert(
                    '과제 / 중간 / 기말 중\n' +
                    '하나라도 입력을 시작했다면\n' +
                    '해당 항목은 모든 학생이 전부 입력해야 합니다.'
                );
                return;
            }
        }
    });
	

});