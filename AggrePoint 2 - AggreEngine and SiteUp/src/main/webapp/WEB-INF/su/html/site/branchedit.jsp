<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.adk.EnumMarkup" %>
<%@ page import="com.aggrepoint.adk.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SiteBranch branch = (SiteBranch)request.getAttribute("BRANCH");
	SiteBPage rootPage = (SiteBPage)request.getAttribute("ROOTPAGE");
	Vector vecTemplates = (Vector)request.getAttribute("TEMPLATES");
	Vector errMsgs = (Vector)request.getAttribute("ERRORMSG");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">
<script	language="javascript" src="<adk:resUrl res="/siteup/theme/validate.js"/>"></script>

<adk:tbody>
<adk:form name="frmBack" action="gobranchlist">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%"><%
			if (branch.m_lBranchID == -1) {
		%>[@CN:新建@EN:New]<%
			} else {
		%>[@CN:编辑@EN:Edit]<%
			}
		%>[@CN:站点分支@EN: Site Branch]</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmBack"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
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
	validateEmpty(form.name, "[@CN:请输入分支名称。@EN:Plesae input branch name.]");
	return validateEnd();
}
</script>
<adk:form name="frmEdit" action="savebranch" focus="name" validate="validateFrmEdit">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:名称@EN:Name]</td>
		<td class="lineContent" nowrap><input type="text" name="name" value="<%=branch.m_strBranchName%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:类型@EN:Type]</td>
		<td class="lineContent" nowrap><%=branch.m_iMarkupType == EnumMarkup.HTML.getId() ? "HTML" : (branch.m_iMarkupType == EnumMarkup.XHTML.getId() ? "XHTML" : "WML")%> <%
 	if (branch.m_iPsnType == SiteBranch.PSN_TYPE_STATIC) {
 %>[@CN:静态@EN:Static]<%
 	} else {
 %>[@CN:动态@EN:Dynamic]<%
 	}
 %></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:根栏目路径@EN:Root Path Path]</td>
		<td class="lineContent" nowrap><input type="text" name="rootpath" size="20" value="<%=rootPage.m_strPathName%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:说明@EN:Description]</td>
		<td class="lineContent" nowrap><textarea name="desc" cols="80" rows="5" wrap="OFF"><%=branch.m_strBranchDesc%></textarea></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:访问规则@EN:Access Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="accessrule" size="80" value="<%=branch.m_strAccessRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:管理规则@EN:Manage Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="managerule" size="80" value="<%=branch.m_strManageRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:协作个性化规则@EN:Collaborate PSN Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="clbpsnrule" size="80" value="<%=branch.m_strClbPsnRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:私有个性化规则@EN:Private PSN Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="pvtpsnrule" size="80" value="<%=branch.m_strPvtPsnRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:主页面路径@EN:Home Page Path]</td>
		<td class="lineContent" nowrap><input type="text" name="homepath" size="80" value="<%=branch.m_strHomePath%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:登录页面路径@EN:Login Page Path]</td>
		<td class="lineContent" nowrap><input type="text" name="loginpath" size="80" value="<%=branch.m_strLoginPath%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:无权访问页面路径@EN:No Access Page Path]</td>
		<td class="lineContent" nowrap><input type="text" name="noaccesspath" size="80" value="<%=branch.m_strNoAccessPath%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:管理模板@EN:Admin Template]</td>
		<td class="lineContent" nowrap>
			<select name="admintmpl"><%
				for (Enumeration enm = vecTemplates.elements(); enm.hasMoreElements(); ) { SiteTemplate tmpl = (SiteTemplate)enm.nextElement(); if (tmpl.m_iMarkupType != EnumMarkup.HTML.getId()) continue;
			%>
				<option value="<%=tmpl.m_lTemplateID%>" <%if (branch.m_lTemplateID == tmpl.m_lTemplateID) {%>selected<%}%>>(<%
					if (tmpl.isDynamic()) {
				%>[@CN:动态@EN:Dynamic]<%
					} else {
				%>[@CN:静态@EN:Static]<%
					}
				%>)<%=tmpl.m_strTmplName%></option><%
					}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:缺省模板@EN:Default Tempalte]</td>
		<td class="lineContent" nowrap>
			<select name="tmpl"><%
				for (Enumeration enm = vecTemplates.elements(); enm.hasMoreElements(); ) { SiteTemplate tmpl = (SiteTemplate)enm.nextElement(); if (tmpl.m_iMarkupType != branch.m_iMarkupType) continue;
			%>
				<option value="<%=tmpl.m_lTemplateID%>" <%if (rootPage.m_lTemplateID == tmpl.m_lTemplateID) {%>selected<%}%>>(<%
					if (tmpl.isDynamic()) {
				%>[@CN:动态@EN:Dynamic]<%
					} else {
				%>[@CN:静态@EN:Static]<%
					}
				%>)<%=tmpl.m_strTmplName%></option><%
					}
				%>
			</select>
		</td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
