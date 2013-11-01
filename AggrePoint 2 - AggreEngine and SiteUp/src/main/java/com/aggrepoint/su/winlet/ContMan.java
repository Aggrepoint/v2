package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApContCat;
import com.aggrepoint.su.core.data.ApContent;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 内容管理
 * 
 * @author YJM
 */
public class ContMan extends WinletBase {
	private static final long serialVersionUID = 1L;

	/** 用于显示内容分类列表的数据对象 */
	public ApContCat m_cat;

	public long m_lCatSelected;

	/** 用于编辑内容分类的数据对象 */
	public ApContCat m_catEdit;

	/** 用于显示内容列表的数据对象。CatId小于等于0表示没有选中的内容分类，显示内容分类列表而不是单个内容分类 */
	public ApContent m_content;

	public ContMan() throws Exception {
		m_cat = new ApContCat();
		m_content = new ApContent();
	}

	public void reset() {
		super.reset();
		m_cat.setCommonData(ICommDataKey.SORT, "0");
		m_cat.setCommonData(ICommDataKey.PAGE, "1");
		m_lCatSelected = -1;
		m_catEdit = null;
		m_content.setCommonData(ICommDataKey.SORT, "0");
		m_content.setCommonData(ICommDataKey.PAGE, "1");
	}

	/**
	 * 
	 */
	public int selectCat(IModuleRequest req, long id) throws Exception {
		reset();

		ContEdit edit = (ContEdit) req.getWinlet(ContEdit.class.getName());
		if (edit != null)
			edit.reset();

		long siteId = getSiteId(req);

		if (id > 0) {
			ApContCat cat = new ApContCat();
			cat.m_lSiteID = siteId;
			cat.m_lContCatID = id;

			if (new DbAdapter(req.getDBConn()).retrieve(cat, "loadInSite") == null)
				return 1001;

			m_lCatSelected = cat.m_lContCatID;
			m_content.m_lContCatID = cat.m_lContCatID;
		}

		return 0;
	}

	public int getDisplayLevel(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return m_lCatSelected <= 0 ? 0 : 1;
	}

	// //////////////////////////////////////////////////////////
	// 内容分类选择
	// //////////////////////////////////////////////////////////
	public int showCatSelect(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_cat.m_lSiteID = getSiteId(req);

		req.setAttribute("SELECTED", m_lCatSelected);
		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_cat, "loadBySite", null, 1000, 1, true));
		return 0;
	}

	public int selectCatMain(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long cid = req.getParameter("iid", -1l);
		if (cid == -1)
			return 1;

		return selectCat(req, cid);
	}

	// //////////////////////////////////////////////////////////
	// 内容分类列表
	// //////////////////////////////////////////////////////////

	public int showCatList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lCatSelected > 0)
			return 8000;

		m_cat.m_lSiteID = getSiteId(req);
		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_cat, "loadBySite", null));
		return 0;
	}

	public int searchCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_cat.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_cat.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	ApContCat getSelectedCat(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		ApContCat cat = new ApContCat();
		cat.m_lContCatID = req.getParameter("cid", -1l);
		if (adapter.retrieve(cat) == null)
			return null;
		if (cat.m_lSiteID != getSiteId(req))
			return null;
		return cat;
	}

	public int selectCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return selectCat(req, req.getParameter("cid", -1l));
	}

	public int deleteCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApContCat cat = getSelectedCat(req, adapter);
		if (cat == null)
			return 1001;
		cat.delete(adapter);

		if (m_lCatSelected == cat.m_lContCatID) {
			m_lCatSelected = -1;
			m_content.m_lContCatID = -1;
		}

		return 0;
	}

	public int newCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_catEdit = new ApContCat();
		m_catEdit.m_lSiteID = getSiteId(req);
		m_lCatSelected = -1;
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 添加内容分类，仅在DisplayLevel 0出现
	// //////////////////////////////////////////////////////////
	public int showCatAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lCatSelected > 0 || m_catEdit == null
				|| m_catEdit.m_lContCatID > 0)
			return 8000;

		req.setAttribute("EDIT", m_catEdit);
		return 0;
	}

	public int validateCatEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_catEdit, "edit");
	}

	public int saveCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_catEdit).populate(m_catEdit, "edit"))
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_catEdit.m_lSiteID = getSiteId(req);
		if (adapter.isNew(m_catEdit)) {
			m_catEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_catEdit);
		} else
			adapter.update(m_catEdit);

		m_catEdit = null;
		return 0;
	}

	public int cancelCatEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_catEdit = null;

		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 显示内容分类信息
	// //////////////////////////////////////////////////////////
	ApContCat getSelectedCat(DbAdapter adapter) throws Exception {
		if (m_lCatSelected <= 0)
			return null;

		ApContCat cat = new ApContCat();
		cat.m_lContCatID = m_lCatSelected;
		if (adapter.retrieve(cat) == null) {
			reset();
			return null;
		}

		return cat;
	}

	public int showCatInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_catEdit != null || m_lCatSelected <= 0)
			return 8000;

		ApContCat cat = getSelectedCat(new DbAdapter(req.getDBConn()));
		if (cat == null)
			return 1001;

		req.setAttribute("INFO", cat);
		return 0;
	}

	public int editCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApContCat cat = getSelectedCat(new DbAdapter(req.getDBConn()));
		if (cat == null)
			return 1001;

		m_catEdit = cat;
		return 0;
	}

	public int closeCat(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		selectCat(req, 0);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 内容分类编辑
	// //////////////////////////////////////////////////////////
	public int showCatEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_catEdit == null || m_catEdit.m_lContCatID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_catEdit);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 内容列表
	// //////////////////////////////////////////////////////////

	public int showContentList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lCatSelected <= 0)
			return 8000;

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_content, "loadByCat", null));
		return 0;
	}

	public int searchContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_content.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_content
				.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	ApContent getSelectedContent(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		ApContent cont = new ApContent();
		cont.m_lContentID = req.getParameter("cid", -1l);
		if (adapter.retrieve(cont) == null)
			return null;
		if (cont.m_lContCatID != m_lCatSelected)
			return null;
		return cont;
	}

	public int editContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ContEdit edit = (ContEdit) req.getWinlet(ContEdit.class.getName());
		if (edit == null)
			return 3000;
		edit.editContent(req, req.getParameter("cid", -1l));
		return 0;
	}

	public int deleteContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApContent cont = getSelectedContent(req, adapter);
		if (cont == null)
			return 1002;
		adapter.proc(cont, "delete");

		return 0;
	}

	public int addContent(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ContEdit edit = (ContEdit) req.getWinlet(ContEdit.class.getName());
		if (edit == null)
			return 3000;
		edit.addContent(req, m_lCatSelected);

		return 0;
	}
}
