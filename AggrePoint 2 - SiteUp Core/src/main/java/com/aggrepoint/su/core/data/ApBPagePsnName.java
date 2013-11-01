package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 站点页面个性化名称
 * 
 * @author YJM
 */
public class ApBPagePsnName extends ADB {
	public long m_lPsnNameID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lBranchID;
	public long m_lPageID;
	public String m_strPageName;
	public String m_strOwnerID;
	public String m_strAccessRule;

	public ApBPagePsnName() throws AdbException {
		m_lPsnNameID = m_lPageID = 0;
		m_strAccessRule = "T";
		m_strPageName = m_strOwnerID = "";
	}

	public String getOwnerId() {
		return m_strOwnerID == null ? "" : m_strOwnerID;
	}

	public void setOwnerId(String str) {
		m_strOwnerID = str == null ? "" : str;
	}

	public long getId() {
		return m_lPsnNameID;
	}

	public String getName() {
		return m_strPageName;
	}

	public String getAccessRule() {
		return m_strAccessRule;
	}
}
