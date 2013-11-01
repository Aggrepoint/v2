package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * 返回码级别上使用的Logger的定义
 * 
 * @author YJM
 */
public class RetCodeLevelLoggerDef extends ADB {
	public String m_strID;

	public RetCodeLevelLoggerDef() throws AdbException {
		m_strID = "";
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<logger id=\"");
		pw.print(m_strID);
		pw.println("\"/>");
	}
}
