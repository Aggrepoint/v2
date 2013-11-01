package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 删除栏目页面
 * 
 * @author YJM
 */
public class BPageDel extends BaseModule implements RuleConst, IParamConst {
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		// {获取参数
		long lPageID = req.getParameter(PARAM_PAGE_ID, -1l);

		if (lPageID == -1)
			return 1001;
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

		adapter.delete(page);
		page.m_lPageID = page.m_lParentID;
		adapter.proc(page, "updateChildCount");

		resp.setRetUrl(req.getParameter("urlback"));
		return 0;
	}
}
