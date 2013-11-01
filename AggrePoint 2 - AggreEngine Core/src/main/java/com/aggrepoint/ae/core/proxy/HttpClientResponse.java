/*
 * 创建日期 2004-10-14
 */
package com.aggrepoint.ae.core.proxy;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IWinletConst;

/**
 * HttpProxy请求后获得的结果
 * 
 * @author YJM
 */
public class HttpClientResponse implements IWinletConst {
	/** 响应头中与IAAP有关的值 */
	public String m_strHeaderSetProfile;

	public String m_strHeaderSetRedirect;

	public String m_strHeaderWindowTitle;

	public String m_strHeaderMarkupType;

	public EnumWinMode m_headerSetWindowMode;

	public boolean m_bHeaderCache;

	public String m_strHeaderUpdateWindow;

	public String m_strHeaderEnsureVisible;

	public String m_strHeaderMessage;

	public boolean m_bHeaderUseDialog;

	/** 获得的对象 */
	public String m_response;

	/** 直接返回的重定向URL。只有在资源访问方式中才可以使用这种方式返回重定向 */
	public String m_strRedirect;

	/** 用户身份是否发生了改变 */
	public boolean m_bUserChanged;

	public HttpClientResponse() {
		m_strHeaderSetProfile = null;
		m_strHeaderWindowTitle = null;
		m_strHeaderUpdateWindow = "";
		m_strHeaderEnsureVisible = "";
		m_headerSetWindowMode = EnumWinMode.NOCHANGE;
		m_strHeaderMarkupType = null;
		m_strHeaderMessage = "";
		m_bHeaderCache = false;
		m_bHeaderUseDialog = false;
		m_response = "";
		m_strRedirect = null;
	}

	public HttpClientResponse(HttpURLConnection conn)
			throws UnsupportedEncodingException {
		m_strHeaderSetProfile = conn
				.getHeaderField(RESPONSE_HEADER_SET_USER_PROFILE);
		m_strHeaderSetRedirect = conn
				.getHeaderField(RESPONSE_HEADER_SET_REDIRECT);
		m_strHeaderWindowTitle = conn
				.getHeaderField(RESPONSE_HEADER_SET_WINDOW_TITLE);
		m_strHeaderUpdateWindow = conn
				.getHeaderField(RESPONSE_HEADER_SET_UPDATE_WINDOW);
		m_strHeaderEnsureVisible = conn
				.getHeaderField(RESPONSE_HEADER_SET_ENSURE_VISIBLE);
		m_strHeaderMarkupType = conn
				.getHeaderField(RESPONSE_HEADER_SET_MARKUP_TYPE);
		m_strHeaderMessage = conn.getHeaderField(RESPONSE_HEADER_SET_MESSAGE);
		String str = conn.getHeaderField(RESPONSE_HEADER_SET_USE_DIALOG);
		m_bHeaderUseDialog = str != null && str.equalsIgnoreCase("YES");
		str = conn.getHeaderField(RESPONSE_HEADER_SET_CACHE);
		if (str != null && str.equalsIgnoreCase("YES"))
			m_bHeaderCache = true;

		if (m_strHeaderUpdateWindow == null)
			m_strHeaderUpdateWindow = "";
		if (m_strHeaderEnsureVisible == null)
			m_strHeaderEnsureVisible = "";
		if (m_strHeaderWindowTitle != null)
			m_strHeaderWindowTitle = URLDecoder.decode(m_strHeaderWindowTitle,
					"UTF-8");
		if (m_strHeaderMessage == null)
			m_strHeaderMessage = "";
		else
			m_strHeaderMessage = java.net.URLDecoder.decode(m_strHeaderMessage,
					"UTF-8");
		try {
			m_headerSetWindowMode = EnumWinMode.fromStrId(conn
					.getHeaderField(RESPONSE_HEADER_SET_WINDOW_MODE));
		} catch (Exception e) {
		}

		try {
			if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
					|| conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
					|| conn.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER)
				m_strRedirect = conn.getHeaderField("Location");
		} catch (Exception e) {
		}
	}
}
