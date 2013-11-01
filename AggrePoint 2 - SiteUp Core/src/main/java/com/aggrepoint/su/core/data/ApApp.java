package com.aggrepoint.su.core.data;

import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.adk.FileParameter;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbAdapter;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApApp extends ADB {
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_TEST = 1;
	public static final int STATUS_STOP = -1;

	public long m_lAppID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lSiteID;
	public long m_lResDirID;
	public String m_strAppDesc;
	public long m_lLogoImageID;
	public String m_strAppName;
	public String m_strAppVer;
	public int m_iStatusID;
	public String m_strHostURL;
	public String m_strRootPath;
	public int m_iConnTimeout;
	public int m_iReadTimeout;
	public Vector<ApWindow> m_vecWindows;
	public String m_strUUID;

	public FileParameter m_previewImage;

	/** 以下属性用于从XML文件中导入 */
	public String m_strLogoContentType;
	public String m_strLogoName;
	public String m_strLogoFile;
	public ApResDir m_dir;

	/** AAApp Cache */
	static Hashtable<String, ApApp> m_htApps = new Hashtable<String, ApApp>();

	public ApApp() throws AdbException {
		m_strAppName = m_strAppDesc = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strHostURL = m_strUUID = "";
		m_strAppVer = "1.0";
		m_iStatusID = 0;
		m_iConnTimeout = 3;
		m_iReadTimeout = 0;
	}

	public String getDesc() {
		return m_strAppDesc == null ? "" : m_strAppDesc;
	}

	public ApResDir getDir() throws Exception {
		if (m_dir == null)
			m_dir = new ApResDir();
		return m_dir;
	}

	public void setDir(ApResDir dir) {
		m_dir = dir;
	}

	public long getId() {
		return m_lAppID;
	}

	public String getName() {
		return m_strAppName;
	}

	public String getHostUrl() {
		return m_strHostURL;
	}

	public String getStatus() {
		switch (m_iStatusID) {
		case STATUS_NORMAL:
			return "正常";
		case STATUS_TEST:
			return "测试";
		default:
			return "停止";
		}
	}

	public long getLogoImageId() {
		return m_lLogoImageID;
	}

	public Vector<ApWindow> getWindows() {
		if (m_vecWindows == null)
			m_vecWindows = new Vector<ApWindow>();
		return m_vecWindows;
	}

	public void setWindows(Vector<ApWindow> vec) {
	}

	/**
	 * 从Cache中获取App
	 */
	public static ApApp getApp(AdbAdapter adapter, long appID,
			boolean forceCheck) throws Exception {
		ApApp app = null;
		String key = Long.toString(appID);

		app = m_htApps.get(key);

		if (app != null) {
			if (!adapter.syncCheck(app, forceCheck, false)) {
				// 不同步时不直接在原对象上进行同步，以防影响已经获取该对象的线程的处理
				m_htApps.remove(key);
				app = null;
			}
		}

		if (app == null) { // 需要加载
			app = new ApApp();
			app.m_lAppID = appID;

			if (adapter.retrieve(app) == null)
				return null;

			m_htApps.put(key, app);
		}

		return app;
	}
}
