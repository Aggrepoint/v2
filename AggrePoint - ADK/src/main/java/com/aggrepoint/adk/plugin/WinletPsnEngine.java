package com.aggrepoint.adk.plugin;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.plugin.SimplePsnEngine;
import com.aggrepoint.adk.plugin.WinletUserAgent;

/**
 * 增加User Agent的支持
 * 
 * @author Owner
 * 
 */
public class WinletPsnEngine extends SimplePsnEngine implements IWinletConst {
	WinletUserAgent m_userAgent;
	EnumMarkup m_markup;

	public WinletUserAgent getUserAgent() {
		return m_userAgent;
	}

	public EnumMarkup getMarkup() {
		EnumMarkup markup = super.getMarkup();
		if (markup == null)
			return m_markup;
		return markup;
	}

	public void init(IModuleRequest request, IUserProfile userProfile) {
		super.init(request, userProfile);
		try {
			m_markup = EnumMarkup.fromName(request.getHeader(REQUEST_HEADER_MARKUP_TYPE));

			m_userAgent = new WinletUserAgent(request.getHeader(REQUEST_HEADER_USER_AGENT));
		} catch (Exception e) {
			m_userAgent = new WinletUserAgent();
			e.printStackTrace();
		}
	}

	public String translate(String value) {
		String val = super.translate(value);
		if (val != null)
			return val;

		if (value.startsWith("ua.")) {
			value = value.substring(3);
			if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_AJAX))
				if (m_userAgent.isSupportAjax())
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_HTML))
				if (m_userAgent.isSupportHtml())
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_XHTML))
				if (m_userAgent.isSupportXhtml())
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_SUPPORT_WML))
				if (m_userAgent.isSupportWml())
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_IS_SPIDER))
				if (m_userAgent.isSpider())
					val = "T";
				else
					val = "F";
			else if (value.equalsIgnoreCase(WinletUserAgent.PROPERTY_IS_MOBILE))
				if (m_userAgent.isMobile())
					val = "T";
				else
					val = "F";
			else if (value
					.equalsIgnoreCase(WinletUserAgent.PROPERTY_DEFAULT_MARKUP))
				val = m_userAgent.getDefaultMarkup();
		} else if (value.startsWith("markup"))
			val = m_markup.getName();

		return val;
	}
}
