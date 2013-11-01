<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">

<adk:tbody>
<adk:form name="frmBack" action="gobranchlist">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">[@CN:选择分支类型@EN:Select Branch Type]</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmBack"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
			<span class="button__normal" onclick="<adk:submit form="frmSel"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/save.gif"/>">[@CN:选择@EN:Select]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
<adk:form name="frmSel" action="selbranchtype">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:类型:@EN:Type:]</td>
		<td class="lineContent" nowrap>
			<input type="radio" name="typeid" value="0" checked>[@CN:静态@EN:Static]
			<input type="radio" name="typeid" value="1">[@CN:动态@EN:Dynamic]
			<input type="radio" name="typeid" value="2">[@CN:组合@EN:Combination]
		</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:标记语言:@EN:Markup:]</td>
		<td class="lineContent" nowrap>
			<input type="radio" name="markup" value="0" checked>HTML
			<input type="radio" name="markup" value="1">WML
			<input type="radio" name="markup" value="2">XHTML
		</td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
