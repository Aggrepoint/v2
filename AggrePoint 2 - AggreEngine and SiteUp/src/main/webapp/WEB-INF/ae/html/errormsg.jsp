<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.aggrepoint.adk.*" %>
<%@ page import="com.aggrepoint.adk.data.*" %>
<%
	RetCode retCode = (RetCode)com.icebean.core.common.ThreadContext.getAttribute(IAdkConst.THREAD_ATTR_RETCODE);
%>
<style type="text/css">
<!--
td {  font-family: "Tahoma", "Verdana"; font-size: 9pt}
-->
</style>
<table border="0" cellspacing="1" cellpadding="1" align="center">
  <tr>
    <td align="center"><img src="<adk:resUrl res="/ae/images/error.jpg"/>"></td>
  </tr>
  <tr> 
    <td nowrap><%= retCode.m_strUserMessage %></td>
  </tr>
</table>
