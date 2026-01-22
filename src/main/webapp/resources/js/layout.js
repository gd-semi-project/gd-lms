document.addEventListener("DOMContentLoaded", function () {
  const btn = document.getElementById("lectureToggle");
  const icon = document.getElementById("lectureChevron");

  if (!btn || !icon) return;

  function syncIcon() {
    const expanded = btn.getAttribute("aria-expanded") === "true";

    if (expanded) {
      icon.classList.remove("bi-chevron-right");
      icon.classList.add("bi-chevron-down");
    } else {
      icon.classList.remove("bi-chevron-down");
      icon.classList.add("bi-chevron-right");
    }
  }

  // 초기 상태 반영
  syncIcon();

  // 클릭 후 aria-expanded 변경 반영
  btn.addEventListener("click", function () {
    setTimeout(syncIcon, 0);
  });
});