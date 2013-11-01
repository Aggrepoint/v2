package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApFrame;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.RuleConst;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 窗框管理窗口
 * 
 * @author YJM
 */
public class FrameMan extends WinletBase implements RuleConst {
	private static final long serialVersionUID = 1L;

	public ResTree tree = new ResTree();
	public ResMan man = new ResMan();

	/** 用于列表的窗框对象 */
	public ApFrame m_frame;

	/** 查看的窗框 */
	public long m_lFrameId;

	/** 窗框编辑对象 */
	ApFrame m_frameEdit;

	public FrameMan() throws Exception {
		m_frame = new ApFrame();
	}

	public void reset() {
		super.reset();
		m_frame.setCommonData(ICommDataKey.SORT, "0");
		m_frame.setCommonData(ICommDataKey.PAGE, "1");
		m_lFrameId = -1;
		m_frameEdit = null;
	}

	/**
	 * 选择窗框
	 */
	public int selectFrame(IModuleRequest req, long fid) throws Exception {
		if (fid == 0) {
			m_lFrameId = -1;
			return 0;
		}

		ApFrame frame = new ApFrame();
		frame.m_lSiteID = getSiteId(req);
		frame.m_lFrameID = fid;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (adapter.retrieve(frame, "loadInSite") == null)
			return 1002;

		m_lFrameId = fid;
		tree.setRoot(frame.m_iOfficialFlag, frame.m_lDefResDirID);
		man.selectDir(req, frame.m_lDefResDirID);
		m_frameEdit = null;
		return 0;
	}

	ApFrame getSelectedFrame(DbAdapter adapter, long siteId) throws Exception {
		if (m_lFrameId <= 0)
			return null;
		ApFrame frame = new ApFrame();
		frame.m_lFrameID = m_lFrameId;
		if (adapter.retrieve(frame) == null || frame.m_lSiteID != siteId) {
			m_lFrameId = -1;
			return null;
		}
		return frame;
	}

	public int getDisplayType(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lFrameId <= 0)
			return 0;

		return 1;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 窗框列表
	//
	//
	// /////////////////////////////////////////////////////////

	public int searchFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_frame.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_frame.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	/**
	 * 显示窗框列表页面
	 */
	public int showFrameList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_frame.m_lSiteID = getSiteId(req);

		if ("yes".equalsIgnoreCase(req.getParam("isSelect", null))) {
			req.setAttribute("SELECTED", m_lFrameId);
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_frame, "loadBySite", "order", 1000, 1,
							true));
		} else
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_frame, "loadBySite", "order",
							getWinMode(req) == EnumWinMode.NORMAL ? 10 : 100,
							-1, true));
		return 0;
	}

	/**
	 * 新建窗框
	 */
	public int addFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lFrameId = -1;
		m_frameEdit = new ApFrame();
		return 0;
	}

	/**
	 * 选择窗框
	 */
	public int selectFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long fid = req.getParameter("fid", -1l);
		if (fid == -1)
			fid = req.getParameter("iid", -1l);

		selectFrame(req, fid);
		return 0;
	}

	/**
	 * 删除窗框
	 */
	public int delFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApFrame frame = new ApFrame();
		frame.m_lFrameID = req.getParameter("fid", -1l);
		if (frame.m_lFrameID == -1)
			return 1001;

		DbAdapter adapter = new DbAdapter(req.getDBConn());

		// 加载资源目录等属性以便删除
		if (adapter.retrieve(frame) == null)
			return 1002;

		// 删除窗框及连带的数据
		adapter.proc(frame, "delete");

		if (m_lFrameId == frame.m_lFrameID)
			closeFrame(req, resp);

		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 添加窗框
	//
	//
	// /////////////////////////////////////////////////////////

	public int showFrameAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_frameEdit == null || m_frameEdit.m_lFrameID > 0)
			return 8000;

		// 编辑
		req.setAttribute("EDIT", m_frameEdit);
		return 0;
	}

	/**
	 * 检验窗框输入
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public int validateFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_frameEdit, "edit");
	}

	/**
	 * 保存窗框编辑
	 */
	public int saveFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_frameEdit).populate(m_frameEdit, "edit"))
			return 1;

		long siteId = getSiteId(req);
		m_frameEdit.m_lSiteID = siteId;

		if (adapter.isNew(m_frameEdit)) {
			ApResDir dir = new ApResDir();
			dir.m_lSiteID = siteId;
			dir.m_strFullPath = "/sys/frame/";
			adapter.retrieve(dir, "loadByPath");

			ApResDir resDir = ApResDir.createResDir(adapter, siteId, dir, "",
					true);

			m_frameEdit.m_lDefResDirID = resDir.m_lResDirID;
			m_frameEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_frameEdit);

			resDir.setDir(dir, Long.toString(m_frameEdit.m_lFrameID));
			adapter.update(resDir, "updateDir");
		} else
			adapter.update(m_frameEdit);

		m_frameEdit = null;
		return 0;
	}

	/**
	 * 取消窗框编辑
	 */
	public int cancelFrameEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_frameEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 窗框详情
	//
	//
	// /////////////////////////////////////////////////////////

	public int showFrameInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_frameEdit != null || m_lFrameId <= 0)
			return 8000;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApFrame frame = getSelectedFrame(adapter, getSiteId(req));
		if (frame == null)
			return 8000;

		req.setAttribute("INFO", frame);

		return 0;
	}

	/**
	 * 编辑窗框
	 */
	public int editFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lFrameId == -1)
			return 1001;

		ApFrame frame = new ApFrame();
		frame.m_lFrameID = m_lFrameId;

		// 加载要编辑的窗框
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_frameEdit = adapter.retrieve(frame);

		if (m_frameEdit == null)
			return 1002;

		adapter.retrieve(m_frameEdit, "loadDetail");

		return 0;
	}

	/**
	 * 关闭窗框显示
	 */
	public int closeFrame(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lFrameId = -1;
		m_frameEdit = null;
		return 0;
	}

	// /////////////////////////////////////////////////////////
	//
	//
	// 窗框编辑
	//
	//
	// /////////////////////////////////////////////////////////

	public int showFrameEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_frameEdit == null || m_frameEdit.m_lFrameID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_frameEdit);
		return 0;
	}
}
