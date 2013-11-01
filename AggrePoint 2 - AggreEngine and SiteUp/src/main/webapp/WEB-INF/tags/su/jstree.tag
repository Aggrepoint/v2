<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.attribute name="name" required="true" />
	<jsp:directive.attribute name="rootid" required="true" />
	<jsp:directive.attribute name="width" required="false" />
	<jsp:directive.variable name-given="selRoot" scope="AT_END" />

	<c:if test="${empty width}">
		<c:set var="width" value="250px" />
	</c:if>

	<script	language="javascript" src="${adk:resurl('/su/script/jstree/jquery.cookie.js')}">function a(){}</script>
	<script	language="javascript" src="${adk:resurl('/su/script/jstree/jquery.jstree.js')}">function a(){}</script>

	<adk:form name="frmSelect" action="selectNode">
		<input type="hidden" name="nid" />

		<adk:func name="selectNode" param="nid" submit="yes" />
	</adk:form>
	<c:set var="selRoot" value="javascript:${adk:func('selectNode')}(${rootid})" />

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr><td>
			<div id="${adk:encodens(name)}" style="width:${width}; height:50px; margin:0; overflow:auto;"></div>
		</td></tr>
	</table>

	<script language="javascript">
	var func = function() {
		var treewin = $("#${adk:encodens(name)}");
		var h1 = $(window).height() - treewin.offset().top - 90;
		if (h1 &lt; 0)
			h1 = 0;

		var diff = $(document).height() - $(window).height();
		if (diff > 0) {
			if (diff > 70)
				diff = 70;
		} else
			diff = 0;

		treewin.height(h1 + diff);
	};
	$(document).ready(func);
	$(window).resize(func);
	</script>
</jsp:root>
