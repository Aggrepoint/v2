<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.core.data.*" %>
<%
	Vector vecMaps = (Vector)request.getAttribute("MAPS");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">

<adk:tbody>
<script language="javascript">
<adk:func name="doEdit"/>(id) {
var form = document.<adk:encodeNamespace value="frmEdit"/>;
form.mapid.value=id;
<adk:submit name="form"/>;
}
<adk:func name="doDel"/>(id) {
if (!confirm("[@CN:要删除选定的映射吗？@EN:Delete selected mapping?]"))
return;

var form = document.<adk:encodeNamespace value="frmDel"/>;
form.mapid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="enable"/>(id) {
var form = document.<adk:encodeNamespace value="frmEnable"/>;
form.mapid.value=id;
form.actionid.value="1";
<adk:submit name="form"/>;
}

<adk:func name="disable"/>(id) {
var form = document.<adk:encodeNamespace value="frmEnable"/>;
form.mapid.value=id;
form.actionid.value="0";
<adk:submit name="form"/>;
}
</script>
<adk:form name="frmEdit" action="editmap">
<input type="hidden" name="mapid">
</adk:form>
<adk:form name="frmDel" action="delmap">
<input type="hidden" name="mapid">
</adk:form>
<adk:form name="frmEnable" action="enablemap">
<input type="hidden" name="mapid">
<input type="hidden" name="actionid">
</adk:form>
<adk:form name="frmGoBack" action="gobranchlist">
</adk:form>
<adk:form name="frmNewMap" action="newmap">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">[@CN:路径映射@EN:Path Mapping]&nbsp;</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmGoBack"/>"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:返回@EN:Back]</span>
			<span class="button__normal" onclick="<adk:submit form="frmNewMap"/>"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/new.gif"/>">[@CN:新建映射@EN:New Mapping]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
<table border="1" cellspacing="0" cellpadding="4" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="99%" align="center">
	<tr class="lineHeader">
		<td width="50%" nowrap>[@CN:源路径（正则表达式）@EN:Source Path(Regexp)]</td>
		<td width="50%" nowrap>[@CN:映射路径@EN:Map to Path]</td>
		<td colspan="3" align="center" nowrap>[@CN:操作]</td>
	</tr>
	<adk:iteration name="map" type="SitePathMap" group="<%= vecMaps %>">
	<tr valign="middle" class="line__normal" valign="center">
		<td width="50%" nowrap><%= map.m_strFromPath %></td>
		<td width="50%" nowrap><%= map.m_strToPath %></td>
		<td nowrap>
			<span class="trans_button__normal" onclick="<adk:func ref="doEdit"/>(<%= map.m_lMapID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/edit.gif"/>" alt="[@CN:编辑@EN:Edit]"></span>
			<% if (map.m_iStatusID != SitePathMap.STATUS_ENABLE) { %>
			<span class="trans_button__normal" onclick="<adk:func ref="enable"/>(<%= map.m_lMapID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/right.gif"/>" alt="[@CN:启用@EN:Enable]"></span>
			<% } else { %>
			<span class="trans_button__normal" onclick="<adk:func ref="disable"/>(<%= map.m_lMapID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>" alt="[@CN:禁用@EN:Disable]"></span>
			<% } %>
			<span class="trans_button__normal" onclick="<adk:func ref="doDel"/>(<%= map.m_lMapID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/delete.gif"/>" alt="[@CN:删除@EN:Delete]"></span>
		</td>
	</tr>
	</adk:iteration>
</table>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
