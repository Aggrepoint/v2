package com.aggrepoint.ae.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IUserEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.ae.core.Tools;
import com.aggrepoint.ae.data.CookieConst;
import com.aggrepoint.ae.data.SessionConst;
import com.aggrepoint.ae.win.WindowMode;
import com.aggrepoint.su.core.data.UserSession;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CookieTool;

/**
 * 生成用户身份
 * 
 * @author YJM
 */
public class AEUserEngine implements IUserEngine, SessionConst, CookieConst {
	public static final String SESSION_ATTR_USERPROFILE = AEUserEngine.class
			.getName() + ".UserProfile";

	public static final WinletUserProfile ANONYMOUS_USER = new WinletUserProfile();

	public IUserProfile getUserProfile(IModuleRequest req, IModuleResponse resp) {
		IUserProfile userProfile = (IUserProfile) req
				.getSessionAttribute(SESSION_ATTR_USERPROFILE);

		if (userProfile != null)
			return userProfile;

		// {加载保存的用户身份
		try {
			String sessionId = CookieTool.getCookie(
					(HttpServletRequest) req.getRequestObject(),
					COOKIE_SESSION_ID);

			if (sessionId != null) {
				UserSession session = new UserSession();
				session.sessionID = sessionId;

				// 加载会话
				DbAdapter adapter = new DbAdapter(req.getDBConn());
				if (adapter.retrieve(session) == null) { // 会话在库中找不到
					CookieTool.deleteCookie(
							(HttpServletResponse) resp.getResponseObject(),
							COOKIE_SESSION_ID);
					return ANONYMOUS_USER;
				}
				if (session.validTime.getTime() < System.currentTimeMillis()
						|| !session.ip.equals(Tools.getRemoteAddr(req))) { // 超时或地址不对
					adapter.delete(session);
					CookieTool.deleteCookie(
							(HttpServletResponse) resp.getResponseObject(),
							COOKIE_SESSION_ID);
					return ANONYMOUS_USER;
				}

				WinletUserProfile profile = new WinletUserProfile(
						session.profile);
				req.setSessionAttribute(SESSION_ATTR_USERPROFILE, profile);

				// 加载保存的用户窗口状态
				WindowMode.retrievePersistanceMode(adapter,
						((HttpServletRequest) req.getRequestObject())
								.getSession(), profile
								.getProperty(IUserProfile.PROPERTY_ID));

				return profile;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ANONYMOUS_USER;
		// }
	}
}
