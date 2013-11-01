<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SiteBranch branch = (SiteBranch)request.getAttribute("BRANCH");
	Vector vecOptions = (Vector)request.getAttribute("OPTIONS");
	Vector vecBranches = (Vector)request.getAttribute("BRANCHES");
	Vector errMsgs = (Vector)request.getAttribute("ERRORMSG");
	String strOptionHTML = "<select name='branchid'>";
	for (Enumeration enu = vecOptions.elements(); enu.hasMoreElements(); ) {
		SiteBranch option = (SiteBranch)enu.nextElement();
		strOptionHTML += "<option value='" + option.m_lBranchID + "'>" + option.m_strBranchName + "</option>";
	}
	strOptionHTML += "</select>";
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
	validateEmpty(form.name, "[@CN:请输入分支名称。@EN:Please input branch name.]");
	return validateEnd();
}

<adk:func name="addBranch"/>() {
	var tr = document.all.branchTable.insertRow();
	tr.addBehavior("<adk:resUrl res="/siteup/theme/delself.htc"/>");
	var td = tr.insertCell();
	td.innerHTML = "<%=strOptionHTML%>";
	td = tr.insertCell();
	td.innerHTML = "<input type='text' size='2' name='order' maxlength='2' value='99'>";
	td = tr.insertCell();
	td.innerHTML = "<input type='text' size='30' name='rule'>";
	td = tr.insertCell();
	td.innerHTML = "<span class='button__normal'><img border='0' align=absmiddle delself='yes' src='<adk:resUrl res="/siteup/images/del.gif"/>'></span>";
}
</script>
<adk:form name="frmEdit" action="savegroupbranch" focus="name" validate="validateFrmEdit">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:名称@EN:Name]</td>
		<td class="lineContent" nowrap><input type="text" name="name" value="<%=branch.m_strBranchName%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:根栏目路径@EN:Root Path]</td>
		<td class="lineContent" nowrap><input type="text" name="rootpath" size="20" value="<%=branch.m_strRootPath%>"></td>
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
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:私有个性化规则@EN:Collaborate PSN Rule]</td>
		<td class="lineContent" nowrap><input type="text" name="pvtpsnrule" size="80" value="<%=branch.m_strPvtPsnRule%>"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:分支选择结果@EN:Branch Select]</td>
		<td class="lineContent" nowrap><input type="radio" name="savesel" value="0"<%if (branch.m_iMarkupType == 0) {%> checked<%}%>>[@CN:不保存在会话中@EN:Don't save in session]<input type="radio" name="savesel" value="1"<%if (branch.m_iMarkupType == 1) {%> checked<%}%>>[@CN:保存在会话中@EN:Save in session]</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right" valign="top">[@CN:包含分支@EN:Includes] <span class="button__normal" onclick="<adk:encodeNamespace value="addBranch"/>()"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/plus.gif"/>"></span></td>
		<td class="lineContent" nowrap>
		<table border="1" cellspacing="0" cellpadding="2" width="100%" id="branchTable">
			<tr><td width="10%" nowrap>[@CN:分支@EN:Branch]</td><td nowrap>[@CN:顺序@EN:Order]</td><td width="80%" nowrap>[@CN:适配规则@EN:Adaptation Rule]</td><td>&nbsp;</td></tr><%
				if (vecBranches != null) for (Enumeration enu = vecBranches.elements(); enu.hasMoreElements(); ) { SiteBranchGroup group = (SiteBranchGroup)enu.nextElement();
			%>
			<tr style="behavior:url(<adk:resUrl res="/siteup/theme/delself.htc"/>)">
				<td>
					<select name="branchid"><%
						if (vecOptions != null) for (Enumeration enu2 = vecOptions.elements(); enu2.hasMoreElements(); ) { SiteBranch option = (SiteBranch) enu2.nextElement();
					%>
						<option value="<%=option.m_lBranchID%>" <%if (group.m_lBranchID == option.m_lBranchID) {%>selected<%}%>><%=option.m_strBranchName%></option><%
							}
						%>
					</select>
				</td>
				<td><input type="text" size="2" name="order" value="<%=group.m_iOrder%>"></td>
				<td><input type="text" size="30" name="rule" value="<%=group.m_strRule%>"></td>
				<td><span class="button__normal"><img border="0" align=absmiddle delself="yes" src="<adk:resUrl res="/siteup/images/del.gif"/>"></span></td>
			</tr><%
				}
			%>
		</table>
		</td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
