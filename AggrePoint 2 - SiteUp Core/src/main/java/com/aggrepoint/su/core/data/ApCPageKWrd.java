package com.aggrepoint.su.core.data;

import com.icebean.core.adb.*;

/**
 * 
 * @author YJM
 */
public class ApCPageKWrd extends ADB {
	public long m_lKeywordID;
	public int m_iOfficialFlag;
	public boolean m_bPubFlag;
	public long m_lPageID;

	public ApCPageKWrd() throws AdbException {
		m_lKeywordID = m_lPageID = 0;
	}
}
