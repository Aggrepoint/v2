package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApTemplate;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 模板树
 * 
 * @author YJM
 */
public class TmplTree extends WinletBase {
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
		int nid = req.getParameter("nid", -1);
		if (nid == -1)
			return 1;

		if (nid == 0)
			return 10;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApTemplate tmpl = new ApTemplate();
		tmpl.m_lSiteID = getSiteId(req);
		tmpl.m_iTmplType = nid - 1;
		req.setAttribute("ITEMS",
				adapter.retrieveMulti(tmpl, "loadByType", "order"));
		return 0;
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

		((TmplMan) req.getWinlet(TmplMan.class.getName())).selectTemplate(req,
				nid);
		return 0;
	}
}
