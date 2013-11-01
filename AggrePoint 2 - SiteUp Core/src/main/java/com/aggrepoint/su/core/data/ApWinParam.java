package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApWinParam extends ADB {
	public long m_lWinParamID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lWindowID;
	public String m_strParamName;
	public String m_strParamDesc;
	public String m_strDefaultValue;

	public ApWinParam() throws AdbException {
		m_strParamName = m_strParamDesc = m_strDefaultValue = "";
	}

	public String getParamDesc() {
		return m_strParamDesc == null ? "" : m_strParamDesc;
	}

	public void setParamDesc(String str) {
		m_strParamDesc = str == null ? "" : str;
	}
}
