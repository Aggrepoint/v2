<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk"
	xmlns:su="urn:jsptagdir:/WEB-INF/tags/su">
	<jsp:directive.attribute name="zonecount" type="java.lang.Integer" required="true" />
	<jsp:directive.attribute name="contents" type="java.util.Vector" required="true" />

	<c:set var="hasMoreZone" value="${false}" />

	<c:forEach var="zone" begin="0" end="${zonecount}" step="1" varStatus="loopStatus">
		<div class="adk_box adk_boxwhite uitype#area#">
			<div>区域：${zone}</div>
			<p>
				<!--
				<button type="submit" class="adk_btn0 adk_btnwhite__normal">创建内容</button>
				<button type="submit" class="adk_btn0 adk_btnwhite__normal">插入内容</button>
				<button type="submit" class="adk_btn0 adk_btnwhite__normal">添加窗口</button>
				-->
			</p>

			<c:forEach var="cont" items="${contents}">
				<c:if test="${cont.zoneId gt zonecount}">
					<c:set var="hasMoreZone" value="${true}" />
				</c:if>

				<c:if test="${cont.zoneId eq zone}">
					<su:pagecont cont="${cont}" />
				</c:if>
			</c:forEach>
		</div>
		<br/>
	</c:forEach>

	<c:if test="${hasMoreZone}">
		<div class="adk_box adk_boxwhite">
			<p><font color="red"><b>区域：不可见</b></font></p>

			<c:forEach var="cont" items="${contents}">
				<c:if test="${cont.zoneId gt zonecount}">
					<su:pagecont cont="${cont}" />
				</c:if>
			</c:forEach>
		</div>
		<br/>
	</c:if>
</jsp:root>
