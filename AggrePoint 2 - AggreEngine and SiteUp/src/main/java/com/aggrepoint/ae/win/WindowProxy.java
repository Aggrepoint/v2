package com.aggrepoint.ae.win;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.ae.AccessPointProcess;
import com.aggrepoint.ae.BPageURLConstructor;
import com.aggrepoint.ae.core.Tools;
import com.aggrepoint.ae.core.proxy.HttpClientResponse;
import com.aggrepoint.ae.core.proxy.HttpProxy;
import com.aggrepoint.ae.data.CookieConst;
import com.aggrepoint.ae.data.SessionConst;
import com.aggrepoint.ae.plugin.AEUserEngine;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.UUIDGen;
import com.aggrepoint.su.core.data.UserSession;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CombineString;
import com.icebean.core.common.CookieTool;
import com.icebean.core.common.HTTPUtils;

/**
 * 窗口操作代理
 * 
 * @author YJM
 */
public class WindowProxy implements IWinletConst, SessionConst, CookieConst {
	static SimpleDateFormat m_cookieDate = new SimpleDateFormat(
			"E, dd MMM yyyy k:m:s 'GMT'");

	/**
	 * 根据HttpProxy返回的HttpClientResponse更新标记语言选择
	 * 
	 * @param hcr
	 * @param req
	 * @return
	 */
	static HttpClientResponse updateMarkupSel(HttpClientResponse hcr,
			HttpServletRequest req, HttpServletResponse resp) {
		if (hcr.m_strHeaderMarkupType != null
				&& !hcr.m_strHeaderMarkupType.equals("")) {
			if (hcr.m_strHeaderMarkupType
					.equals(RESPONSE_HEADER_CONST_MARKUP_SAVE)) { // 将当前选定的标记类型保存为cookie
				EnumMarkup markup = (EnumMarkup) req.getSession(true)
						.getAttribute(ATTR_MARKUP);
				if (markup != null)
					CookieTool.setCookie(resp, COOKIE_MARKUP,
							Integer.toString(markup.getId()), "/",
							365 * 60 * 60 * 24);
			} else { // 为当前会话选用新的标记类型
				req.getSession().setAttribute(ATTR_MARKUP,
						EnumMarkup.fromName(hcr.m_strHeaderMarkupType));
				req.getSession().removeAttribute(ATTR_BRANCH_SEL);
			}
		}
		return hcr;
	}

	/**
	 * 根据根据HttpProxy返回的HttpClientResponse更新窗口状态
	 * 
	 * @param hcr
	 * @param req
	 * @param pageID
	 * @param areaName
	 * @param iid
	 * @return
	 */
	static HttpClientResponse updateWindowState(AdbAdapter adapter,
			HttpClientResponse hcr, HttpServletRequest req, String uid,
			long pageID, long iid, boolean flag) {
		if (flag && hcr.m_headerSetWindowMode != EnumWinMode.NOCHANGE
				&& hcr.m_headerSetWindowMode != EnumWinMode.HIDE) {
			if (hcr.m_headerSetWindowMode != WindowMode.getMode(
					req.getSession(), pageID, iid)) { // 变更窗口状态
				WindowMode.setMode(adapter, req.getSession(), uid, pageID, iid,
						hcr.m_headerSetWindowMode);
			}
		}

		return hcr;
	}

