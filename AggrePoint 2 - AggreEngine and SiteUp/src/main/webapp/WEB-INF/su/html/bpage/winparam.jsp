<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.su.core.data.*" %>
<%
	ApApp app = (ApApp)request.getAttribute("APP");
	ApWindow win = (ApWindow)request.getAttribute("WINDOW");
	Vector vecFrames = (Vector)request.getAttribute("FRAMES");
	ApBPageContent cont = (ApBPageContent)request.getAttribute("CONTENT");
	boolean bIsNew = (cont.m_lPageContID <= 0);
%>
<adk:tbody>
<html>
<head>
<title><% if (bIsNew) { %>[@CN:指定窗口插入参数@EN:Specify Window Parameters]<% } else { %>[@CN:编辑窗口@EN:Edit Window]<% } %></title>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/su/theme/popup.css"/>">
<% if (bIsNew) { %>
<script language="javascript">
function goBack() {
	document.frmEdit.action="/ap2/app/su/winins_sel";
	document.frmEdit.submit();
}
</script>
<% } %>
</head>
<body leftmargin="0" topmargin="0">
<span id="<adk:encodeNamespace value="main"/>">
<form id="frmEdit" name="frmEdit" action="<%= bIsNew ? "/ap2/app/su/winins_insert" : "/ap2/app/su/winedit_update" %>" method="post">
<input type="hidden" name="pcontid" value="<%= cont.m_lPageContID %>">
<input type="hidden" name="pid" value="<%= cont.m_lPageID %>">
<input type="hidden" name="areaname" value="<%= cont.m_strAreaName %>">
<input type="hidden" name="zoneid" value="<%= cont.m_iZoneID %>">
<input type="hidden" name="container" value="<%= cont.m_lContainContID %>">
<input type="hidden" name="winid" value="<%= win.m_lWindowID %>">
<table border="0" cellspacing="10" cellpadding="0" width="100%" align="center">
	<tr>
		<td width="10%" valign="top">
			<table border="0" cellspacing="0" cellpadding="0" width="100%" align="center">
				<tr>
					<td class="box3_lt"><img width="5" src="<adk:resUrl res="/su/images/blank.gif"/>"></td>
					<td align="left" nowrap class="box3_t" width=100%><%= app.m_strAppName %></td>
					<td class="box3_rt"><img width="5" src="<adk:resUrl res="/su/images/blank.gif"/>"></td>
				</tr>
				<tr>
					<td colspan="3" class="box3_c"><% if (app.m_lLogoImageID > 0) { String path = "/res/img?id=" + app.m_lLogoImageID; %><img align="left" src="<adk:url path="<%= path %>"/>"><% } %><%= app.getDesc() %>&nbsp;
						<table border="0" cellspacing="0" cellpadding="0" width="100%" align="center">
							<tr><td nowrap><%= win.m_strName %><% if (win.m_lPreviewImageID > 0) { String path = "/res/img?id=" + win.m_lPreviewImageID; %><br><img src="<adk:url path="<%= path %>"/>"><% } else { %>&nbsp;<% } %></td></tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
		<td width="90%">
			<table border="1" cellspacing="0" cellpadding="3" bgcolor="#C1C1FF"	bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="100%" align="center">
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right">[@CN:窗口标题@EN:Window Title]</td>
					<td class="lineContent" nowrap><input type="text" name="title" size="45" value="<%= cont.m_strContName %>"></td>
				</tr>
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right">[@CN:访问规则@EN:Access Rule]</td>
					<td class="lineContent" nowrap><textarea name="accessrule" rows="3" cols="45"><%= cont.m_strAccessRule %></textarea></td>
				</tr>
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right">[@CN:继承给子栏目@EN:Inherit to Sub]</td>
					<td class="lineContent" nowrap><input type="radio" name="inherit" value="0" <% if (!cont.m_bInheritable) { %>checked<% } %>>[@CN:不继承@EN:No] <input type="radio" name="inherit" value="1" <% if (cont.m_bInheritable) { %>checked<% } %>>[@CN:继承@EN:Yes]</td>
				</tr>
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right">[@CN:弹出窗口@EN:Popup Window]</td>
					<td class="lineContent" nowrap><input type="radio" name="popwin" value="0" <% if (!cont.m_bPopWinFlag) { %>checked<% } %>>[@CN:不弹出@EN:No] <input type="radio" name="popwin" value="1" <% if (cont.m_bPopWinFlag) { %>checked<% } %>>[@CN:弹出@EN:Yes]</td>
				</tr><% if (win.getParams().size() > 0) { %>
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right" valign="middle">[@CN:参数@EN:Parameter]</td>
					<td class="lineContent" nowrap>
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<tr bgcolor="white"><td width="10%" nowrap>[@CN:名称@EN:Name]</td><td width="30%" nowrap>[@CN:说明@EN:Description]</td><td width="60%" nowrap>[@CN:取值@EN:Value]</td></tr>
						<adk:iteration name="param" type="ApWinParam" group="<%= win.getParams() %>">
						<tr bgcolor="white"><td nowrap><%= param.m_strParamName %><input type="hidden" name="paramname" value="<%= param.m_strParamName %>">&nbsp;</td><td nowrap><%= param.m_strParamDesc %>&nbsp;</td><td><input type="text" size="50" name="paramvalue" value="<%= cont.getWinParam(param.m_strParamName, param.m_strDefaultValue) %>"></td></tr>
						</adk:iteration>
					</table>
					</td>
				</tr><%
					}
				%>
				<tr>
					<td class="lineHeader" width="20%" nowrap align="right" valign="middle">[@CN:窗框@EN:Frame]</td>
					<td class="lineContent" nowrap>
					<table border="0" cellspacing="2" cellpadding="2" width="100%">
						<adk:iteration name="frame" type="ApFrame" group="<%= vecFrames %>">
						<tr bgcolor="white">
							<td><input type="radio" name="frameid" value="<%=frame.m_lFrameID%>" <%if (cont.m_lFrameID == frame.m_lFrameID) {%>checked<%}%>>&nbsp;</td>
							<td width="100%" nowrap><%=frame.m_strFrameName%><%
								if (frame.m_lPreviewImgID > 0) { String path = "/res/img?id=" + frame.m_lPreviewImgID;
							%><br><img src="<adk:url path="<%= path %>"/>"><%
								}
							%>&nbsp;</td>
						</tr>
						</adk:iteration>
					</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="5"></tr>
	<tr>
		<td nowrap align="center"><%
			if (bIsNew) {
		%>
			<span class="button__normal" onclick="goBack()"><img border="0" align=absmiddle src="<adk:resUrl res="/su/images/back.gif"/>">[@CN:返回@EN:Back]</span><%
				}
			%>
			<span class="button__normal" onclick="document.frmEdit.submit()"><img border="0" align=absmiddle src="<adk:resUrl res="/su/images/start.gif"/>"><%
				if (bIsNew) {
			%>[@CN:插入@EN:Insert]<%
				} else {
			%>[@CN:更新@EN:Update]<%
				}
			%></span>
		</td>
	</tr>
</table>
</span>
</body>
</html>
</adk:tbody>
