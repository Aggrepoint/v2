<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%><%
	String content = (String)request.getAttribute("CONTENT");
	if (content == null)
		content = "";
	String update = (String)request.getAttribute("UPDATE");
	if (update == null)
		update = "";
	String dialog = (String)request.getAttribute("DIALOG");
%><?xml version='1.0' encoding='UTF-8'?>
<response>
	<message><![CDATA[<%= request.getAttribute("MESSAGE") %>]]></message>
	<refresh><%= request.getAttribute("REFRESH") %></refresh>
	<ensurevisible><%= request.getAttribute("ENSUREVISIBLE") %></ensurevisible>
	<title><%= request.getAttribute("TITLE") %></title>
	<content><![CDATA[
<%= content %>
	]]></content>
	<% if (dialog != null) { %>
	<dialog><![CDATA[
<%= dialog %>
	]]></dialog>
	<% } %>
	<update><%= update %></update>
</response>