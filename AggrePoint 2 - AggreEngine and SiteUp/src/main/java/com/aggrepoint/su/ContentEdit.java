package com.aggrepoint.su;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.adk.BaseModule;
import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApContCat;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.StringUtils;

/**
 * 内容编辑
 * 
 * @author YJM
 */
public class ContentEdit extends BaseModule implements RuleConst, IParamConst {
	static final int ACTION_EDIT = 0;
	static final int ACTION_SAVE = 1;
	static final int ACTION_LIST_RES = 2;
	static final int ACTION_ADD_RES = 3;

	static Pattern P_STYLE_SHEET = Pattern
			.compile("<link[^>]+href=\"<ae:res\\s+name\\s*=\\s*\"\\s*(def.css)\\s*\"\\s*/\\s*>\\s*\"");

	public static void setStyles(IModuleRequest req, DbAdapter adapter,
			ApBPage page) throws Exception {
		// { 计算要使用的样式文件
		ApRes res = new ApRes();
		StringBuffer sbStyles = new StringBuffer();
		boolean bFirst = true;

		// 由页面内容引入的样式
		for (ApBPageContent c : ApBranch.getSiteBranchWithGlobalAssocs(adapter,
				page.m_lBranchID, true, true).m_rootPage
				.findPage(page.m_lPageID).m_vecContents) {
			res.m_lResDirID = c.m_lPageContID;
			for (ApRes r : adapter.retrieveMulti(res,
					"loadCssResByPageContent", null)) {
				if (bFirst)
					bFirst = false;
				else
					sbStyles.append(",");
				sbStyles.append("/ap2/res/cont/" + r.m_lResDirID + "/"
						+ r.m_strFileName);
			}
		}

		// 由页面模板引入的样式
		ApTemplate template = new ApTemplate();
		template.m_lTemplateID = page.m_lTemplateID;
		adapter.retrieve(template, "loadDetail");

		synchronized (P_STYLE_SHEET) {
			Matcher m = P_STYLE_SHEET.matcher(template.m_strContent);
			if (m.find()) {
				if (bFirst)
					bFirst = false;
				else
					sbStyles.append(",");
				sbStyles.append("/ap2/res/tmpl/" + template.m_lTemplateID + "/"
						+ m.group(1));
			}
		}

		req.setAttribute("STYLES", sbStyles.toString());
		// }
	}

