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

(function () {
  function norm(s) {
    if (s === undefined || s === null) s = "";
    s = String(s);
    s = s.toLowerCase();
    s = s.replace(/\s+/g, " ");
    // trim()도 구형에서 문제면 아래 주석 해제해서 쓰세요
    // s = s.replace(/^\s+|\s+$/g, "");
    if (s.trim) s = s.trim();
    else s = s.replace(/^\s+|\s+$/g, "");
    return s;
  }

  function getRows() {
    // 페이지 전체 테이블 tbody tr 중에서 실제 td가 있는 행만
    var nodes = document.querySelectorAll("table tbody tr");
    var rows = [];
    var i, tr;

    for (i = 0; i < nodes.length; i++) {
      tr = nodes[i];
      if (tr.getElementsByTagName("td").length > 0) {
        rows.push(tr);
      }
    }
    return rows;
  }

  function applyFilter(query) {
    var q = norm(query);
    var rows = getRows();
    var i, tr, hay, ok;

    for (i = 0; i < rows.length; i++) {
      tr = rows[i];
      hay = tr.getAttribute("data-search");
      if (!hay) hay = tr.textContent || tr.innerText || "";
      hay = norm(hay);

      ok = (q === "") || (hay.indexOf(q) !== -1);
      tr.style.display = ok ? "" : "none";
    }
  }

  function onReady(fn) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else {
      fn();
    }
  }

  onReady(function () {
    var form = document.getElementById("quickSearchForm");
    var input = document.getElementById("quickSearchInput");
    if (!form || !input) return;

    // submit 막고 즉시 필터 적용
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      applyFilter(input.value);
    });

    // 입력 즉시 검색 (디바운스)
    var t = null;
    input.addEventListener("input", function () {
      if (t) clearTimeout(t);
      t = setTimeout(function () {
        applyFilter(input.value);
      }, 80);
    });

    // 초기값 있으면 바로 반영
    if (input.value && norm(input.value) !== "") {
      applyFilter(input.value);
    }
  });
}());

