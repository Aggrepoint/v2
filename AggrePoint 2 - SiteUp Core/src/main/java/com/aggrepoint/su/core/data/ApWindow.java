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
public class ApWindow extends ADB {
	public long m_lWindowID;

	public int m_iOfficialFlag;

	public boolean m_bPubFlag;

	public long m_lSiteID;

	public long m_lAppID;

	public long m_lPreviewImageID;

	public String m_strURL;

	public String m_strAccessRule;

	public String m_strName;

	public String m_strDesc;

	public int m_iWinMode;

	public String m_strUUID;

	public FileParameter m_previewImage;

	/** 以下属性用于从XML文件中导入 */
	public long m_lImportID;

	public String m_strLogoContentType;

	public String m_strLogoName;

	public String m_strLogoFile;

	public Vector<ApWinParam> m_vecParams;

	/** AAWindow Cache */
	static Hashtable<String, ApWindow> m_htWindows = new Hashtable<String, ApWindow>();

	public ApWindow() throws AdbException {
		m_strURL = m_strName = m_strDesc = m_strLogoContentType = m_strLogoName = m_strLogoFile = m_strUUID = "";
		m_strAccessRule = "T";
		m_iWinMode = 6;
	}

	public long getId() {
		return m_lWindowID;
	}

	public String getName() {
		return m_strName;
	}

	public String getUrl() {
		return m_strURL;
	}

	public String getDesc() {
		return m_strDesc == null ? "" : m_strDesc;
	}

	public Vector<ApWinParam> getParams() {
		if (m_vecParams == null)
			m_vecParams = new Vector<ApWinParam>();
		return m_vecParams;
	}

	public void setParams(Vector<ApWinParam> vec) {
	}

	/**
	 * 是否可以通过action设置用户身份
	 * 
	 * @return
	 */
	public boolean getUserProfileFlag() {
		return (m_iWinMode & 1) != 0;
	}

	public void setUserProfileFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 1;
		else
			m_iWinMode &= 0xFE;
	}

	/**
	 * 是否可以在view时改变用户身份。如果是，则需要在显示页面前先请求窗口，如果用户身份改变则重新开始处理过程
	 * 
	 */
	public boolean getViewChangeUserFlag() {
		return (m_iWinMode & 64) != 0;
	}

	public void setViewChangeUserFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 64;
		else
			m_iWinMode &= 0xBF;
	}

	/**
	 * 是否可以动态设置窗口标题
	 * 
	 * @return
	 */
	public boolean getDynaTitleFlag() {
		return (m_iWinMode & 2) != 0;
	}

	public void setDynaTitleFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 2;
		else
			m_iWinMode &= 0xFD;
	}

	/**
	 * 是否可以自己变更窗口模式
	 * 
	 * @return
	 */
	public boolean getDynaWinStateFlag() {
		return (m_iWinMode & 4) != 0;
	}

	public void setDynaWinStateFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 4;
		else
			m_iWinMode &= 0xFB;
	}

	/**
	 * 最小化时是否需要发起请求
	 * 
	 * @return
	 */
	public boolean getMinRequestFlag() {
		return (m_iWinMode & 8) != 0;
	}

	public void setMinRequestFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 8;
		else
			m_iWinMode &= 0xF7;
	}

	public boolean getSupportMaxFlag() {
		return (m_iWinMode & 16) != 0;
	}

	public void setSupportMaxFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 16;
		else
			m_iWinMode &= 0xDF;
	}

	public boolean getSupportMinFlag() {
		return (m_iWinMode & 32) != 0;
	}

	public void setSupportMinFlag(boolean flag) {
		if (flag)
			m_iWinMode |= 32;
		else
			m_iWinMode &= 0xDF;
	}

	public String getAccessRule() {
		return m_strAccessRule;
	}

	/**
	 * 从Cache中获取Window
	 */
	public static ApWindow getWindow(AdbAdapter adapter, long windowID,
			boolean forceCheck) throws Exception {
		ApWindow window = null;
		String key = Long.toString(windowID);

		window = m_htWindows.get(key);

		if (window != null) {
			if (!adapter.syncCheck(window, forceCheck, false)) {
				// 不同步时不直接在原对象上进行同步，以防影响已经获取该对象的线程的处理
				m_htWindows.remove(key);
				window = null;
			}
		}

		if (window == null) { // 需要加载
			window = new ApWindow();
			window.m_lWindowID = windowID;

			if (adapter.retrieve(window) == null)
				return null;

			m_htWindows.put(key, window);
		}

		return window;
	}

	/**
	 * 根据id查找UUID
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ApWindow findUuidById(AdbAdapter adapter, long id)
			throws Exception {
		ApWindow window = new ApWindow();

		window.m_lWindowID = id;
		window = adapter.retrieve(window, "loadUUIDWithCache");
		if (window == null)
			throw new Exception("Window " + id + " not found!");
		return window;
	}

	/**
	 * 根据UUID查找ID
	 * 
	 * @param adapter
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static long findIdByUuid(AdbAdapter adapter, String uuid)
			throws Exception {
		ApWindow window = new ApWindow();

		window.m_strUUID = uuid;
		window = adapter.retrieve(window, "loadIDWithCache");
		if (window == null)
			throw new Exception("Window " + uuid + " not found!");
		return window.m_lWindowID;
	}
}