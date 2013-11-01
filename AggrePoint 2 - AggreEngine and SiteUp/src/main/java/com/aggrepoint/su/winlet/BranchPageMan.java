package com.aggrepoint.su.winlet;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.http.HttpModuleRequest;
import com.aggrepoint.su.BPageEdit;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApBPageLayout;
import com.aggrepoint.su.core.data.ApBPagePsnName;
import com.aggrepoint.su.core.data.ApBPagePsnTmpl;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApSite;
import com.aggrepoint.su.core.data.ApTemplate;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 分支页面管理窗口
 * 
 * @author YJM
 */
public class BranchPageMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	/** 查看的分支 */
	long m_lBranchId;

	int m_iBranchPsnType;

	/** 用于列表的页面对象,其m_lParentID为当前查看页面 */
	ApBPage m_page;

	/** 查看的是否分支根页面 */
	boolean m_bIsRoot;

	/** 页面编辑 */
	ApBPage m_pageEdit;

	/** 页面添加 */
	ApBPage m_pageAdd;

	ApBPagePsnName m_psnNameEdit;

	ApBPagePsnTmpl m_psnTmplEdit;

	public boolean m_bSelectLayout;

	ContEdit m_contEdit;

	/** 发布结果信息 */
	public String m_strPubResults;

	public BranchPageMan() throws Exception {
		m_page = new ApBPage();
	}

	void resetData() {
		m_lBranchId = -1;
		m_iBranchPsnType = -1;
		m_pageEdit = null;
		m_pageAdd = null;
		m_page.m_lParentID = -1;
		m_bIsRoot = false;
		m_bSelectLayout = false;
		if (m_contEdit != null)
			m_contEdit.reset();
		m_strPubResults = null;
	}

	public void reset() {
		super.reset();
		resetData();
		m_page.setCommonData(ICommDataKey.SORT, "0");
		m_page.setCommonData(ICommDataKey.PAGE, "1");
	}

	/**
	 * 选择分支
	 */
	public int selectBranch(DbAdapter adapter, long bid, long siteId)
			throws Exception {
		resetData();

		ApBranch branch = new ApBranch();
		branch.m_lBranchID = bid;
		if (adapter.retrieve(branch) != null && branch.m_lSiteID == siteId) {
			m_lBranchId = bid;
			m_iBranchPsnType = branch.m_iPsnType;

			ApBPage page = new ApBPage();
			page.m_lBranchID = m_lBranchId;
			if (adapter.retrieve(page, "loadRoot") != null) {
				m_page.m_lParentID = page.m_lPageID;
				m_bIsRoot = true;
			}
		}
		return 0;
	}

	/**
	 * 选择页面
	 */
	public int selectPage(DbAdapter adapter, long pid, long siteId)
			throws Exception {
		resetData();

		ApBPage page = new ApBPage();
		page.m_lPageID = pid;
		if (adapter.retrieve(page) != null && page.m_lSiteID == siteId) {
			m_page.m_lParentID = pid;
			m_lBranchId = page.m_lBranchID;

			ApBranch branch = new ApBranch();
			branch.m_lBranchID = m_lBranchId;
			adapter.retrieve(branch);
			m_iBranchPsnType = branch.m_iPsnType;

			m_bIsRoot = false;
		}

		return 0;
	}

	/**
	 * @return 0 隐藏 1 显示子页面 2 显示所有
	 */
	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lBranchId <= 0)
			return 0;

		if (m_bIsRoot)
			return 1;

		return 2;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 页面详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showPageInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_pageEdit != null || m_page.m_lParentID <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApBPage page = new ApBPage();
		page.m_lPageID = m_page.m_lParentID;
		if (adapter.retrieve(page) == null)
			return 8000;

		req.setAttribute("INFO", page);
		if (m_strPubResults != null)
			req.setAttribute("MSG", m_strPubResults);

		if (m_iBranchPsnType == 0)
			req.setAttribute("FLAGS", "pub");

		return 0;
	}

	/**
	 * 编辑页面
	 */
	public int editPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_strPubResults = null;

		if (m_page.m_lParentID <= 0)
			return 1001;

		ApBPage page = new ApBPage();
		page.m_lPageID = m_page.m_lParentID;

		// 加载要编辑的页面
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_pageEdit = adapter.retrieve(page);

		if (m_pageEdit == null)
			return 1002;

		return 0;
	}

	/**
	 * 发布页面
	 */
	public int publish(IModuleRequest req, IModuleResponse resp,
			boolean includingInherited) throws Exception {
		if (m_page.m_lParentID <= 0)
			return 1001;

		m_strPubResults = null;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// {页面
		ApBranch branch = ApBranch.getSiteBranchWithGlobalAssocs(adapter,
				m_lBranchId, true, true);
		ApBPage page = branch.m_rootPage.findPage(m_page.m_lParentID);
		if (page == null)
			return 1002;

		ApSite site = new ApSite();
		site.m_lSiteID = getSiteId(req);
		if (adapter.retrieve(site) == null)
			return 1003;

		try {
			// 发布页面
			page.publish(adapter, ((HttpModuleRequest) req).getServerNamePort()
					+ "/ap2/site", site.m_strPublishBranchDir, null, false);

			int[] counts = new int[2];

			// 发布页面内容资源
			for (ApBPageContent c : page.m_vecContents) {
				if (c.isWindow())
					continue;

				if (!includingInherited && c.m_lPageID != page.m_lPageID)
					continue;

				ApContent cont = ApContent.getFromCache(adapter,
						c.m_lContentID, true);
				ApResDir dir = new ApResDir();
				dir.m_lResDirID = cont.m_lResDirID;
				if (adapter.retrieve(dir) == null)
					continue;
				dir.publish(adapter, site.m_strPublishResDir, counts);

				m_strPubResults = req.getMessage("success",
						Integer.toString(counts[1]),
						Integer.toString(counts[0]));
			}
		} catch (Exception e) {
			m_strPubResults = req.getMessage("fail", e.getMessage());
		}
		return 0;
	}

	public int pubFull(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return publish(req, resp, true);
	}

	public int pubPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return publish(req, resp, false);
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 页面编辑
	//
	//
	// /////////////////////////////////////////////////////////
	public int showPageEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBPage edit = "y".equals(req.getParam("isadd")) ? m_pageAdd
				: m_pageEdit;

		if (edit == null)
			return 8000;
		req.setAttribute("EDIT", edit);
		return 0;
	}

	public int validatePage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBPage edit = "y".equals(req.getParam("isadd")) ? m_pageAdd
				: m_pageEdit;

		return ajaxValidate(req, resp, edit, "edit");
	}

	public int cancelPageEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if ("y".equals(req.getParam("isadd")))
			m_pageAdd = null;
		else
			m_pageEdit = null;
		return 0;
	}

	public int savePage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBPage edit = "y".equals(req.getParam("isadd")) ? m_pageAdd
				: m_pageEdit;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// 重新加载，因为在Ajax校验过程中模板ID等已经变化，为了在savePage判断模板是否已经变化，需要重新加载
		if (!adapter.isNew(edit))
			adapter.retrieve(edit);
		switch (BPageEdit.savePage(req, resp, adapter, edit, null)) {
		case 1:
			return 1;
		case 1004:
			return 2;
		case 0:
			if ("y".equals(req.getParam("isadd")))
				m_pageAdd = null;
			else
				m_pageEdit = null;
			return 0;
		}

		return 3;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 子页面列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_page.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_page.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示子页面列表页面
	 */
	public int showPageList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lBranchId <= 0)
			return 8000;

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_page, "loadByParent", null,
						getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100, -1,
						true));

		return 0;
	}

	/**
	 * 新建子页面
	 */
	public int addPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_page.m_lParentID <= 0)
			return 1;

		m_pageAdd = new ApBPage();
		m_pageAdd.m_lSiteID = getSiteId(req);
		m_pageAdd.m_lParentID = m_page.m_lParentID;
		m_pageAdd.m_lBranchID = m_lBranchId;
		return 0;
	}

	/**
	 * 删除子页面
	 */
	public int delPage(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lBranchId <= 0)
			return 1;

		ApBPage page = new ApBPage();
		page.m_lPageID = req.getParameter("pid", -1l);
		if (page.m_lPageID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		if (adapter.retrieve(page) == null)
			return 1002;
		if (page.m_lBranchID != m_lBranchId)
			return 2;
		if (page.m_lParentID == page.m_lPageID || page.m_lParentID == 0)
			return 3;

		adapter.delete(page);
		page.m_lPageID = page.m_lParentID;
		adapter.proc(page, "updateChildCount");

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 内容列表
	//
	//
	// /////////////////////////////////////////////////////////

	void flatten(Vector<ApBPageContent> flat, Vector<ApBPageContent> conts) {
		flat.addAll(conts);
		for (ApBPageContent cont : conts)
			flatten(flat, cont.getChilds());
	}

	/**
	 * 显示内容列表
	 */
	public int showContList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_page.m_lParentID <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// {页面
		ApBranch branch = ApBranch.getSiteBranchWithGlobalAssocs(adapter,
				m_lBranchId, true, true);
		ApBPage page = branch.m_rootPage.findPage(m_page.m_lParentID);
		if (page == null)
			return 1002;

		req.setAttribute("PAGE", page);
		// }

		// {模板
		ApTemplate tmpl = ApTemplate.getTemplate(adapter, page.m_lTemplateID,
				true);
		if (tmpl == null)
			return 1;

		req.setAttribute("TMPL", tmpl);
		// }

		// { 区域
		Vector<String> areas = new Vector<String>();
		StringTokenizer st = new StringTokenizer(tmpl.getAreas(), ", ");
		while (st.hasMoreTokens())
			areas.add(st.nextToken());

		req.setAttribute("AREAS", areas);
		// }

		// { 布局及页面内容
		Hashtable<String, ApLayout> htLayouts = new Hashtable<String, ApLayout>();
		Hashtable<String, Vector<ApBPageContent>> htPageConts = new Hashtable<String, Vector<ApBPageContent>>();
		Vector<ApBPageContent> vecOther = new Vector<ApBPageContent>();
		vecOther.addAll(page.m_vecContents);
		for (String area : areas) {
			Vector<ApBPageContent> conts = new Vector<ApBPageContent>();

			for (ApBPageLayout layout : page.m_vecLayouts)
				if (layout.m_strAreaName.equals(area)) {
					htLayouts.put(area, ApLayout.getLayout(adapter,
							layout.m_lLayoutID, page.m_iOfficialFlag, true));
					break;
				}

			for (ApBPageContent c : page.m_vecContents)
				if (c.m_strAreaName.equals(area)) {
					vecOther.remove(c);
					conts.add(c);
				}

			htPageConts.put(area, conts);
		}

		htPageConts.put("", vecOther);

		req.setAttribute("LAYOUTS", htLayouts);
		req.setAttribute("PAGECONTS", htPageConts);
		// }

		Vector<ApBPageContent> all = new Vector<ApBPageContent>();
		flatten(all, page.m_vecContents);

		// { 内容，窗口，应用及窗框
		Hashtable<ApBPageContent, ApContent> htConts = new Hashtable<ApBPageContent, ApContent>();
		Hashtable<ApBPageContent, ApBPage> htContPages = new Hashtable<ApBPageContent, ApBPage>();
		Hashtable<ApBPageContent, ApWindow> htWindows = new Hashtable<ApBPageContent, ApWindow>();
		Hashtable<ApBPageContent, ApApp> htApps = new Hashtable<ApBPageContent, ApApp>();
		Hashtable<ApBPageContent, ApFrame> htFrames = new Hashtable<ApBPageContent, ApFrame>();

		for (ApBPageContent c : all) {
			if (c.isWindow()) {
				ApWindow win = ApWindow.getWindow(adapter, c.m_lWindowID, true);
				htWindows.put(c, win);
				htApps.put(c, ApApp.getApp(adapter, win.m_lAppID, true));
				htFrames.put(c, ApFrame.getFrame(adapter, c.m_lFrameID, true));
			} else
				htConts.put(c,
						ApContent.getFromCache(adapter, c.m_lContentID, true));

			htContPages.put(c, branch.m_rootPage.findPage(c.m_lPageID));
		}

		req.setAttribute("CONTS", htConts);
		req.setAttribute("CONTPAGES", htContPages);
		req.setAttribute("WINDOWS", htWindows);
		req.setAttribute("APPS", htApps);
		req.setAttribute("FRAMES", htFrames);
		// }
		/*
		 * if (m_bSelectLayout) req.setAttribute("LAYOUTS",
		 * ApLayout.getLayouts(adapter, 0, true));
		 */

		return 0;
	}

	public int editContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_contEdit = (ContEdit) req.getWinlet(ContEdit.class.getName());
		if (m_contEdit == null)
			return 3000;
		m_contEdit.editContent(req, req.getParameter("cid", -1l));
		return 0;
	}

	public int changeLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_bSelectLayout = !m_bSelectLayout;
		return 0;
	}

	public int selectLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBPageLayout bPageLayout = new ApBPageLayout();
		bPageLayout.m_lPageID = m_page.m_lParentID;
		bPageLayout.m_lBranchID = m_lBranchId;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		boolean bExists = adapter.retrieve(bPageLayout, "loadByPageAndArea") != null;

		bPageLayout.m_lLayoutID = req.getParameter("layoutid", -1l);
		if (bExists) {
			if (bPageLayout.m_lLayoutID <= 0)
				adapter.delete(bPageLayout);
			else
				adapter.update(bPageLayout);
		} else if (bPageLayout.m_lLayoutID > 0)
			adapter.create(bPageLayout);

		m_bSelectLayout = false;
		return 0;
	}
}
