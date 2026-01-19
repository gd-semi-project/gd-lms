<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  request.getRequestDispatcher("/WEB-INF/test.jsp")
         .forward(request, response);
%>

 