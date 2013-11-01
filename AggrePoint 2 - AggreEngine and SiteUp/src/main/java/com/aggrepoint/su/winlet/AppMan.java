package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApApp;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.ApWinParam;
import com.aggrepoint.su.core.data.ApWindow;
import com.aggrepoint.su.core.data.UUIDGen;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 应用管理
 * 
 * @author YJM
 */
public class AppMan extends WinletBase {
	private static final long serialVersionUID = 1L;

	public ResTree tree = new ResTree();
	public ResMan man = new ResMan();

	/** 0 - 所有应用 1 - 单个应用 2 - 单个窗口 */
	int m_iDisplayLevel;

	/** 用于显示应用列表的数据对象 */
	public ApApp m_app;

	public long m_lAppSelected;

	/** 用于编辑应用的数据对象 */
	public ApApp m_appEdit;

	/** 用于显示窗口列表的数据对象。AppId小于等于0表示没有选中的应用，显示应用列表而不是单个应用 */
	public ApWindow m_window;

	public long m_lWinSelected;

	/** 用于编辑窗口的数据对象 */
	public ApWindow m_winEdit;

	public ApWinParam m_param;
	public ApWinParam m_paramEdit;

	public AppMan() throws Exception {
		m_app = new ApApp();
		m_window = new ApWindow();
		m_param = new ApWinParam();
	}

	public void reset() {
		super.reset();
		m_iDisplayLevel = 0;
		m_app.setCommonData(ICommDataKey.SORT, "0");
		m_app.setCommonData(ICommDataKey.PAGE, "1");
		m_lAppSelected = -1;
		m_appEdit = null;
		m_window.setCommonData(ICommDataKey.SORT, "");
		m_window.setCommonData(ICommDataKey.PAGE, "1");
		m_lWinSelected = -1;
		m_winEdit = null;
		m_param.setCommonData(ICommDataKey.SORT, "0");
		m_param.setCommonData(ICommDataKey.PAGE, "1");
		m_paramEdit = null;
	}

	/**
	 * 
	 */
	public int selectMode(IModuleRequest req, int displayLevel, long id)
			throws Exception {
		if (displayLevel < 0 || displayLevel > 2)
			return 1000;

		reset();

		long siteId = getSiteId(req);
		m_lAppSelected = -1;
		m_lWinSelected = -1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		switch (displayLevel) {
		case 1:
			ApApp app = new ApApp();
			app.m_lSiteID = siteId;
			app.m_lAppID = id;

			if (adapter.retrieve(app, "loadInSite") == null)
				return 1001;

			m_lAppSelected = app.m_lAppID;
			m_window.m_lAppID = app.m_lAppID;

			tree.setRoot(app.m_iOfficialFlag, app.m_lResDirID);
			man.selectDir(req, app.m_lResDirID);
			break;
		case 2:
			ApWindow win = new ApWindow();
			win.m_lSiteID = siteId;
			win.m_lWindowID = id;

			if (adapter.retrieve(win, "loadInSite") == null)
				return 1002;

			m_lAppSelected = win.m_lAppID;
			m_lWinSelected = win.m_lWindowID;
			m_window.m_lAppID = win.m_lAppID;
			m_param.m_lWindowID = win.m_lWindowID;
			break;
		}

		m_iDisplayLevel = displayLevel;

		return 0;
	}

	public int getDisplayLevel(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return m_iDisplayLevel;
	}

	// //////////////////////////////////////////////////////////
	// 应用列表，仅在DisplayLevel 0出现
	// //////////////////////////////////////////////////////////