	/**
	 * 根据HttpProxy返回的HttpClientResponse设置用户身份
	 * 
	 * @param req
	 * @param hcr
	 */
	public static HttpClientResponse setUserProfile(AdbAdapter adapter,
			HttpServletRequest req, HttpServletResponse resp,
			HttpClientResponse hcr, boolean flag)
			throws UnsupportedEncodingException {
		if (flag && hcr.m_strHeaderSetProfile != null) {
			WinletUserProfile currProfile = (WinletUserProfile) req
					.getSession().getAttribute(
							AEUserEngine.SESSION_ATTR_USERPROFILE);

			if (!hcr.m_strHeaderSetProfile.equals("")) {
				WinletUserProfile profile = new WinletUserProfile(
						hcr.m_strHeaderSetProfile);
				hcr.m_bUserChanged = currProfile == null
						|| !currProfile.equals(profile);

				req.getSession().setAttribute(
						AEUserEngine.SESSION_ATTR_USERPROFILE, profile);

				// 恢复用户窗口设置
				WindowMode.retrievePersistanceMode(adapter, req.getSession(),
						profile.getProperty(IUserProfile.PROPERTY_ID));

				String keepTime = profile.getProperty(IUserProfile.KEEP_TIME);

				if (keepTime != null && !keepTime.equals("")) { // 需要创建持久会话
					try {
						int time = Integer.parseInt(keepTime);

						// 保存用户身份
						UserSession session = new UserSession();
						session.sessionID = UUIDGen.get();
						session.ip = Tools.getRemoteAddr(req);
						session.profile = hcr.m_strHeaderSetProfile;
						session.validTime = new Timestamp(
								System.currentTimeMillis() + time * 1000);

						CookieTool.setCookie(resp, COOKIE_SESSION_ID,
								session.sessionID, "/", time);

						adapter.create(session);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else { // 无需持久会话，删除cookie
					CookieTool.deleteCookie(resp, COOKIE_SESSION_ID);
				}
			} else {
				hcr.m_bUserChanged = currProfile != null;

				// 如果用户身份被保存了，退出登录时取消保存
				String sid = CookieTool.getCookie(req, COOKIE_SESSION_ID);

				if (sid != null && !sid.equals("")) {
					try {
						UserSession session = new UserSession();
						session.sessionID = sid;
						adapter.delete(session);
					} catch (Exception e) {
						e.printStackTrace();
					}

					// 删除对应的cookie
					CookieTool.deleteCookie(resp, COOKIE_SESSION_ID);
				}

				// 清除会话
				req.getSession().invalidate();
			}

			// 用户身份变更了，清除页面跳转子页面URL
			AccessPointProcess.clearSubPage(req);

			// 清除后端cookie，重新生成会话
			HttpProxy.clearCookies(req);
		}
		return hcr;
	}

	/**
	 * View window
	 * 
	 * In order to prevent db deadlock, roll back the transaction before the
	 * request.
	 */
	public static HttpClientResponse request(DbAdapter adapter,
			BPageURLConstructor urlc, long reqId, HttpServletRequest request,
			HttpServletResponse response, String view, long winId, long iid,
			long pid, String areaName, long bid, String winParams, int winMode,
			String otherParams, String uid, String userProfile,
			String userAgent, String ip, String uri, String markup,
			boolean resetWin, String resUrl, boolean forceCheck)
			throws AdbException, WindowNotFoundException, Exception {
		ApWindow win = ApWindow.getWindow(adapter, winId, forceCheck);
		if (win == null)
			throw new WindowNotFoundException();

		ApApp app = ApApp.getApp(adapter, win.m_lAppID, forceCheck);

		if (adapter instanceof DbAdapter)
			((DbAdapter) adapter).rollback();

		if (app.m_iStatusID != ApApp.STATUS_NORMAL)
			return new HttpClientResponse();

		if (view == null)
			view = "";

		return updateMarkupSel(
				updateWindowState(
						adapter,
						setUserProfile(
								adapter,
								request,
								response,
								HttpProxy
										.request(
												0,
												reqId,
												request,
												null,
												HTTPUtils
														.appendGetParameters(
																app.m_strHostURL
																		+ app.m_strRootPath
																		+ win.m_strURL,
																new String[] {
																		REQUEST_PARAM_WIN_INST_ID,
																		Long.toString(iid),
																		REQUEST_PARAM_WIN_PAGE_ID,
																		Long.toString(pid),
																		REQUEST_PARAM_VIEW_ID,
																		view })
														+ "&" + otherParams,
												bid, app.m_lAppID, winParams,
												winMode, userProfile,
												userAgent, ip, uri, markup,
												urlc.appRes(adapter, app),
												resUrl, areaName, resetWin,
												app.m_iConnTimeout,
												app.m_iReadTimeout), win
										.getViewChangeUserFlag()), request,
						uid, pid, iid, win.getDynaWinStateFlag()), request,
				response);
	}

	/**
	 * Request window resource
	 * 
	 * In order to prevent db deadlock, roll back the transaction before the
	 * request.
	 */
	public static HttpClientResponse resource(DbAdapter adapter,
			BPageURLConstructor urlc, long reqId, HttpServletRequest request,
			IModuleResponse response, String view, String res,
			ApBPageContent winst, String param, int winMode,
			String otherParams, String userProfile, String userAgent,
			String ip, String uri, String markup, String resUrl,
			boolean forceCheck) throws AdbException, WindowNotFoundException,
			Exception {
		ApWindow win = ApWindow.getWindow(adapter, winst.m_lWindowID,
				forceCheck);
		if (win == null)
			throw new WindowNotFoundException();

		ApApp app = ApApp.getApp(adapter, win.m_lAppID, forceCheck);

		if (view == null)
			view = "";

		if (adapter instanceof DbAdapter)
			((DbAdapter) adapter).rollback();

		if (app.m_iStatusID != ApApp.STATUS_NORMAL)
			return new HttpClientResponse();

		HttpServletResponse resp = (HttpServletResponse) response
				.getResponseObject();

		return setUserProfile(adapter, request, resp, HttpProxy.request(
				2,
				reqId,
				request,
				response,
				HTTPUtils.appendGetParameters(
						app.m_strHostURL + app.m_strRootPath + win.m_strURL,
						new String[] { REQUEST_PARAM_WIN_INST_ID,
								Long.toString(winst.m_lPageContID),
								REQUEST_PARAM_WIN_PAGE_ID,
								Long.toString(winst.m_lPageID),
								REQUEST_PARAM_VIEW_ID, view,
								REQUEST_PARAM_ACTION, res })
						+ "&" + otherParams, winst.m_lBranchID, app.m_lAppID,
				CombineString.combine(winst.m_strWinParams, param, '~'),
				winMode, userProfile, userAgent, ip, uri, markup,
				urlc.appRes(adapter, app), resUrl, winst.m_strAreaName, false,
				app.m_iConnTimeout, app.m_iReadTimeout),
				win.getViewChangeUserFlag());
	}

	/**
	 * Do window action
	 * 
	 * In order to prevent db deadlock, roll back the transaction before the
	 * request.
	 */
	public static HttpClientResponse action(DbAdapter adapter,
			BPageURLConstructor urlc, long reqId, HttpServletRequest request,
			HttpServletResponse response, String view, String action,
			String form, boolean validateField, ApBPageContent winst,
			String param, String uid, String userProfile, String userAgent,
			String ip, String uri, String markup, String resUrl,
			boolean forceCheck) throws AdbException, WindowNotFoundException,
			Exception {
		ApWindow win = ApWindow.getWindow(adapter, winst.m_lWindowID,
				forceCheck);
		if (win == null)
			throw new WindowNotFoundException();

		ApApp app = ApApp.getApp(adapter, win.m_lAppID, forceCheck);

		if (view == null)
			view = "";

		if (adapter instanceof DbAdapter)
			((DbAdapter) adapter).rollback();

		return updateMarkupSel(
				updateWindowState(
						adapter,
						setUserProfile(
								adapter,
								request,
								response,
								HttpProxy
										.request(
												1,
												reqId,
												request,
												null,
												HTTPUtils
														.appendGetParameters(
																app.m_strHostURL
																		+ app.m_strRootPath
																		+ win.m_strURL,
																new String[] {
																		REQUEST_PARAM_WIN_INST_ID,
																		Long.toString(winst.m_lPageContID),
																		REQUEST_PARAM_WIN_PAGE_ID,
																		Long.toString(winst.m_lPageID),
																		REQUEST_PARAM_VIEW_ID,
																		view,
																		REQUEST_PARAM_ACTION,
																		action,
																		REQUEST_PARAM_FORM,
																		form,
																		REQUEST_PARAM_VALIDATE_FIELD,
																		validateField ? "yes"
																				: "no" }),
												winst.m_lBranchID,
												app.m_lAppID,
												CombineString.combine(
														winst.m_strWinParams,
														param, '~'),
												EnumWinMode.NOCHANGE.getId(),
												userProfile, userAgent, ip,
												uri, markup, urlc.appRes(
														adapter, app), resUrl,
												winst.m_strAreaName, false,
												app.m_iConnTimeout,
												app.m_iReadTimeout), win
										.getUserProfileFlag()), request, uid,
						winst.m_lPageID, winst.m_lPageContID,
						win.getDynaWinStateFlag()), request, response);
	}
}
