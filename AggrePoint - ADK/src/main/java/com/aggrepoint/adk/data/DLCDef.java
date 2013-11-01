package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * 需动态加载的类的定义
 * 
 * @author YJM
 */
public class DLCDef extends ADB {
	public String m_strClass;

	public DLCDef() throws AdbException {
		m_strClass = "";
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<class path=\"");
		pw.print(m_strClass);
		pw.println("\"/>");
	}
}
