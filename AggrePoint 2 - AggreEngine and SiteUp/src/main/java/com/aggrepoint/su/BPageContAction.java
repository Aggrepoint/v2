package com.aggrepoint.su;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 栏目页面内容操作：删除，移动，拖动
 * 
 * @author YJM
 */
public class BPageContAction extends BaseModule implements RuleConst,
		IParamConst {
	public static final int ACTION_DEL = 0;
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_DRAG = 2;

	public static final String PARAM_CONT_ID = "pcontid";

	public int commonProc(IModuleRequest req, IModuleResponse resp, int action)
			throws Exception {
		// {获取参数
		long lContID = req.getParameter(PARAM_CONT_ID, -1l);
		resp.setRetUrl(req.getParameter("urlback"));

		if (lContID == -1)
			return 1;
		// }

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// {加载内容
		ApBPageContent cont = new ApBPageContent();
		cont.m_lPageContID = lContID;
		if (adapter.retrieve(cont) == null)
			return 1;
		// }

		// {加载页面数据
		ApBPage page = new ApBPage();
		page.m_lPageID = cont.m_lPageID;

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

		switch (action) {
		case ACTION_DEL: // 删除内容引用
			adapter.retrieve(cont, "loadWithChilds");
			cont.delete(adapter);
			break;
		case ACTION_MOVE: // 移动内容
			int iStep = req.getParameter(PARAM_STEP, 0);
			if (iStep == 0)
				return 0;
			cont.move(adapter, iStep);
			break;
		case ACTION_DRAG:
			String areaName = req.getParameter(PARAM_AREA_NAME, "");
			long containerId = req.getParameter(PARAM_CONTAINER_ID, 0l);
			if (areaName.equals("") && containerId <= 0)
				return 0;
			cont.drag(adapter, areaName, req.getParameter(PARAM_ZONE_ID, 0),
					containerId, req.getParameter(PARAM_ON_CONT_ID, -1l),
					req.getParameter(PARAM_IS_BEFORE, 0) == 1);
			break;
		}

		return 0;
	}

	/**
	 * 删除
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_DEL);
	}

	/**
	 * 移动
	 */
	public int move(IModuleRequest req, IModuleResponse resp) throws Exception {
		return commonProc(req, resp, ACTION_MOVE);
	}

	/**
	 * 拖动
	 */
	public int drag(IModuleRequest req, IModuleResponse resp) throws Exception {
		return commonProc(req, resp, ACTION_DRAG);
	}
}
