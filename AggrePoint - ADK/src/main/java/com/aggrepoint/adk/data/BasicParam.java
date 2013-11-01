package com.aggrepoint.adk.data;

import com.icebean.core.adb.*;

/**
 * Controler的参数定义
 * 
 * @author YJM
 */
public class BasicParam extends ADB{
	/** 参数名称 */
	public String m_strName;
	/** 参数值 */
	public String m_strValue;

	public BasicParam() throws AdbException {
		m_strName = m_strValue = "";
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<param name=\"");
		pw.print(m_strName);
		pw.print("\" value=\"");
		pw.print(m_strValue);
		pw.println("\"/>");
	}
}
