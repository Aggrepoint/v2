package com.aggrepoint.su;

import java.sql.Connection;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UserAgent;
import com.icebean.core.adb.ADBList;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 用户代理管理
 * 
 * @author YJM
 */
public class UAgent extends Winlet implements RuleConst {
	private static final long serialVersionUID = 1L;

	String m_strUserAgent = "";

	/** -1 - 所有，1 - Auto，0 - 非Auto */
	int m_iAuto = -1;

	int m_iPageNo;

	long m_lUAID;

	UserAgent m_uaEdit;

	/**
	 * 显示用户代理列表页面
	 */
	public int showList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		Connection conn = req.getDBConn();

		DbAdapter adapter = new DbAdapter(conn);
		UserAgent ua = new UserAgent();
		if (m_strUserAgent != null && !m_strUserAgent.trim().equals("")) {
			ua.setUA("%" + m_strUserAgent + "%");
			adapter.setFlag(ua, "useragent");
		}
		if (m_iAuto >= 0) {
			ua.autoFlag = m_iAuto > 0;
			adapter.setFlag(ua, "autoflag");
		}
		ADBList<UserAgent> uas = adapter.retrieveMultiDbl(ua, "search",
				"default", 50, m_iPageNo, true);
		req.setAttribute("UAS", uas);

		req.setAttribute("USERAGENT", m_strUserAgent);
		req.setAttribute("AUTO", m_iAuto);
		req.setAttribute("PAGENO", m_iPageNo);

		return 0;
	}

	/**
	 * 显示用户代理编辑页面
	 */
	public int showEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UserAgent ua;

		if (m_uaEdit != null) { // 若是在服务器端处理时出错
			ua = m_uaEdit;
		} else {
			ua = new UserAgent();
			ua.agentID = m_lUAID;

			if (ua.agentID != 0) { // 编辑
				if (adapter.retrieve(ua) == null)
					return 1001;
			}
		}

		req.setAttribute("UA", ua);

		return 0;
	}

	/**
	 * 清除所有数据
	 */
	public int clearData(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lUAID = 0;
		m_uaEdit = null;

		return 0;
	}

	/**
	 * 输入翻页条件
	 * 
	 * @author Administrator
	 */
	public int doPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_iPageNo = req.getParameter("pno", 0);
		return 0;
	}

	/**
	 * 输入查询条件
	 * 
	 * @author Administrator
	 */
	public int doSearch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_strUserAgent = req.getParameter("ua", "");
		m_iAuto = req.getParameter("auto", 0);
		return 0;
	}

	/**
	 * 操作UA
	 */
	public int action(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long l = req.getParameter("uaid", -1l);
		if (l == -1)
			return 1002;
		m_lUAID = l;
		return 0;
	}

	/**
	 * 保存修改
	 */
	public int save(IModuleRequest req, IModuleResponse resp) throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UserAgent ua = new UserAgent();
		ua.agentID = m_lUAID;

		if (ua.agentID > 0) { // 编辑
			if (adapter.retrieve(ua) == null)
				return 1001;
		}

		ua.setDeviceTypeID(req.getParameter("devtype", 0));
		ua.deviceModel = req.getParameter("devmodel", "");
		ua.deviceVersion = req.getParameter("devver", "");
		ua.deviceMajorVersion = req.getParameter("devmajver", 0);
		ua.deviceMinorVersion = req.getParameter("devminver", 0);
		ua.setOSTypeID(req.getParameter("ostype", 0));
		ua.osVersion = req.getParameter("osver", "");
		ua.osMajorVersion = req.getParameter("osmajver", 0);
		ua.osMinorVersion = req.getParameter("osminver", 0);
		ua.setBrowserTypeID(req.getParameter("btype", 0));
		ua.browserVersion = req.getParameter("bver", "");
		ua.browserMajorVersion = req.getParameter("bmajver", 0);
		ua.browserMinorVersion = req.getParameter("bminver", 0);
		ua.defaultMarkup = req.getParameter("markup", 0);
		ua.supportAjax = req.getParameter("ajx", 0) != 0;
		ua.supportHTML = req.getParameter("html", 0) != 0;
		ua.supportXHTML = req.getParameter("xhtml", 0) != 0;
		ua.supportWML = req.getParameter("wml", 0) != 0;
		ua.isSpider = req.getParameter("spider", 0) != 0;
		ua.isMobile = req.getParameter("mobile", 0) != 0;
		ua.autoFlag = false;

		// 简单起见，自动修正support，确保至少支持一种标记语言
		if (!ua.supportHTML && !ua.supportWML && !ua.supportXHTML)
			ua.supportHTML = true;

		// {简单起见，自动修正Default Markup
		if (ua.defaultMarkup != EnumMarkup.HTML.getId()
				&& ua.defaultMarkup != EnumMarkup.WML.getId()
				&& ua.defaultMarkup != EnumMarkup.XHTML.getId())
			ua.defaultMarkup = EnumMarkup.HTML.getId();
		if (ua.defaultMarkup == EnumMarkup.HTML.getId() && !ua.supportHTML)
			ua.defaultMarkup = ua.supportXHTML ? EnumMarkup.XHTML.getId()
					: EnumMarkup.WML.getId();
		else if (ua.defaultMarkup == EnumMarkup.XHTML.getId()
				&& !ua.supportXHTML)
			ua.defaultMarkup = ua.supportHTML ? EnumMarkup.HTML.getId()
					: EnumMarkup.WML.getId();
		else if (ua.defaultMarkup == EnumMarkup.WML.getId() && !ua.supportWML)
			ua.defaultMarkup = ua.supportHTML ? EnumMarkup.HTML.getId()
					: EnumMarkup.XHTML.getId();
		// }

		if (ua.agentID > 0)
			adapter.update(ua);
		else
			adapter.create(ua);

		// {清除编辑站点过程中可能产生的数据
		clearData(req, resp);

		return 0;
	}
}
