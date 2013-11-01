package com.aggrepoint.ae;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IServerContext;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.WebBaseException;
import com.aggrepoint.adk.data.BaseModuleDef;
import com.aggrepoint.adk.data.Param;
import com.aggrepoint.ae.core.proxy.HttpClientResponse;
import com.aggrepoint.ae.data.EnumViewMode;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.RequestInfo;
import com.aggrepoint.ae.data.ResponseInfo;
import com.aggrepoint.ae.data.ResponseInfo.ResponseType;
import com.aggrepoint.ae.data.SessionConst;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.ae.taglib.html.btmpl.AreaContentTag;
import com.aggrepoint.ae.win.WindowMode;
import com.aggrepoint.ae.win.WindowProxy;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.TypeCast;

/**
 * 动态显示站点页面 目前只支持动态网站，请求URL必须是页面URL，请求中不一定有窗口ID参数。
 * 
 * 以后再考虑实现支持静态网站，请求URL不是页面URL，请求中一定要有窗口ID参数，对于非Ajax 请求必须提供返回URL
 * 
 * 由于处理过程中可能会改变当前用户身份，在同一个Module中获取新用户身份比较困难，因此将
 * 显示部分作为一个单独的Module来实现，在Module之间转换时ADK会重新获取用户身份和生成个性化引擎。分模块
 * 还便于在用户身份或标记语言变更后重新计算当前页面
 * 
 * @author YJM
 */
