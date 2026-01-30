(function () {
  function openKeronBall(ctx) {
    window.open(
      (ctx || "") + "/keronBall/remote",
      "keronBallWindow",
      "width=420,height=520,resizable=yes,scrollbars=no"
    );
  }

  function init() {
    var a = document.getElementById("keronBallLauncher");
    if (!a) return;

    var ctx = a.getAttribute("data-ctx") || "";

    a.addEventListener("click", function (e) {
      e.preventDefault();
      openKeronBall(ctx);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
