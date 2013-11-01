package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApWindow;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 应用树
 * 
 * @author YJM
 */
public class AppTree extends WinletBase {
	private static final long serialVersionUID = 1L;

	/**
	 * 提供节点内容
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int getNodeContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long nid = req.getParameter("nid", -1);
		if (nid == -1)
			return 1;

		ApApp app = new ApApp();

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (nid == 0) { // 展开顶级目录，显示应用列表
			app.m_lSiteID = getSiteId(req);

			req.setAttribute("ITEMS",
					adapter.retrieveMulti(app, "loadBySite", "def"));
			return 0;
		}

		// 展开应用，显示窗口列表
		app.m_lAppID = nid;
		if (adapter.retrieve(app) == null)
			return 2;
		if (app.m_lSiteID != getSiteId(req))
			return 3;

		ApWindow win = new ApWindow();
		win.m_lAppID = nid;
		req.setAttribute("ITEMS",
				adapter.retrieveMulti(win, "loadByApp", ""));

		return 10;
	}

	/**
	 * 选择
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int selectNode(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long nid = req.getParameter("nid", -1l);
		if (nid == -1)
			return 1;

		AppMan appMan = (AppMan) req.getWinlet(AppMan.class.getName());
		if (nid == 0)
			return appMan.selectMode(req, 0, 0);
		else if (nid > 0)
			return appMan.selectMode(req, 1, nid);
		else
			return appMan.selectMode(req, 2, -nid);
	}
}
