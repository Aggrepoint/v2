package com.aggrepoint.adk.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.adk.ControlException;
import com.aggrepoint.adk.IAdkConst;

/**
 * ADK入口Servlet 负责根据请求URL和配置加载并运行相应的Module
 * 
 * @author YJM
 */
public class HttpControlServlet extends HttpServlet implements IAdkConst {
	private static final long serialVersionUID = 1L;

	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	/** 已经完成处理个数 */
	long m_lRunCount = 0;

	/** 并发正在运行的处理个数 */
	int m_iCurrentCount = 0;

	static final String WINDOW_DATA_KEY_STATE = "_state";

	HttpController m_controller;

	/**
	 * 初始化 根据Servlet参数加载配置XML
	 */
	public void init(ServletConfig config) throws ServletException {
		m_controller = new HttpController(config);
	}

	public final void doGet(HttpServletRequest req, HttpServletResponse res)
			throws javax.servlet.ServletException, java.io.IOException {
		defaultProc(req, res);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse res)
			throws javax.servlet.ServletException, java.io.IOException {
		defaultProc(req, res);
	}

	/**
	 * 处理请求
	 */
	public void defaultProc(HttpServletRequest req, HttpServletResponse res) {
		long lStart = System.currentTimeMillis();
		String strPath = req.getRequestURL().toString();

		m_lRunCount++;
		m_iCurrentCount++;

		if (m_log.isDebugEnabled())
			m_log.debug("Run: " + m_lRunCount + " Current: " + m_iCurrentCount
					+ " On: " + strPath);

		try {
			req.setCharacterEncoding("UTF-8");
			res.setHeader("Pragma", "no-cache");
			res.setHeader("Cache-Control", "no-cache");
			res.setDateHeader("Expires", 0);
		} catch (Exception e) {
		}

		try {
			if (m_controller.m_strResourceRootPath == null) {
				String str = req.getRequestURI();
				if (req.getPathInfo() != null)
					str = str.substring(0, str.lastIndexOf(req.getPathInfo()));
				m_controller.m_strResourceRootPath = str.substring(0, str
						.indexOf('/', 1));
			}

			HttpModuleRequest request = new HttpModuleRequest(m_controller,
					req, m_lRunCount);
			HttpModuleResponse response = new HttpModuleResponse(res);

			try {
				m_controller.defaultProc(request, response);
			} catch (ControlException e) {
				m_controller.handleControlException(request, response, e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			m_iCurrentCount--;
			if (m_log.isDebugEnabled())
				m_log.debug("Finish: " + m_lRunCount + " Current: "
						+ m_iCurrentCount + " In: "
						+ (System.currentTimeMillis() - lStart) + " On: "
						+ strPath);
		}
	}
}
