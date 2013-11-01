<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	Vector vecSites = (Vector)request.getAttribute("SITES");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">

<adk:tbody>
<script language="javascript">
<adk:func name="doEdit"/>(id) {
var form = document.<adk:encodeNamespace value="frmEdit"/>;
form.siteid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="doView"/>(id) {
var form = document.<adk:encodeNamespace value="frmView"/>;
form.siteid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="doPubCfg"/>(id) {
var form = document.<adk:encodeNamespace value="frmPubCfg"/>;
form.siteid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="doDel"/>(id, name) {
if (!confirm([@CN:'要删除站点"' + name + '"吗？'@EN:'Delete site ' + name + '?']))
	return;
var form = document.<adk:encodeNamespace value="frmDel"/>;
form.siteid.value=id;
<adk:submit name="form"/>;
}
</script>
<adk:form name="frmNew" action="new">
</adk:form>
<adk:form name="frmEdit" action="edit">
<input type="hidden" name="siteid">
</adk:form>
<adk:form name="frmView" action="view">
<input type="hidden" name="siteid">
</adk:form>
<adk:form name="frmPubCfg" action="editpubcfg">
<input type="hidden" name="siteid">
</adk:form>
<adk:form name="frmDel" action="del">
<input type="hidden" name="siteid">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">&nbsp;</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmNew"/>"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/new.gif"/>">[@CN:新建@EN:New]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
<table border="1" cellspacing="0" cellpadding="4" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="99%" align="center">
	<tr class="lineHeader">
		<td width="100%" nowrap>[@EN:Name@CN:名称]</td>
		<td nowrap>[@CN:图标@EN:Logo]</td>
		<td colspan="3" align="center" nowrap>[@CN:操作]</td>
	</tr>
	<adk:iteration name="site" type="SiteSite" group="<%= vecSites %>">
	<tr valign="middle" class="line__normal">
		<td width="100%" nowrap><%= site.m_strSiteName %></td>
		<td><% if (site.m_lSiteLogoID > 0) { String path = "/res/img?id=" + site.m_lSiteLogoID; %><img src="<adk:url path="<%= path %>"/>"><% } %>&nbsp;</td>
		<td nowrap>
			<span class="trans_button__normal" onclick="<adk:func ref="doEdit"/>(<%= site.m_lSiteID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/edit.gif"/>" alt="[@CN:编辑@EN:Edit]"></span>
			<span class="trans_button__normal" onclick="<adk:func ref="doView"/>(<%= site.m_lSiteID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/site.gif"/>" alt="[@CN:管理分支@EN:Manage Branch]"></span>
			<span class="trans_button__normal" onclick="<adk:func ref="doPubCfg"/>(<%= site.m_lSiteID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/param.gif"/>" alt="[@CN:编辑发布配置@EN:Edit Publish Config]"></span>
			<span class="trans_button__normal" onclick="<adk:func ref="doDel"/>(<%= site.m_lSiteID %>, '<%= site.m_strSiteName %>')"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/delete.gif"/>" alt="[@CN:删除@EN:Delete]"></span>
		</td>
	</tr>
	</adk:iteration>
</table>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
