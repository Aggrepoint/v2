package com.aggrepoint.su.winlet;

import java.io.File;
import java.util.Vector;

import com.aggrepoint.adk.EnumWinMode;
import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.ui.UIAdapter;
import com.aggrepoint.su.core.data.ApRes;
import com.aggrepoint.su.core.data.ApResDir;
import com.aggrepoint.su.core.data.UUIDGen;
import com.aggrepoint.su.data.ResUpload;
import com.icebean.core.adb.ICommDataKey;
import com.icebean.core.adb.db.DbAdapter;
import com.icebean.core.common.HTTPUtils;

/**
 * 资源管理
 * 
 * @author YJM
 */
public class ResMan extends WinletBase {
	private static final long serialVersionUID = 1L;

	/** 用于显示目录列表的数据对象 */
	public ApResDir m_dir;
	/** 用于显示资源列表的数据对象 */
	public ApRes m_res;

	/** 编辑的子目录 */
	public ApResDir m_dirEdit;
	/** 资源上传 */
	public boolean m_bUpload;
	/** 批量上传参数输入 */
	public ResUpload m_resUploadInput;
	/** 批量上传结果信息 */
	public Vector<String> m_vecMessage;

	public ResMan() throws Exception {
		m_dir = new ApResDir();
		m_dir.m_strFullPath = "/";
		m_res = new ApRes();
		reset();
	}

	public void reset() {
		super.reset();
		m_dir.setCommonData(ICommDataKey.SORT, "0");
		m_dir.setCommonData(ICommDataKey.PAGE, "1");
		m_res.setCommonData(ICommDataKey.SORT, "0");
		m_res.setCommonData(ICommDataKey.PAGE, "1");
		m_res.m_lResID = 0;
		m_resUploadInput = null;
		m_bUpload = false;
		m_vecMessage = null;
		m_dirEdit = null;
	}

	public boolean allowContent(String id) throws Exception {
		return m_dir.m_lResDirID != 0 && m_dir.m_bContFlag;
	}

	public boolean allowSubDir(String id) throws Exception {
		return m_dir.m_lResDirID == 0 || m_dir.m_bContFlag;
	}

	// //////////////////////////////////////////////////////////
	// Directory List
	// //////////////////////////////////////////////////////////

	public int selectDir(IModuleRequest req, long dirId) throws Exception {
		if (dirId == -1)
			return 1001;

		if (dirId != 0) {
			ApResDir dir = new ApResDir();
			dir.m_lSiteID = getSiteId(req);
			dir.m_lResDirID = dirId;

			DbAdapter adapter = new DbAdapter(req.getDBConn());
			if (adapter.retrieve(dir, "loadInSite") == null)
				return 1002;

			m_dir = dir;
			m_dir.m_lParentDirID = m_dir.m_lResDirID;
		} else {
			m_dir = new ApResDir();
			m_dir.m_strFullPath = "/";
		}

		m_res.m_lResDirID = dirId;
		m_res.m_lResID = 0;
		m_resUploadInput = null;
		m_bUpload = false;
		m_vecMessage = null;
		m_dirEdit = null;

		return 0;
	}

	public int showDirList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_dir.m_lSiteID = getSiteId(req);

