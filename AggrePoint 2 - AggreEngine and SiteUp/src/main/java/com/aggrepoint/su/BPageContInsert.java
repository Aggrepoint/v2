package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApContCat;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 插入内容
 * 
 * @author YJM
 */
public class BPageContInsert extends BaseModule implements RuleConst,
		IParamConst {
	public static final int ACTION_SEL_CAT = 0;
	public static final int ACTION_SEL_CONTENT = 1;
	public static final int ACTION_INSERT_CONTENT = 2;

	public static final String PARAM_CONT_ID = "contid";

	public int commonProc(IModuleRequest req, IModuleResponse resp, int action)
			throws Exception {
		long lPageID = req.getParameter(PARAM_PAGE_ID, -1l);
		if (lPageID == -1)
			return 1001;

		String strAreaName = req.getParameter(PARAM_AREA_NAME, "");
		int iZoneID = req.getParameter(PARAM_ZONE_ID, 0);
		long lContainerID = req.getParameter(PARAM_CONTAINER_ID, 0l);

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// {加载页面数据
		ApBPage page = new ApBPage();
		page.m_lPageID = lPageID;

		if (adapter.retrieve(page) == null)
			return 1003;
		// }

		// 加载页面所属Branch
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = page.m_lBranchID;
		if (adapter.retrieve(branch) == null)
			return 1005;
		// }

		// {检验管理权限
		IPsnEngine psnEngine = req.getPsnEngine();
		IUserProfile userProfile = req.getUserProfile();
		String strUserID = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		if (strUserID == null)
			strUserID = "";

		// 是否可以进行全面的管理（选择页面文档类型、设置各种规则）
		boolean bFullManage = psnEngine.eveluate(SU_ROOT)
				|| psnEngine.eveluate(branch.m_strManageRule);

		// 是否可以进行管理
		boolean bAllowEdit = false;
		bAllowEdit = bFullManage || page.ownerIs(strUserID);

		if (!bAllowEdit)
			return 8002;
		// }

		req.setAttribute("PAGEID", new Long(lPageID));
		req.setAttribute("AREANAME", strAreaName);
		req.setAttribute("ZONEID", new Integer(iZoneID));
		req.setAttribute("CONTAINER", new Long(lContainerID));

		ApContCat cat = new ApContCat();
		ApContent content = new ApContent();

		switch (action) {
		case ACTION_SEL_CAT:
			cat.m_lSiteID = 100;
			req.setAttribute("ROOT_CATS",
					adapter.retrieveMulti(cat, "loadBySite", null));
			if (cat.m_lSiteID != page.m_lSiteID) {
				cat.m_lSiteID = page.m_lSiteID;
				req.setAttribute("CATS",
						adapter.retrieveMulti(cat, "loadBySite", null));
			}
			break;
		case ACTION_SEL_CONTENT: // 选择要插入的内容
			cat.m_lContCatID = req.getParameter(PARAM_CAT_ID, -1l);
			if (cat.m_lContCatID == -1)
				return 1;
			if (adapter.retrieve(cat) == null)
				return 1;

			if (cat.m_lSiteID != 100 && cat.m_lSiteID != page.m_lSiteID)
				return 8002;

			content.m_lSiteID = cat.m_lSiteID;
			content.m_lContCatID = cat.m_lContCatID;
			req.setAttribute("CONTENTS",
					adapter.retrieveMulti(content, "loadByCat", "default"));

			break;
		case ACTION_INSERT_CONTENT:
			content.m_lContentID = req.getParameter(PARAM_CONT_ID, -1l);
			if (content.m_lContentID == -1)
				return 1;
			if (adapter.retrieve(content) == null)
				return 1;

			if (content.m_lSiteID != 100 && content.m_lSiteID != page.m_lSiteID)
				return 8002;

			// 插入内容
			ApBPageContent cont = new ApBPageContent();
			cont.m_lContentID = content.m_lContentID;
			cont.m_lPageID = page.m_lPageID;
			cont.m_lBranchID = page.m_lBranchID;
			cont.m_strContName = content.m_strName;
			cont.m_strAreaName = req.getParameter(PARAM_AREA_NAME, "");
			cont.m_iZoneID = req.getParameter(PARAM_ZONE_ID, 0);
			cont.m_lContainContID = req.getParameter(PARAM_CONTAINER_ID, 0);
			adapter.create(cont);

			break;
		}

		return 0;
	}

	/**
	 * 选择要插入的内容的分类
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_SEL_CAT);
	}

	/**
	 * 选择要插入的内容
	 */
	public int sel(IModuleRequest req, IModuleResponse resp) throws Exception {
		return commonProc(req, resp, ACTION_SEL_CONTENT);
	}

	/**
	 * 插入内容
	 */
	public int insert(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_INSERT_CONTENT);
	}
}
