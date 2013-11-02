<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk"
	xmlns:adkhtml="urn:jsptagdir:/WEB-INF/tags/adk/html">
	<jsp:directive.tag language="java" pageEncoding="UTF-8" />
	<jsp:directive.attribute name="rowClass" fragment="true"
		required="true" />
	<jsp:directive.attribute name="rowId" fragment="true" required="true" />
	<jsp:directive.attribute name="rowFlags" fragment="true"
		required="true" />
	<jsp:directive.variable name-given="prop" scope="NESTED" />
	<jsp:directive.variable name-given="item" scope="NESTED" />
	<jsp:directive.variable name-given="itemNum" scope="NESTED" />

	<adk:cfgval var="listGroup" path="/list" attr="group" />
	<adk:cfgval var="navSize" path="/list" attr="navsize" default="10" />

	<c:set var="props" value="${adk:uiprops(LIST.source, null, listGroup)}" />
	<c:set var="fullProps" value="${adk:uiprops(LIST.source, null, null)}" />
	<c:forEach var="prop" items="${props}">
		<adk:cfgnode var="link" path="/list/link|property=${prop.name}" />
		${adk:uiattach(prop, 'link', link)}
	</c:forEach>
	<adk:cfgval var="keyProp" path="/list" attr="keyprop" />
	<c:if test="${keyProp ne ''}">
		<c:set var="keyid" value="${adk:uiprop(fullProps, keyProp).id}" />
	</c:if>
	<adk:cfgval var="inlineEdit" path="/list" attr="edit" />
	<c:set var="supportInlineEdit" value="${inlineEdit ne null}"/>
	<adk:cfgval var="nosort" path="/list" attr="nosort" />

	<adk:tbody>
		<script language="javascript">
		document.${adk:encodens("checkAll")} = function(form, id, val) {
			var i;
			for (i = form.length - 1; i >= 0; i--)
				if (form.elements[i].type == "checkbox" &amp;&amp; form.elements[i].name == id)
					form.elements[i].checked = val;
		}
	
		document.${adk:encodens("checkCount")} = function(form, id) {
			var i;
			var c = 0;
			for (i = form.length - 1; i >= 0; i--)
				if ((form.elements[i].type == "checkbox" &amp;&amp; form.elements[i].name == id &amp;&amp; form.elements[i].checked)
					|| (form.elements[i].type == "option" &amp;&amp; form.elements[i].name == id &amp;&amp; form.elements[i].selected))
					c++;
			return c;
		}
		</script>

		<adk:form name="frmSearch" action="search">
			<input type="hidden" name="pno" value="${LIST.pageNo}" />
			<input type="hidden" name="order"
				value="${adk:adbData(LIST.source, 'SORT')}" />

			<adk:func name="selPage" param="pno" submit="yes" />
			<adk:func name="setOrder" param="order" submit="yes" />
		</adk:form>

		<c:if test="${supportInlineEdit}">
			<adk:form name="frmEdit" action="${inlineEdit}">
				<input type="hidden" name="id" />
				<input type="hidden" name="name" />
				<input type="hidden" name="value" />
	
				<adk:func name="doEdit" param="id, name, value" submit="yes" />
			</adk:form>
		</c:if>

		<!-- Generate forms for global buttons -->
		<adk:cfgnodes var="btn" path="/button|type=global">
			<c:if test="${adk:cfgval(btn, 'showcheck') eq null or adk:exec1(m, adk:cfgval(btn, 'showcheck'), adk:cfgval(btn, 'id'))}">
				<c:if test="${adk:cfgval(btn, 'resource') eq null}">
					<adk:form name="frmBtn${adk:cfgval(btn, 'id')}" action="${adk:cfgval(btn, 'action')}">
						<adk:func name="btn_${adk:cfgval(btn, 'id')}" submit="yes" />
					</adk:form>
				</c:if>
			</c:if>
		</adk:cfgnodes>

		<!-- Generate forms for row buttons -->
		<adk:cfgnodes var="btn" path="/button|type=row" flags="ALL_FLAGS">
			<adk:form name="frmBtn${adk:cfgval(btn, 'id')}" action="${adk:cfgval(btn, 'action')}">
				<input type="hidden" name="${keyid}" />

				<adk:func name="btn_${adk:cfgval(btn, 'id')}" param="${keyid}" submit="yes" />
			</adk:form>
		</adk:cfgnodes>

		<!-- Generate forms for links on property values in the table -->
		<adk:cfgnodes var="link" path="/list/link">
			<adk:form name="frmLink${adk:cfgval(link, 'id')}" action="${adk:cfgval(link, 'action')}">
				<c:set var="params" value="" />
				<input type="hidden" name="${keyid}" />

				<adk:func name="link_${adk:cfgval(link, 'id')}" param="${keyid}" submit="yes" />
			</adk:form>
		</adk:cfgnodes>

		<!-- Check if needs to generate the radio or checkbox -->
		<c:if test="${LIST.totalCount ne 0}">
			<adk:cfgnode var="btnSelect" path="/button|type=select" />
		</c:if>
		<c:set var="selAction" value="" />
		<c:if test="${btnSelect != null}">
			<adk:cfgval var="selType" path="/list" attr="seltype" default="checkbox" />
			<c:set var="selAction" value="${adk:cfgval(btnSelect, 'action')}" />
		</c:if>

		<adk:cfgnode var="btnRow" path="/button|type=row" />

		<!-- generate column def -->
		<jsp:useBean id="editProps" class="java.util.Vector" scope="page"/>

		<c:set var="sortOrder" value="${adk:adbData(LIST.source, 'SORT')}" />
		<c:set var="json_cols" value="" />
		<c:if test="${btnSelect != null}">
			<adk:strcat var="json_cols" str="{}" />
		</c:if>
		<c:forEach var="prop" items="${props}">
			<c:set var="coldef" value="" />

			<c:if test="${adk:uiattr(prop, 'wrap', 'no') == 'no'}">
				<adk:strcat var="coldef" sep="," str="nw:'y'" />
			</c:if>
			<c:if test="${adk:uiattr(prop, 'hideoverflow', 'no') == 'yes'}">
				<adk:strcat var="coldef" sep="," str="ov:'y'" />
			</c:if>
			<c:if test="${'yes' ne nosort and sortOrder != null and sortOrder eq prop.sortDesc}">
				<adk:strcat var="coldef" sep="," str="so: 'down'" />
			</c:if>
			<c:if test="${'yes' ne nosort and sortOrder != null and sortOrder eq prop.sortAsc}">
				<adk:strcat var="coldef" sep="," str="so: 'up'" />
			</c:if>
			<c:if test="${adk:uiattr(prop, 'pre', 'no') == 'yes'}">
				<adk:strcat var="coldef" sep="," str="pre: 'y'" />
			</c:if>
			<c:if test="${adk:uiattr(prop, 'width', '') ne ''}">
				<adk:strcat var="coldef" sep=","
					str="w: '${adk:uiattr(prop, 'width', '')}'" />
			</c:if>
			<c:set var="link" value="${adk:uiget(prop, 'link')}"/>
			<c:if test="${link != null}">
				<c:set var="funcName" value="link_${adk:cfgval(link, 'id')}" />
				<adk:strcat var="coldef" sep="," str="link: '${adk:func(funcName)}'" />
			</c:if>
			<c:if test="${'yes' ne nosort and prop.supportSort}">
				<c:choose>
					<c:when test="${sortOrder eq prop.sortDesc}">
						<adk:strcat var="coldef" sep="," str="s: '${prop.sortAsc}'" />
					</c:when>
					<c:otherwise>
						<adk:strcat var="coldef" sep="," str="s: '${prop.sortDesc}'" />
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:if test="${supportInlineEdit}">
				<c:set var="edit" value="${prop.edit}"/>
				<c:if test="${edit ne null}">
					<c:if test="${adk:exec1(editProps, 'add', prop)}" />
					<adk:strcat var="coldef" sep="," str="et: '${edit.type}'" />
					<adk:strcat var="coldef" sep="," str="en: '${prop.id}'" />
				</c:if>
			</c:if>

			<adk:strcat var="json_cols" sep="," str="{${coldef}}" />
		</c:forEach>
		<c:if test="${btnRow != null}">
			<adk:strcat var="json_cols" sep="," str="{nw:'y', btn:'y'}" />
		</c:if>

		<!-- generate row & cont def -->
		<adk:cfgnode var="lk" path="/list/link|property=" />
		<jsp:useBean id="selectOptions" class="java.util.Hashtable" scope="page"/>
		<adk:strcat var="json_rows" str="" />
		<adk:strcat var="json_conts" str="" />
		<c:forEach var="item" items="${LIST.items}">
			<c:set var="fullProps" value="${adk:propsbind(fullProps, item)}" />

			<c:set var="rowdef" value="" />
			<adk:strcat var="rowdef"
				str="id: '${adk:uiprop(fullProps, keyProp).value}'" />

			<c:if test="${btnRow != null}">
				<jsp:invoke fragment="rowFlags" var="rowflags" />

				<c:set var="btns" value="" />
				<adk:cfgnodes var="btn" path="/button|type=row" flags="${rowflags}">
					<adk:strcat var="btns" sep="," str="'${adk:cfgval(btn, 'id')}'" />
				</adk:cfgnodes>

				<adk:strcat var="rowdef" sep="," str="btns: [${btns}]" />
			</c:if>

			<c:if test="${lk ne null}">
				<c:set var="funcName" value="link_${adk:cfgval(lk, 'id')}" />
				<adk:strcat var="rowdef" sep="," str="link: '${adk:func(funcName)}'" />
			</c:if>

			<adk:strcat var="json_rows" sep="," str="{${rowdef}}" />

			<c:if test="${supportInlineEdit}">
				<c:set var="editProps" value="${adk:propsbind(editProps, item)}" />
				<c:set var="contdef" value="" />
				<c:forEach var="prop" items="${editProps}">
					<adk:strcat var="contdef" sep="," str="${adk:exec1(prop, 'getInlineEditValue', selectOptions)}" />
				</c:forEach>
				<adk:strcat var="json_conts" sep="," str="[${contdef}]" />
			</c:if>
		</c:forEach>

		<!-- generate list def -->
		<adk:strcat var="json_lists" str="" />
		<c:forEach var="list" items="${adk:exec(selectOptions, 'keySet')}">
			<adk:strcat var="json_lists" sep="," str="{name: '${list}', options: ${selectOptions[list]}, iconRoot: '${adk:resurl('')}'}" />
		</c:forEach>

		<!-- generate buttn def -->
		<adk:strcat var="json_btns" str="" />
		<adk:cfgnodes var="btn" path="/button|type=row" flags="ALL_FLAGS">
			<c:set var="funcName" value="btn_${adk:cfgval(btn, 'id')}" />
			<c:set var="resource" value="${adk:cfgval(btn, 'resource')}" />
			<c:if test="${resource ne null}">
				<c:set var="resource" value="${adk:resproxy(resource)}" />
			</c:if>
			<adk:strcat var="json_btns" sep=","
				str="['${adk:cfgval(btn, 'id')}', '${adk:func(funcName)}', '${adk:resurl(adk:cfgval(btn, 'image'))}', '${adk:cfgval(btn, 'tips')}', '${adk:cfgval(btn, 'confirm')}', '${resource}']" />
		</adk:cfgnodes>

		<!-- generate data column template -->
		<adk:conttmpl var="tmpl">
			<tr>
				<!-- generate select -->
				<c:if test="${btnSelect != null}"><td width="1%"><input type="${selType}" name="${keyid}" value="%VALUE(${keyProp})%" /></td></c:if>
				<!-- generate data columns -->
				<c:forEach var="prop" items="${props}"><td align="${adk:uiattr(prop, 'align', 'center')}">%DISPLAY(${prop.name})%</td></c:forEach>
				<c:if test="${btnRow != null}"><td>&amp;nbsp;</td></c:if>
			</tr>
		</adk:conttmpl>

		<adk:form name="frmList" action="${selAction}">
			<table width="100%" align="center" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<!-- Generate global and select buttons -->
					<td colspan="2" align="right">
						<adk:cfgnodes var="btn" path="/button">
							<c:if test="${adk:cfgval(btn, 'type') == 'global'}">
								<c:if test="${adk:cfgval(btn, 'showcheck') eq null or adk:exec1(m, adk:cfgval(btn, 'showcheck'), adk:cfgval(btn, 'id'))}">
									<c:choose>
										<c:when test="${adk:cfgval(btn, 'resource') ne null}">
											<button type="button"
												onclick="location.href='${adk:resproxy(adk:cfgval(btn, 'resource'))}'"
												class="adk_btn adk_btnsilver__normal"
												title="${adk:cfgval(btn, 'tips')}">
												<c:if test="${adk:cfgval(btn, 'image') ne null}">
													<img src="${adk:resurl(adk:cfgval(btn, 'image'))}" border="0" align="absmiddle" />
												</c:if>
												<c:if test="${adk:cfgval(btn, 'name') ne null}">${adk:cfgval(btn, 'name')}</c:if>
											</button>
										</c:when>
										<c:when test="${adk:cfgval(btn, 'form') eq 'link'}">
											<c:set var="funcName" value="btn_${adk:cfgval(btn, 'id')}" />
											<a href="javascript:${adk:func(funcName)}()"
												class="adk_btn adk_btnsilver__normal"
												title="${adk:cfgval(btn, 'tips')}">
												<c:if test="${adk:cfgval(btn, 'image') ne null}">
													<img src="${adk:resurl(adk:cfgval(btn, 'image'))}" border="0" align="absmiddle" />
												</c:if>
												<c:if test="${adk:cfgval(btn, 'name') ne null}"> ${adk:cfgval(btn, 'name')}</c:if>
											</a>
										</c:when>
										<c:otherwise>
											<c:set var="funcName" value="btn_${adk:cfgval(btn, 'id')}" />
											<button type="button" onclick="${adk:func(funcName)}()"
												class="adk_btn adk_btnsilver__normal"
												title="${adk:cfgval(btn, 'tips')}">
												<c:if test="${adk:cfgval(btn, 'image') ne null}">
													<img src="${adk:resurl(adk:cfgval(btn, 'image'))}" border="0" align="absmiddle" />
												</c:if>
												<c:if test="${adk:cfgval(btn, 'name') ne null}"> ${adk:cfgval(btn, 'name')}</c:if>
											</button>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:if>
							<c:if test="${adk:cfgval(btn, 'type') == 'select'}">
								<c:if test="${LIST.totalCount ne 0 and (adk:cfgval(btn, 'showcheck') eq null or adk:exec1(m, adk:cfgval(btn, 'showcheck'), adk:cfgval(btn, 'id')))}">
									<c:set var="nosel" value="${adk:cfgval(btn, 'nosel')}" />
									<c:if test="${nosel ne null}">
										<c:set var="nosel" value="if (document.${adk:encodens('checkCount')}(this.form, '${keyid}') == 0) {alert('${nosel}'); return;}" />
									</c:if>
									<c:set var="confirm" value="${adk:cfgval(btn, 'confirm')}" />
									<c:if test="${confirm ne null}">
										<c:set var="confirm" value="if (!confirm('${confirm}')) return;" />
									</c:if>

									<button type="button" onclick="${nosel} ${confirm} $(this.form).submit();"
										class="adk_btn adk_btnsilver__normal"
										title="${adk:cfgval(btn, 'tips')}">
										<c:if test="${adk:cfgval(btn, 'image') ne null}">
											<img src="${adk:resurl(adk:cfgval(btn, 'image'))}" border="0" align="absmiddle" />
										</c:if>
										<c:if test="${adk:cfgval(btn, 'name') ne null}"> ${adk:cfgval(btn, 'name')}</c:if>
									</button>
								</c:if>
							</c:if>
						</adk:cfgnodes>
					</td>
				</tr> 
				<tr>
					<!-- Record number information -->
					<td align="left" width="10%" nowrap="nowrap">
						<adk:cfgmsg id="summary" p0="${LIST.totalCount}" p1="${LIST.pageCount}" p2="${LIST.itemsPerPage}" />
					</td>
					<!-- Paging navigation -->
					<td align="right">
						<adkhtml:navpage list="${LIST}" size="${navSize}" func="${adk:func('selPage')}" />
					</td>
				</tr>
			</table>

			<table width="100%" align="center" border="0" cellspacing="1" cellpadding="2" id="${adk:encodens('table')}">
			<!-- table header -->
			<thead>
				<tr class="adk_lineHeader">
					<c:if test="${btnSelect != null}">
						<c:choose>
							<c:when test="${selType == 'checkbox'}">
								<th width="1%"><input type="checkbox"
									onclick="document.${adk:encodens('checkAll')}(this.form, '${keyid}', this.checked)" /></th>
							</c:when>
							<c:otherwise>
								<th width="1%">&amp;nbsp;</th>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:forEach var="prop" items="${props}">
						<th align="center"> ${prop.label} </th>
					</c:forEach>
					<c:if test="${btnRow != null}">
						<th width="1%" nowrap="nowrap" align="center">操作</th>
					</c:if>
				</tr>
			</thead>
			<!-- table content -->
			<tbody>
				<c:set var="itemNum" value="0" />
				<c:forEach var="item" items="${LIST.items}">
					<c:set var="itemNum" value="${itemNum + 1}" />
					<c:set var="props" value="${adk:propsbind(props, item)}" />
					<c:set var="fullProps" value="${adk:propsbind(fullProps, item)}" />

					<!-- generate columns with template -->
					${adk:exec2(tmpl, 'execute', fullProps, props)}
				</c:forEach>
			</tbody>
			</table>

			<table width="100%" align="center" border="0" cellspacing="5" cellpadding="0">
				<tr>
					<!-- Record number information -->
					<td nowrap="nowrap">
						<adk:cfgmsg id="summary" p0="${LIST.totalCount}" p1="${LIST.pageCount}" p2="${LIST.itemsPerPage}" />
					</td>
					<!-- Paging navigation -->
					<td align="right">
						<adkhtml:navpage list="${LIST}" size="${navSize}" func="${adk:func('selPage')}" />
					</td>
				</tr>
			</table>
		</adk:form>

		<script type="text/javascript">
		$(function() {
			$('#${adk:encodens('table')}').adktable({cols: [${json_cols}],
				rows: [${json_rows}],
				btns: [${json_btns}],
				conts: [${json_conts}],
				lists: [${json_lists}],
				sortFunc: ${adk:func("setOrder")}<c:if test="${supportInlineEdit}">,
				editFunc: ${adk:func("doEdit")}</c:if>});
		});
		</script>
	</adk:tbody>
</jsp:root>