package com.aggrepoint.adk.http;

import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.aggrepoint.adk.Controller;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.data.RetCode;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.xml.XmlAdapter;
import com.icebean.core.common.StringUtils;

/**
 * @author YJM
 */
public class HttpController extends Controller implements Serializable {
	private static final long serialVersionUID = 1L;

	ServletContext m_context;

	public HttpController(ServletConfig config) {
		InputStream is = null;
		ServletContext context = config.getServletContext();

		try {
			m_context = context;

			is = context.getResourceAsStream("/WEB-INF/apapp.xml");

			Document doc = javax.xml.parsers.DocumentBuilderFactory
					.newInstance().newDocumentBuilder().parse(
							new org.xml.sax.InputSource(is));

			AdbAdapter adapter = new XmlAdapter(doc);

			String rootDir = config.getServletContext().getRealPath("/");
			if (!rootDir.endsWith("/") && !rootDir.endsWith("\\"))
				rootDir += "/";

			init(adapter, rootDir, rootDir + "WEB-INF/temp/");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (Exception e) {
				}
		}
	}

	/**
	 * 设置防止客户端和Proxy对页面进行Cache的响应头
	 */
	void setNoCache(HttpServletResponse resp) {
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Expires", "0");
	}

	String transformJspUrl(String url) {
		if (url.endsWith(".jsp")) {
			url = url.substring(0, url.lastIndexOf(".jsp"));
			if (url.startsWith("/"))
				url = url.substring(1);
			return "/servlet/com.aggrepoint.view."
					+ StringUtils.replaceString(url, "/", ".");
		}
		return url;
	}

	/**
	 * 根据响应码定义处理展现
	 */
	public void present(IModuleRequest req, IModuleResponse res, RetCode code)
			throws Exception {
		switch (code.m_pMethod) {
		case NOACTION: // 无动作
			return;
		case FORWARD: // Forward
			if (!code.m_bCache)
				setNoCache((HttpServletResponse) res.getResponseObject());

			m_context.getRequestDispatcher(
					m_bDev ? code.m_strUrl : transformJspUrl(code.m_strUrl))
					.forward((HttpServletRequest) req.getRequestObject(),
							(HttpServletResponse) res.getResponseObject());
			return;
		case INCLUDE: // Include
			if (!code.m_bCache)
				setNoCache((HttpServletResponse) res.getResponseObject());

			m_context.getRequestDispatcher(
					m_bDev ? code.m_strUrl : transformJspUrl(code.m_strUrl))
					.include((HttpServletRequest) req.getRequestObject(),
							(HttpServletResponse) res.getResponseObject());
			return;
		case REDIRECT: // Redirect
			((HttpServletResponse) res.getResponseObject())
					.sendRedirect(code.m_strUrl);
			return;
		case FORWARD_CUSTOMIZED: // Forward
			m_context.getRequestDispatcher(res.getRetUrl()).forward(
					(HttpServletRequest) req.getRequestObject(),
					(HttpServletResponse) res.getResponseObject());
			return;
		case INCLUDE_CUSTOMIZED: // Include
			m_context.getRequestDispatcher(res.getRetUrl()).include(
					(HttpServletRequest) req.getRequestObject(),
					(HttpServletResponse) res.getResponseObject());
			return;
		case REDIRECT_CUSTOMIZED: // Redirect
			((HttpServletResponse) res.getResponseObject()).sendRedirect(res
					.getRetUrl());
			return;
		}
	}
}
