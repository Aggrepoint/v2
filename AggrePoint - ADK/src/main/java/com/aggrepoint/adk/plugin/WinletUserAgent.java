package com.aggrepoint.adk.plugin;

import java.io.UnsupportedEncodingException;

import com.aggrepoint.adk.IPropertyObject;
import com.aggrepoint.adk.PropertyObject;

/**
 * 用户代理特性
 * 
 * @author YJM
 */
public class WinletUserAgent extends PropertyObject {
	public static final String PROPERTY_SUPPORT_AJAX = "ajax";

	public static final String PROPERTY_SUPPORT_HTML = "html";

	public static final String PROPERTY_SUPPORT_XHTML = "xhtml";

	public static final String PROPERTY_SUPPORT_WML = "wml";

	public static final String PROPERTY_IS_SPIDER = "spider";

	public static final String PROPERTY_IS_MOBILE = "mobile";

	public static final String PROPERTY_DEFAULT_MARKUP = "markup";

	public static final String BOOLEAN_TRUE = "Y";

	public static final String BOOLEAN_FALSE = "N";

	public WinletUserAgent() {
		super();
	}

	public WinletUserAgent(IPropertyObject ref) {
		super(ref);
	}

	public WinletUserAgent(String prop) throws UnsupportedEncodingException {
		super(prop);
	}

	public boolean isSupportAjax() {
		return getProperty(PROPERTY_SUPPORT_AJAX, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isSupportHtml() {
		return getProperty(PROPERTY_SUPPORT_HTML, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isSupportWml() {
		return getProperty(PROPERTY_SUPPORT_WML, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isSupportXhtml() {
		return getProperty(PROPERTY_SUPPORT_XHTML, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isSpider() {
		return getProperty(PROPERTY_IS_SPIDER, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isMobile() {
		return getProperty(PROPERTY_IS_MOBILE, "").equalsIgnoreCase(
				BOOLEAN_TRUE);
	}

	public boolean isSupportFlash() {
		return isSupportAjax() && !isMobile();
	}

	public String getDefaultMarkup() {
		return getProperty(PROPERTY_DEFAULT_MARKUP, "");
	}
}
