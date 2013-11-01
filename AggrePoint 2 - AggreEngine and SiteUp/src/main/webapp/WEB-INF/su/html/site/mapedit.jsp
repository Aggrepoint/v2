<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	SitePathMap map = (SitePathMap)request.getAttribute("MAP");
	Vector errMsgs = (Vector)request.getAttribute("ERRORMSG");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">
<script	language="javascript" src="<adk:resUrl res="/siteup/theme/validate.js"/>"></script>

<adk:tbody>
<adk:form name="frmGoList" action="golist">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%"><%
			if (map.m_lMapID == 0) {
		%>[@CN:新建@EN:New]<%
			} else {
		%>[@CN:编辑@EN:Edit]<%
			}
		%>[@CN:路径映射@EN: Path Mapping]</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmGoList"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
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
	validateEmpty(form.from, "[@CN:请输入源路径。@EN:Please input source path.]");
	validateEmpty(form.to, "[@CN:请输入目的路径。@EN:Please input target path.]");
	return validateEnd();
}
</script>
<adk:form name="frmEdit" action="savemap" focus="from" validate="validateFrmEdit">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:正向映射模式（正则表达式）@EN:Mapping Pattern(Regexp)]</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="from" value="<%=map.m_strFromPath%>">[@CN:（不包括站点根路径，无需以‘/’开头和结尾）@EN:(Not including site root path, not started or ended with '/']</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:正向映射路径@EN:Mapping Path]</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="to" value="<%=map.m_strToPath%>"><br>[@CN:（不包括站点根路径，无需以‘/’开头和结尾，中括号[]用于引用正向模式的取值）]</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:反向映射模式（正则表达式）@EN:Reverse Mapping Pattern(Regexp)]</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="fromlink" value="<%=map.m_strFromLink%>">[@CN:（不包括站点根路径，无需以‘/’开头和结尾）]</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:反向映射路径@EN:Reverse Mapping Path]</td>
		<td class="lineContent" nowrap><input type="text" size="50" name="tolink" value="<%=map.m_strToLink%>"><br>[@CN:（不包括站点根路径，无需以‘/’开头和结尾，中括号[]用于引用正向模式的取值，打括号{}用于引用反向模式的取值）]</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right" rowspan="5">[@CN:参数名称@EN:Parameter Name]</td>
		<td class="lineContent" nowrap>1：<input type="text" name="pname1" value="<%=map.m_strParamName1%>"></td>
	</tr>
	<tr>
		<td class="lineContent" nowrap>2：<input type="text" name="pname2" value="<%=map.m_strParamName2%>"></td>
	</tr>
	<tr>
		<td class="lineContent" nowrap>3：<input type="text" name="pname3" value="<%=map.m_strParamName3%>"></td>
	</tr>
	<tr>
		<td class="lineContent" nowrap>4：<input type="text" name="pname4" value="<%=map.m_strParamName4%>"></td>
	</tr>
	<tr>
		<td class="lineContent" nowrap>5：<input type="text" name="pname5" value="<%=map.m_strParamName5%>"></td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
