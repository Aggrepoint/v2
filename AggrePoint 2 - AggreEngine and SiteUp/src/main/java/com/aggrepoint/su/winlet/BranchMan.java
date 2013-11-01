package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApBPage;
import com.aggrepoint.su.core.data.ApBranch;
import com.aggrepoint.su.core.data.ApBranchGroup;
import com.aggrepoint.su.core.data.ApPathMap;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 分支管理窗口
 * 
 * @author YJM
 */
public class BranchMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	/** 查看的分支。小于0表示隐藏，0表示列表所有分支 */
	public long m_lBranchId;

	/** 用于列表的分支对象 */
	public ApBranch m_branch;

	/** 分支编辑对象 */
	ApBranch m_branchTypeSel;
	ApBranch m_branchEdit;

	/** 分支组编辑 */
	ApBranchGroup m_branchGroupEdit;

	ApPathMap m_mapEdit;

	public BranchMan() throws Exception {
		m_branch = new ApBranch();
	}

	void resetData() {
		m_lBranchId = 0;
		m_branchTypeSel = null;
		m_branchEdit = null;
		m_branchGroupEdit = null;
		m_mapEdit = null;
	}

	public void reset() {
		super.reset();
		resetData();
		m_branch.setCommonData(ICommDataKey.SORT, "0");
		m_branch.setCommonData(ICommDataKey.PAGE, "1");
	}

	/**
	 * 选择分支
	 */
	public int selectBranch(DbAdapter adapter, long bid, long siteId)
			throws Exception {
		resetData();

		if (bid <= 0) {
			m_lBranchId = bid;
			return 0;
		}

		ApBranch branch = new ApBranch();
		branch.m_lBranchID = bid;
		if (adapter.retrieve(branch) != null && branch.m_lSiteID == siteId)
			m_lBranchId = bid;
		return 0;
	}

	ApBranch getSelectedBranch(DbAdapter adapter, long siteId) throws Exception {
		if (m_lBranchId <= 0)
			return null;
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = m_lBranchId;
		if (adapter.retrieve(branch) == null || branch.m_lSiteID != siteId) {
			reset();
			return null;
		}
		return branch;
	}

	/**
	 * @return 0 分支列表 1 选中非组合分支 2 选中组合分支 3 不显示（页面管理）
	 */
	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lBranchId < 0)
			return 3;

		if (m_lBranchId == 0 && m_branchTypeSel == null && m_branchEdit == null)
			return 0;

		ApBranch branch = getSelectedBranch(new DbAdapter(req.getDBConn()),
				getSiteId(req));
		if (branch == null)
			return 0;
		return branch.m_iPsnType == 2 ? 2 : 1;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 分支列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branch.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_branch.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示分支列表页面
	 */
	public int showBranchList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branch.m_lSiteID = getSiteId(req);

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_branch, "loadAllBySite", null,
						getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100, -1,
						true));
		return 0;
	}

	/**
	 * 新建分支
	 */
	public int addBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lBranchId = 0;
		m_branchTypeSel = new ApBranch();
		m_branchEdit = null;
		m_branchGroupEdit = null;
		return 0;
	}

	/**
	 * 选择分支
	 */
	public int selectBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		selectBranch(new DbAdapter(req.getDBConn()),
				req.getParameter("bid", -1l), getSiteId(req));
		return 0;
	}

	/**
	 * 删除分支
	 */
	public int delBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBranch branch = new ApBranch();
		branch.m_lBranchID = req.getParameter("bid", -1l);
		if (branch.m_lBranchID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// 删除分支及连带的数据
		adapter.delete(branch);

		if (m_lBranchId == branch.m_lBranchID)
			closeBranch(req, resp);

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 添加分支
	//
	//
	// /////////////////////////////////////////////////////////

	public int showBranchAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_branchTypeSel == null
				&& (m_branchEdit == null || m_branchEdit.m_lBranchID > 0))
			return 8000;

		// 新建类型选择
		if (m_branchTypeSel != null) {
			req.setAttribute("EDIT", m_branchTypeSel);
			return 10;
		}

		// 编辑
		req.setAttribute("EDIT", m_branchEdit);
		if (m_branchEdit.m_iPsnType == 2)
			return 30;
		return 20;
	}

	/**
	 * 选择新建分支类型
	 */
	public int selBranchType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_branchTypeSel).populate(m_branchTypeSel,
				"typesel"))
			return 1;

		m_branchEdit = new ApBranch();
		m_branchEdit.m_iPsnType = m_branchTypeSel.m_iPsnType;
		m_branchEdit.m_iMarkupType = m_branchTypeSel.m_iMarkupType;
		m_branchEdit.m_lSiteID = getSiteId(req);
		m_branchEdit.m_rootPage = new ApBPage();
		m_branchTypeSel = null;
		return 0;
	}

	/**
	 * 检验分支输入
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int validateBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_branchEdit, "edit");
	}

	public int validateBranchGroup(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_branchEdit, "groupedit");
	}

	/**
	 * 保存分支编辑
	 */
	public int saveBranch(IModuleRequest req, IModuleResponse resp, String gp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_branchEdit).populate(m_branchEdit, gp))
			return 1;

		m_branchEdit.m_lSiteID = getSiteId(req);

		if (m_branchEdit.m_iPsnType != 2) {
			m_branchEdit.m_rootPage.m_lSiteID = m_branchEdit.m_lSiteID;
			m_branchEdit.m_rootPage.m_iOfficialFlag = m_branchEdit.m_iOfficialFlag;
			m_branchEdit.m_rootPage.m_strAccessRule = m_branchEdit.m_strAccessRule;
			m_branchEdit.m_rootPage.m_strPathName = m_branchEdit.m_strRootPath;
			m_branchEdit.m_rootPage.m_strFullPath = "/"
					+ m_branchEdit.m_rootPage.m_strPathName + "/";
			m_branchEdit.m_rootPage.m_strDirectPath = "/";
		}

		if (adapter.isNew(m_branchEdit)) {
			m_branchEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_branchEdit);

			if (m_branchEdit.m_iPsnType != 2) { // 非组合分支
				m_branchEdit.m_rootPage.m_strUUID = UUIDGen.get();
				m_branchEdit.m_rootPage.m_lBranchID = m_branchEdit.m_lBranchID;

				// 创建根页面
				m_branchEdit.m_rootPage.m_iPageType = ApBPage.TYPE_PAGE;
				adapter.create(m_branchEdit.m_rootPage);
			}
		} else {
			adapter.update(m_branchEdit);

			if (m_branchEdit.m_iPsnType != 2) { // 非组合分支
				adapter.update(m_branchEdit.m_rootPage);

				m_branchEdit.m_rootPage.cascadeTemplateAndPath(adapter, true,
						true, false);
			}
		}

		m_branchTypeSel = null;
		m_branchEdit = null;
		return 0;
	}

	public int saveBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return saveBranch(req, resp, "edit");
	}

	public int saveBranchGroup(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return saveBranch(req, resp, "groupedit");
	}

	/**
	 * 取消分支编辑
	 */
	public int cancelBranchEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branchTypeSel = null;
		m_branchEdit = null;
		m_branchGroupEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 分支详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showBranchInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_branchEdit != null || m_branchTypeSel != null || m_lBranchId <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApBranch branch = getSelectedBranch(adapter, getSiteId(req));
		if (branch == null)
			return 8000;

		req.setAttribute("INFO", branch);
		if (branch.m_iPsnType != 2) {
			branch.m_rootPage = new ApBPage();
			branch.m_rootPage.m_lBranchID = branch.m_lBranchID;
			branch.m_rootPage.m_iOfficialFlag = branch.m_iOfficialFlag;
			adapter.retrieve(branch.m_rootPage, "loadRoot");
			return 10;
		}

		return 20;
	}

	/**
	 * 编辑分支
	 */
	public int editBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		// 加载要编辑的分支
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_branchEdit = getSelectedBranch(adapter, getSiteId(req));

		// 清空其他编辑对象
		m_branchTypeSel = null;
		m_mapEdit = null;

		if (m_branchEdit == null)
			return 1002;

		if (m_branchEdit.m_iPsnType != 2) { // 加载根页面
			m_branchEdit.m_rootPage = new ApBPage();
			m_branchEdit.m_rootPage.m_lBranchID = m_branchEdit.m_lBranchID;
			m_branchEdit.m_rootPage.m_iOfficialFlag = m_branchEdit.m_iOfficialFlag;
			if (adapter.retrieve(m_branchEdit.m_rootPage, "loadRoot") == null) {
				m_branchEdit = null;
				return 1;
			}
		}

		return 0;
	}

	/**
	 * 关闭分支显示
	 */
	public int closeBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lBranchId = 0;
		m_branchTypeSel = null;
		m_branchEdit = null;
		m_branchGroupEdit = null;
		m_mapEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 分支编辑
	//
	//
	// /////////////////////////////////////////////////////////

	public int showBranchEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_branchTypeSel != null || m_branchEdit == null
				|| m_branchEdit.m_lBranchID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_branchEdit);
		if (m_branchEdit.m_iPsnType == 2)
			return 20;
		return 10;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 组合分支列表
	//
	//
	// /////////////////////////////////////////////////////////

	/**
	 * 显示组合分支列表
	 */
	public int showGroupBranchList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApBranch branch = getSelectedBranch(adapter, getSiteId(req));
		if (branch == null)
			return 8000;

		if (branch.m_iPsnType != 2)
			return 8000;

		ApBranchGroup group = new ApBranchGroup();
		group.m_lGroupBranchID = branch.m_lBranchID;
		group.m_iOfficialFlag = branch.m_iOfficialFlag;
		req.setAttribute("LIST",
				adapter.retrieveMultiDbl(group, "loadByBranch", "default"));
		return 0;
	}

	public int newGroupBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branchGroupEdit = new ApBranchGroup();
		m_branchGroupEdit.m_lSiteID = getSiteId(req);
		m_branchGroupEdit.m_lGroupBranchID = m_lBranchId;
		return 0;
	}

	public int editGroupBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branchGroupEdit = null;
		ApBranchGroup group = new ApBranchGroup();
		group.m_lGroupID = req.getParameter("gid", -1);
		if (group.m_lGroupID == -1)
			return 1011;
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_branchGroupEdit = adapter.retrieve(group);
		if (m_branchGroupEdit == null)
			return 1012;

		return 0;
	}

	public int delGroupBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApBranchGroup group = new ApBranchGroup();
		group.m_lGroupID = req.getParameter("gid", -1);
		if (group.m_lGroupID == -1)
			return 1011;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		adapter.delete(group);
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 组合分支编辑
	//
	//
	// /////////////////////////////////////////////////////////
	public int showGroupBranchEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_branchGroupEdit == null)
			return 8000;
		req.setAttribute("EDIT", m_branchGroupEdit);
		return 0;
	}

	public int validateGroupBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_branchGroupEdit, "edit");
	}

	/**
	 * 取消组合分支编辑
	 */
	public int cancelGroupBranchEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_branchGroupEdit = null;
		return 0;
	}

	/**
	 * 保存分支编辑
	 */
	public int saveGroupBranch(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_branchGroupEdit).populate(m_branchGroupEdit,
				"edit"))
			return 1;

		ApBranch branch = new ApBranch();
		branch.m_lBranchID = m_branchGroupEdit.m_lBranchID;
		adapter.retrieve(branch);
		m_branchGroupEdit.m_iMarkupType = branch.m_iMarkupType;
		adapter.createOrUpdate(m_branchGroupEdit);
		m_branchGroupEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 路径影射列表
	//
	//
	// /////////////////////////////////////////////////////////

	/**
	 * 显示路径影射列表
	 */
	public int showPathMapList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApBranch branch = getSelectedBranch(adapter, getSiteId(req));
		if (branch == null)
			return 8000;

		ApPathMap map = new ApPathMap();
		map.m_lBranchID = branch.m_lBranchID;
		map.m_iOfficialFlag = branch.m_iOfficialFlag;
		req.setAttribute("LIST",
				adapter.retrieveMultiDbl(map, "loadByBranch", "order"));
		return 0;
	}

	public int newPathMap(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_mapEdit = new ApPathMap();
		m_mapEdit.m_lBranchID = m_lBranchId;
		return 0;
	}

	public int editPathMap(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_mapEdit = null;
		ApPathMap map = new ApPathMap();
		map.m_lMapID = req.getParameter("mid", -1);
		if (map.m_lMapID == -1)
			return 3001;
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_mapEdit = adapter.retrieve(map);
		if (m_mapEdit == null)
			return 3002;

		return 0;
	}

	public int delPathMap(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApPathMap map = new ApPathMap();
		map.m_lMapID = req.getParameter("mid", -1);
		if (map.m_lMapID == -1)
			return 3001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		adapter.delete(map);
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 路径影射编辑
	//
	//
	// /////////////////////////////////////////////////////////
	public int showPathMapEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_mapEdit == null)
			return 8000;
		req.setAttribute("EDIT", m_mapEdit);
		return 0;
	}

	public int validatePathMap(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_mapEdit, "edit");
	}

	public int cancelPathMapEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_mapEdit = null;
		return 0;
	}

	public int savePathMap(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_mapEdit).populate(m_mapEdit, "edit"))
			return 1;

		if (adapter.isNew(m_mapEdit))
			m_mapEdit.m_strUUID = UUIDGen.get();
		adapter.createOrUpdate(m_mapEdit);
		m_mapEdit = null;
		return 0;
	}
}
