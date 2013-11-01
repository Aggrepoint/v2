package com.aggrepoint.su.core.data;

import com.icebean.core.adb.*;

/**
 * 系统公用的发布配置
 * 
 * @author YJM
 */
public class SysPubCfg extends ADB {
	public String m_strCfgName;
	public String m_strCfgValue;

	public SysPubCfg() throws AdbException {
		m_strCfgName = m_strCfgValue = "";
	}
}
