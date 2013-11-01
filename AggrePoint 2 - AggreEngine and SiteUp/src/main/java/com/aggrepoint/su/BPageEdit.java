package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 显示栏目页面编辑
 * 
 * @author YJM
 */
public class BPageEdit extends BaseModule implements RuleConst, IParamConst {
	public static int savePage(IModuleRequest req, IModuleResponse resp,
			DbAdapter adapter, ApBPage page, ApBPage parentPage)
			throws Exception {
		// 保存
		if (parentPage == null) {
			parentPage = new ApBPage();
			parentPage.m_lPageID = page.m_lParentID;
			if (adapter.retrieve(parentPage) == null)
				return 1004;
		}

		// 保存
		long lTemplateID = page.m_lTemplateID;
		String strPathName = page.m_strPathName;
		boolean m_bUseMap = page.m_bInheritUseMap || page.m_bUseMap;

		UIAdapter uiad = new UIAdapter(req);
		uiad.populate(page, "edit_check");
		if (!uiad.populate(page, "edit"))
			return 1;

		page.m_strFullPath = parentPage.m_strFullPath + page.m_strPathName
				+ "/";
		page.m_strDirectPath = parentPage.m_strDirectPath + page.m_strPathName
				+ "/";
		page.m_iPageType = ApBPage.TYPE_PAGE;

		if (page.m_lTemplateID <= 0) { // 继承上级页面的模板
			page.m_bInheritTmpl = true;
			page.m_lTemplateID = parentPage.m_lTemplateID;
		} else
			page.m_bInheritTmpl = false;

		if (page.m_lPageID <= 0) {
			page.m_iOrder = 10000;
			page.m_strUUID = UUIDGen.get();
			page.m_bInheritUseMap = parentPage.m_bUseMap
					|| parentPage.m_bInheritUseMap;
			adapter.create(page);
			page.m_lPageID = page.m_lParentID;
			adapter.proc(page, "updateChildCount");
		} else {
			adapter.update(page);

			page.cascadeTemplateAndPath(adapter,
					lTemplateID != page.m_lTemplateID,
					!strPathName.equals(page.m_strPathName),
					m_bUseMap != (page.m_bUseMap || page.m_bInheritUseMap));
		}

		return 0;
	}

	int defaultProc(IModuleRequest req, IModuleResponse resp, boolean isSave)
			throws Exception {
		// {获取参数
		long lPageID = req.getParameter(PARAM_PAGE_ID, -1l);
		long lParentPageID = req.getParameter(PARAM_PARENT_PAGE_ID, -1l);

		if (lPageID == -1 && lParentPageID == -1)
			return 1001;
		// }

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// {加载页面或父页面数据
		ApBPage page = new ApBPage();
		page.m_lPageID = lPageID;
		ApBPage parentPage = null;

		if (lPageID == -1) {
			parentPage = new ApBPage();
			parentPage.m_lPageID = lParentPageID;
			if (adapter.retrieve(parentPage) == null)
				return 1004;

			page.m_lParentID = parentPage.m_lPageID;
			page.m_lBranchID = parentPage.m_lBranchID;
			page.m_lSiteID = parentPage.m_lSiteID;
		} else {
			if (adapter.retrieve(page) == null)
				return 1003;
			if (page.m_bInheritTmpl)
				page.m_lTemplateID = 0;
		}
		// }

		// 加载页面所属Branch
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = parentPage == null ? page.m_lBranchID
				: parentPage.m_lBranchID;
		if (adapter.retrieve(branch) == null)
			return 1005;
		// }

		page.m_iMarkupType = branch.m_iMarkupType;

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
		if (parentPage == null) { // 编辑页面
			bAllowEdit = bFullManage || page.ownerIs(strUserID);
		} else { // 增加页面
			bAllowEdit = bFullManage
					|| psnEngine.eveluate(parentPage.m_strPvtPsnRule);
		}

		if (!bAllowEdit)
			return 8002;
		// }

		req.setAttribute("EDIT", page);

		if (!isSave) { // 编辑
			if (parentPage != null) { // 新增
				// 设置缺省模板
				page.m_lTemplateID = -1;
				page.m_iPageType = ApBPage.TYPE_PAGE;
			}

			return 0;
		}

		return savePage(req, resp, adapter, page, parentPage);
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
