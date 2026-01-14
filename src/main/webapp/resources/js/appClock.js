(function () {
  function initAppClock() {
    var root = document.getElementById("appClock");
    if (!root) return;

    var ctx = root.getAttribute("data-ctx") || "";
    var el = document.getElementById("appClockText");
    if (!el) return;

    function tick() {
      fetch(ctx + "/appTime.now", { cache: "no-store" })
        .then(function (r) {
          if (!r.ok) throw new Error();
          return r.json();
        })
        .then(function (j) {
          el.textContent = j.now || "--:--:--";
        })
        .catch(function () {
          el.textContent = "TIME ERR";
        });
    }

    tick();
    setInterval(tick, 1000);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initAppClock);
  } else {
    initAppClock();
  }
})();
