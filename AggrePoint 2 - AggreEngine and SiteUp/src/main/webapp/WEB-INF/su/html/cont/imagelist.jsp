<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.su.core.data.*" %>
<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ApContent content = (ApContent)request.getAttribute("CONTENT");
	Vector vecReses = (Vector)request.getAttribute("RESES");
%>
var tinyMCEImageList = new Array(
<% for (Enumeration enm = vecReses.elements(); enm.hasMoreElements(); ) { ApRes res = (ApRes)enm.nextElement(); %>
        ["<%= res.getFileName() %>", "/ap2/res/cont/<%= content.getId() %>/<%= res.m_strFileName %>"]<% if (enm.hasMoreElements()) { %>,<% } %>
<% } %>
);