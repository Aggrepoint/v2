package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApLayout;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 布局管理窗口
 * 
 * @author YJM
 */
public class LayoutMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	/** 用于列表的布局对象 */
	public ApLayout m_layout;

	/** 查看的布局 */
	public long m_lLayoutId;

	/** 布局编辑对象 */
	ApLayout m_layoutEdit;

	public LayoutMan() throws Exception {
		m_layout = new ApLayout();
	}

	public void reset() {
		super.reset();
		m_layout.setCommonData(ICommDataKey.SORT, "0");
		m_layout.setCommonData(ICommDataKey.PAGE, "1");
		m_lLayoutId = -1;
		m_layoutEdit = null;
	}

	/**
	 * 选择布局
	 */
	public int selectLayout(IModuleRequest req, long lid) throws Exception {
		if (lid == 0) {
			m_lLayoutId = -1;
			return 0;
		}

		ApLayout layout = new ApLayout();
		layout.m_lSiteID = getSiteId(req);
		layout.m_lLayoutID = lid;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (adapter.retrieve(layout, "loadInSite") == null)
			return 1002;

		m_lLayoutId = lid;
		m_layoutEdit = null;
		return 0;
	}

	ApLayout getSelectedLayout(DbAdapter adapter, long siteId) throws Exception {
		if (m_lLayoutId <= 0)
			return null;
		ApLayout layout = new ApLayout();
		layout.m_lLayoutID = m_lLayoutId;
		if (adapter.retrieve(layout) == null || layout.m_lSiteID != siteId) {
			m_lLayoutId = -1;
			return null;
		}
		return layout;
	}

	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lLayoutId <= 0)
			return 0;

		return 1;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 布局列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_layout.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_layout.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示布局列表页面
	 */
	public int showLayoutList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_layout.m_lSiteID = getSiteId(req);

		if ("yes".equalsIgnoreCase(req.getParam("isSelect", null))) {
			req.setAttribute("SELECTED", m_lLayoutId);
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_layout, "loadBySite", "order", 1000, 1,
							true));
		} else
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_layout, "loadBySite", "order",
							getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100,
							-1, true));
		return 0;
	}

	/**
	 * 新建布局
	 */
	public int addLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lLayoutId = -1;
		m_layoutEdit = new ApLayout();
		return 0;
	}

	/**
	 * 选择布局
	 */
	public int selectLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long lid = req.getParameter("lid", -1l);
		if (lid == -1)
			lid = req.getParameter("iid", -1l);

		selectLayout(req, lid);
		return 0;
	}

	/**
	 * 删除布局
	 */
	public int delLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApLayout layout = new ApLayout();
		layout.m_lLayoutID = req.getParameter("lid", -1l);
		if (layout.m_lLayoutID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// 删除布局及连带的数据
		adapter.delete(layout);

		if (m_lLayoutId == layout.m_lLayoutID)
			closeLayout(req, resp);

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 添加布局
	//
	//
	// /////////////////////////////////////////////////////////

	public int showLayoutAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_layoutEdit == null || m_layoutEdit.m_lLayoutID > 0)
			return 8000;

		// 编辑
		req.setAttribute("EDIT", m_layoutEdit);
		return 0;
	}

	/**
	 * 检验布局输入
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int validateLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_layoutEdit, "edit");
	}

	/**
	 * 保存布局编辑
	 */
	public int saveLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_layoutEdit).populate(m_layoutEdit, "edit"))
			return 1;

		m_layoutEdit.m_lSiteID = getSiteId(req);

		if (adapter.isNew(m_layoutEdit)) {
			m_layoutEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_layoutEdit);
		} else
			adapter.update(m_layoutEdit);

		m_layoutEdit = null;
		return 0;
	}

	/**
	 * 取消布局编辑
	 */
	public int cancelLayoutEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_layoutEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 布局详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showLayoutInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_layoutEdit != null || m_lLayoutId <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApLayout layout = getSelectedLayout(adapter, getSiteId(req));
		if (layout == null)
			return 8000;

		req.setAttribute("INFO", layout);

		return 0;
	}

	/**
	 * 编辑布局
	 */
	public int editLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lLayoutId == -1)
			return 1001;

		ApLayout layout = new ApLayout();
		layout.m_lLayoutID = m_lLayoutId;

		// 加载要编辑的布局
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_layoutEdit = adapter.retrieve(layout);

		if (m_layoutEdit == null)
			return 1002;

		return 0;
	}

	/**
	 * 关闭布局显示
	 */
	public int closeLayout(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lLayoutId = -1;
		m_layoutEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 布局编辑
	//
	//
	// /////////////////////////////////////////////////////////

	public int showLayoutEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_layoutEdit == null || m_layoutEdit.m_lLayoutID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_layoutEdit);
		return 0;
	}
}
