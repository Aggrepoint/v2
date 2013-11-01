package com.icebean.ds.data;

import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * Daemon参数定义
 * 
 * @author YJM
 */
public class DaemonParamDef extends ADB {
	/** Daemon编号 */
	public long m_lID;
	/** 参数名称 */
	public String m_strName;
	/** 缺省值 */
	public String m_strValue;
	/** 是否可以动态更改 */
	public int m_iCanChangeDyna;
	/** 介绍 */
	public String m_strDescription;

	public DaemonParamDef() throws AdbException {
		m_lID = -1;
		m_strName = "";
		m_strValue = "";
		m_iCanChangeDyna = 0;
		m_strDescription = "";
	}

	public int getValue(int defaultValue) {
		try {
			return Integer.parseInt(m_strValue);
		} catch (Exception e) {
		}

		return defaultValue;
	}
}
