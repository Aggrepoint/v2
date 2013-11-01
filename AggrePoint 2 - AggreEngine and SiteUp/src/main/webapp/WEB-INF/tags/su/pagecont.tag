<?xml version="1.0" encoding="UTF-8"?>

<jsp:root version="2.1" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:adk="http://www.aggrepoint.com/adk"
	xmlns:su="urn:jsptagdir:/WEB-INF/tags/su">
	<jsp:directive.attribute name="cont" type="com.aggrepoint.su.core.data.ApBPageContent" required="true" />

	<div class="adk_frame adk_frame${adk:ifelse(cont.window, 'blue', 'green')} uitype#cont#">
		<div class="handle">
				<c:choose>
					<c:when test="${cont.window}">
						<div class="left uitype#drag#">窗口：${adk:exec1(WINDOWS, 'get', cont).name}</div>
					</c:when>
					<c:when test="${cont.pageContent}">
						<div class="right">
							<button type="submit" class="adk_btn0 adk_btnwhite__normal" onclick="${adk:func('editContent')}(${adk:exec1(CONTS, 'get', cont).id})">编辑</button>
						</div>

						<div class="left uitype#drag#">页面内容  ${cont.contentId}</div>
					</c:when>
					<c:otherwise>
						<div class="left uitype#drag#">引用内容  ${cont.contentId}</div>
					</c:otherwise>
				</c:choose>
		</div>
		<div class="box">
			<c:choose>
				<c:when test="${cont.pageId ne PAGE.id}">
					<c:set var="srcPage" value="${adk:exec1(CONTPAGES, 'get', cont)}" />
						继承自页面${srcPage.name} (${srcPage.fullPath})
						<hr width="100%" size="1" />
				</c:when>
				<c:otherwise>
					<c:if test="${cont.inherit}">
						继承给子页面
						<hr width="100%" size="1" />
					</c:if>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${cont.window}">
					<c:set var="app" value="${adk:exec1(APPS, 'get', cont)}" />
					<p>应用： ${app.name}，状态：${app.status}，URL：${app.hostUrl}</p>
					<p>实例访问规则：${cont.accessRule}，窗口访问规则：${adk:exec1(WINDOWS, 'get', cont).accessRule}</p>
					<c:set var="frame" value="${adk:exec1(FRAMES, 'get', cont)}" />
					<c:if test="${not empty frame}">
						<p>
							窗框：${frame.name}
							<c:if test="${frame.previewImgId gt 0}">
								<img src="/ap2/res/img?id=${frame.previewImgId}"/>&amp;nbsp;
							</c:if>
						</p>
					</c:if>
				</c:when>
				<c:when test="${cont.pageContent}">
					<c:set var="content" value="${adk:exec1(CONTS, 'get', cont)}" />
					<p>实例访问规则：${cont.accessRule}，内容访问规则：${content.accessRule}</p>

					<c:if test="${fn:length(cont.childs) gt 0 or content.zoneCount gt 0}">
						<su:pagezone zonecount="${content.zoneCount - 1}" contents="${cont.childs}" />
					</c:if>
				</c:when>
				<c:otherwise>
					<p>实例访问规则：${cont.accessRule}，内容访问规则：${adk:exec1(CONTS, 'get', cont).accessRule}</p>

					<c:if test="${fn:length(cont.childs) gt 0 or content.zoneCount gt 0}">
						<su:pagezone zonecount="${content.zoneCount - 1}" contents="${cont.childs}" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</jsp:root>