public class AccessPointProcess extends BaseModule implements ReqAttrConst,
		UrlConst, RuleConst, SessionConst, IWinletConst {
	private static final String SESSION_KEY_SKIP_PAGE = "com.aggrepoint.ae.skippage";
	/**
	 * true表示该模块被配置为负责重新加载页面
	 */
	private boolean m_bForReloadPage = false;

	/**
	 * 将页面可跳转的子页面保存
	 * 
	 * @param req
	 * @param pid
	 * @param sub
	 */
	public static void saveSubPageToCache(IModuleRequest req, long pid,
			ApBPage sub) {
		Hashtable<Long, ApBPage> ht = TypeCast.cast(req
				.getSessionAttribute(SESSION_KEY_SKIP_PAGE));
		if (ht == null) {
			ht = new Hashtable<Long, ApBPage>();
			req.setSessionAttribute(SESSION_KEY_SKIP_PAGE, ht);
		}
		if (sub != null)
			ht.put(pid, sub);
		else
			ht.remove(pid);
	}

	/**
	 * 从Session中获取指定页面可跳转的子页面
	 * 
	 * @param req
	 * @param pid
	 * @return
	 */
	public static ApBPage findSubPageFromCache(IModuleRequest req, long pid) {
		Hashtable<Long, ApBPage> ht = TypeCast.cast(req
				.getSessionAttribute(SESSION_KEY_SKIP_PAGE));
		if (ht == null)
			return null;

		return ht.get(pid);
	}

	/**
	 * 清除保存在Session中的子页面
	 * 
	 * @param req
	 */
	public static void clearSubPage(HttpServletRequest req) {
		req.getSession(true).removeAttribute(SESSION_KEY_SKIP_PAGE);
	}

	public void init(BaseModuleDef def, IServerContext server)
			throws WebBaseException {
		Param param = def.getParameter("reloadPage");
		if (param != null
				&& (param.m_strValue.equalsIgnoreCase("y") || param.m_strValue
						.equalsIgnoreCase("yes")))
			m_bForReloadPage = true;
	}

	/**
	 * 执行处理过程
	 * 
	 * @param req
	 * @param resp
	 * @param adapter
	 * @param reqInfo
	 * @return
	 * @throws Exception
	 */
	public ResponseInfo process(DbAdapter adapter, IModuleRequest req,
			IModuleResponse resp, RequestInfo reqInfo) throws Exception {
		ResponseInfo respInfo = new ResponseInfo();

		// 查找被访问的页面
		reqInfo.findPage(req, adapter);

		if (reqInfo.page == null) { // 页面找不到
			// 不响应对favicon.ico的请求
			if (reqInfo.strRequestPath.contains("/favicon.ico"))
				return respInfo.returnNotFound(20);

			if (reqInfo.branch != null) // 存在对应分支
				return respInfo.returnRedirectHome(10, reqInfo.branch);

			return respInfo.returnNotFound(20);
		}

		// { 检查页面访问权限
		boolean allowAccess = false;

		switch (reqInfo.viewMode) {
		case PUBLISH: // 发布系统发布页面
			if (reqInfo.branch.m_iPsnType != ApBranch.PSN_TYPE_STATIC)
				return respInfo.returnNone(30);
			allowAccess = true;
			break;
		case VIEW: // 查看页面内容
		case RESET: // 查看页面内容
		case EDIT: // 可视化编辑页面内容
			allowAccess = reqInfo.allowAccess(reqInfo.page);
			break;
		}

		if (!allowAccess) {
			// 不允许访问
			if (reqInfo.userProfile.isAnonymous())
				return respInfo.returnRedirectLogin(40, reqInfo.branch);
			else
				return respInfo.returnRedirectNoAccess(50, reqInfo.branch);
		}

		// 若用户访问根页面，但站点指定了缺省的主页面，则将用户重定向到主页面
		if (reqInfo.page.m_lParentID == 0
				&& reqInfo.viewMode != EnumViewMode.EDIT
				&& reqInfo.branch.m_strHomePath != null
				&& !reqInfo.branch.m_strHomePath.equals("")
				&& !reqInfo.branch.m_strHomePath.equals("/"))
			return respInfo.returnRedirectHome(60, reqInfo.branch);
		// }

		// 查看页面
		if (reqInfo.requestType == RequestInfo.RequestType.PAGE_VIEW) {
			// { 跳入子页面处理
			// 对于窗口访问不进行跳入子页面处理。原因是如果用直接指定Winlet
			// ID的方式访问从设置了跳入子页面规则的页面中继承下来的窗口，如果进行跳入子页面处理会导致不想要的重定向
			if (reqInfo.page.m_bSkipToSub
					&& (reqInfo.viewMode == EnumViewMode.VIEW || reqInfo.viewMode == EnumViewMode.RESET)) {
				// 查看是否有保存的计算结果
				ApBPage sub = findSubPageFromCache(req, reqInfo.page.m_lPageID);
				if (sub != null) {
					if (reqInfo.allowAccess(sub))
						return respInfo.returnRedirectPage(70, sub);

					saveSubPageToCache(req, reqInfo.page.m_lPageID, null);
				}

				reqInfo.getPrivatePages(adapter);
				reqInfo.getPrivateNames(adapter);
				reqInfo.mergePrivate(adapter);

				// 从全局页面中查找子页面
				if (reqInfo.currentPageInTree != null) {
					for (Enumeration<ApBPage> enm = reqInfo.currentPageInTree
							.getSubPages().elements(); enm.hasMoreElements();) {
						sub = enm.nextElement();
						if (reqInfo.allowAccess(sub)) {
							saveSubPageToCache(req, reqInfo.page.m_lPageID, sub);
							return respInfo.returnRedirectPage(70, sub);
						}
					}
				}

				// 从私有页面中查找子页面
				for (Enumeration<ApBPage> enm = reqInfo
						.getPrivatePages(adapter).elements(); enm
						.hasMoreElements();) {
					sub = enm.nextElement();
					if (sub.m_lParentID == reqInfo.page.m_lPageID)
						if (reqInfo.allowAccess(sub)) {
							saveSubPageToCache(req, reqInfo.page.m_lPageID, sub);
							return respInfo.returnRedirectPage(70, sub);
						}
				}

				// 子页面未找到
				return respInfo.returnNone(80);
			}
			// }

			// {判断上级页面是否需要跳入子页面，如果有的话则将上级页面的缺省跳入页面设置成当前页面
			ApBPage p = reqInfo.currentPageInTree.m_parent;
			while (p != null && p.m_parent != null && p.m_parent != p) {
				if (p.m_bSkipToSub)
					saveSubPageToCache(req, p.m_lPageID,
							reqInfo.currentPageInTree);
				p = p.m_parent;
			}
			// }

			return respInfo.returnPage(90);
		}

		// 查看内容
		if (reqInfo.requestType == RequestInfo.RequestType.CONT_VIEW) {
			// 只允许在Ajax模式下访问内容
			if (!reqInfo.bAjax)
				return respInfo.returnRedirectHome(135, reqInfo.branch);

			return respInfo.returnAjaxUpdateContent(135);
		}

		// 获取窗口对象
		reqInfo.findWin(req, adapter);
		if (reqInfo.winst == null) // 找不到要访问的窗口
			return respInfo.returnRedirectHome(100, reqInfo.branch);

		switch (reqInfo.requestType) {
		case WIN_RES:
			return respInfo.returnWinRes(110);
		case WIN_MODE:
			// 设置窗口状态
			if (reqInfo.winMode != EnumWinMode.NOCHANGE)
				respInfo.newWinMode = WindowMode.setMode(adapter,
						((HttpServletRequest) req.getRequestObject())
								.getSession(), reqInfo.strUserID,
						reqInfo.winst.m_lPageID, reqInfo.winst.m_strAreaName,
						reqInfo.winst.m_lPageContID, reqInfo.winMode);

			// 非Ajax模式下，更新窗口状态后直接返回整个页面内容
			if (!reqInfo.bAjax)
				return respInfo.returnPage(120);

			return respInfo.returnAjaxUpdateContent(120);
		case WIN_VIEW:
			// 只允许在Ajax模式下访问窗口内容
			if (!reqInfo.bAjax)
				return respInfo.returnRedirectHome(130, reqInfo.branch);

			return respInfo.returnAjaxUpdateContent(130);
		case WIN_ACTION:
			// 执行窗口动作
			respInfo.hcr = WindowProxy.action(adapter, reqInfo.urlConstructor,
					req.getRequestID(), (HttpServletRequest) req
							.getRequestObject(), (HttpServletResponse) resp
							.getResponseObject(), reqInfo.strWinView,
					reqInfo.strWinAction, reqInfo.strFormId,
					reqInfo.bIsValidateField, reqInfo.winst,
					reqInfo.strMapParam, reqInfo.strUserID, reqInfo.userProfile
							.serialize(), reqInfo.psnEngine.getUserAgent()
							.serialize(), reqInfo.strRemoteAddr,
					reqInfo.strRequestUri,
					EnumMarkup.fromId(reqInfo.branch.m_iMarkupType).getName(),
					BPageURLConstructor.getUrlRoot(reqInfo), false);

			respInfo.strMessage = respInfo.hcr.m_strHeaderMessage;

			if (respInfo.hcr.m_strRedirect != null)
				return respInfo.returnRedirectUrl(145,
						respInfo.hcr.m_strRedirect);

			// 检查是否窗口返回结果中指定了重定向
			if (respInfo.hcr.m_strHeaderSetRedirect != null
					&& !respInfo.hcr.m_strHeaderSetRedirect.equals("")) {
				// 根据指定的target检索属于同一branch、同一应用的窗口实例
				ApBPageContent winst = new ApBPageContent();
				winst.m_strContName = respInfo.hcr.m_strHeaderSetRedirect;
				winst.m_lContentID = reqInfo.win.m_lAppID;
				if (adapter.retrieve(winst, "loadWindowByUrl") != null) {
					ApBPage page = new ApBPage();
					page.m_lPageID = winst.m_lPageID;
					adapter.retrieve(page, "loadFullPath");
					return respInfo.returnRedirectPage(140, page);
				}
			}

			// 若请求是以Multipart方式发起的，重定向以清除URL上所带参数
			if (req.isMultipart())
				return respInfo.returnRedirectPage(147, reqInfo.page);

			// 非Ajax模式
			if (!reqInfo.bAjax) {
				// 变更了用户身份，需要重新计算被显示页面
				if (respInfo.hcr.m_strHeaderSetProfile != null)
					return respInfo.returnReloadPagePage(1);

				// 若变更了标记类型，需要重新计算被显示页面
				if (respInfo.hcr.m_strHeaderMarkupType != null
						&& !respInfo.hcr.m_strHeaderMarkupType.equals(""))
					return respInfo.returnReloadPagePage(2);

				// 执行完窗口动作后直接返回整个页面内容
				return respInfo.returnPage(150);
			}

			// 由此往下为Ajax模式的处理

			// 字段校验，无须请求窗口页面
			if (reqInfo.bIsValidateField)
				return respInfo.returnAjaxValidate(170);

			// { Ajax模式下需要刷新整个页面，或者变更了标记类型或者变更了用户身份需要重新加载页面
			if (respInfo.hcr.m_strHeaderUpdateWindow != null
					&& respInfo.hcr.m_strHeaderUpdateWindow
							.equalsIgnoreCase("page"))
				return respInfo.returnAjaxUpdatePage(160);
			if (respInfo.hcr.m_strHeaderSetProfile != null)
				return respInfo.returnAjaxUpdatePage(162);
			if (respInfo.hcr.m_strHeaderMarkupType != null
					&& !respInfo.hcr.m_strHeaderMarkupType.equals(""))
				return respInfo.returnAjaxUpdatePage(164);
			// }

			// 获取新状态
			respInfo.newWinMode = WindowMode.getMode(
					((HttpServletRequest) req.getRequestObject()).getSession(),
					reqInfo.winst.m_lPageID, reqInfo.winst.m_strAreaName,
					reqInfo.winst.m_lPageContID);

			// {获取要刷新的窗口的ID
			respInfo.strAjaxWinIds = respInfo.hcr.m_strHeaderUpdateWindow;
			respInfo.strAjaxEnsureVisible = respInfo.hcr.m_strHeaderEnsureVisible;
			// }

			if (respInfo.hcr.m_bHeaderUseDialog) // 返回弹出对话框
				return respInfo.returnAjaxDialog(190);

			// 窗口被隐藏了，而且窗口原本不是最大化，返回空内容刷新窗口即可
			// 窗口模式未改变，而且窗口内容可以被Cache，则无需重新获取窗口内容
			if (respInfo.newWinMode == EnumWinMode.HIDE
					&& reqInfo.currWinMode != EnumWinMode.MAX
					|| respInfo.newWinMode == reqInfo.currWinMode
					&& respInfo.hcr.m_bHeaderCache) {
				return respInfo.returnNone(170);
			}

			return respInfo.returnAjaxUpdateContent(180);
		}

		return respInfo;
	}

	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		try {
			DbAdapter adapter = new DbAdapter(req.getDBConn());

			RequestInfo reqInfo = new RequestInfo(adapter, req,
					m_bForReloadPage);
			ResponseInfo respInfo = process(adapter, req, resp, reqInfo);

			// 判断是否需要重新计算页面
			if (respInfo.responseType == ResponseType.RELOAD_PAGE)
				return respInfo.iRetCode;

			//
			// 预加载window，以便处理可以在显示页面时改变用户身份的window，并且通过多线程加载减少加载排队所需时间
			// 在forward到页面模板jsp前获取window的内容，以便处理用户身份变化或者重定向等响应
			//
			if (respInfo.responseType == ResponseType.PAGE) {
				reqInfo.getPrivatePages(adapter);
				reqInfo.getPrivateNames(adapter);
				reqInfo.mergePrivate(adapter);
				reqInfo.loadTempate(adapter);
				adapter.rollback();

				Vector<HttpClientResponse> vec = AreaContentTag
						.preloadWindowsForPage(adapter,
								(HttpServletRequest) req.getRequestObject(),
								(HttpServletResponse) resp.getResponseObject(),
								reqInfo);

				// 第一轮请求检查如果用户身份被改变，则重新进入
				if (!m_bForReloadPage)
					for (HttpClientResponse hcr : vec)
						if (hcr.m_bUserChanged)
							return 10;

				// 第二轮请求忽略用户身份变化，只处理重定向，避免死循环。第一轮用户身份未改变也进行重定向检查
				for (HttpClientResponse hcr : vec) {
					if (hcr.m_strRedirect != null) {
						respInfo.returnRedirectUrl(145, hcr.m_strRedirect);
						break;
					} else {
						// 检查是否窗口返回结果中指定了重定向
						if (hcr.m_strHeaderSetRedirect != null
								&& !hcr.m_strHeaderSetRedirect.equals("")) {
							// 根据指定的target检索属于同一branch、同一应用的窗口实例
							ApBPageContent winst = new ApBPageContent();
							winst.m_strContName = hcr.m_strHeaderSetRedirect;
							winst.m_lContentID = reqInfo.win.m_lAppID;
							if (adapter.retrieve(winst, "loadWindowByUrl") != null) {
								ApBPage page = new ApBPage();
								page.m_lPageID = winst.m_lPageID;
								adapter.retrieve(page, "loadFullPath");
								respInfo.returnRedirectPage(140, page);
								break;
							}
						}
					}
				}
			}

			req.setAttribute(ATTR_REQUEST_INFO, reqInfo);
			req.setAttribute(ATTR_RESPONSE_INFO, respInfo);

			return 0;
		} catch (Exception e) {
			resp.setRetThrow(e);

			return 100;
		}
	}
}
