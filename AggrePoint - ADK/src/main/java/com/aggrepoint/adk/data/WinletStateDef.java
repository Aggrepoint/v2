package com.aggrepoint.adk.data;

import com.icebean.core.adb.AdbException;

/**
 * 状态定义
 * 
 * @author YJM
 */
public class WinletStateDef extends WinletMethodDef {
	/** 是否缺省的状态 */
	public boolean m_bDefault;

	public WinletStateDef() throws AdbException {
		super();
		m_bDefault = false;
	}

	public String getDefault() {
		return m_bDefault ? "YES" : "NO";
	}

	public void setDefault(String str) {
		if (str != null)
			m_bDefault = str.equalsIgnoreCase("yes");
	}

	public void print(String tab, java.io.PrintWriter pw) {
		printStartTag(tab, "id", pw, "state");
		printSubElements(tab, pw);
		printEndTag(tab, pw, "state");
	}
}
