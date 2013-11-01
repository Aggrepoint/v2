package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 页面个性化名称编辑
 * 
 * @author YJM
 */
public class BPagePsnName extends BaseModule implements RuleConst, IParamConst {
	int defaultProc(IModuleRequest req, IModuleResponse resp, boolean isSave)
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

		// 只能在动态分支中设置个性化名称
		if (branch.m_iPsnType != ApBranch.PSN_TYPE_DYNAMIC)
			return 8002;

		// {检验管理权限
		IPsnEngine psnEngine = req.getPsnEngine();
		IUserProfile userProfile = req.getUserProfile();
		String strUserID = userProfile.getProperty(IUserProfile.PROPERTY_ID);
		if (strUserID == null)
			strUserID = "";

		if (!psnEngine.eveluate(SU_ROOT)
				&& !psnEngine.eveluate(branch.m_strManageRule))
			return 8002;
		// }

		if (!isSave) {
			adapter.retrieve(page, "loadPsnNames");
			req.setAttribute("PAGE", page);
			return 0;
		}

		// 删除原个性化名称
		ApBPagePsnName psnName = new ApBPagePsnName();
		psnName.m_lPageID = page.m_lPageID;
		adapter.delete(psnName, "delByPageAndOwner");

		// 保存新个性化名称
		psnName.m_lBranchID = page.m_lBranchID;
		String[] psnNames = req.getParameterValues("name");
		String[] rules = req.getParameterValues("rule");
		if (psnNames != null && rules != null)
			for (int i = 0; i < psnNames.length && i < rules.length; i++) {
				psnName.m_strPageName = psnNames[i];
				psnName.m_strAccessRule = rules[i];
				adapter.create(psnName);
			}

		return 0;
	}

	/**
	 * 显示编辑页面
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return defaultProc(req, resp, false);
	}

	/**
	 * 保存编辑结果
	 */
	public int save(IModuleRequest req, IModuleResponse resp) throws Exception {
		return defaultProc(req, resp, true);
	}
}
