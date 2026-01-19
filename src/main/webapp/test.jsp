<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  request.getRequestDispatcher("/WEB-INF/views/user/mypage_goheekwon.jsp")
         .forward(request, response);
%>