	public int showAppList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_iDisplayLevel > 0)
			return 8000;

		m_app.m_lSiteID = getSiteId(req);

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_app, "loadBySite", null));
		return 0;
	}

	public int searchApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_app.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_app.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	ApApp selectApp(IModuleRequest req, DbAdapter adapter) throws Exception {
		ApApp app = new ApApp();
		app.m_lAppID = req.getParameter("aid", -1l);
		if (adapter.retrieve(app) == null)
			return null;
		if (app.m_lSiteID != getSiteId(req))
			return null;
		return app;
	}

	public int selectApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_lAppSelected = -1;
		ApApp app = selectApp(req, new DbAdapter(req.getDBConn()));
		if (app == null)
			return 1001;
		selectMode(req, 1, app.m_lAppID);

		return 0;
	}

	public int deleteApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApApp app = selectApp(req, adapter);
		if (app == null)
			return 1001;
		adapter.proc(app, "delete");

		if (m_lAppSelected == app.m_lAppID) {
			m_lAppSelected = -1;
			m_window.m_lAppID = -1;
		}

		return 0;
	}

	public int newApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_appEdit = new ApApp();
		m_appEdit.m_lSiteID = getSiteId(req);
		m_lAppSelected = -1;
		m_lWinSelected = -1;
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 添加应用，仅在DisplayLevel 0出现
	// //////////////////////////////////////////////////////////
	public int showAppAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_iDisplayLevel > 0 || m_appEdit == null || m_appEdit.m_lAppID > 0)
			return 8000;

		req.setAttribute("EDIT", m_appEdit);
		return 0;
	}

	public int validateAppEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_appEdit, "edit");
	}

	public int saveApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_appEdit).populate(m_appEdit, "edit"))
			return 1;

		long siteId = getSiteId(req);

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_appEdit.m_lSiteID = siteId;
		
		// 修正
		if (adapter.isNew(m_appEdit)) {
			ApResDir dir = new ApResDir();
			dir.m_lSiteID = siteId;
			dir.m_strFullPath = "/sys/app/";
			adapter.retrieve(dir, "loadByPath");

			ApResDir resDir = ApResDir.createResDir(adapter, siteId, dir, "",
					true);

			m_appEdit.m_lResDirID = resDir.m_lResDirID;
			m_appEdit.m_strUUID = UUIDGen.get();
			adapter.create(m_appEdit);

			resDir.setDir(dir, Long.toString(m_appEdit.m_lAppID));
			adapter.update(resDir, "updateDir");
		} else
			adapter.update(m_appEdit);

		m_appEdit = null;
		return 0;
	}

	public int cancelAppEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_appEdit = null;

		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 显示应用信息，可以在Display Level 0、1、2出现
	// //////////////////////////////////////////////////////////
	ApApp getSelectedApp(DbAdapter adapter) throws Exception {
		if (m_lAppSelected <= 0)
			return null;

		ApApp app = new ApApp();
		app.m_lAppID = m_lAppSelected;
		if (adapter.retrieve(app) == null) {
			reset();
			return null;
		}

		return app;
	}

	public int showAppInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_appEdit != null || m_lAppSelected <= 0)
			return 8000;

		ApApp app = getSelectedApp(new DbAdapter(req.getDBConn()));
		if (app == null)
			return 1001;

		req.setAttribute("INFO", app);
		if (app.m_iStatusID == ApApp.STATUS_NORMAL)
			req.setAttribute("FLAGS", "stop");
		else
			req.setAttribute("FLAGS", "start");
		return 0;
	}

	public int editApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApApp app = getSelectedApp(new DbAdapter(req.getDBConn()));
		if (app == null)
			return 1001;

		m_appEdit = app;
		return 0;
	}

	public int changeAppStatus(IModuleRequest req, int status) throws Exception {
		ApApp app = getSelectedApp(new DbAdapter(req.getDBConn()));
		if (app == null)
			return 1001;

		app.m_iStatusID = status;
		new DbAdapter(req.getDBConn()).update(app, "updateStatus");
		return 0;
	}

	public int startApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return changeAppStatus(req, ApApp.STATUS_NORMAL);
	}

	public int stopApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return changeAppStatus(req, ApApp.STATUS_STOP);
	}

	public int closeApp(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		selectMode(req, 0, 0);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 应用编辑，仅在Display Level 0和1使用
	// //////////////////////////////////////////////////////////
	public int showAppdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_appEdit == null || m_appEdit.m_lAppID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_appEdit);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 窗口列表，仅在Display Level 0和1使用
	// //////////////////////////////////////////////////////////

	public int showWindowList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_iDisplayLevel > 1 || m_lAppSelected <= 0)
			return 8000;

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_window, "loadByApp", null));
		return 0;
	}

	public int searchWindow(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_window.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_window.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	ApWindow selectWindow(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		ApWindow win = new ApWindow();
		win.m_lWindowID = req.getParameter("wid", -1l);
		if (adapter.retrieve(win) == null)
			return null;
		if (win.m_lAppID != m_lAppSelected)
			return null;
		return win;
	}

	public int selectWindow(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApWindow win = selectWindow(req, new DbAdapter(req.getDBConn()));
		if (win == null)
			return 1002;
		selectMode(req, 2, win.m_lWindowID);
		return 0;
	}

	public int deleteWindow(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApWindow win = selectWindow(req, adapter);
		if (win == null)
			return 1002;
		adapter.delete(win);

		return 0;
	}

	public int addWindow(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_winEdit = new ApWindow();
		m_winEdit.m_lSiteID = getSiteId(req);
		m_winEdit.m_lAppID = m_lAppSelected;
		m_lWinSelected = -1;
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 添加窗口，仅在DisplayLevel 0和1出现
	// //////////////////////////////////////////////////////////
	public int showWinAdd(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_iDisplayLevel > 1 || m_winEdit == null
				|| m_winEdit.m_lWindowID > 0)
			return 8000;

		req.setAttribute("EDIT", m_winEdit);
		return 0;
	}

	public int validateWinEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_winEdit, "edit");
	}

	public int saveWin(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_winEdit).populate(m_winEdit, "edit"))
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_winEdit.m_lSiteID = getSiteId(req);
		m_winEdit.m_lAppID = m_lAppSelected;
		if (adapter.isNew(m_winEdit))
			m_winEdit.m_strUUID = UUIDGen.get();
		adapter.createOrUpdate(m_winEdit);

		m_winEdit = null;
		return 0;
	}

	public int cancelWinEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_winEdit = null;

		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 显示窗口信息，可以在Display Level 0、1、2出现
	// //////////////////////////////////////////////////////////
	ApWindow getSelectedWin(DbAdapter adapter) throws Exception {
		if (m_lWinSelected <= 0)
			return null;

		ApWindow win = new ApWindow();
		win.m_lWindowID = m_lWinSelected;
		if (adapter.retrieve(win) == null) {
			reset();
			return null;
		}

		return win;
	}

	public int showWinInfo(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_winEdit != null)
			return 8000;

		if (m_lWinSelected <= 0)
			return 8000;

		ApWindow win = getSelectedWin(new DbAdapter(req.getDBConn()));
		if (win == null)
			return 1002;

		req.setAttribute("INFO", win);
		return 0;
	}

	public int editWin(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApWindow win = getSelectedWin(new DbAdapter(req.getDBConn()));
		if (win == null)
			return 1002;

		m_winEdit = win;
		return 0;
	}

	public int closeWin(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		selectMode(req, 1, m_lAppSelected);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 窗口编辑
	// //////////////////////////////////////////////////////////
	public int showWinEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_winEdit == null || m_winEdit.m_lWindowID <= 0)
			return 8000;

		req.setAttribute("EDIT", m_winEdit);
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 参数列表
	// //////////////////////////////////////////////////////////

	public int showParamList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_lWinSelected <= 0)
			return 8000;

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_param, "loadByWindow", null));
		return 0;
	}

	ApWinParam selectParam(IModuleRequest req, DbAdapter adapter)
			throws Exception {
		ApWinParam param = new ApWinParam();
		param.m_lWinParamID = req.getParameter("pid", -1l);
		if (adapter.retrieve(param) == null)
			return null;
		if (param.m_lWindowID != m_lWinSelected)
			return null;
		return param;
	}

	public int editParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		ApWinParam param = selectParam(req, new DbAdapter(req.getDBConn()));
		if (param == null)
			return 1003;
		m_paramEdit = param;
		return 0;
	}

	public int deleteParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApWinParam param = selectParam(req, adapter);
		if (param == null)
			return 1003;
		adapter.delete(param);

		return 0;
	}

	public int addParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_paramEdit = new ApWinParam();
		m_paramEdit.m_lWindowID = m_lWinSelected;
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// 参数编辑
	// //////////////////////////////////////////////////////////
	public int showParamEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_paramEdit == null)
			return 8000;

		req.setAttribute("EDIT", m_paramEdit);
		return 0;
	}

	public int validateParamEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_paramEdit, "edit");
	}

	public int saveParam(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_paramEdit).populate(m_paramEdit, "edit"))
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_paramEdit.m_lWindowID = m_lWinSelected;
		adapter.createOrUpdate(m_paramEdit);

		m_paramEdit = null;
		return 0;
	}

	public int cancelParamEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_paramEdit = null;

		return 0;
	}
}
