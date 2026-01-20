<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>오류 발생</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; background: #f5f5f5; }
        .error-container { max-width: 900px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h2 { color: #dc3545; margin-bottom: 20px; }
        .error-info { background: #fff3cd; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .error-info p { margin: 5px 0; }
        .stack-trace { background: #f8f9fa; padding: 15px; border-radius: 5px; overflow-x: auto; font-family: monospace; font-size: 12px; white-space: pre-wrap; word-wrap: break-word; max-height: 400px; overflow-y: auto; }
        .btn-back { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #6c757d; color: white; text-decoration: none; border-radius: 5px; }
        .btn-back:hover { background: #5a6268; }
    </style>
</head>
<body>
    <div class="error-container">
        <h2>⚠️ 오류가 발생했습니다</h2>
        
        <div class="error-info">
            <p><strong>에러 메시지:</strong> ${errorMessage}</p>
            <c:if test="${not empty exception}">
                <p><strong>예외 타입:</strong> ${exception.getClass().getName()}</p>
                <p><strong>예외 메시지:</strong> ${exception.message}</p>
            </c:if>
        </div>
        
        <h4>Stack Trace:</h4>
        <div class="stack-trace"><%
            Exception ex = (Exception) request.getAttribute("exception");
            if (ex != null) {
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                ex.printStackTrace(pw);
                out.print(sw.toString());
            } else {
                out.print("예외 정보가 없습니다.");
            }
        %></div>
        
        <a class="btn-back" href="javascript:history.back()">← 뒤로 가기</a>
    </div>
</body>
</html>
