<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.tag dynamic-attributes="attrMap" language="java" pageEncoding="UTF-8" />
	<jsp:directive.attribute name="name" required="true" />
	<jsp:directive.attribute name="object" required="false" type="java.lang.Object"/>
	<jsp:directive.attribute name="property" required="false" />
	<jsp:directive.attribute name="value" required="false" />
	<jsp:directive.attribute name="error" required="false" />
	<jsp:directive.attribute name="validate" required="false" />
	<jsp:directive.attribute name="mandatory" required="false" />
	<jsp:directive.attribute name="disabled" required="false" type="java.lang.Object" />
	<jsp:directive.attribute name="readOnly" required="false" type="java.lang.Boolean" />

	<adk:getinput var="inp" type="text" name="${name}" object="${object}" property="${property}" value="${value}" validate="${validate}" error="${error}" disabled="${disabled}">
		<jsp:doBody />
	</adk:getinput>

	<adk:element elm="input">
		<adk:attr name="type" value="text" />
		<adk:attr name="name" value="${name}" />
		<adk:attr name="value" value="${inp.value}" />
		<adk:attr name="disabled" value="disabled" test="${inp.disabled}" />
		<adk:attr name="readOnly" value="readOnly" test="${readOnly}" />

		<c:forEach var="attr" items="${attrMap}">
			<adk:attr name="${attr.key}" value="${attr.value}" />
	    </c:forEach>
	</adk:element>

	<c:if test="${mandatory eq 'yes'}">
		<span class="adk_mandatory" title="必须填写">&amp;nbsp;</span>
	</c:if>

	<span id="validate_res_${name}">
		<c:if test="${inp.hasError}">
			<div class="adk_valfailed">${adk:htmlencode(inp.error)}</div>
		</c:if>
	</span>
</jsp:root>