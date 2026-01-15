(() => {
  // 지금 JSP는 id="processedBox"를 사용 중
  const box = document.getElementById('processedBox');
  if (!box) return;

  // 버튼은 data-filter 로 구분
  const buttons = document.querySelectorAll('.btn-group [data-filter]');
  // tbody는 data-body 로 구분
  const bodies = box.querySelectorAll('tbody[data-body]');

  if (buttons.length === 0 || bodies.length === 0) return;

  function setFilter(filter) {
    bodies.forEach(tb => {
      tb.style.display = (tb.dataset.body === filter) ? '' : 'none';
    });

    buttons.forEach(btn => {
      const isActive = btn.dataset.filter === filter;

      btn.classList.toggle('active', isActive);
      btn.classList.toggle('btn-primary', isActive);
      btn.classList.toggle('btn-outline-primary', !isActive);
    });
  }

  buttons.forEach(btn => {
    btn.addEventListener('click', () => setFilter(btn.dataset.filter));
  });

  setFilter('all');
})();
