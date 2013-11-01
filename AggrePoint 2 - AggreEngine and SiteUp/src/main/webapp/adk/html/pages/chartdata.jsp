<% byte[] utf8Bom = new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
String utf8BomStr = new String(utf8Bom,"UTF-8");
%><%=utf8BomStr%>
<?xml version='1.0' encoding='UTF-8'?>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.aggrepoint.com/adk" prefix="adk" %>
<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%>

<adk:cfgnode var="cfg" path="/chart" />

<adk:tbody>
	<c:choose>
		<c:when test="${CD.multiSeries and CD.inSeries eq null}">
			<chart chartRightMargin="35" caption="${CD.name}" xAxisName="${CD.xtitle}"
				yAxisName="${CD.ytitle}"
				bgColor="${adk:cfgval(cfg, 'bgcolor')}"
				showBorder="${adk:cfgval(cfg, 'border')}"
				borderColor="${adk:cfgval(cfg, 'bordercolor')}"
				decimalPrecision="${adk:cfgval(cfg, 'decimalprecision')}"
				formatNumberScale="0"
				showFCMenuItem="0" baseFont="Arial" baseFontSize="12">
				<categories>
					<c:forEach var="cat" items="${CD.categoryNames}">
						<category label='${cat}' />
					</c:forEach>
				</categories>

				<c:if test="${CD.showSumAsSeries}">
					<dataset seriesName="${CD.sumSeries.name}" renderAs="${CD.sumSeries.renderType}" showValues="${CD.sumSeries.showValue}" color="${CD.sumSeries.color}">
						<c:forEach var="cat" items="${CD.categories}">
							<set value='${cat.sum}' toolText="${cat.name} ${CD.sumSeries.name}: ${adk:decfmt('#,###,###,##0', cat.sum)}"/>
						</c:forEach>
					</dataset>
				</c:if>

				<c:forEach var="series" items="${CD.serieses}">
					<dataset seriesName="${series.name}"
						renderAs="${series.renderType}" showValues="${series.showValue}" color="${series.color}">
						<c:forEach var="data" items="${series.dataWithDescription}">
							<set value='${data.value}' toolText="${data.categoryName} ${data.seriesName}: ${adk:decfmt('#,###,###,##0', data.value)}"/>
						</c:forEach>
					</dataset>
				</c:forEach>
			</chart>
		</c:when>
		<c:otherwise>
			<c:set var="series" value="${CD.inSeries}" />
			<c:if test="${not CD.multiSeries}">
				<c:set var="series" value="${CD.firstSeries}" />
			</c:if>
			<c:set var="skipNegative" value="false" />
			<c:if test="${CD.chartType.name eq 'pie'}">
				<c:set var="skipNegative" value="true" />
			</c:if>

			<chart chartRightMargin="35" caption="${CD.name}" xAxisName="${CD.xtitle}"
				yAxisName="${CD.ytitle}"
				bgColor="${adk:cfgval(cfg, 'bgcolor')}"
				showBorder="${adk:cfgval(cfg, 'border')}"
				borderColor="${adk:cfgval(cfg, 'bordercolor')}"
				decimalPrecision="${adk:cfgval(cfg, 'decimalprecision')}"
				formatNumberScale="0" showValues="${series.showValue}" decimals="0"
				showFCMenuItem="0" baseFont="Arial" baseFontSize="12"
				animation="${animation}">

				<c:choose>
					<c:when test="${CD.supportDrill}">
						<c:forEach var="data" items="${series.dataWithDescription}">
							<c:if test="${!skipNegative or data.value >= 0.0}">
								<c:set var="id" value="${adk:exec1(data, 'getCategoryProp', 'id')}" />
								<set label="${data.categoryName}" value='${data.value}'
											link="JavaScript:${adk:func(adk:cfgval(cfg, 'drillfunc'))}('${id}')" isSliced="${data.category.sliced ? 1 : 0}" />
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach var="data" items="${series.dataWithDescription}">
							<c:if test="${!skipNegative or data.value >= 0.0}">
								<set label="${data.categoryName}" value='${data.value}' />
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</chart>
		</c:otherwise>
	</c:choose>
</adk:tbody>
