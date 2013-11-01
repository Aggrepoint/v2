package com.aggrepoint.su.winlet;

import java.io.File;
import java.io.FilenameFilter;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.FileUtils;

/**
 * 站点管理窗口
 * 
 * @author YJM
 */
public class SiteMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	/** 用于列表的站点对象 */
	public ApSite m_site;

	/** 站点编辑对象 */
	ApSite m_siteEdit;

	/** 发布结果信息 */
	public String m_strPubResults;

	public SiteMan() throws Exception {
		m_site = new ApSite();
	}

	public void reset() {
		super.reset();
		m_site.setCommonData(ICommDataKey.SORT, "0");
		m_site.setCommonData(ICommDataKey.PAGE, "1");
		m_siteEdit = null;
	}

	/**
	 * 选择站点
	 */
	public int selectSite(IModuleRequest req, IModuleResponse resp, long sid)
			throws Exception {
		m_strPubResults = null;

		if (sid == 0) {
			sid = -1;
		} else {
			ApSite site = new ApSite();
			site.m_lSiteID = sid;
			DbAdapter adapter = new DbAdapter(req.getDBConn());
			if (adapter.retrieve(site) == null)
				return 1002;
		}

		WinletUserProfile userProfile = (WinletUserProfile) req
				.getUserProfile();
		if (sid == -1)
			userProfile.setProperty("site", null);
		else {
			userProfile.setProperty("site", Long.toString(sid));
			if (sid == 100)
				userProfile.setProperty("rootsite", "y");
			else
				userProfile.setProperty("rootsite", null);
		}
		setUserProfile(resp, userProfile);

		m_siteEdit = null;

		return 0;
	}

	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (getSiteId(req) <= 0)
			return 0;

		return 1;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 站点列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_site.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_site.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示站点列表页面
	 */
	public int showSiteList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if ("yes".equalsIgnoreCase(req.getParam("isSelect", null))) {
			req.setAttribute("SELECTED", getSiteId(req));
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_site, "", null, 1000, 1, true));
		} else
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_site, "", null,
							getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100,
							-1, true));
		return 0;
	}

	/**
	 * 新建站点
	 */
	public int addSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_siteEdit = new ApSite();
		return 0;
	}

	/**
	 * 选择站点
	 */
	public int selectSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long lid = req.getParameter("sid", -1l);
		if (lid == -1)
			lid = req.getParameter("iid", -1l);

		selectSite(req, resp, lid);
		return 0;
	}

	/**
	 * 删除站点
	 */
	public int delSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApSite site = new ApSite();
		site.m_lSiteID = req.getParameter("sid", -1l);
		if (site.m_lSiteID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (adapter.retrieve(site) == null)
			return 1002;

		// 删除站点及连带的数据
		adapter.proc(site, "delete");

		if (getSiteId(req) == site.m_lSiteID)
			selectSite(req, resp, -1);

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 添加站点
	//
	//
	// /////////////////////////////////////////////////////////

	public int showSiteAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_siteEdit == null || m_siteEdit.m_lSiteID > 0)
			return 8000;

		// 编辑
		req.setAttribute("EDIT", m_siteEdit);
		return 0;
	}

	/**
	 * 检验站点输入
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int validateSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_siteEdit, "edit");
	}

	/**
	 * 保存站点编辑
	 */
	public int saveSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_siteEdit).populate(m_siteEdit, "edit"))
			return 1;

		if (adapter.isNew(m_siteEdit)) {
			m_siteEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_siteEdit);
			m_siteEdit.initSysResDir(adapter);
		} else
			adapter.update(m_siteEdit);

		m_siteEdit = null;
		return 0;
	}

	/**
	 * 取消站点编辑
	 */
	public int cancelSiteEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_siteEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 站点详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showSiteInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long siteId = getSiteId(req);

		if (m_siteEdit != null || siteId <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApSite site = new ApSite();
		site.m_lSiteID = siteId;
		if (adapter.retrieve(site) == null)
			return 8000;

		req.setAttribute("INFO", site);

		StringBuffer sb = new StringBuffer();
		if (site.m_strPublishResDir != null)
			sb.append("pubres ");
		if (site.m_strPublishBranchDir != null)
			sb.append("pubbranch ");
		req.setAttribute("FLAGS", sb.toString());

		req.setAttribute("MSG", m_strPubResults);
		return 0;
	}

	/**
	 * 编辑站点
	 */
	public int editSite(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long siteId = getSiteId(req);

		if (siteId == -1)
			return 1001;

		ApSite site = new ApSite();
		site.m_lSiteID = siteId;

		// 加载要编辑的站点
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_siteEdit = adapter.retrieve(site);

		if (m_siteEdit == null)
			return 1002;

		m_strPubResults = null;

		return 0;
	}

	static final String AE_PATH = File.separator + "ae";

	static class AdkPublishFilter implements FilenameFilter {
		static final String[] SKIP = new String[] { ".jsp", ".jspx" };

		@Override
		public boolean accept(File dir, String name) {
			for (String str : SKIP)
				if (name.endsWith(str))
					return false;
			if (dir.getPath().endsWith(AE_PATH) && name.equals("tmpls"))
				return false;

			return true;
		}
	}

	/**
	 * 发布站点资源
	 */
	public int pubRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long siteId = getSiteId(req);

		if (siteId == -1)
			return 1001;

		m_strPubResults = null;

		try {
			int[] counts = new int[2];

			DbAdapter adapter = new DbAdapter(req.getDBConn());
			ApSite site = new ApSite();
			site.m_lSiteID = siteId;
			if (adapter.retrieve(site) == null)
				return 8000;

			ApResDir dir = new ApResDir();
			dir.m_lSiteID = siteId;
			for (ApResDir d : adapter.retrieveMulti(dir, "loadRoot", null))
				d.publish(adapter, site.m_strPublishResDir, counts);

			// 发布adk内容
			FileUtils.copyDir(new File(req.getContext().getRootDir() + "adk"),
					new File(site.m_strPublishResDir + File.separator
							+ "sys/adk"), new AdkPublishFilter(), counts);

			m_strPubResults = req.getMessage("success",
					Integer.toString(counts[1]), Integer.toString(counts[0]));

			// 发布ae内容
			FileUtils.copyDir(new File(req.getContext().getRootDir() + "ae"),
					new File(site.m_strPublishResDir + File.separator
							+ "sys/ae"), new AdkPublishFilter(), counts);

			m_strPubResults = req.getMessage("success",
					Integer.toString(counts[1]), Integer.toString(counts[0]));
		} catch (Exception e) {
			m_strPubResults = req.getMessage("fail", e.getMessage());
		}

		return 0;
	}

	/**
	 * 发布站点页面
	 */
	public int pubBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long siteId = getSiteId(req);

		if (siteId == -1)
			return 1001;

		m_strPubResults = null;

		try {
			int[] counts = new int[1];

			DbAdapter adapter = new DbAdapter(req.getDBConn());
			ApSite site = new ApSite();
			site.m_lSiteID = siteId;
			if (adapter.retrieve(site) == null)
				return 8000;

			ApBranch branch = new ApBranch();
			branch.m_lSiteID = siteId;
			for (ApBranch b : adapter.retrieveMulti(branch, "loadAllBySite",
					null)) {
				if (b.m_iPsnType != ApBranch.PSN_TYPE_STATIC)
					continue;

				ApBPage page = new ApBPage();
				page.m_lBranchID = b.m_lBranchID;
				page.m_iOfficialFlag = b.m_iOfficialFlag;
				if (adapter.retrieve(page, "loadRoot") == null)
					continue;
				page.publish(adapter,
						((HttpModuleRequest) req).getServerNamePort()
								+ "/ap2/site", site.m_strPublishBranchDir,
						counts, true);
			}

			m_strPubResults = req.getMessage("success",
					Integer.toString(counts[0]));
		} catch (Exception e) {
			m_strPubResults = req.getMessage("fail", e.getMessage());
		}

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 站点编辑
	//
	//
	// /////////////////////////////////////////////////////////
	public int showSiteEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_siteEdit == null || m_siteEdit.m_lSiteID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_siteEdit);
		return 0;
	}
}
