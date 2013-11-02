<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.tag dynamic-attributes="attrMap" language="java" pageEncoding="UTF-8" />
	<jsp:directive.attribute name="value" required="true" />

	<adk:element elm="option">
		<adk:attr name="value" value="${value}" />
		<adk:attr name="selected" value="selected" test="${adk_input_select_value eq value}" />
		<jsp:doBody />
	</adk:element>
</jsp:root>