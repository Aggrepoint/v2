<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SiteSite site = (SiteSite)request.getAttribute("SITE");
	SitePublishConfig pubcfg = (SitePublishConfig)request.getAttribute("PUBCFG");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">
<script	language="javascript" src="<adk:resUrl res="/siteup/theme/validate.js"/>"></script>

<adk:tbody>
<adk:form name="frmGoHome" action="home">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">[|<%=site.m_strSiteName%>@CN:设置站点(0)发布配置@EN:Publish Config of Site (0)]</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmGoHome"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
			<span class="button__normal" onclick="<adk:submit form="frmEdit"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/save.gif"/>">[@CN:保存@EN:Save]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>

<script language="javascript">
<adk:func name="validateFrmEdit"/>(form) {
	validateBegin();
	validateEmpty(form.rooturl, "[@CN:请输入客户端访问已发布的网站页面的根URL。@EN:Please input the root page URL.]");
	validateEmpty(form.ext, "[@CN:请输入发布生成的页面文件的后缀。@EN:Please input the generated file extension.]");
	return validateEnd();
}

<adk:func name="validateFrmAdd"/>(form) {
	validateBegin();
	validateEmpty(form.path, "[@CN:请输入发布网站页面的路径。@EN:Please input publish page path.]");
	return validateEnd();
}
</script>
<adk:form name="frmEdit" action="savepubcfg" focus="rooturl" validate="validateFrmEdit">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="center">[@CN:参数名称@EN:Parameter Name]</td>
		<td class="lineHeader" nowrap align="center">[@CN:参数值@EN:Parameter Value]</td>
	</tr>
	<tr>
		<td class="lineContent" width="20%" nowrap align="right">客户端访问已发布的网站页面的根URL</td>
		<td class="lineContent" nowrap><input type="text" size="20" name="rooturl" value="<%=pubcfg.m_strSiteRootUrl%>"></td>
	</tr>
	<tr>
		<td class="lineContent" width="20%" nowrap align="right">发布生成的页面文件的后缀</td>
		<td class="lineContent" nowrap><input type="text" size="10" name="ext" maxlength="20" value="<%=pubcfg.m_strPageExtention%>"></td>
	</tr>
	<adk:iteration name="path" type="String" group="<%= pubcfg.m_vecSitePubPath %>">
	<tr>
		<td class="lineContent" width="20%" nowrap align="right">发布网站页面的路径</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="path" value="<%=path%>"></td>
	</tr>
	</adk:iteration>
</table>
<input type="image" width="0" height="0">
</adk:form>
<adk:form name="frmAdd" action="addpubpath" validate="validateFrmAdd">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineContent" width="20%" nowrap align="right">添加发布网站页面的路径</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="path"></td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
