package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * 设备定义
 * 
 * @author YJM
 */
public class DeviceDef extends ADB {
	public String m_strID;
	public boolean m_bDefault;

	public void setDefault(String def) {
		m_bDefault = def.equalsIgnoreCase("YES");
	}

	public String getDefault() {
		return m_bDefault ? "YES" : "NO";
	}

	public DeviceDef() throws AdbException {
		m_strID = "";
		m_bDefault = false;
	}
}
