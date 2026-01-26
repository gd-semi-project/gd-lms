document.addEventListener("DOMContentLoaded", () => {
	const resetLink = document.getElementById("resetLoginPassword");

    resetLink.addEventListener("click", (e) => {
        e.preventDefault();

        window.open(
            `${ctx}/login/passwordReset`,
            "passwordReset",
            "width=450,height=500,resizable=no,scrollbars=yes"
        );
    });
});