<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.adk.EnumMarkup" %>
<%@ page import="com.aggrepoint.adk.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SiteSite site = (SiteSite)request.getAttribute("SITE");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">

<adk:tbody>
<script language="javascript">
<adk:func name="doEditBranch"/>(id) {
var form = document.<adk:encodeNamespace value="frmEditBranch"/>;
form.branchid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="doDelBranch"/>(id) {
if (!confirm('[@CN:要删除选定的分支吗？@EN:Delete selected branch?]'))
	return;
var form = document.<adk:encodeNamespace value="frmDelBranch"/>;
form.branchid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="doListMap"/>(id) {
var form = document.<adk:encodeNamespace value="frmListMap"/>;
form.branchid.value=id;
<adk:submit name="form"/>;
}
</script>
<adk:form name="frmNewBranch" action="newbranch">
<input type="hidden" name="siteid" value="<%= site.m_lSiteID %>">
</adk:form>
<adk:form name="frmEditBranch" action="editbranch">
<input type="hidden" name="branchid">
</adk:form>
<adk:form name="frmDelBranch" action="delbranch">
<input type="hidden" name="branchid">
</adk:form>
<adk:form name="frmListMap" action="listmap">
<input type="hidden" name="branchid">
</adk:form>
<adk:form name="frmGoHome" action="home">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">[@CN:站点分支管理:@EN:Site Branch Management:] <%= site.m_strSiteName %></td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmGoHome"/>"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
			<span class="button__normal" onclick="<adk:submit form="frmNewBranch"/>"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/new.gif"/>">[@CN:新建分支@EN:New Branch]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
<table border="1" cellspacing="0" cellpadding="4" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="99%" align="center">
	<tr bgcolor="#9D9DFF">
		<td width="10%" nowrap>[@CN:名称@EN:Name]</td>
		<td width="10%" nowrap>[@CN:类型@EN:Type]</td>
		<td width="10%" nowrap>[@CN:目录@EN:Path]</td>
		<td width="10%" nowrap>[@CN:主页@EN:Home Page]</td>
		<td width="10%" nowrap>[@CN:登录页@EN:Login Page]</td>
		<td width="10%" nowrap>[@CN:无权访问页@EN:No Access Page]</td>
		<td width="40%" nowrap>[@CN:说明@EN:Description]</td>
		<td colspan="3" align="center" nowrap>[@CN:操作]</td>
	</tr>
	<adk:iteration name="branch" type="SiteBranch" group="<%= site.m_vecBranches %>">
	<tr valign="middle" class="line__normal">
		<td width="10%" nowrap>[<%= branch.m_lBranchID %>]<%= branch.m_strBranchName %>&nbsp;</td>
		<td width="10%" nowrap><% if (branch.m_iPsnType != SiteBranch.PSN_TYPE_GROUP) { %><%= branch.m_iMarkupType == EnumMarkup.HTML.getId() ? "HTML" : (branch.m_iMarkupType == EnumMarkup.XHTML.getId() ? "XHTML" : "WML") %><% } %> <% if (branch.m_iPsnType == SiteBranch.PSN_TYPE_STATIC) { %>[@CN:静态@EN:Static]<% } else if (branch.m_iPsnType == SiteBranch.PSN_TYPE_DYNAMIC) { %>[@CN:动态@EN:Dynamic]<% } else { %>[@CN:组合@EN:Combination]<% } %>&nbsp;</td>
		<td width="10%" nowrap><%= branch.m_strRootPath %>&nbsp;</td>
		<td width="10%" nowrap><%= branch.m_strHomePath %>&nbsp;</td>
		<td width="10%" nowrap><%= branch.m_strLoginPath %>&nbsp;</td>
		<td width="10%" nowrap><%= branch.m_strNoAccessPath %>&nbsp;</td>
		<td width="40%" nowrap><%= branch.m_strBranchDesc %>&nbsp;</td>
		<td nowrap align="center">
			<span class="trans_button__normal" onclick="<adk:func ref="doEditBranch"/>(<%= branch.m_lBranchID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/edit.gif"/>" alt="[@CN:编辑@EN:Edit]"></span><% if (branch.m_iPsnType != SiteBranch.PSN_TYPE_GROUP) { %>
			<span class="trans_button__normal" onclick="<adk:func ref="doListMap"/>(<%= branch.m_lBranchID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/map.gif"/>" alt="[@CN:映射@EN:Mapping]"></span><% } %>
			<span class="trans_button__normal" onclick="<adk:func ref="doDelBranch"/>(<%= branch.m_lBranchID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/delete.gif"/>" alt="[@CN:删除@EN:Delete]"></span>
		</td>
	</tr>
	</adk:iteration>
</table>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
