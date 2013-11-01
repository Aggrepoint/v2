package com.aggrepoint.ae;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.ae.core.proxy.HttpClientResponse;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.ResponseInfo;
import com.aggrepoint.ae.data.SessionConst;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.ae.taglib.html.btmpl.AreaContentTag;
import com.aggrepoint.ae.win.WindowProxy;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApPubFlag;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.ThreadContext;
import com.icebean.core.servlet.GZIPFilter;

/**
 * 向客户端返回内容
 * 
 * @author YJM
 */
public class AccessPointPresent extends BaseModule implements ReqAttrConst,
		UrlConst, RuleConst, SessionConst, IWinletConst {
	/**
	 * 返回
	 * 
	 * @param req
	 * @param resp
	 * @param adapter
	 * @param info
	 * @return
	 */
	public void response(DbAdapter adapter, IModuleRequest req,
			IModuleResponse resp, RequestInfo reqInfo, ResponseInfo respInfo)
			throws Exception {
		req.setAttribute("MESSAGE", respInfo.strMessage);

		switch (respInfo.responseType) {
		case UNDEFINED:
		case NONE:
			req.setAttribute("REFRESH", "none");
			req.setAttribute("UPDATE", respInfo.strAjaxWinIds);
			req.setAttribute("ENSUREVISIBLE", respInfo.strAjaxEnsureVisible);
			break;

		case NOT_FOUND:
			((HttpServletResponse) resp.getResponseObject()).setStatus(404);
			break;

		case AJAX_UPDATE_PAGE:
			req.setAttribute("REFRESH", "page");
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理跳转到栏目首页
		//
		// //////////////////////////////////////////////////////
		case REDIRECT_HOME: {
			if (!reqInfo.bAjax)
				resp.setRetUrl(reqInfo.urlConstructor
						.branchHome(respInfo.redirectBranch));
			else {
				req.setAttribute("REFRESH", "url");
				req.setAttribute("CONTENT", reqInfo.urlConstructor
						.branchHome(respInfo.redirectBranch));
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理跳转到栏目登陆页
		//
		// //////////////////////////////////////////////////////
		case REDIRECT_LOGIN: {
			if (!reqInfo.bAjax)
				resp.setRetUrl(reqInfo.urlConstructor
						.branchLogin(respInfo.redirectBranch));
			else {
				req.setAttribute("REFRESH", "url");
				req.setAttribute("CONTENT", reqInfo.urlConstructor
						.branchLogin(respInfo.redirectBranch));
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理跳转到栏目无权访问页
		//
		// //////////////////////////////////////////////////////
		case REDIRECT_NOACCESS: {
			if (!reqInfo.bAjax)
				resp.setRetUrl(reqInfo.urlConstructor
						.branchNoAccess(respInfo.redirectBranch));
			else {
				req.setAttribute("REFRESH", "url");
				req.setAttribute("CONTENT", reqInfo.urlConstructor
						.branchNoAccess(respInfo.redirectBranch));
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理跳转到指定的URL
		//
		// //////////////////////////////////////////////////////
		case REDIRECT_URL: {
			if (!reqInfo.bAjax)
				resp.setRetUrl(respInfo.strRedirectUrl);
			else {
				req.setAttribute("REFRESH", "page");
				req.setAttribute("CONTENT", respInfo.strRedirectUrl);
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理跳转到指定的页面
		//
		// //////////////////////////////////////////////////////
		case REDIRECT_PAGE: {
			if (!reqInfo.bAjax)
				resp.setRetUrl(reqInfo.urlConstructor
						.siteBPage(respInfo.redirectPage));
			else {
				req.setAttribute("REFRESH", "page");
				req.setAttribute("CONTENT",
						reqInfo.urlConstructor.siteBPage(respInfo.redirectPage));
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 处理显示窗口资源
		//
		// //////////////////////////////////////////////////////
		case WIN_RES: {
			// 如果启用了内容压缩Filter，不要对窗口资源内容进行压缩
			ThreadContext.setAttribute(GZIPFilter.THREAD_ATTR_DO_NOT_FILTER,
					"yes");

			HttpClientResponse hcr = WindowProxy.resource(adapter,
					reqInfo.urlConstructor, req.getRequestID(),
					(HttpServletRequest) req.getRequestObject(), resp,
					reqInfo.strWinView, reqInfo.strWinRes, reqInfo.winst,
					reqInfo.strWinMapParams, reqInfo.currWinMode.getId(),
					reqInfo.strWinOtherParams, reqInfo.userProfile.serialize(),
					reqInfo.psnEngine.getUserAgent().serialize(),
					reqInfo.strRemoteAddr, reqInfo.strRequestUri, EnumMarkup
							.fromId(reqInfo.branch.m_iMarkupType).getName(),
					BPageURLConstructor.getUrlRoot(reqInfo), false);
			if (hcr.m_strRedirect != null) {
				resp.setRetUrl(hcr.m_strRedirect);
				respInfo.iRetCode += 1;
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// AJAX数据校验
		//
		// //////////////////////////////////////////////////////
		case AJAX_VALIDATE: {
			req.setAttribute("REFRESH", "none");
			req.setAttribute("UPDATE", respInfo.strAjaxWinIds);
			req.setAttribute("ENSUREVISIBLE", respInfo.strAjaxEnsureVisible);
			req.setAttribute("CONTENT", respInfo.hcr.m_response);
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 显示对话框
		//
		// //////////////////////////////////////////////////////
		case AJAX_DIALOG: {
			req.setAttribute("UPDATE", respInfo.strAjaxWinIds);
			req.setAttribute("ENSUREVISIBLE", respInfo.strAjaxEnsureVisible);
			req.setAttribute("TITLE", respInfo.hcr.m_strHeaderWindowTitle);
			req.setAttribute("DIALOG", respInfo.hcr.m_response);
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 显示窗口或栏位内容，或者显示页面内容
		//
		// //////////////////////////////////////////////////////
		case AJAX_UPDATE_CONTENT: {
			if (reqInfo.cont != null) { // 显示页面内容
				StringBuffer sb = new StringBuffer();
				AreaContentTag.processContent(sb, null, adapter,
						(HttpServletRequest) req.getRequestObject(),
						(HttpServletResponse) resp.getResponseObject(),
						reqInfo, reqInfo.winst, false, null);

				req.setAttribute("CONTENT", sb.toString());
			} else { // 显示窗口或栏位内容
				// 判断更新区域是窗口还是整个区域
				boolean bRefreshWin = true;
				if (respInfo.newWinMode != EnumWinMode.NOCHANGE
						&& reqInfo.currWinMode != respInfo.newWinMode)
					switch (reqInfo.currWinMode) {
					case NORMAL:
						if (respInfo.newWinMode == EnumWinMode.MAX)
							bRefreshWin = false;
						break;
					case MAX:
						bRefreshWin = false;
						break;
					case MIN:
						if (respInfo.newWinMode == EnumWinMode.MAX)
							bRefreshWin = false;
						break;
					case HIDE:
						if (respInfo.newWinMode == EnumWinMode.MAX)
							bRefreshWin = false;
						break;
					}

				// 加载私有内容
				reqInfo.getPrivatePages(adapter);
				reqInfo.getPrivateNames(adapter);
				reqInfo.mergePrivate(adapter);
				reqInfo.loadTempate(adapter);

				StringBuffer sb = new StringBuffer();
				StringBuffer sbTitle = new StringBuffer();

				req.setAttribute("UPDATE", respInfo.strAjaxWinIds);
				req.setAttribute("ENSUREVISIBLE", respInfo.strAjaxEnsureVisible);

				if (bRefreshWin) { // 仅仅返回窗口的内容
					for (ApBPageContent content : reqInfo.page.getContents()) {
						content = content
								.findContent(reqInfo.winst.m_lPageContID);
						if (content != null) { // 找到内容
							int ret = AreaContentTag
									.processContent(sb, sbTitle, adapter,
											(HttpServletRequest) req
													.getRequestObject(),
											(HttpServletResponse) resp
													.getResponseObject(),
											reqInfo, content, false, null);
							if (ret == 2)
								req.setAttribute("REFRESH", "url");
							else
								req.setAttribute("REFRESH", "current");
							req.setAttribute("CONTENT", sb.toString());
							req.setAttribute("TITLE", sbTitle.toString());
							// 成功返回
							return;
						}
					}

					// 没有找到内容
					respInfo.iRetCode = 9999;
					return;
				} else { // 需要返回整个区域的内容
					reqInfo.strWinView = null;
					AreaContentTag.preloadWindowsForArea(adapter,
							(HttpServletRequest) req.getRequestObject(),
							(HttpServletResponse) resp.getResponseObject(),
							reqInfo, reqInfo.winst.m_strAreaName);
					AreaContentTag.processArea(sb, adapter,
							(HttpServletRequest) req.getRequestObject(),
							(HttpServletResponse) resp.getResponseObject(),
							reqInfo, reqInfo.winst.m_strAreaName, null);

					req.setAttribute("REFRESH", "area");
					req.setAttribute("CONTENT", sb.toString());
					// 成功返回
					return;
				}
			}
		}
			break;

		// //////////////////////////////////////////////////////
		//
		// 显示页面
		//
		// //////////////////////////////////////////////////////
		case PAGE: {
			// { 生成本地模板
			ApPubFlag pubFlag = new ApPubFlag();
			pubFlag.m_lDocID = reqInfo.template.m_lTemplateID;
			pubFlag.m_iDocTypeID = ApPubFlag.TYPE_TEMPLATE
					+ reqInfo.template.m_iOfficialFlag;
			pubFlag.m_strServerName = req.getContext().getServerName();
			if (adapter.retrieve(pubFlag) == null) { // 需要生成本地模板
				ApTemplate template = new ApTemplate();
				template.m_lTemplateID = reqInfo.template.m_lTemplateID;
				adapter.retrieve(template, "loadDetail");
				template.generateJspFiles(PathConstructor.getBPageTmplDir(req));
				try {
					adapter.create(pubFlag);
				} catch (Exception e) {
					// 多个并发访问可能导致异常
				}
				adapter.commit();
			}
			// }

			// 设置内容类型
			switch (EnumMarkup.fromId(reqInfo.template.m_iMarkupType)) {
			case WML:
				resp.setContentType("text/vnd.wap.wml; charset=UTF-8");
				break;
			case XHTML:
				resp.setContentType(reqInfo.psnEngine.getUserAgent().xhtmlType
						+ "; charset=UTF-8");
				break;
			default:
				resp.setContentType("text/html; charset=UTF-8");
				break;
			}

			// 转页面显示
			reqInfo.strWinView = null;
			resp.setRetUrl(URL_TMPL_BPAGE
					+ Long.toString(reqInfo.template.m_lTemplateID) + ".jsp");
		}
			break;
		}

		return;
	}

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		try {
			DbAdapter adapter = new DbAdapter(req.getDBConn());
			RequestInfo reqInfo = (RequestInfo) req
					.getAttribute(ATTR_REQUEST_INFO);
			ResponseInfo respInfo = (ResponseInfo) req
					.getAttribute(ATTR_RESPONSE_INFO);

			// 执行窗口动作后用户身份可能会发生变化
			reqInfo.reloadUserInfo(req);

			response(adapter, req, resp, reqInfo, respInfo);

			if (reqInfo.bAjax)
				return respInfo.iRetCode + 1;

			return respInfo.iRetCode;
		} catch (Exception e) {
			e.printStackTrace();

			resp.setRetThrow(e);

			return 1;
		}
	}
}
