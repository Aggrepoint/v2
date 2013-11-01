package com.aggrepoint.ae.plugin;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.plugin.SimplePsnEngine;
import com.aggrepoint.adk.plugin.WinletUserAgent;
import com.aggrepoint.su.core.data.UserAgent;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 增加识别浏览器能力的支持
 * 
 * @author Owner
 * 
 */
public class AEPsnEngine extends SimplePsnEngine {
	public static final String USER_AGENT_SESSION_KEY = "com.aggrepoint.ae.useragent";

	public UserAgent getUserAgent() {
		UserAgent ua = (UserAgent) m_request
				.getSessionAttribute(USER_AGENT_SESSION_KEY);
		if (ua == null) {
			try {
				ua = UserAgent.getUserAgent(
						new DbAdapter(m_request.getDBConn()),
						m_request.getHeader("user-agent"),
						m_request.getHeader("accept"));
			} catch (Exception e) {
				e.printStackTrace();
				ua = new UserAgent();
			}

			m_request.setSessionAttribute(USER_AGENT_SESSION_KEY, ua);
		}

		return ua;
	}

	public void init(IModuleRequest request, IUserProfile userProfile) {
		super.init(request, userProfile);
		getUserAgent();
	}

	public String translate(String value) {
		String val = super.translate(value);
		if (val != null)
			return val;

		if (value.startsWith("ua.")) {
			UserAgent ua = getUserAgent();

			value = value.substring(3);
			if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_AJAX))
				if (ua.supportAjax)
					val = "T";
				else
					val = "F";
			else if (value
					.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_HTML))
				if (ua.supportHTML)
					val = "T";
				else
					val = "F";
			else if (value
					.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_XHTML))
				if (ua.supportXHTML)
					val = "T";
				else
					val = "F";
			else if (value
					.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_WML))
				if (ua.supportWML)
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_IS_SPIDER))
				if (ua.isSpider)
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_IS_MOBILE))
				if (ua.isMobile)
					val = "T";
				else
					val = "F";
		}

		return val;
	}
}
