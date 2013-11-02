<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.tag language="java" pageEncoding="UTF-8" />
	<jsp:directive.attribute name="list"
		type="com.icebean.core.adb.ADBList" required="true" />
	<jsp:directive.attribute name="size" required="true" />
	<jsp:directive.attribute name="func" required="true" />

	<adk:tbody>
		<adk:navpage pid="pid" list="${list}" size="${size}">
			<adk:prev><button type="button" onclick="${func}(1)" class="adk_btn3 adk_btnsilver__normal">|&lt;</button><button type="button" onclick="${func}(${pid})" class="adk_btn3 adk_btnsilver__normal">&lt;&lt;</button></adk:prev>
			<adk:curr>&amp;nbsp;${pid}&amp;nbsp;</adk:curr>
			<adk:page><button type="button" onclick="${func}(${pid})" class="adk_btn3 adk_btnsilver__normal">${pid}</button></adk:page>
			<adk:gap></adk:gap>
			<adk:next><button type="button" onclick="${func}(${pid})" class="adk_btn3 adk_btnsilver__normal">&gt;&gt;</button><button type="button" onclick="${func}(${list.pageCount})" class="adk_btn3 adk_btnsilver__normal">&gt;|</button></adk:next>
		</adk:navpage>
	</adk:tbody>
</jsp:root>