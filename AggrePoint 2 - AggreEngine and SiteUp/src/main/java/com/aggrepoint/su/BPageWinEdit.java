package com.aggrepoint.su;

import java.util.Enumeration;
import java.util.Vector;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApWinParam;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.CombineString;

/**
 * 编辑窗口
 * 
 * @author YJM
 */
public class BPageWinEdit extends BaseModule implements RuleConst {
	public static final int ACTION_EDIT = 0;
	public static final int ACTION_UPDATE = 1;

	public int commonProc(IModuleRequest req, IModuleResponse resp, int action)
			throws Exception {
		ApBPageContent cont = new ApBPageContent();
		cont.m_lPageContID = req.getParameter("pcontid", -1l);
		if (cont.m_lPageContID == -1)
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		if (adapter.retrieve(cont) == null)
			return 1;

		if (cont.m_lWindowID <= 0)
			return 1;

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

		ApWindow win = new ApWindow();
		win.m_lWindowID = cont.m_lWindowID;
		if (win.m_lWindowID == -1)
			return 1;
		if (adapter.retrieve(win, "loadWithParams") == null)
			return 1;

		if (win.m_lSiteID != 100 && win.m_lSiteID != page.m_lSiteID)
			return 8002;

		ApFrame frame = new ApFrame();

		switch (action) {
		case ACTION_EDIT:
			req.setAttribute("WINDOW", win);

			ApApp app = new ApApp();
			app.m_lAppID = win.m_lAppID;
			adapter.retrieve(app);
			req.setAttribute("APP", app);

			// {可选择的窗框
			frame.m_lSiteID = 100;
			Vector<ApFrame> vecFrames = adapter.retrieveMulti(frame,
					"loadBySite", "order");

			frame.m_lSiteID = page.m_lSiteID;
			vecFrames.addAll(adapter
					.retrieveMulti(frame, "loadBySite", "order"));

			req.setAttribute("FRAMES", vecFrames);
			// }

			req.setAttribute("CONTENT", cont);
			break;
		case ACTION_UPDATE:
			// {检查选定的窗框
			frame.m_lFrameID = req.getParameter("frameid", -1l);

			if (frame.m_lFrameID == -1)
				return 2;
			if (adapter.retrieve(frame) == null)
				return 2;
			if (frame.m_lSiteID != 100 && frame.m_lSiteID != page.m_lSiteID)
				return 8002;
			// }

			// {构造窗口参数
			String[] paramNames = req.getParameterValues("paramname");
			String[] paramValues = req.getParameterValues("paramvalue");
			Vector<String> vecStrs = new Vector<String>();
			if (paramNames != null)
				for (Enumeration<ApWinParam> enm = win.getParams().elements(); enm
						.hasMoreElements();) {
					ApWinParam param = enm.nextElement();
					boolean bFounded = false;

					vecStrs.add(param.m_strParamName);
					for (int i = 0; i < paramNames.length
							&& i < paramValues.length; i++)
						if (paramNames[i].equals(param.m_strParamName)) {
							vecStrs.add(paramValues[i]);
							bFounded = true;
						}

					if (!bFounded)
						vecStrs.add(param.m_strDefaultValue);
				}
			// }

			// 插入窗口
			cont.m_lFrameID = frame.m_lFrameID;
			cont.m_strContName = req.getParameter("title", "");
			cont.m_strAccessRule = req.getParameter("accessrule", "T");
			cont.m_bInheritable = req.getParameter("inherit", 0) == 1;
			cont.m_bPopWinFlag = req.getParameter("popwin", 0) == 1;
			cont.m_strWinParams = CombineString.combine(vecStrs, '~');
			adapter.update(cont);
			break;
		}

		return 0;
	}

	/**
	 * 选择应用供应商
	 */
	public int edit(IModuleRequest req, IModuleResponse resp) throws Exception {
		return commonProc(req, resp, ACTION_EDIT);
	}

	/**
	 * 插入窗口
	 */
	public int update(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_UPDATE);
	}
}
