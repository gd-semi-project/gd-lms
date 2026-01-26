export async function checkUserInfo(ctx, email, birthDate) {
    try {
        const res = await fetch(`${ctx}/login/check-info`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
            body: `email=${encodeURIComponent(email)}&birthDate=${encodeURIComponent(birthDate)}`
        });

        if (!res.ok) throw new Error("정보 검증 실패: " + res.status);
        const data = await res.json();
        return data.match;

    } catch (err) {
        console.error("checkUserInfo 에러:", err);
        throw err;
    }
}