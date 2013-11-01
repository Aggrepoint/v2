package com.aggrepoint.adk.plugin;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IWinletConst;
import com.aggrepoint.adk.WinletModule;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.icebean.core.common.ThreadContext;

/**
 * 从请求中获取与Winlet相关的信息
 * 
 * @author YJM
 * 
 */
public class WinletReqInfo implements IWinletConst, IAdkConst {
	static final String REQ_ATTR_REQ_INFO = WinletReqInfo.class.getName();

	/** 是否AE发起的访问 */
	public boolean m_bViaAE;
	public EnumMarkup m_markup;
	public IModuleRequest m_req;
	public WinletPsnEngine m_engine;
	public WinletUserAgent m_agent;
	/** 是否在页面中使用Javascript */
	public boolean m_bUseJS = true;
	/** 是否在生成页面时启用Ajax */
	public boolean m_bUseAjax = false;
	public String m_strWinAreaName;
	public String m_strStaticWinResUrl;
	public String m_strAppId;

	private WinletReqInfo(HttpServletRequest req) {
		// { 判断当前使用的标记类型
		String markup = req.getHeader(REQUEST_HEADER_MARKUP_TYPE);
		m_bViaAE = markup != null;

		// 获取页面中声明的标记类型
		m_markup = MarkupTag.getMarkup(req);
		if (m_markup == null)
			m_markup = EnumMarkup.fromName(markup);
		// }

		m_strStaticWinResUrl = req.getHeader(REQUEST_HEADER_STATIC_WIN_RES_URL);
		m_strAppId = req.getHeader(REQUEST_HEADER_SITE_APP);
		m_strWinAreaName = req.getHeader(REQUEST_HEADER_WINDOW_AREA_NAME);

		// 获取IModuleRequest
		m_req = (IModuleRequest) ThreadContext
				.getAttribute(THREAD_ATTR_REQUEST);
		if (m_req == null)
			return;

		// 获取个性化引擎
		IPsnEngine engine = m_req.getPsnEngine();
		if (engine instanceof WinletPsnEngine)
			m_engine = (WinletPsnEngine) engine;

		if (m_engine == null)
			return;

		// 获取用户代理
		m_agent = m_engine.getUserAgent();

		// 判断是否要启用Ajax
		if (m_markup == EnumMarkup.HTML && m_agent.isSupportAjax()) {
			m_bUseAjax = true;
		}
	}

	public WinletUserAgent getUa() {
		return m_agent;
	}

	public static WinletReqInfo getInfo(ServletRequest req) {
		WinletReqInfo info = (WinletReqInfo) req
				.getAttribute(REQ_ATTR_REQ_INFO);
		if (info != null)
			return info;
		info = new WinletReqInfo((HttpServletRequest) req);
		req.setAttribute(REQ_ATTR_REQ_INFO, info);
		return info;
	}

	public static WinletReqInfo getInfo(IModuleRequest req) {
		WinletReqInfo info = (WinletReqInfo) req
				.getAttribute(REQ_ATTR_REQ_INFO);
		if (info != null)
			return info;
		info = new WinletReqInfo((HttpServletRequest) req.getRequestObject());
		req.setAttribute(REQ_ATTR_REQ_INFO, info);
		return info;
	}

	/**
	 * 用于EL Function和Tag判断执行的是否为Winlet
	 * 
	 * @return
	 */
	public static boolean isInWinlet() {
		Object val = ThreadContext.getAttribute(THREAD_ATTR_MODULE);
		if (val == null)
			return false;
		return val instanceof WinletModule;
	}
}
