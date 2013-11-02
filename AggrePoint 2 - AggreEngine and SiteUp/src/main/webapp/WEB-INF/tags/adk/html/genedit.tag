<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk">
	<jsp:directive.tag dynamic-attributes="attrMap" language="java" pageEncoding="UTF-8" />
	<jsp:directive.attribute name="prop"
		type="com.aggrepoint.adk.ui.PropertyInstance" required="true" />
	<jsp:directive.attribute name="showmandatory" required="false" />
	<jsp:directive.attribute name="generror" required="false" />

	<c:set var="copyto" value=""/>
	<c:forEach var="attr" items="${attrMap}">
        <c:if test="${attr.key == 'copyto'}">
            <c:set var="copyto" value="${attr.value}"/>
        </c:if>
    </c:forEach>

	<c:set var="edit" value="${prop.edit}" />

	<c:choose>
		<c:when test="${edit eq null}">
			${prop.display}
		</c:when>
		<c:when test="${edit.type == 'custom'}">
			${adk:exec1(edit, 'getContent', prop.editValue)}
		</c:when>
		<c:when test="${edit.type == 'text'}">
			<c:if test="${copyto eq ''}">
				<input type="text" name="${prop.id}" value="${prop.editValue}"
					size="${edit.width}" maxlength="${edit.maxLength}" title="${prop.remarks}"/>
			</c:if>
			<c:if test="${copyto ne ''}">
				<input type="text" name="${prop.id}" value="${prop.editValue}"
					size="${edit.width}" maxlength="${edit.maxLength}" onchange="this.form.${copyto}.value = this.value;" title="${prop.remarks}"/>
			</c:if>
		</c:when>
		<c:when test="${edit.type == 'password'}">
			<input type="password" name="${prop.id}" value="${prop.editValue}"
				size="${edit.width}" maxlength="${edit.maxLength}" title="${prop.remarks}"/>
		</c:when>
		<c:when test="${edit.type == 'file'}">
			<input type="file" name="${prop.id}" value="${prop.editValue}"
				size="${edit.width}" maxlength="${edit.maxLength}" title="${prop.remarks}"/>
		</c:when>
		<c:when test="${edit.type == 'date'}">
			<input type="text" name="${prop.id}" id="cal_${adk:encodens(prop.id)}"
				value="${prop.editValue}" size="${edit.width}"
				maxlength="${edit.maxLength}" title="${prop.remarks}"/>

			<c:if test="${adk:markup() == 'html'}">
				<script language="javascript">$(function() {$("#cal_${adk:encodens(prop.id)}").datepicker({changeMonth: true, changeYear: true}); $("#cal_${adk:encodens(prop.id)}").datepicker('option', {showAnim: ''});});</script>
			</c:if>
		</c:when>
		<c:when test="${edit.type == 'number'}">
			<c:if test="${copyto eq ''}">
				<input type="text" name="${prop.id}" value="${prop.editValue}"
					size="${edit.width}" maxlength="${edit.maxLength}" title="${prop.remarks}"/>
			</c:if>
			<c:if test="${copyto ne ''}">
				<input type="text" name="${prop.id}" value="${prop.editValue}"
					size="${edit.width}" maxlength="${edit.maxLength}" onchange="this.form.${copyto}.value = this.value" title="${prop.remarks}"/>
			</c:if>
		</c:when>
		<c:when test="${edit.type == 'vcode'}">
			<img src="${adk:resproxy(edit.vcode)}"/><br/>
			<input type="text" name="${prop.id}" value="${prop.editValue}"
				size="${edit.width}" maxlength="${edit.maxLength}" title="${prop.remarks}"/>
		</c:when>
		<c:when test="${edit.type == 'textarea'}">
			<textarea name="${prop.id}" rows="${edit.rows}" cols="${edit.cols}">${prop.editValue}</textarea>
		</c:when>
		<c:when test="${edit.type == 'select'}">
			<select name="${prop.id}" title="${prop.remarks}">
			<c:forEach var="option" items="${adk:exec1(edit, 'getFlatOptions', prop.data)}">
				<c:set var="oid" value="${option.id}"/>
				<adk:option value="${option.id}" name="${option.name}" selected="${prop.valueId eq option.id}"/>
			</c:forEach>
			</select>
		</c:when>
		<c:when test="${edit.type == 'checkbox'}">
			<c:choose>
				<c:when test="${prop.editValue}">
					<input type="checkbox" name="${prop.id}" value="1" checked="checked" title="${prop.remarks}"/>
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="${prop.id}" value="1" title="${prop.remarks}"/>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${edit.type == 'radio'}">
			<c:forEach var="option" items="${adk:exec1(edit, 'getFlatOptions', prop.data)}">
				<adk:input type="radio" name="${prop.id}" value="${option.id}" checked="${prop.valueId eq option.id}"/> <c:if test="${option.logo ne null}"><img src="${adk:resurl(option.logo)}"/> </c:if>${option.name}<c:if test="${edit.break}"><br/></c:if>
			</c:forEach>
		</c:when>
		<c:when test="${edit.type == 'iconsel'}">
			<c:choose>
				<c:when test="${adk:markup() == 'html'}">
					<c:set var="onchange" value=""/>
					<c:forEach var="attr" items="${attrMap}">
				        <c:if test="${attr.key == 'onchange'}">
				            <c:set var="onchange" value="${attr.value}"/>
				        </c:if>
				    </c:forEach>

					<input type="hidden" name="${prop.id}" id="is_hid_${adk:encodens(prop.id)}" />
					<span id="is_${adk:encodens(prop.id)}" class="adk_btn3 adk_btnsilver__normal">&amp;nbsp;</span>
					<script language="javascript">
					$(function() {
						$("#is_${adk:encodens(prop.id)}").adkiconsel({
							options: ${adk:exec1(edit, 'getJsonOptions', prop.data)}, 
							iconRoot: "${adk:resurl('')}",
							input: $("#is_hid_${adk:encodens(prop.id)}")[0],
							value: "${prop.valueId}"
						});
					});

					<c:if test="${onchange ne ''}">
					$("#is_${adk:encodens(prop.id)}")[0].onchange = ${onchange};
					</c:if>
					</script>
				</c:when>
				<c:otherwise>
					<select name="${prop.id}">
					<c:forEach var="option" items="${adk:exec1(edit, 'getFlatOptions', prop.data)}">
						<adk:option value="${option.id}" name="${option.name}"
							selected="${prop.valueId eq option.id}" />
					</c:forEach>
					</select>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			${prop.display}
		</c:otherwise>
	</c:choose>
	<c:if test="${showmandatory ne 'no' and prop.mandatory eq 'yes'}">
		<span class="adk_mandatory" title="必须填写">&amp;nbsp;</span>
	</c:if>
	<c:if test="${prop.help != null and prop.help != ''}">
		<span class="adk_help" title="${prop.help}">&amp;nbsp;</span>
	</c:if>
	<c:if test="${generror == null or generror ne 'no'}">
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
	</c:if>
	<c:if test="${edit.type == 'file'}">
		<div>${prop.display}</div>
	</c:if>
</jsp:root>