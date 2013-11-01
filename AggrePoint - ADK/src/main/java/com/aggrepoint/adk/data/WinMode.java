package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * 窗口模式定义
 * 
 * @author YJM
 */
public class WinMode extends ADB{
	/** 参数名称 */
	public String m_strName;
	/** 参数值 */
	public String m_strValue;

	public WinMode() throws AdbException {
		m_strName = m_strValue = "";
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<winmode name=\"");
		pw.print(m_strName);
		pw.print("\" value=\"");
		pw.print(m_strValue);
		pw.println("\"/>");
	}
}
