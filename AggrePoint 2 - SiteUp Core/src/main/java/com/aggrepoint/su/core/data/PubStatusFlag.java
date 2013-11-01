package com.aggrepoint.su.core.data;

import com.icebean.core.adb.*;

/**
 * 与SitePub同步标记
 * 
 * @author YJM
 */
public class PubStatusFlag extends ADB {
	public int m_iTableID;
	public String m_strSitePubID;

	public PubStatusFlag() throws AdbException {
		m_iTableID = 0;
		m_strSitePubID = "";
	}
}
