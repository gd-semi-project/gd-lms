export async function generateToken(ctx, userId) {
    try {
        const response = await fetch(`${ctx}/login/create-token`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
            },
            body: `userId=${encodeURIComponent(userId)}`
        });

        if (!response.ok) {
            throw new Error(`토큰 발급 실패: HTTP ${response.status}`);
        }

        const token = await response.json(); // 토큰 발급여부 반환
        return token;

    } catch (err) {
        console.error("generateToken 에러:", err);
        throw err; // 호출한 쪽에서 catch 처리 가능
    }
}
