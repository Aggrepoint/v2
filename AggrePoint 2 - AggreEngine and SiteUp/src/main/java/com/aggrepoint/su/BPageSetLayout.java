package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageLayout;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 选择栏目布局
 * 
 * @author YJM
 */
public class BPageSetLayout extends BaseModule implements RuleConst,
		IParamConst {
	public int defaultProc(IModuleRequest req, IModuleResponse resp,
			boolean isGo) throws Exception {
		// {获取参数
		long lPageID = req.getParameter(PARAM_PAGE_ID, -1l);

		if (lPageID == -1)
			return 1001;

		String areaName = req.getParameter(PARAM_AREA_NAME, "");
		if (areaName.equals(""))
			return 2101;
		// }

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

		ApBPageLayout bPageLayout = new ApBPageLayout();
		bPageLayout.m_lPageID = page.m_lPageID;
		bPageLayout.m_lBranchID = page.m_lBranchID;
		bPageLayout.m_strAreaName = areaName;
		boolean bExists = adapter.retrieve(bPageLayout, "loadByPageAndArea") != null;

		if (!isGo) { // 显示布局选择
			req.setAttribute("PAGEID", new Long(lPageID));
			req.setAttribute("AREANAME", areaName);
			req.setAttribute("LAYOUTS", ApLayout.getLayouts(adapter, 0, true));
			if (bExists)
				req.setAttribute("LAYOUTID", new Long(bPageLayout.m_lLayoutID));
		} else { // 保存布局选择
			bPageLayout.m_lLayoutID = req.getParameter(PARAM_LAYOUT_ID, -1l);

			if (bExists) {
				if (bPageLayout.m_lLayoutID <= 0)
					adapter.delete(bPageLayout);
				else
					adapter.update(bPageLayout);
			} else if (bPageLayout.m_lLayoutID > 0)
				adapter.create(bPageLayout);
		}

		return 0;
	}

	/**
	 * 显示选择页面
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return defaultProc(req, resp, false);
	}

	/**
	 * 保存选择结果
	 */
	public int go(IModuleRequest req, IModuleResponse resp) throws Exception {
		return defaultProc(req, resp, true);
	}
}