		if (m_dir.m_lParentDirID == 0)
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_dir, "loadRoot", null,
							getWinMode(req) == EnumWinMode.NORMAL ? 20 : 100,
							-1, true));
		else
			req.setAttribute("LIST", new DbAdapter(req.getDBConn())
					.retrieveMultiDbl(m_dir, "loadByDir", null,
							getWinMode(req) == EnumWinMode.NORMAL ? 20 : 100,
							-1, true));
		return 0;
	}

	public int searchDir(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_dir.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_dir.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	public int selectDir(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return selectDir(req, req.getParameter("id", -1l));
	}

	public int deleteDir(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		String[] ids = req.getParameterValues("id");
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApResDir dir = new ApResDir();
		dir.m_lSiteID = getSiteId(req);
		for (String id : ids) {
			dir.m_lResDirID = Long.parseLong(id);
			if (adapter.retrieve(dir, "loadInSite") == null)
				continue;
			if (dir.m_bSysFlag)
				continue;
			if (dir.m_lParentDirID != m_dir.m_lResDirID)
				continue;
			adapter.delete(dir);
		}
		adapter.proc(m_dir, "updateChildFlag");

		return 0;
	}

	public int addDir(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_dirEdit = new ApResDir();
		m_dirEdit.m_lSiteID = getSiteId(req);
		m_dirEdit.m_lParentDirID = m_dir.m_lResDirID;
		m_dirEdit.m_iOfficialFlag = m_dir.m_iOfficialFlag;
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// Directory Edit
	// //////////////////////////////////////////////////////////
	public int showDirEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_dirEdit == null)
			return 8000;

		req.setAttribute("EDIT", m_dirEdit);
		return 0;
	}

	public int validateDirEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_dirEdit, "edit");
	}

	/**
	 * 目前只能用于新建目录，需加入编辑目录的处理
	 */
	public int saveDir(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		UIAdapter uiad = new UIAdapter(req);
		if (!uiad.clearErrors(m_dirEdit).populate(m_dirEdit, "edit"))
			return 1;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		m_dirEdit.m_iOfficialFlag = m_dir.m_iOfficialFlag;
		m_dirEdit.m_lSiteID = m_dir.m_lSiteID;
		m_dirEdit.m_strFullPath = m_dir.m_strFullPath + m_dirEdit.getName()
				+ "/";
		m_dirEdit.m_strUUID = UUIDGen.get();
		m_dirEdit.m_lParentDirID = m_dir.m_lResDirID;
		m_dirEdit.m_bContFlag = true;
		adapter.createOrUpdate(m_dirEdit);

		adapter.proc(m_dir, "updateChildFlag");

		m_dirEdit = null;
		return 0;
	}

	public int cancelDirEdit(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_dirEdit = null;

		return 0;
	}

	// //////////////////////////////////////////////////////////
	// Resource List
	// //////////////////////////////////////////////////////////

	public int showResList(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_res.m_lResDirID == 0)
			return 8000;

		req.setAttribute("LIST", new DbAdapter(req.getDBConn())
				.retrieveMultiDbl(m_res, "loadByDir", null,
						getWinMode(req) == EnumWinMode.NORMAL ? 20 : 100, -1,
						true));
		return 0;
	}

	public int searchRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_res.setCommonData(ICommDataKey.SORT, req.getParameter("order"));
		m_res.setCommonData(ICommDataKey.PAGE, req.getParameter("pno", "0"));
		return 0;
	}

	public int selectRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		long resId = req.getParameter("id", -1l);
		if (resId == -1)
			return 1001;

		ApRes res = new ApRes();
		res.m_lResDirID = m_dir.m_lResDirID;
		res.m_lResID = resId;

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (adapter.retrieve(res, "loadInSite") == null)
			return 1002;

		m_res.m_lResID = resId;
		return 0;
	}

	public int deleteRes(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		String[] ids = req.getParameterValues("id");
		DbAdapter adapter = new DbAdapter(req.getDBConn());
		ApRes res = new ApRes();
		res.m_lResDirID = m_res.m_lResDirID;
		for (String id : ids) {
			res.m_lResID = Long.parseLong(id);
			adapter.delete(res, "deleteInDir");
		}

		return 0;
	}

	public int startUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_dir.m_bContFlag) {
			m_resUploadInput = null;
			m_bUpload = true;
			m_vecMessage = null;
		}
		return 0;
	}

	public int startBatchUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_dir.m_bContFlag) {
			m_resUploadInput = new ResUpload();
			m_bUpload = false;
			m_vecMessage = null;
		}
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// Resource Upload
	// //////////////////////////////////////////////////////////

	public int showUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (!m_bUpload)
			return 8000;

		if (m_vecMessage != null)
			return 100;

		return 0;
	}

	public int doUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		boolean bOverwrite = req.getParameter("overwrite").equals("1");
		DbAdapter adapter = new DbAdapter(req.getDBConn());

		for (int i = 1; i <= 5; i++) {
			FileParameter fp = req.getFileParameter("file" + i);

			if (fp == null || fp.m_lSize <= 0)
				continue;

			ApRes res = new ApRes();
			res.m_iOfficialFlag = m_dir.m_iOfficialFlag;
			res.m_lResDirID = m_dir.m_lResDirID;
			res.m_strFileName = fp.m_strFileName;
			if (bOverwrite)
				adapter.delete(res, "deleteByDirAndName");
			else {
				if (adapter.retrieve(res, "loadByDirAndName") != null) {
					if (m_vecMessage == null)
						m_vecMessage = new Vector<String>();
					m_vecMessage
							.add(req.getMessage("exist", res.m_strFileName));
					continue;
				}
			}
			res.m_strContentType = fp.m_strContentType;
			res.m_strFile = fp.m_strFullPath;
			res.m_iOrder = 0;
			res.m_lSize = fp.m_lSize;

			res.createWithIcons(req.getTempDir(), adapter);
		}
		return 0;
	}

	// //////////////////////////////////////////////////////////
	// Resource Batch Upload
	// //////////////////////////////////////////////////////////

	public int showBatchUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (m_resUploadInput == null)
			return 8000;

		if (m_vecMessage != null)
			return 100;

		req.setAttribute("EDIT", m_resUploadInput);
		return 0;
	}

	public int validateBatchUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		return ajaxValidate(req, resp, m_resUploadInput, "edit");
	}

	void batchUpload(IModuleRequest req, DbAdapter adapter, ApResDir dir,
			File path) throws Exception {
		ApRes res = new ApRes();

		for (String name : path.list()) {
			File file = new File(path, name);

			if (file.isDirectory()) {
				ApResDir sub = new ApResDir();

				sub.m_iOfficialFlag = dir.m_iOfficialFlag;
				sub.m_lSiteID = dir.m_lSiteID;
				sub.setName(file.getName());
				sub.m_strFullPath = dir.m_strFullPath + sub.getName() + "/";
				sub.m_strUUID = UUIDGen.get();
				sub.m_lParentDirID = dir.m_lResDirID;
				sub.m_bContFlag = true;
				adapter.create(sub);

				if (!dir.m_bChildFlag) {
					dir.m_bChildFlag = true;
					adapter.update(dir, "updateAllChildFlag");
				}

				batchUpload(req, adapter, sub, file);
			} else {
				res.m_iOfficialFlag = dir.m_iOfficialFlag;
				res.m_lResDirID = dir.m_lResDirID;
				res.m_strFileName = file.getName();
				res.m_strContentType = HTTPUtils.getContentType(file.getName());
				res.m_strFile = file.getCanonicalPath();
				res.m_lSize = file.length();
				res.m_iOrder = 0;
				res.createWithIcons(req.getTempDir(), adapter);
			}
		}
	}

	public int doBatchUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		if (!new UIAdapter(req).clearErrors(m_resUploadInput).populate(
				m_resUploadInput, "edit"))
			return 1;

		if (!m_dir.m_bContFlag) {
			cancelUpload(req, resp);
			return 2;
		}

		DbAdapter adapter = new DbAdapter(req.getDBConn());
		if (m_resUploadInput.m_bCleanExisting) {
			adapter.proc(m_dir, "clearContent");
		}
		batchUpload(req, adapter, m_dir, new File(m_resUploadInput.m_strPath));
		adapter.proc(m_dir, "updateChildFlag");

		m_vecMessage = new Vector<String>();
		m_vecMessage.add("Upload completed.");

		return 0;
	}

	public int cancelUpload(IModuleRequest req, IModuleResponse resp)
			throws Exception {
		m_resUploadInput = null;
		m_bUpload = false;
		m_vecMessage = null;

		return 0;
	}
}
