package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.ContentEdit;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBPageContent;
import com.aggrepoint.su.core.data.ApContent;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 内容编辑
 * 
 * @author YJM
 */
public class ContEdit extends WinletBase {
	private static final long serialVersionUID = 1L;

	public ResTree tree = new ResTree();
	public ResMan man = new ResMan();

	/** 用于编辑内容的数据对象 */
	public ApContent m_contEdit;
	/** */
	public ApBPageContent m_pageContEdit;

	public ContEdit() throws Exception {
	}

	public void reset() {
		super.reset();
		m_contEdit = null;
		m_pageContEdit = null;
	}

	/**
	 * 在分类中添加内容
	 */
	public void addContent(IModuleRequest req, long catId) throws Exception {
		// 创建实例，但暂不与分类挂钩
		m_contEdit = ApContent.newContent(new DbAdapter(req.getDBConn()),
				getSiteId(req), 0, 0);

		m_contEdit.m_lContCatID = catId;

		tree.setRoot(0, m_contEdit.m_lResDirID);
		man.selectDir(req, m_contEdit.m_lResDirID);
	}

	/**
	 * 添加页面内容
	 */
	public boolean addContent(IModuleRequest req, long pageId, String areaName,
			long container, int zone) throws Exception {
		m_contEdit = null;
		m_pageContEdit = null;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		long siteId = getSiteId(req);

		ApBPage page = new ApBPage();
		page.m_lPageID = pageId;
		if (adapter.retrieve(page) == null || page.m_lSiteID != siteId)
			return false;

		if (container > 0) {
			ApBPageContent cont = new ApBPageContent();
			cont.m_lPageContID = container;
			if (adapter.retrieve(cont) == null
					|| cont.m_lBranchID != page.m_lBranchID)
				return false;
		}

		// 创建实例，但暂不与页面挂钩
		m_contEdit = ApContent.newContent(new DbAdapter(req.getDBConn()),
				siteId, 0, 0);
		m_contEdit.m_lPageID = pageId;
		tree.setRoot(0, m_contEdit.m_lResDirID);
		man.selectDir(req, m_contEdit.m_lResDirID);

		// 创建页面内容引用
		m_pageContEdit = new ApBPageContent();
		m_pageContEdit.m_strAreaName = areaName;
		m_pageContEdit.m_iZoneID = zone;
		m_pageContEdit.m_lContainContID = container;
		m_pageContEdit.m_lContentID = m_contEdit.m_lContentID;

		return true;
	}

	/**
	 * 编辑内容
	 */
	public boolean editContent(IModuleRequest req, long contId)
			throws Exception {
		m_contEdit = null;
		m_pageContEdit = null;

		if (contId <= 0)
			return false;

		ApContent cont = new ApContent();
		cont.m_lContentID = contId;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (adapter.retrieve(cont) == null || cont.m_lSiteID != getSiteId(req))
			return false;

		if (cont.m_lPageID > 0) { // 页面内容
			ApBPageContent pageCont = new ApBPageContent();
			pageCont.m_lContentID = cont.m_lContentID;
			if (adapter.retrieve(pageCont, "loadByContent") == null)
				return false;

			m_pageContEdit = pageCont;
		}

		// 统一可视化编辑和管理编辑输入的资源URL
		cont.m_strContent = cont.m_strContent.replaceAll("(\\.\\./)+res/cont/"
				+ cont.m_lContentID + "/", "/ap2/res/cont/" + cont.m_lContentID
				+ "/");

		m_contEdit = cont;
		tree.setRoot(0, m_contEdit.m_lResDirID);
		man.selectDir(req, m_contEdit.m_lResDirID);

		return true;
	}

	public int showRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_contEdit == null)
			return 8000;
		return 0;
	}

	public int showContentEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_contEdit == null)
			return 8000;

		if (m_pageContEdit != null) {
			DbAdapter adapter = new DbAdapter(req.getDBConn());

			ApBPage page = new ApBPage();
			page.m_lPageID = m_pageContEdit.m_lPageID;
			adapter.retrieve(page);

			ContentEdit.setStyles(req, adapter, page);
		}
		return 0;
	}

	public int validateContentEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_contEdit,
				m_pageContEdit == null ? "edit" : "edit_page");
	}

	public int saveContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_contEdit).populate(m_contEdit,
				m_pageContEdit == null ? "edit" : "edit_page"))
			return 1;

		if (m_pageContEdit != null)
			uiad.populate(m_pageContEdit, "edit");

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		adapter.update(m_contEdit);
		if (m_pageContEdit != null)
			adapter.createOrUpdate(m_pageContEdit);

		m_contEdit = null;
		return 0;
	}

	public int closeContentEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_contEdit = null;
		m_pageContEdit = null;

		return 0;
	}
}
