package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * 需要Logger自动记录日志的参数的名称
 * 
 * @author YJM
 */
public class LogParam extends ADB {
	/** 参数名称 */
	public String m_strName;

	public LogParam() throws AdbException {
		m_strName = "";
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<logparam name=\"");
		pw.print(m_strName);
		pw.println("\"/>");
	}
}
