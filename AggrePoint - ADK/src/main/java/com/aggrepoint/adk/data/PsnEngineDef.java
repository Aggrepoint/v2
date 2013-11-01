package com.aggrepoint.adk.data;

import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.plugin.SimplePsnEngine;
import com.icebean.core.adb.ADB;
import com.icebean.core.adb.AdbException;

/**
 * 个性化引擎定义
 * 
 * @author YJM
 */
public class PsnEngineDef extends ADB {
	public String m_strID;
	public String m_strModule;
	public boolean m_bDefault;

	public PsnEngineDef() throws AdbException {
		m_strID = "";
		m_strModule = SimplePsnEngine.class.getName();
		m_bDefault = false;
	}

	public String getDefault() {
		return m_bDefault ? "y" : "n";
	}

	public void setDefault(String str) {
		if (str != null)
			m_bDefault = str.equalsIgnoreCase("y");
	}

	public IPsnEngine newEngineInstance() throws Exception {
		return (IPsnEngine) Class.forName(m_strModule).newInstance();
	}

	public void print(String tab, java.io.PrintWriter pw) {
		pw.print(tab);
		pw.print("<psn id=\"");
		pw.print(m_strID);
		pw.print("\" module=\"");
		pw.print(m_strModule);
		pw.print("\" default=\"");
		pw.print(getDefault());
		pw.println("\"/>");
	}
}
