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
	<jsp:directive.attribute name="options" required="false" type="java.util.Map"/>

	<adk:getinput var="inp" type="select" name="${name}" object="${object}" property="${property}" value="${value}" validate="${validate}" error="${error}" disabled="${disabled}">
		<adk:inputattr name="options" value="${options}" />
	</adk:getinput>
	<c:set var="adk_input_select_value" value="${inp.value}" scope="request" />

	<adk:element elm="select">
		<adk:attr name="name" value="${name}" />
		<adk:attr name="disabled" value="disabled" test="${inp.disabled}" />

		<c:forEach var="attr" items="${attrMap}">
			<adk:attr name="${attr.key}" value="${attr.value}" />
	    </c:forEach>

		<c:choose>
			<c:when test="${empty inp.options}">
				<jsp:doBody />
			</c:when>
			<c:otherwise>
				<c:forEach var="option" items="${inp.options}">
					<adk:element elm="option">
						<adk:attr name="value" value="${option.id}" />
						<adk:attr name="selected" value="selected" test="${inp.value eq option.id}" />
						${option.name}
					</adk:element>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</adk:element>

	<c:if test="${mandatory eq 'yes'}">
		<span class="adk_mandatory" title="必须填写">&amp;nbsp;</span>
	</c:if>

	<c:if test="${not empty validate}">
		<span id="validate_res_${name}">
			<c:if test="${inp.hasError}">
				<div class="adk_valfailed">${adk:htmlencode(inp.error)}</div>
			</c:if>
		</span>
	</c:if>
</jsp:root>