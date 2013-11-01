package com.aggrepoint.su;

import java.util.Vector;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPagePsnTmpl;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 页面个性化模版编辑
 * 
 * @author YJM
 */
public class BPagePsnTmpl extends BaseModule implements RuleConst, IParamConst {
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

		// { 查找可用的模板
		ApTemplate template = new ApTemplate();

		Vector<ApTemplate> vecTemplates = new Vector<ApTemplate>();
		Vector<ApTemplate> vec;

		// 全局
		template.m_iTmplType = ApTemplate.TYPE_BPAGE;
		template.m_lSiteID = 100;
		vec = adapter.retrieveMulti(template, "loadByType", "order");
		vecTemplates.addAll(vec);
		// 站点
		if (branch.m_lSiteID != 100) {
			template.m_lSiteID = branch.m_lSiteID;
			vec = adapter.retrieveMulti(template, "loadByType", "order");
			vecTemplates.addAll(vec);
		}

		if (vecTemplates.size() == 0)
			return 1006;
		// }

		if (!isSave) {
			adapter.retrieve(page, "loadPsnTmpls");
			req.setAttribute("PAGE", page);

			req.setAttribute("TEMPLATES", vecTemplates);
			return 0;
		}

		// 删除原个性化模版
		ApBPagePsnTmpl psnTmpl = new ApBPagePsnTmpl();
		psnTmpl.m_lPageID = page.m_lPageID;
		adapter.delete(psnTmpl, "delByPageAndOwner");

		// 保存新个性化模版
		psnTmpl.m_lBranchID = page.m_lBranchID;
		String[] tmplids = req.getParameterValues("tmplid");
		String[] rules = req.getParameterValues("rule");
		if (tmplids != null && rules != null)
			for (int i = 0; i < tmplids.length && i < rules.length; i++) {
				psnTmpl.m_lTemplateID = Long.parseLong(tmplids[i]);
				psnTmpl.m_strAccessRule = rules[i];
				if (psnTmpl.m_strAccessRule == null
						|| psnTmpl.m_strAccessRule.equals(""))
					continue;
				for (ApTemplate tmpl : vecTemplates) {
					if (psnTmpl.m_lTemplateID == tmpl.m_lTemplateID) {
						adapter.create(psnTmpl);
						break;
					}
				}
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
