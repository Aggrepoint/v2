package com.aggrepoint.ae;

import java.sql.Connection;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletParamParser;
import com.aggrepoint.ae.data.ReqAttrConst;
import com.aggrepoint.ae.data.UrlConst;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApCPage;
import com.aggrepoint.su.core.data.ApChannel;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 弹出指定栏目中第一个信息
 * 
 * @author YJM
 */
public class NewsPop extends Winlet implements ReqAttrConst, UrlConst,
		RuleConst, IAdkConst {
	private static final long serialVersionUID = 1L;

	/** 信息栏目路径。若是从当前站点中查找信息栏目，则路径不以/开始；若是在全局站点中查找，则路径以/开始 */
	public static final String PARAM_CHANNEL_PATH = "cpath";

	/** 弹出窗口属性 */
	public static final String PARAM_WINDOW_PARAMS = "param";

	public static final String SESSION_KEY = NewsPop.class.getName()
			+ ".POPED.";

	/**
	 * 弹出信息
	 */
	public int show(IModuleRequest req, IModuleResponse resp) throws Exception {
		WinletParamParser param = new WinletParamParser(req);

		String path = param.getParam(PARAM_CHANNEL_PATH);
		if (path == null)
			return 1;

		if (req.getSessionAttribute(SESSION_KEY + path) != null)
			return 10;
		req.setSessionAttribute(SESSION_KEY + path, true);

		Connection conn = req.getDBConn();
		DbAdapter adapter = new DbAdapter(conn);
		ApCPage cpage = new ApCPage();

		// {获取当前页面，用于判断当前站点
		ApBPage page = new ApBPage();
		page.m_lPageID = req.getParameter(REQUEST_PARAM_WIN_PAGE_ID, -1l);
		if (page.m_lPageID == -1l)
			return 2;
		if (adapter.retrieve(page, "loadSite") == null)
			return 3;
		// }

		// {获取要显示的栏目
		ApChannel channel = new ApChannel();
		IPsnEngine psnEngine = req.getPsnEngine();
		channel.m_strPath = path;

		if (channel.m_strPath.startsWith("/")) {
			channel.m_lSiteID = 100;
			channel.m_strPath = channel.m_strPath.substring(1);
		} else {
			channel.m_lSiteID = page.m_lSiteID;
		}

		if (adapter.retrieve(channel, "loadByOwnerAndPath") == null)
			return 4;

		// {检查访问权限
		if (!psnEngine.eveluate(SU_ROOT)
				&& !psnEngine.eveluate(channel.m_strAccessRule))
			return 5;
		// }

		cpage.m_lChannelID = channel.m_lChannelID;
		// }

		cpage.m_iStatusID = ApCPage.STATUS_PUBLISHED;
		if (adapter.retrieve(cpage, "loadByStatusInChannels", "order") == null)
			return 6;

		req.setAttribute("URL", URL_VIEW_PAGE_CHAN + "/" + page.m_lPageID + "/");
		req.setAttribute("PARAMS", param.getParam(PARAM_WINDOW_PARAMS, ""));
		return 0;
	}
}