	int commonProc(IModuleRequest req, IModuleResponse resp, int action)
			throws Exception {
		// {获取参数
		long lContentID = req.getParameter("contid", -1l);
		long lPageID = req.getParameter(BPageWinInsert.PARAM_PAGE_ID, -1l);
		String strAreaName = req.getParameter(BPageWinInsert.PARAM_AREA_NAME,
				"");
		int iZoneID = req.getParameter(BPageWinInsert.PARAM_ZONE_ID, 0);
		long lContainer = req.getParameter(BPageWinInsert.PARAM_CONTAINER_ID,
				0l);

		if (lPageID == -1l && lContentID == -1l) // 必须提供页面ID或者内容ID
			return 2001;
		// }

		req.setAttribute("AREANAME", strAreaName);
		req.setAttribute("ZONEID", new Integer(iZoneID));
		req.setAttribute("CONTAINER", new Long(lContainer));

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		ApContent content = new ApContent();

		if (lContentID != -1) {
			content.m_lContentID = lContentID;
			if (adapter.retrieve(content) == null)
				return 2002;

			lPageID = content.m_lPageID;
		}

		// { 加载页面并检查权限
		ApBPage page = new ApBPage();
		page.m_lPageID = lPageID;

		if (adapter.retrieve(page) == null)
			return 1003;

		// 加载页面所属Branch
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = page.m_lBranchID;
		if (adapter.retrieve(branch) == null)
			return 1005;

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

		req.setAttribute("PAGE", page);
		// }

		if (adapter.isNew(content)) // 创建内容及资源目录
			content = ApContent.newContent(adapter, page.m_lSiteID, 0,
					page.m_lPageID);
		else if (content.m_lContCatID > 0) { // 编辑栏目级内容，检查权限
			// {加载分类
			ApContCat cat = new ApContCat();
			cat.m_lContCatID = content.m_lContCatID;
			if (adapter.retrieve(cat) == null)
				return 2003;
			// }

			// {检验管理权限
			boolean isRoot = req.getPsnEngine().eveluate(SU_ROOT);

			if (cat.m_lSiteID == 100) {
				if (!isRoot)
					return 8002;
			} else {
				ApSite site = new ApSite();
				site.m_lSiteID = cat.m_lSiteID;
				if (adapter.retrieve(site) == null)
					return 2004;
				if (!isRoot
						&& !req.getPsnEngine().eveluate(site.m_strManageRule))
					return 8002;

				req.setAttribute("SITE", site);
			}
			// }
		}

		// 统一可视化编辑和管理编辑输入的资源URL
		content.m_strContent = content.m_strContent.replaceAll(
				"(\\.\\./)+res/cont/" + content.m_lContentID + "/",
				"/ap2/res/cont/" + content.m_lContentID + "/");
		req.setAttribute("CONTENT", content);

		ApBPageContent cont = new ApBPageContent();
		cont.m_lContentID = content.m_lContentID;
		cont.m_lPageID = page.m_lPageID;
		adapter.retrieve(cont, "loadByPageAndContent");
		req.setAttribute("PAGECONT", cont);

		setStyles(req, adapter, page);

		int rtcode = 0;
		ApRes res;

		switch (action) {
		case ACTION_SAVE:
			content.setContent(req.getParameter("content", ""));
			content.m_strName = StringUtils.fixDBString(
					req.getParameter("name", ""), 100, 3);
			content.m_strDesc = StringUtils.fixDBString(
					req.getParameter("desc", ""), 10000, 3);
			content.m_strAccessRule = StringUtils.fixDBString(
					req.getParameter("accessrule", ""), 10000, 3);

			adapter.update(content);

			cont.m_bInheritable = req.getParameter("inherit", 0) == 1;
			cont.m_strAreaName = strAreaName;
			cont.m_iZoneID = iZoneID;
			cont.m_lContainContID = lContainer;
			cont.m_lBranchID = branch.m_lBranchID;
			cont.m_lContPageID = content.m_lPageID;
			if (adapter.isNew(cont))
				adapter.create(cont);
			else
				adapter.update(cont, "updateByPageAndContent");

			break;
		case ACTION_LIST_RES:
			res = new ApRes();
			res.m_lResDirID = content.m_lResDirID;

			req.setAttribute("RESES",
					adapter.retrieveMultiDbl(res, "loadByDir", ""));
			break;
		case ACTION_ADD_RES:
			res = new ApRes();
			res.m_lResDirID = content.m_lResDirID;

			FileParameter fp = req.getFileParameter("res");

			res.m_strFileName = fp.m_strFileName;
			res.m_strContentType = fp.m_strContentType;
			res.m_strFile = fp.m_strFullPath;
			res.m_iOrder = 0;
			res.m_lSize = fp.m_lSize;
			res.createWithIcons(req.getTempDir(),
					new DbAdapter(req.getDBConn()));

			req.setAttribute("RESES",
					adapter.retrieveMultiDbl(res, "loadByDir", ""));
			break;
		default:
			break;
		}

		return rtcode;
	}

	/**
	 * 显示编辑页面
	 */
	public int execute(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_EDIT);
	}

	/**
	 * 保存编辑结果
	 */
	public int save(IModuleRequest req, IModuleResponse resp) throws Exception {
		return commonProc(req, resp, ACTION_SAVE);
	}

	/**
	 * 显示资源列表页面
	 */
	public int listRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_LIST_RES);
	}

	/**
	 * 上传资源
	 */
	public int addRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return commonProc(req, resp, ACTION_ADD_RES);
	}
}
