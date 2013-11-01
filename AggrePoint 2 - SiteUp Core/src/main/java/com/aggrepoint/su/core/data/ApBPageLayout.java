package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApBPageLayout extends ADB {
	public long m_lBPLayoutID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lPageID;
	public long m_lBranchID;
	public long m_lLayoutID;
	public String m_strAreaName;
	public boolean m_bInheritable;
	/** 用于从XML文件导入 */
	public String m_strLayoutUUID;

	public ApBPageLayout() throws AdbException {
		m_strAreaName = "";
		m_bInheritable = false;
	}

	public String getInheritable() {
		return m_bInheritable ? "y" : "n";
	}

	public void setInheritable(String str) {
		if (str != null && str.equalsIgnoreCase("y"))
			m_bInheritable = true;
		else
			m_bInheritable = false;
	}

	public long getLayoutId() {
		return m_lLayoutID;
	}

	public String getAreaName() {
		return m_strAreaName;
	}
}
