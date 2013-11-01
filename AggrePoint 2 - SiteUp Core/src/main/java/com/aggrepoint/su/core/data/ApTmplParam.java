package com.aggrepoint.su.core.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 
 * @author YJM
 */
public class ApTmplParam extends ADB {
	public long m_lTmplParamID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lTemplateID;
	public String m_strParamName;
	public String m_strParamDesc;
	public String m_strDefaultValue;

	public ApTmplParam() throws AdbException {
		m_lTmplParamID = m_lTemplateID = 0;
		m_strParamName = m_strParamDesc = m_strDefaultValue = "";
	}
}
