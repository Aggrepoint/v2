package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 分支树
 * 
 * @author YJM
 */
public class BranchTree extends WinletBase {
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

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		if (nid == 0) {
			ApBranch branch = new ApBranch();
			branch.m_lSiteID = getSiteId(req);

			req.setAttribute("ITEMS",
					adapter.retrieveMulti(branch, "loadAllBySite", null));
			return 0;
		}

		ApBPage page = new ApBPage();
		page.m_lSiteID = getSiteId(req);

		if (nid > 0) { // 打开分支
			page.m_lBranchID = nid;
			if (adapter.retrieve(page, "loadRoot") == null)
				return 2;
			page.m_lParentID = page.m_lPageID;
		}
		if (nid < 0) // 展开页面
			page.m_lParentID = -nid;

		req.setAttribute("ITEMS",
				adapter.retrieveMulti(page, "loadByParent", null));

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

		BranchMan branchMan = (BranchMan) req.getWinlet(BranchMan.class
				.getName());
		BranchPageMan branchPageMan = (BranchPageMan) req
				.getWinlet(BranchPageMan.class.getName());
		if (branchMan == null || branchPageMan == null)
			return 2;

		long siteId = getSiteId(req);
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (nid >= 0) {
			branchMan.selectBranch(adapter, nid, siteId);
			branchPageMan.selectBranch(adapter, nid, siteId);
		} else {
			branchMan.selectBranch(adapter, -1, siteId);
			branchPageMan.selectPage(adapter, -nid, siteId);
		}

		return 0;
	}
}
