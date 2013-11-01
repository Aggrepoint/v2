package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.su.core.data.ApResDir;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 资源树
 * 
 * @author YJM
 */
public class ResTree extends WinletBase {
	private static final long serialVersionUID = 1L;

	int m_iOfficial;
	long m_lRoot;

	public void setRoot(int official, long dirId) throws Exception {
		m_lRoot = dirId;
		m_iOfficial = official;
	}

	public int root(IModuleRequest req, IModuleResponse resp) throws Exception {
		ApResDir root = new ApResDir();
		if (m_lRoot == 0) {
			root.m_strFullPath = "/";
			root.m_bChildFlag = true;
		} else {
			root.m_lSiteID = getSiteId(req);
			root.m_iOfficialFlag = m_iOfficial;
			root.m_lResDirID = m_lRoot;
			if (new DbAdapter(req.getDBConn()).retrieve(root) == null) {
				root.m_strFullPath = "";
				root.m_bChildFlag = false;
			}
		}

		req.setAttribute("ROOT", root);
		return 0;
	}

	/**
	 * 提供树内容
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int treeContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApResDir dir = new ApResDir();
		dir.m_lParentDirID = req.getParameter("nid", -1l);
		if (dir.m_lParentDirID == -1)
			return 1;
		dir.m_lSiteID = getSiteId(req);
		dir.m_iOfficialFlag = m_iOfficial;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (dir.m_lParentDirID == 0)
			req.setAttribute("ITEMS",
					adapter.retrieveMulti(dir, "loadRoot", "def"));
		else
			req.setAttribute("ITEMS",
					adapter.retrieveMulti(dir, "loadByDir", "def"));

		return 0;
	}

	/**
	 * 选择目录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int selectNode(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ResMan resMan = (ResMan) req.getWinlet(ResMan.class.getName());
		if (resMan != null)
			resMan.selectDir(req, req.getParameter("nid", -1l));

		return 0;
	}
}
