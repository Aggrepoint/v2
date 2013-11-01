<%@ page language="java" contentType="application/xml; charset=UTF-8" pageEncoding="UTF-8"%><%
	String content = (String)request.getAttribute("CONTENT");
	if (content == null)
		content = "";
	String update = (String)request.getAttribute("UPDATE");
	if (update == null)
		update = "";
%><?xml version='1.0' encoding='UTF-8'?>
<response>
	<refresh><%= request.getAttribute("REFRESH") %></refresh>
	<content><![CDATA[
<%= content %>
	]]></content>
	<update><%= update %></update>
</response>