<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SiteSite site = (SiteSite)request.getAttribute("SITE");
	Vector errMsgs = (Vector)request.getAttribute("ERRORMSG");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">
<script	language="javascript" src="<adk:resUrl res="/siteup/theme/validate.js"/>"></script>

<adk:tbody>
<adk:form name="frmGoHome" action="home">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%"><%
			if (site.m_lSiteID == -1) {
		%>[@CN:新建@EN:New]<%
			} else {
		%>[@CN:编辑@EN:Edit]<%
			}
		%>[@CN:站点@EN: Site]</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmGoHome"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
			<span class="button__normal" onclick="<adk:submit form="frmEdit"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/save.gif"/>">[@CN:保存@EN:Save]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
<%
	if (errMsgs != null) {
%>
<table border="0" cellspacing="0" cellpadding="0" width="50%" align="center">
	<tr><td><adk:vector name="msg" type="String" vector="<%= errMsgs %>"><font color="red"><%=msg%></font><br></adk:vector></td></tr>
</table>
<%
	}
%>

<script language="javascript">
<adk:func name="validateFrmEdit"/>(form) {
	validateBegin();
	validateEmpty(form.name, "[@CN:请输入站点名称。@EN:Please input site name.]");
	return validateEnd();
}
</script>
<adk:form name="frmEdit" action="save" method="post" focus="name" enctype="multipart/form-data" validate="validateFrmEdit">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:站点名称@EN:Site Name]</td>
		<td class="lineContent" nowrap><input type="text" name="name" value="<%=site.m_strSiteName%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:站点说明@EN:Description]</td>
		<td class="lineContent" nowrap><textarea name="desc" cols="30" rows="3"><%=site.m_strSiteDesc%></textarea></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:管理规则@EN:Manage Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="rule" value="<%=site.m_strManageRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right" valign="top">[@CN:站点图标@EN:Logo]</td>
		<td class="lineContent" nowrap><input type="file" name="file"><%
			if (site.m_lSiteLogoID > 0) { String path = "/res/img?id=" + site.m_lSiteLogoID;
		%><br><img src="<adk:url path="<%= path %>"/>"><%
			}
		%></td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
