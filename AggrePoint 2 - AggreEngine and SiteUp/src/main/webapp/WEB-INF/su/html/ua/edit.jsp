<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.adk.EnumMarkup" %>
<%@ page import="com.aggrepoint.core.data.*, com.icebean.core.common.*, com.aggrepoint.adk.*" %>
<%
	UserAgent ua = (UserAgent)request.getAttribute("UA");
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">
<script	language="javascript" src="<adk:resUrl res="/siteup/theme/validate.js"/>"></script>

<adk:tbody>
<adk:form name="frmCancel" action="cancel">
</adk:form>

<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">[@CN:编辑@EN:Edit ]User Agent</td>
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmCancel"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/back.gif"/>">[@CN:取消@EN:Cancel]</span>
			<span class="button__normal" onclick="<adk:submit form="frmEdit"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/save.gif"/>">[@CN:保存@EN:Save]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>

<adk:form name="frmEdit" action="save">
<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="50%" align="center">
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">User Agent</td>
		<td class="lineContent"><%=ua.userAgent%><br><%=ua.accept%></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:XHTML内容类型@EN:XHTML Content Type]</td>
		<td class="lineContent" nowrap><input type="text" name="xhtmltype" value="<%=ua.xhtmlType%>" size="20"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:设备类型@EN:Device Type]</td>
		<td class="lineContent"><%
			for (EnumDevice e : EnumDevice.values()) {
		%><input type="radio" name="devtype" value="<%=e.getId()%>" <%if (ua.deviceType == e) {%>checked<%}%>><%=e.getKey()%><%
			}
		%></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:设备型号@EN:Device Model]</td>
		<td class="lineContent" nowrap><input type="text" name="devmodel" value="<%=ua.deviceModel%>" size="20"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:设备版本号@EN:Device Version]</td>
		<td class="lineContent" nowrap><input type="text" name="devver" value="<%=ua.deviceVersion%>" size="10"> [@CN:主@EN:Major]：<input type="text" name="devmajver" value="<%=ua.deviceMajorVersion%>" size="5">[@CN:次@EN:Minor]：<input type="text" name="devminver" value="<%=ua.deviceMinorVersion%>" size="5"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:操作系统类型@EN:OS Type]</td>
		<td class="lineContent"><%
			for (EnumOS e : EnumOS.values()) {
		%><input type="radio" name="ostype" value="<%=e.getId()%>" <%if (ua.osType == e) {%>checked<%}%>><%=e.getKey()%><%
			}
		%></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:操作系统版本号@EN:OS Version]</td>
		<td class="lineContent" nowrap><input type="text" name="osver" value="<%=ua.osVersion%>" size="10"> [@CN:主@EN:Major]：<input type="text" name="osmajver" value="<%=ua.osMajorVersion%>" size="5">[@CN:次@EN:Minor]：<input type="text" name="osminver" value="<%=ua.osMinorVersion%>" size="5"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:浏览器类型@EN:Browser Type]</td>
		<td class="lineContent"><%
			for (EnumBrowser e : EnumBrowser.values()) {
		%><input type="radio" name="btype" value="<%=e.getId()%>" <%if (ua.browserType == e) {%>checked<%}%>><%=e.getKey()%><%
			}
		%></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:浏览器版本号@EN:Browser Version]</td>
		<td class="lineContent" nowrap><input type="text" name="bver" value="<%=ua.browserVersion%>" size="10"> [@CN:主@EN:Major]：<input type="text" name="bmajver" value="<%=ua.browserMajorVersion%>" size="5">[@CN:次@EN:Minor]：<input type="text" name="bminver" value="<%=ua.browserMinorVersion%>" size="5"></td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:能力@EN:Capability]</td>
		<td class="lineContent" nowrap>
			<input type="checkbox" name="html" value="1" <%if (ua.supportHTML) {%>checked<%}%>>[@CN:支持HTML@EN:Support HTML]
			<input type="checkbox" name="xhtml" value="1" <%if (ua.supportXHTML) {%>checked<%}%>>[@CN:支持XHTML@EN:Support XHTML]
			<input type="checkbox" name="wml" value="1" <%if (ua.supportWML) {%>checked<%}%>>[@CN:支持WML@EN:Support WML]
			<input type="checkbox" name="ajx" value="1" <%if (ua.supportAjax) {%>checked<%}%>>[@CN:支持Ajax@EN:Support Ajax]
			<input type="checkbox" name="spider" value="1" <%if (ua.isSpider) {%>checked<%}%>>Spider
			<input type="checkbox" name="mobile" value="1" <%if (ua.isMobile) {%>checked<%}%>>[@CN:是移动设备@EN:Is Mobile Device]
		</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:缺省标记语言@EN:Default Markup]</td>
		<td class="lineContent" nowrap>
			<select name="markup">
				<option value="0"<%if (ua.defaultMarkup == EnumMarkup.HTML.getId()) {%> selected<%}%>>HTML</option>
				<option value="2"<%if (ua.defaultMarkup == EnumMarkup.XHTML.getId()) {%> selected<%}%>>XHTML</option>
				<option value="1"<%if (ua.defaultMarkup == EnumMarkup.WML.getId()) {%> selected<%}%>>WML</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="lineHeader" width="20%" nowrap align="right">[@CN:识别方式@EN:Recognize Method]</td>
		<td class="lineContent" nowrap>
			<%
				if (ua.autoFlag) {
			%>[@CN:自动识别@EN:Auto]<%
				} else {
			%>[@CN:手工确认@EN:Manual Confirmed]<%
				}
			%>
		</td>
	</tr>
</table>
<input type="image" width="0" height="0">
</adk:form>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
