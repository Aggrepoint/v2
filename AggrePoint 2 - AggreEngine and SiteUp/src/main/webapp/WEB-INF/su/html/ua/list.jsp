<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.aggrepoint.adk.EnumMarkup" %>
<%@ page import="com.aggrepoint.core.data.*, com.icebean.core.adb.*, com.aggrepoint.adk.*" %>
<%
	ADBList adbl = (ADBList)request.getAttribute("UAS");
	String strUserAgent = (String)request.getAttribute("USERAGENT");
	int iAuto = ((Integer)request.getAttribute("AUTO")).intValue();
%>
<link rel="STYLESHEET" type="text/css" href="<adk:resUrl res="/siteup/theme/default.css"/>">

<adk:tbody>
<script language="javascript">
<adk:func name="doEdit"/>(id) {
var form = document.<adk:encodeNamespace value="frmEdit"/>;
form.uaid.value=id;
<adk:submit name="form"/>;
}

<adk:func name="selPage"/>(page) {
var form = document.<adk:encodeNamespace value="frmSelPage"/>;
form.pno.value=page;
<adk:submit name="form"/>;
}
</script>
<adk:form name="frmEdit" action="edit">
<input type="hidden" name="uaid">
</adk:form>
<adk:form name="frmSelPage" action="page">
<input type="hidden" name="pno">
</adk:form>

<adk:form name="frmSearch" action="search">
<table width="99%" align="center" border="0" cellspacing="1" cellpadding="4">
	<tr height="2"></tr>
	<tr>
		<td width="100%">
			[@CN:User Agent关键字：@EN:User Agent Keyword:]<input type="text" name="ua" size="20" value="<%=strUserAgent%>">
			[@CN:识别方式：@EN:Recognize Method:]<input type="radio" name="auto" value="-1"<%if (iAuto < 0) {%> checked<%}%>>[@CN:所有@EN:All] <input type="radio" name="auto" value="0"<%if (iAuto == 0) {%> checked<%}%>>[@CN:手工确认@EN:Manual Confirmed] <input type="radio" name="auto" value="1"<%if (iAuto > 0) {%> checked<%}%>>[@CN:自动识别@EN:Auto]
		</td>  
		<td nowrap>
			<span class="button__normal" onclick="<adk:submit form="frmSearch"/>"><img border="0" align=absmiddle src="<adk:resUrl res="/siteup/images/view.gif"/>">[@CN:查找@EN:Search]</span>
		</td>
	</tr>
	<tr height="2"></tr>
</table>
</adk:form>
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>[|<%= adbl.m_iTotalCount %>|<%= adbl.m_iPageCount %>|<%= adbl.m_iItemsPerPage %>@CN:共(0)个符合查询条件的记录，分(1)页显示，每页(2)个@EN:Totally (0) records, display in (1) pages, (2) per page]</td>
		<td align="right">
		<% int j = adbl.m_iPageNo - 4; if (j + 9 > adbl.m_iPageCount) j = adbl.m_iPageCount - 9; if (j < 1) j = 1; if (j > 1) { %><a href="javascript:<adk:func ref="selPage"/>(<%= adbl.m_iPageNo - 10 %>)">&lt;&lt;</a>&nbsp;<% } %>
		<% for (int i = 1; j <= adbl.m_iPageCount && i <= 10; i++, j++) { if (j == adbl.m_iPageNo) {%><%= j %>&nbsp;<% } else { %><a href="javascript:<adk:func ref="selPage"/>(<%= j %>)"><%= j %></a>&nbsp;<% }} %>
		<% j--; if (j < adbl.m_iPageCount) { %><a href="javascript:<adk:func ref="selPage"/>(<%= adbl.m_iPageNo + 10 %>)">&gt;&gt;</a>&nbsp;<% } %>
		</td>
	</tr>
</table>
<table border="1" cellspacing="0" cellpadding="4" bgcolor="#C1C1FF" bordercolorlight="#9D9DFF" bordercolordark="#D2D2FF" width="99%" align="center">
	<tr class="lineHeader">
		<td>User Agent</td>
		<td nowrap>[@CN:设备@EN:Device]</td>
		<td nowrap>[@CN:操作系统@EN:OS]</td>
		<td nowrap>[@CN:浏览器@EN:Browser]</td>
		<td nowrap>HTML</td>
		<td nowrap>XHTML</td>
		<td nowrap>WML</td>
		<td nowrap>[@CN:首选@EN:Default]</td>
		<td nowrap>Ajax</td>
		<td nowrap>[@CN:移动设备@EN:Mobile]</td>
		<td nowrap>Spider</td>
		<td nowrap>[@CN:识别@EN:Recognize]</td>
		<td nowrap>[@CN:操作]</td>
	</tr><adk:iteration name="ua" type="UserAgent" group="<%= adbl.m_vecObjects %>">
	<tr class="line__normal">
		<td>Agent: <%= ua.userAgent %><br>Accept: <%= ua.accept %><br>XHTML Type: <%= ua.xhtmlType %></td>
		<td nowrap><%= ua.deviceType.getKey() %> (<%= ua.deviceModel %> <%= ua.deviceVersion %>)</td>
		<td nowrap><%= ua.osType.getKey() %> (<%= ua.osVersion %>)</td>
		<td nowrap><%= ua.browserType.getKey() %> (<%= ua.browserVersion %>)</td>
		<td nowrap><%= ua.supportHTML ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.supportXHTML ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.supportWML ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.defaultMarkup == EnumMarkup.HTML.getId() ? "HTML" : (ua.defaultMarkup == EnumMarkup.WML.getId() ? "WML" : "XHTML") %></td>
		<td nowrap><%= ua.supportAjax ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.isMobile ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.isSpider ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap><%= ua.autoFlag ? "&#10003;" : "&nbsp;" %></td>
		<td nowrap>
			<span class="trans_button__normal" onclick="<adk:func ref="doEdit"/>(<%= ua.agentID %>)"><img border=0 align=absmiddle src="<adk:resUrl res="/siteup/images/edit.gif"/>" alt="[@CN:编辑@EN:Edit]"></span>
		</td>
	</tr></adk:iteration>
</table>
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>[|<%=adbl.m_iTotalCount%>|<%=adbl.m_iPageCount%>|<%=adbl.m_iItemsPerPage%>@CN:共(0)个符合查询条件的记录，分(1)页显示，每页(2)个@EN:Totally (0) records, display in (1) pages, (2) per page]</td>
		<td align="right">
		<%
			j = adbl.m_iPageNo - 4; if (j + 9 > adbl.m_iPageCount) j = adbl.m_iPageCount - 9; if (j < 1) j = 1; if (j > 1) {
		%><a href="javascript:<adk:func ref="selPage"/>(<%=adbl.m_iPageNo - 10%>)">&lt;&lt;</a>&nbsp;<%
			}
		%>
		<%
			for (int i = 1; j <= adbl.m_iPageCount && i <= 10; i++, j++) { if (j == adbl.m_iPageNo) {
		%><%=j%>&nbsp;<%
			} else {
		%><a href="javascript:<adk:func ref="selPage"/>(<%=j%>)"><%=j%></a>&nbsp;<%
			}}
		%>
		<%
			j--; if (j < adbl.m_iPageCount) {
		%><a href="javascript:<adk:func ref="selPage"/>(<%=adbl.m_iPageNo + 10%>)">&gt;&gt;</a>&nbsp;<%
			}
		%>
		</td>
	</tr>
</table>
<img src="<adk:resUrl res="/siteup/images/blank.gif"/>" height=2>
</adk:tbody>
