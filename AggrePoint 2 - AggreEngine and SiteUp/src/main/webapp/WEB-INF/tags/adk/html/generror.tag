<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.attribute name="prop"
		type="com.aggrepoint.adk.ui.PropertyInstance" required="true" />

	<c:choose>
		<c:when test="${prop.ajaxValidate}">
			<span id="validate_res_${prop.id}">
				<c:if test="${prop.errors != null}">
					<div class="adk_valfailed"><c:forEach var="error" items="${prop.errors}">${error}&amp;nbsp;</c:forEach></div>
				</c:if>
			</span>
		</c:when>
		<c:otherwise>
			<c:if test="${prop.errors != null}">
				<div class="adk_valfailed"><c:forEach var="error" items="${prop.errors}">${error}&amp;nbsp;</c:forEach></div>
			</c:if>
		</c:otherwise>
	</c:choose>
</jsp:root>