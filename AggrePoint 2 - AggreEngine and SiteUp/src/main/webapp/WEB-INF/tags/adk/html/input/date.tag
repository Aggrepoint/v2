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
	<jsp:directive.attribute name="value" required="false" type="java.lang.Object" />
	<jsp:directive.attribute name="error" required="false" />
	<jsp:directive.attribute name="validate" required="false" />
	<jsp:directive.attribute name="mandatory" required="false" />
	<jsp:directive.attribute name="size" required="false" />
	<jsp:directive.attribute name="maxlength" required="false" />
	<jsp:directive.attribute name="format" required="false" />
	<jsp:directive.attribute name="start" required="false" />
	<jsp:directive.attribute name="end" required="false" />
	<jsp:directive.attribute name="disabled" required="false" type="java.lang.Object" />
	<jsp:directive.attribute name="readOnly" required="false" type="java.lang.Boolean" />

	<adk:getinput var="inp" type="date" name="${name}" object="${object}" property="${property}" value="${value}" validate="${validate}" error="${error}" disabled="${disabled}">
		<adk:inputattr name="size" value="${size}" />
		<adk:inputattr name="maxlength" value="${maxlength}" />
		<adk:inputattr name="format" value="${format}" />
		<jsp:doBody />
	</adk:getinput>

	<c:set var="calid" value="cal_${adk:encodens(name)}" />

	<adk:element elm="input">
		<adk:attr name="type" value="text" />
		<adk:attr name="id" value="${calid}" />
		<adk:attr name="name" value="${name}" />
		<adk:attr name="value" value="${inp.value}" />
		<adk:attr name="size" value="${size}" test="${not empty size}" />
		<adk:attr name="maxlength" value="${maxlength}"	test="${not empty maxlength}" />
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

	<script language="javascript">
	$(function() {
		$("#${calid}").datepicker({changeMonth: true, changeYear: true, showAnim: 'slideDown', beforeShow:function(input, inst){
			<c:if test="${not empty start}">
			try {
				var dt = $("#cal_${adk:encodens(start)}").datepicker("getDate");
				dt.setDate(dt.getDate() + 1);
				return {minDate: dt};
			} catch (e) {
			}
			</c:if>
			<c:if test="${not empty end}">
			try {
				var dt = $("#cal_${adk:encodens(end)}").datepicker("getDate");
				dt.setDate(dt.getDate() - 1);
				return {maxDate: dt};
			} catch (e) {
			}
			</c:if>
		}});
	});
	</script>
</jsp:root>